package com.beyondfivehorizons.block;

import com.beyondfivehorizons.registry.ModBlocks;
import com.beyondfivehorizons.registry.ModParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/**
 * Source of Elysium's Dead Thicket infection.
 * Slowly converts natural grass/moss/Elysium logs within a strictly bounded 5-block radius
 * so it never creates runaway world corruption. Can be purified using Purifying Nectar.
 */
public class BlightCoreBlock extends Block {
    private static final int SPREAD_RADIUS = 5;

    public BlightCoreBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockPos target = pos.add(
                random.nextInt(SPREAD_RADIUS * 2 + 1) - SPREAD_RADIUS,
                random.nextInt(3) - 1,
                random.nextInt(SPREAD_RADIUS * 2 + 1) - SPREAD_RADIUS
        );
        BlockState targetState = world.getBlockState(target);
        if (targetState.isOf(Blocks.GRASS_BLOCK) || targetState.isOf(ModBlocks.ELYSIUM_MOSS_STONE)) {
            world.setBlockState(target, ModBlocks.BLIGHTED_SOIL.getDefaultState(), Block.NOTIFY_LISTENERS);
        } else if (targetState.isOf(ModBlocks.ELYSIUM_LOG)) {
            world.setBlockState(target, ModBlocks.BLIGHTED_LOG.getDefaultState(), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        for (int i = 0; i < 2; i++) {
            world.addParticle(ModParticles.BLIGHT_SPORE,
                    pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 1.2,
                    pos.getY() + 0.8 + random.nextDouble() * 0.6,
                    pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 1.2,
                    0.0, 0.04, 0.0);
        }
    }
}
