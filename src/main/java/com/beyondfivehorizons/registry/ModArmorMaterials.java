package com.beyondfivehorizons.registry;

import com.beyondfivehorizons.BeyondFiveHorizons;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class ModArmorMaterials {
    public static final RegistryEntry<ArmorMaterial> ELYSIUM = registerElysiumMaterial();

    private ModArmorMaterials() {
    }

    private static RegistryEntry<ArmorMaterial> registerElysiumMaterial() {
        Map<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        defense.put(ArmorItem.Type.BOOTS, 3);
        defense.put(ArmorItem.Type.LEGGINGS, 6);
        defense.put(ArmorItem.Type.CHESTPLATE, 8);
        defense.put(ArmorItem.Type.HELMET, 3);
        defense.put(ArmorItem.Type.BODY, 8);

        ArmorMaterial material = new ArmorMaterial(
                defense,
                18,
                SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND,
                () -> Ingredient.ofItems(ModItems.VERDANT_CRYSTAL, ModItems.LIVING_ESSENCE),
                List.of(new ArmorMaterial.Layer(BeyondFiveHorizons.id("elysium"))),
                2.5f,
                0.05f
        );
        return Registry.registerReference(Registries.ARMOR_MATERIAL, BeyondFiveHorizons.id("elysium"), material);
    }

    public static void register() {
        BeyondFiveHorizons.LOGGER.info("Registered Beyond the Five Horizons armor materials.");
    }
}
