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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles safe cross-dimension teleportation between Overworld and the Five Horizons,
 * including cooldowns for physical portal blocks and return portal construction.
 */
public final class HorizonTeleportManager {
    private static final Map<UUID, Long> PORTAL_COOLDOWNS = new HashMap<>();
    private static final long COOLDOWN_TICKS = 80L;

    private HorizonTeleportManager() {
    }

    public static boolean canUsePortal(ServerPlayerEntity player) {
        long now = player.getWorld().getTime();
        Long until = PORTAL_COOLDOWNS.get(player.getUuid());
        return until == null || now >= until;
    }

    public static void setPortalCooldown(ServerPlayerEntity player) {
        PORTAL_COOLDOWNS.put(player.getUuid(), player.getWorld().getTime() + COOLDOWN_TICKS);
    }

    public static boolean teleportToHorizon(ServerPlayerEntity player, HorizonDimension destination) {
        return teleportToHorizon(player, destination, false);
    }

    public static boolean teleportToHorizon(ServerPlayerEntity player, HorizonDimension destination, boolean buildElysiumReturnPortal) {
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

        if (buildElysiumReturnPortal && destination == HorizonDimension.ELYSIUM) {
            ElysiumPortalHelper.ensureReturnPortal(targetWorld, targetPos);
            targetPos = targetPos.south(2);
        }

        player.teleport(
                targetWorld,
                targetPos.getX() + 0.5,
                targetPos.getY(),
                targetPos.getZ() + 0.5,
                player.getYaw(),
                player.getPitch()
        );
        player.fallDistance = 0.0f;
        setPortalCooldown(player);

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
        return teleportToOverworld(player, false);
    }

    public static boolean teleportToOverworld(ServerPlayerEntity player, boolean preserveHorizontalCoords) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return false;
        }

        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        if (overworld == null) {
            return false;
        }

        BlockPos targetPos;
        if (preserveHorizontalCoords) {
            targetPos = findOrCreateOverworldLanding(overworld, player.getBlockX(), player.getBlockZ());
            ElysiumPortalHelper.ensureReturnPortal(overworld, targetPos);
            targetPos = targetPos.south(2);
        } else {
            BlockPos spawnPos = overworld.getSpawnPos();
            overworld.getChunk(spawnPos);
            int topY = overworld.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, spawnPos.getX(), spawnPos.getZ());
            int safeY = Math.max(topY, overworld.getBottomY() + 1);
            targetPos = new BlockPos(spawnPos.getX(), safeY, spawnPos.getZ());
        }

        player.teleport(
                overworld,
                targetPos.getX() + 0.5,
                targetPos.getY(),
                targetPos.getZ() + 0.5,
                player.getYaw(),
                player.getPitch()
        );
        player.fallDistance = 0.0f;
        setPortalCooldown(player);
        overworld.playSound(null, targetPos,
                SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.PLAYERS, 0.7f, 1.1f);
        return true;
    }

    private static BlockPos findOrCreateOverworldLanding(ServerWorld overworld, int x, int z) {
        overworld.getChunk(x >> 4, z >> 4);
        int surfaceY = overworld.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
        if (surfaceY > overworld.getBottomY() + 2 && surfaceY < overworld.getTopY() - 5) {
            BlockPos candidate = new BlockPos(x, surfaceY, z);
            if (isSafeStandingSpot(overworld, candidate)) {
                return candidate;
            }
        }
        BlockPos fallback = new BlockPos(x, Math.max(surfaceY, 70), z);
        buildArrivalPlatform(overworld, fallback);
        return fallback;
    }

    private static BlockPos findOrCreateSafeLanding(ServerWorld world, int x, int z, HorizonDimension dimension) {
        world.getChunk(x >> 4, z >> 4);

        if (dimension.hasCeiling()) {
            int minY = world.getBottomY() + 8;
            int maxY = Math.min(world.getTopY() - 16, 110);
            for (int y = maxY; y >= minY; y--) {
                BlockPos feet = new BlockPos(x, y, z);
                if (isSafeStandingSpot(world, feet)) {
                    return feet;
                }
            }
            BlockPos fallback = new BlockPos(x, 64, z);
            buildArrivalPlatform(world, fallback);
            return fallback;
        }

        int surfaceY = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
        if (surfaceY > world.getBottomY() + 2 && surfaceY < world.getTopY() - 4) {
            BlockPos candidate = new BlockPos(x, surfaceY, z);
            if (isSafeStandingSpot(world, candidate)) {
                return candidate;
            }
        }

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
