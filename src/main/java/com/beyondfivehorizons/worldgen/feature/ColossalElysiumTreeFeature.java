package com.beyondfivehorizons.worldgen.feature;

import com.beyondfivehorizons.registry.ModBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

/**
 * Generates procedural colossal ancient Elysium trees (22-56 blocks tall)
 * with massive buttress roots, hollow interior trunks, glowing lanterns, and multi-tiered canopies.
 */
public class ColossalElysiumTreeFeature extends Feature<DefaultFeatureConfig> {
    public ColossalElysiumTreeFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();

        BlockState below = world.getBlockState(origin.down());
        if (!below.isOf(Blocks.GRASS_BLOCK) && !below.isOf(Blocks.DIRT) && !below.isOf(ModBlocks.ELYSIUM_MOSS_STONE)) {
            return false;
        }
        if (origin.getY() < 60 || origin.getY() > 210) {
            return false;
        }

        int height = 22 + random.nextInt(34); // 22 to 55 blocks tall
        boolean isGiant = height >= 36;
        int radius = isGiant ? 2 : 1;

        BlockState log = ModBlocks.ELYSIUM_LOG.getDefaultState();
        BlockState hollow = ModBlocks.ELYSIUM_HOLLOW_LOG.getDefaultState();
        BlockState root = ModBlocks.ELYSIUM_ROOT_BLOCK.getDefaultState();
        BlockState leaves = ModBlocks.ELYSIUM_LEAVES.getDefaultState();
        BlockState lantern = ModBlocks.LUMINOUS_LANTERN.getDefaultState();

        // 1. Massive buttress roots anchoring into terrain
        for (int dx = -radius - 2; dx <= radius + 2; dx++) {
            for (int dz = -radius - 2; dz <= radius + 2; dz++) {
                int distSq = dx * dx + dz * dz;
                if (distSq <= (radius + 2) * (radius + 2) && distSq >= radius * radius && random.nextInt(2) == 0) {
                    int rootHeight = 2 + random.nextInt(4);
                    for (int y = -3; y < rootHeight; y++) {
                        BlockPos p = origin.add(dx, y, dz);
                        if (y < 0 || world.getBlockState(p).isAir()) {
                            world.setBlockState(p, root, Block.NOTIFY_LISTENERS);
                        }
                    }
                }
            }
        }

        // 2. Trunk with hollow interior on giant trees
        for (int y = 0; y < height; y++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (isGiant && Math.abs(dx) == radius && Math.abs(dz) == radius) {
                        continue; // rounded corners
                    }
                    boolean interior = isGiant && Math.abs(dx) < radius && Math.abs(dz) < radius;
                    BlockPos p = origin.add(dx, y, dz);
                    if (interior) {
                        if (y == 0) {
                            world.setBlockState(p, ModBlocks.ELYSIUM_MOSS_STONE.getDefaultState(), Block.NOTIFY_LISTENERS);
                        } else if (y == 4 && dx == 0 && dz == 0) {
                            world.setBlockState(p, lantern, Block.NOTIFY_LISTENERS);
                        } else {
                            world.setBlockState(p, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                        }
                    } else {
                        // Create doorway into hollow trunk on south side
                        if (isGiant && dz == radius && dx == 0 && (y == 1 || y == 2)) {
                            world.setBlockState(p, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                        } else {
                            world.setBlockState(p, (y % 5 == 0) ? hollow : log, Block.NOTIFY_LISTENERS);
                        }
                    }
                }
            }
        }

        // 3. Multi-layered canopy crowns with hanging glowing lanterns
        int layers = isGiant ? 3 : 2;
        for (int layer = 0; layer < layers; layer++) {
            int crownY = height - (layers - 1 - layer) * 6;
            int crownRad = (isGiant ? 6 : 4) - (layer == layers - 1 ? 1 : 0);
            for (int dy = -2; dy <= 3; dy++) {
                int r = Math.max(1, crownRad - Math.abs(dy));
                for (int dx = -r; dx <= r; dx++) {
                    for (int dz = -r; dz <= r; dz++) {
                        if (dx * dx + dz * dz <= r * r + 1) {
                            BlockPos lp = origin.add(dx, crownY + dy, dz);
                            if (world.getBlockState(lp).isAir()) {
                                world.setBlockState(lp, leaves, Block.NOTIFY_LISTENERS);
                            }
                        }
                    }
                }
            }
            // Hanging luminous lanterns under the crown
            for (int i = 0; i < 4; i++) {
                int lx = random.nextInt(crownRad * 2 + 1) - crownRad;
                int lz = random.nextInt(crownRad * 2 + 1) - crownRad;
                BlockPos hang = origin.add(lx, crownY - 3, lz);
                if (world.getBlockState(hang).isAir() && world.getBlockState(hang.up()).isOf(ModBlocks.ELYSIUM_LEAVES)) {
                    world.setBlockState(hang, lantern, Block.NOTIFY_LISTENERS);
                }
            }
        }

        return true;
    }
}
