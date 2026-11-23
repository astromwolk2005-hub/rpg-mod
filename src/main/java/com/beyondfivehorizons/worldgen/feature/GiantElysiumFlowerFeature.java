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
 * Generates building-sized giant flowers in the Garden of Giants biome.
 */
public class GiantElysiumFlowerFeature extends Feature<DefaultFeatureConfig> {
    public GiantElysiumFlowerFeature(Codec<DefaultFeatureConfig> codec) {
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
        if (origin.getY() < 60) {
            return false;
        }

        int stemHeight = 7 + random.nextInt(8);
        BlockState stem = ModBlocks.ELYSIUM_ROOT_BLOCK.getDefaultState();
        BlockState leaf = ModBlocks.ELYSIUM_LEAVES.getDefaultState();
        BlockState petal = switch (random.nextInt(3)) {
            case 0 -> Blocks.PINK_WOOL.getDefaultState();
            case 1 -> Blocks.MAGENTA_CONCRETE_POWDER.getDefaultState();
            default -> Blocks.LIGHT_BLUE_WOOL.getDefaultState();
        };
        BlockState core = ModBlocks.LUMINOUS_LANTERN.getDefaultState();

        // Stem with side giant leaves
        for (int y = 0; y < stemHeight; y++) {
            world.setBlockState(origin.up(y), stem, Block.NOTIFY_LISTENERS);
            if (y == stemHeight / 2) {
                for (int d = 1; d <= 3; d++) {
                    world.setBlockState(origin.add(d, y, 0), leaf, Block.NOTIFY_LISTENERS);
                    world.setBlockState(origin.add(-d, y, 0), leaf, Block.NOTIFY_LISTENERS);
                    world.setBlockState(origin.add(0, y, d), leaf, Block.NOTIFY_LISTENERS);
                    world.setBlockState(origin.add(0, y, -d), leaf, Block.NOTIFY_LISTENERS);
                }
            }
        }

        // Giant blooming flower head (bowl of petals + glowing center)
        BlockPos head = origin.up(stemHeight);
        world.setBlockState(head, core, Block.NOTIFY_LISTENERS);
        world.setBlockState(head.up(), ModBlocks.NIGHTBLOOM_FLOWER.getDefaultState(), Block.NOTIFY_LISTENERS);

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                int distSq = dx * dx + dz * dz;
                if (distSq == 0) continue;
                if (distSq <= 16 && distSq >= 2) {
                    int dy = distSq >= 10 ? 2 : (distSq >= 5 ? 1 : 0);
                    BlockPos p = head.add(dx, dy, dz);
                    if (world.getBlockState(p).isAir()) {
                        world.setBlockState(p, petal, Block.NOTIFY_LISTENERS);
                    }
                }
            }
        }

        return true;
    }
}
