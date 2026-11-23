package com.beyondfivehorizons.worldgen.feature;

import com.beyondfivehorizons.registry.ModBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

/**
 * Generates natural living root arches and walkways across forest clearings and streams.
 */
public class RootBridgeFeature extends Feature<DefaultFeatureConfig> {
    public RootBridgeFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();

        if (origin.getY() < 60) {
            return false;
        }

        boolean alongX = random.nextBoolean();
        int halfLen = 5;
        BlockState root = ModBlocks.ELYSIUM_ROOT_BLOCK.getDefaultState();
        BlockState moss = ModBlocks.RESTORATIVE_MOSS.getDefaultState();

        for (int i = -halfLen; i <= halfLen; i++) {
            int archY = (int) Math.round(3.0 * (1.0 - (double) (i * i) / (halfLen * halfLen)));
            for (int w = -1; w <= 1; w++) {
                int dx = alongX ? i : w;
                int dz = alongX ? w : i;
                BlockPos p = origin.add(dx, archY, dz);
                world.setBlockState(p, (w == 0 && Math.abs(i) <= 2) ? moss : root, Block.NOTIFY_LISTENERS);
                if (Math.abs(i) == halfLen) {
                    for (int down = 1; down <= 3; down++) {
                        world.setBlockState(p.down(down), root, Block.NOTIFY_LISTENERS);
                    }
                }
            }
        }
        return true;
    }
}
