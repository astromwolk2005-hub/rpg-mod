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
 * Generates blackened blighted trees, patches of Blighted Soil, and occasional Blight Cores
 * inside the Dead Thicket biome.
 */
public class BlightedTreeFeature extends Feature<DefaultFeatureConfig> {
    public BlightedTreeFeature(Codec<DefaultFeatureConfig> codec) {
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

        BlockState soil = ModBlocks.BLIGHTED_SOIL.getDefaultState();
        BlockState log = ModBlocks.BLIGHTED_LOG.getDefaultState();

        // Corrupt soil patch around trunk
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                if (dx * dx + dz * dz <= 9 && random.nextInt(3) != 0) {
                    BlockPos f = origin.add(dx, -1, dz);
                    if (world.getBlockState(f).isSolidBlock(world, f)) {
                        world.setBlockState(f, soil, Block.NOTIFY_LISTENERS);
                    }
                }
            }
        }

        // Twisted blackened trunk
        int height = 7 + random.nextInt(7);
        BlockPos cur = origin;
        for (int y = 0; y < height; y++) {
            world.setBlockState(cur, log, Block.NOTIFY_LISTENERS);
            if (y > 2 && random.nextInt(3) == 0) {
                cur = cur.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
            } else {
                cur = cur.up();
            }
        }

        // Dead jagged branches
        for (int b = 0; b < 3; b++) {
            int bx = random.nextBoolean() ? 1 : -1;
            int bz = random.nextBoolean() ? 1 : -1;
            BlockPos bp = origin.up(height - 2 - random.nextInt(2));
            for (int step = 1; step <= 3; step++) {
                world.setBlockState(bp.add(bx * step, step / 2, bz * step), log, Block.NOTIFY_LISTENERS);
            }
        }

        // Occasional Blight Core at tree base
        if (random.nextInt(5) == 0) {
            world.setBlockState(origin.add(1, 0, 0), ModBlocks.BLIGHT_CORE.getDefaultState(), Block.NOTIFY_LISTENERS);
        }

        return true;
    }
}
