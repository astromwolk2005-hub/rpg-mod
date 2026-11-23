package com.beyondfivehorizons.registry;

import com.beyondfivehorizons.BeyondFiveHorizons;
import com.beyondfivehorizons.item.ElysiumArmorItem;
import com.beyondfivehorizons.item.ElysiumToolMaterial;
import com.beyondfivehorizons.item.ExplorerJournalItem;
import com.beyondfivehorizons.item.HorizonCompassItem;
import com.beyondfivehorizons.item.LivingBladeItem;
import com.beyondfivehorizons.item.PurifyingNectarItem;
import com.beyondfivehorizons.item.RootStaffItem;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

public final class ModItems {
    // --- Core Tools & Horizon Resources ---
    public static final Item EXPLORER_JOURNAL = registerItem("explorer_journal",
            new ExplorerJournalItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item HORIZON_COMPASS = registerItem("horizon_compass",
            new HorizonCompassItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE)));

    public static final Item LIVING_ESSENCE = registerItem("living_essence",
            new Item(new Item.Settings().rarity(Rarity.UNCOMMON)));

    public static final Item CHRONODUST = registerItem("chronodust",
            new Item(new Item.Settings().rarity(Rarity.UNCOMMON)));

    public static final Item SPATIAL_CRYSTAL = registerItem("spatial_crystal",
            new Item(new Item.Settings().rarity(Rarity.RARE)));

    public static final Item PRIMAL_RESIN = registerItem("primal_resin",
            new Item(new Item.Settings().rarity(Rarity.UNCOMMON)));

    public static final Item ASHEN_EMBER = registerItem("ashen_ember",
            new Item(new Item.Settings().fireproof().rarity(Rarity.RARE)));

    // --- Stage 2: Elysium Resources, Artifacts & Utilities ---
    public static final Item LUMINOUS_POLLEN = registerItem("luminous_pollen",
            new Item(new Item.Settings()));

    public static final Item VERDANT_CRYSTAL = registerItem("verdant_crystal",
            new Item(new Item.Settings().rarity(Rarity.UNCOMMON)));

    public static final Item ANCIENT_SAP = registerItem("ancient_sap",
            new Item(new Item.Settings()));

    public static final Item BLIGHTED_SPORE = registerItem("blighted_spore",
            new Item(new Item.Settings().rarity(Rarity.UNCOMMON)));

    public static final Item PURIFYING_NECTAR = registerItem("purifying_nectar",
            new PurifyingNectarItem(new Item.Settings().maxCount(16).rarity(Rarity.RARE)));

    public static final Item HEART_OF_ELYSIUM = registerItem("heart_of_elysium",
            new Item(new Item.Settings().maxCount(1).fireproof().rarity(Rarity.EPIC)));

    // --- Stage 2: Elysium Weapons ---
    public static final Item LIVING_BLADE = registerItem("living_blade",
            new LivingBladeItem(new Item.Settings()
                    .rarity(Rarity.RARE)
                    .attributeModifiers(SwordItem.createAttributeModifiers(ElysiumToolMaterial.INSTANCE, 4, -2.3f))));

    public static final Item ROOT_STAFF = registerItem("root_staff",
            new RootStaffItem(new Item.Settings().maxDamage(380).rarity(Rarity.RARE)));

    // --- Stage 2: Elysium Armor Set ---
    public static final Item ELYSIUM_HELMET = registerItem("elysium_helmet",
            new ElysiumArmorItem(ArmorItem.Type.HELMET,
                    new Item.Settings().rarity(Rarity.RARE).maxDamage(ArmorItem.Type.HELMET.getMaxDamage(34))));

    public static final Item ELYSIUM_CHESTPLATE = registerItem("elysium_chestplate",
            new ElysiumArmorItem(ArmorItem.Type.CHESTPLATE,
                    new Item.Settings().rarity(Rarity.RARE).maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(34))));

    public static final Item ELYSIUM_LEGGINGS = registerItem("elysium_leggings",
            new ElysiumArmorItem(ArmorItem.Type.LEGGINGS,
                    new Item.Settings().rarity(Rarity.RARE).maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(34))));

    public static final Item ELYSIUM_BOOTS = registerItem("elysium_boots",
            new ElysiumArmorItem(ArmorItem.Type.BOOTS,
                    new Item.Settings().rarity(Rarity.RARE).maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(34))));

    // --- Stage 2: Spawn Eggs ---
    public static final Item FIREFLY_SPAWN_EGG = registerItem("firefly_spawn_egg",
            new SpawnEggItem(ModEntities.FIREFLY, 0x3DF5D4, 0xFFF275, new Item.Settings()));

    public static final Item ROOTWALKER_SPAWN_EGG = registerItem("rootwalker_spawn_egg",
            new SpawnEggItem(ModEntities.ROOTWALKER, 0x3D2E20, 0x2EE08C, new Item.Settings()));

    public static final Item PETALMAW_SPAWN_EGG = registerItem("petalmaw_spawn_egg",
            new SpawnEggItem(ModEntities.PETALMAW, 0xE65AA0, 0x36D99C, new Item.Settings()));

    public static final Item ARBOREAL_GUARDIAN_SPAWN_EGG = registerItem("arboreal_guardian_spawn_egg",
            new SpawnEggItem(ModEntities.ARBOREAL_GUARDIAN, 0x2E4A36, 0x76F5E8, new Item.Settings()));

    public static final Item HEART_OF_THE_FOREST_SPAWN_EGG = registerItem("heart_of_the_forest_spawn_egg",
            new SpawnEggItem(ModEntities.HEART_OF_THE_FOREST, 0x16422C, 0x52FFCE, new Item.Settings()));

    private ModItems() {
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, BeyondFiveHorizons.id(name), item);
    }

    public static void register() {
        BeyondFiveHorizons.LOGGER.info("Registered Beyond the Five Horizons items (Stage 2).");
    }
}
