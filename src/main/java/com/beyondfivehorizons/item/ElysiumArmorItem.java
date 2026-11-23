package com.beyondfivehorizons.item;

import com.beyondfivehorizons.dimension.HorizonDimension;
import com.beyondfivehorizons.registry.ModArmorMaterials;
import com.beyondfivehorizons.registry.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

/**
 * Elysium Armor Set.
 * Partial set: protects against Dead Thicket blight hazards.
 * Full set: passive Regeneration I, Poison immunity, and Swiftness inside Elysium.
 */
public class ElysiumArmorItem extends ArmorItem {
    public ElysiumArmorItem(Type type, Settings settings) {
        super(ModArmorMaterials.ELYSIUM, type, settings);
    }

    public static boolean hasAnyElysiumArmor(PlayerEntity player) {
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            if (player.getEquippedStack(slot).getItem() instanceof ElysiumArmorItem) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasFullElysiumSet(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.HEAD).isOf(ModItems.ELYSIUM_HELMET)
                && player.getEquippedStack(EquipmentSlot.CHEST).isOf(ModItems.ELYSIUM_CHESTPLATE)
                && player.getEquippedStack(EquipmentSlot.LEGS).isOf(ModItems.ELYSIUM_LEGGINGS)
                && player.getEquippedStack(EquipmentSlot.FEET).isOf(ModItems.ELYSIUM_BOOTS);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        // Run set bonus logic only once per second via the chestplate slot
        if (!world.isClient && this.getSlotType() == EquipmentSlot.CHEST
                && world.getTime() % 20 == 0
                && entity instanceof PlayerEntity player
                && player.getEquippedStack(EquipmentSlot.CHEST) == stack) {

            if (hasFullElysiumSet(player)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 60, 0, true, false));
                if (player.hasStatusEffect(StatusEffects.POISON)) {
                    player.removeStatusEffect(StatusEffects.POISON);
                }
                if (world.getRegistryKey().equals(HorizonDimension.ELYSIUM.getWorldKey())) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 60, 0, true, false));
                }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.beyondfivehorizons.elysium_armor").formatted(Formatting.GREEN));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
