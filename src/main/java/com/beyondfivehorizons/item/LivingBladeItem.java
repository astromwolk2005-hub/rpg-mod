package com.beyondfivehorizons.item;

import com.beyondfivehorizons.dimension.HorizonDimension;
import com.beyondfivehorizons.registry.ModParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

/**
 * Elysium signature weapon #1: Living Blade.
 * Regenerates durability in sunlight or inside Elysium, and channels living energy on hit.
 */
public class LivingBladeItem extends SwordItem {
    public LivingBladeItem(Settings settings) {
        super(ElysiumToolMaterial.INSTANCE, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && stack.isDamaged() && world.getTime() % 60 == 0) {
            boolean inElysium = world.getRegistryKey().equals(HorizonDimension.ELYSIUM.getWorldKey());
            boolean inSun = world.isDay() && world.isSkyVisible(entity.getBlockPos());
            if (inElysium || inSun) {
                stack.setDamage(Math.max(0, stack.getDamage() - 1));
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient && attacker.getWorld() instanceof ServerWorld serverWorld) {
            attacker.heal(1.0f);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 80, 0));
            serverWorld.spawnParticles(ModParticles.LIVING_SPORE,
                    target.getX(), target.getBodyY(0.5), target.getZ(),
                    10, 0.35, 0.4, 0.35, 0.05);
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.beyondfivehorizons.living_blade").formatted(Formatting.GREEN));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
