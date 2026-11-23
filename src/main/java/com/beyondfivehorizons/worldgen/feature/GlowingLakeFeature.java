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
 * Generates glowing turquoise lakes lined with Luminous Lanterns, Elysium Moss Stone, and Nightbloom flowers.
 */
public class GlowingLakeFeature extends Feature<DefaultFeatureConfig> {
    public GlowingLakeFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();

        if (origin.getY() < 62 || !world.getBlockState(origin.down()).isSolidBlock(world, origin.down())) {
            return false;
        }

        int rad = 4;
        BlockState water = Blocks.WATER.getDefaultState();
        BlockState moss = ModBlocks.ELYSIUM_MOSS_STONE.getDefaultState();
        BlockState lamp = ModBlocks.LUMINOUS_LANTERN.getDefaultState();

        for (int dx = -rad - 1; dx <= rad + 1; dx++) {
            for (int dz = -rad - 1; dz <= rad + 1; dz++) {
                int distSq = dx * dx + dz * dz;
                if (distSq <= rad * rad) {
                    world.setBlockState(origin.add(dx, -2, dz), (distSq <= 2) ? lamp : moss, Block.NOTIFY_LISTENERS);
                    world.setBlockState(origin.add(dx, -1, dz), water, Block.NOTIFY_LISTENERS);
                    world.setBlockState(origin.add(dx, 0, dz), Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                    world.setBlockState(origin.add(dx, 1, dz), Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                } else if (distSq <= (rad + 1) * (rad + 1)) {
                    BlockPos rim = origin.add(dx, -1, dz);
                    world.setBlockState(rim, moss, Block.NOTIFY_LISTENERS);
                    if (random.nextInt(5) == 0 && world.getBlockState(rim.up()).isAir()) {
                        BlockState flora = random.nextBoolean()
                                ? ModBlocks.NIGHTBLOOM_FLOWER.getDefaultState()
                                : ModBlocks.REACTIVE_BLOSSOM.getDefaultState();
                        world.setBlockState(rim.up(), flora, Block.NOTIFY_LISTENERS);
                    }
                }
            }
        }
        return true;
    }
}
