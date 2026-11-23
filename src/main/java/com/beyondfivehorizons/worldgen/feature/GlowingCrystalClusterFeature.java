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
 * Generates glowing turquoise crystal spires and Verdant Crystal ore veins in Crystal Glades.
 */
public class GlowingCrystalClusterFeature extends Feature<DefaultFeatureConfig> {
    public GlowingCrystalClusterFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();

        if (origin.getY() < 58 || !world.getBlockState(origin.down()).isSolidBlock(world, origin.down())) {
            return false;
        }

        BlockState crystal = ModBlocks.ELYSIUM_CRYSTAL_BLOCK.getDefaultState();
        BlockState ore = ModBlocks.ELYSIUM_CRYSTAL_ORE.getDefaultState();
        BlockState moss = ModBlocks.ELYSIUM_MOSS_STONE.getDefaultState();

        // Mineral base pad
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (dx * dx + dz * dz <= 5) {
                    BlockPos floor = origin.add(dx, -1, dz);
                    world.setBlockState(floor, random.nextInt(3) == 0 ? ore : moss, Block.NOTIFY_LISTENERS);
                }
            }
        }

        // Main central spire + 2 tilted satellite crystals
        int mainHeight = 5 + random.nextInt(6);
        for (int y = 0; y < mainHeight; y++) {
            world.setBlockState(origin.up(y), crystal, Block.NOTIFY_LISTENERS);
            if (y < mainHeight / 2) {
                world.setBlockState(origin.add(1, y, 0), crystal, Block.NOTIFY_LISTENERS);
                world.setBlockState(origin.add(-1, y, 0), crystal, Block.NOTIFY_LISTENERS);
                world.setBlockState(origin.add(0, y, 1), crystal, Block.NOTIFY_LISTENERS);
                world.setBlockState(origin.add(0, y, -1), crystal, Block.NOTIFY_LISTENERS);
            }
        }

        for (int i = 0; i < 3; i++) {
            int sx = random.nextInt(5) - 2;
            int sz = random.nextInt(5) - 2;
            int sh = 2 + random.nextInt(4);
            for (int y = 0; y < sh; y++) {
                BlockPos p = origin.add(sx, y, sz);
                if (world.getBlockState(p).isAir() || world.getBlockState(p).isOf(Blocks.SHORT_GRASS)) {
                    world.setBlockState(p, crystal, Block.NOTIFY_LISTENERS);
                }
            }
        }

        return true;
    }
}
