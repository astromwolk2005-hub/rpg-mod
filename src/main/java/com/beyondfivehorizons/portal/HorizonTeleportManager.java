package com.beyondfivehorizons.portal;

import com.beyondfivehorizons.dimension.HorizonDimension;
import com.beyondfivehorizons.progression.HorizonProgressionState;
import com.beyondfivehorizons.registry.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

/**
 * Handles safe cross-dimension teleportation between Overworld and the Five Horizons.
 * Guarantees that the player does not suffocate in solid blocks or fall into the void.
 */
public final class HorizonTeleportManager {
    private HorizonTeleportManager() {
    }

    public static boolean teleportToHorizon(ServerPlayerEntity player, HorizonDimension destination) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return false;
        }

        ServerWorld targetWorld = server.getWorld(destination.getWorldKey());
        if (targetWorld == null) {
            player.sendMessage(Text.translatable("message.beyondfivehorizons.dimension_missing",
                    Text.translatable(destination.getTranslationKey())), false);
            return false;
        }

        BlockPos targetPos = findOrCreateSafeLanding(targetWorld,
                player.getBlockX(), player.getBlockZ(), destination);

        player.teleport(
                targetWorld,
                targetPos.getX() + 0.5,
                targetPos.getY(),
                targetPos.getZ() + 0.5,
                player.getYaw(),
                player.getPitch()
        );
        player.fallDistance = 0.0f;

        targetWorld.playSound(null, targetPos, SoundEvents.BLOCK_PORTAL_TRAVEL,
                SoundCategory.PLAYERS, 0.7f, 1.0f);

        HorizonProgressionState progression = HorizonProgressionState.get(server);
        if (progression.markDiscovered(player.getUuid(), destination)) {
            player.sendMessage(Text.translatable("message.beyondfivehorizons.dimension_discovered",
                    Text.translatable(destination.getTranslationKey())), false);
        }

        return true;
    }

    public static boolean teleportToOverworld(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return false;
        }

        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        if (overworld == null) {
            return false;
        }

        BlockPos spawnPos = overworld.getSpawnPos();
        overworld.getChunk(spawnPos);
        int topY = overworld.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, spawnPos.getX(), spawnPos.getZ());
        int safeY = Math.max(topY, overworld.getBottomY() + 1);

        player.teleport(
                overworld,
                spawnPos.getX() + 0.5,
                safeY,
                spawnPos.getZ() + 0.5,
                player.getYaw(),
                player.getPitch()
        );
        player.fallDistance = 0.0f;
        overworld.playSound(null, BlockPos.ofFloored(spawnPos.getX(), safeY, spawnPos.getZ()),
                SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.PLAYERS, 0.7f, 1.1f);
        return true;
    }

    /**
     * Scans for a safe standing spot (solid block below, two air/non-solid blocks above).
     * If none is found (e.g., open sky in Astralia or solid rock in Underroot), constructs
     * a small 3x3 Obelisk Stone arrival pad and clears headroom.
     */
    private static BlockPos findOrCreateSafeLanding(ServerWorld world, int x, int z, HorizonDimension dimension) {
        // Force-load target chunk so heightmap and block queries are accurate
        world.getChunk(x >> 4, z >> 4);

        if (dimension.hasCeiling()) {
            // Search inside cavern height range for an existing air pocket over solid ground
            int minY = world.getBottomY() + 8;
            int maxY = Math.min(world.getTopY() - 16, 110);
            for (int y = maxY; y >= minY; y--) {
                BlockPos feet = new BlockPos(x, y, z);
                if (isSafeStandingSpot(world, feet)) {
                    return feet;
                }
            }
            // Fallback: carve a safe pocket at mid-depth and place an arrival pad
            BlockPos fallback = new BlockPos(x, 64, z);
            buildArrivalPlatform(world, fallback);
            return fallback;
        }

        int surfaceY = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
        if (surfaceY > world.getBottomY() + 2 && surfaceY < world.getTopY() - 2) {
            BlockPos candidate = new BlockPos(x, surfaceY, z);
            if (isSafeStandingSpot(world, candidate)) {
                return candidate;
            }
        }

        // Void or liquid fallback (especially relevant for Astralia's floating archipelago)
        int defaultY = dimension.isFloatingIslands() ? 100 : Math.max(surfaceY, 72);
        BlockPos fallback = new BlockPos(x, defaultY, z);
        buildArrivalPlatform(world, fallback);
        return fallback;
    }

    private static boolean isSafeStandingSpot(ServerWorld world, BlockPos feetPos) {
        BlockPos belowPos = feetPos.down();
        BlockPos headPos = feetPos.up();
        BlockState below = world.getBlockState(belowPos);
        BlockState feet = world.getBlockState(feetPos);
        BlockState head = world.getBlockState(headPos);

        boolean solidFloor = !below.getCollisionShape(world, belowPos).isEmpty()
                && below.getFluidState().isEmpty();
        boolean clearSpace = feet.getCollisionShape(world, feetPos).isEmpty()
                && feet.getFluidState().isEmpty()
                && head.getCollisionShape(world, headPos).isEmpty()
                && head.getFluidState().isEmpty();
        return solidFloor && clearSpace;
    }

    private static void buildArrivalPlatform(ServerWorld world, BlockPos centerFeet) {
        BlockState padState = ModBlocks.OBELISK_STONE.getDefaultState();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                world.setBlockState(centerFeet.add(dx, -1, dz), padState);
                world.removeBlock(centerFeet.add(dx, 0, dz), false);
                world.removeBlock(centerFeet.add(dx, 1, dz), false);
                world.removeBlock(centerFeet.add(dx, 2, dz), false);
            }
        }
    }

    public static RegistryKey<World> getWorldKey(HorizonDimension dimension) {
        return dimension.getWorldKey();
    }
}
