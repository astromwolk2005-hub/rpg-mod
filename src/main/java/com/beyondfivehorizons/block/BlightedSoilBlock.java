package com.beyondfivehorizons.block;

import com.beyondfivehorizons.item.ElysiumArmorItem;
import com.beyondfivehorizons.registry.ModParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/**
 * Infected soil found in Dead Thicket. Applies weakness/poison to unprotected players.
 * Elysium Armor protects against this environmental hazard.
 */
public class BlightedSoilBlock extends Block {
    public BlightedSoilBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!world.isClient && entity instanceof LivingEntity living && world.getTime() % 30 == 0) {
            if (living instanceof PlayerEntity player && ElysiumArmorItem.hasAnyElysiumArmor(player)) {
                return;
            }
            if (living instanceof PlayerEntity player && player.isCreative()) {
                return;
            }
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 50, 0));
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 50, 0));
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(6) == 0) {
            world.addParticle(ModParticles.BLIGHT_SPORE,
                    pos.getX() + random.nextDouble(),
                    pos.getY() + 1.05,
                    pos.getZ() + random.nextDouble(),
                    0.0, 0.025, 0.0);
        }
    }
}
