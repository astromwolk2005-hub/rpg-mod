package com.beyondfivehorizons.block;

import com.beyondfivehorizons.registry.ModBlocks;
import com.beyondfivehorizons.registry.ModParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

/**
 * Restorative vegetation block: gradually restores adjacent Blighted Soil
 * into healthy Elysium Moss Stone and sprouts Evolving Sprouts above itself.
 */
public class RestorativeMossBlock extends Block {
    public RestorativeMossBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        for (int i = 0; i < 2; i++) {
            BlockPos target = pos.add(random.nextInt(5) - 2, random.nextInt(3) - 1, random.nextInt(5) - 2);
            if (world.getBlockState(target).isOf(ModBlocks.BLIGHTED_SOIL)) {
                world.setBlockState(target, ModBlocks.ELYSIUM_MOSS_STONE.getDefaultState(), Block.NOTIFY_LISTENERS);
                world.spawnParticles(ModParticles.LIVING_SPORE,
                        target.getX() + 0.5, target.getY() + 1.1, target.getZ() + 0.5,
                        6, 0.3, 0.2, 0.3, 0.02);
                return;
            }
        }

        if (random.nextInt(12) == 0 && world.getBlockState(pos.up()).isAir()) {
            world.setBlockState(pos.up(), ModBlocks.EVOLVING_SPROUT.getDefaultState(), Block.NOTIFY_LISTENERS);
        }
    }
}
