package com.beyondfivehorizons.block;

import com.beyondfivehorizons.registry.ModParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/**
 * Arena mechanic block spawned by Heart of the Forest during Phase 2.
 * While intact, it channels healing and protective shields to the boss.
 */
public class VitalityBudBlock extends Block {
    public VitalityBudBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        for (int i = 0; i < 3; i++) {
            world.addParticle(ModParticles.LIVING_SPORE,
                    pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.9,
                    pos.getY() + 1.1,
                    pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.9,
                    0.0, 0.06, 0.0);
        }
    }
}
