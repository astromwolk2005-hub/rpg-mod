package com.beyondfivehorizons.registry;

import com.beyondfivehorizons.BeyondFiveHorizons;
import com.beyondfivehorizons.item.ExplorerJournalItem;
import com.beyondfivehorizons.item.HorizonCompassItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

/**
 * Central item registry for Beyond the Five Horizons.
 */
public final class ModItems {
    public static final Item EXPLORER_JOURNAL = registerItem("explorer_journal",
            new ExplorerJournalItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item HORIZON_COMPASS = registerItem("horizon_compass",
            new HorizonCompassItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE)));

    // Signature dimension resources (also act as Obelisk activation catalysts)
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

    private ModItems() {
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, BeyondFiveHorizons.id(name), item);
    }

    public static void register() {
        BeyondFiveHorizons.LOGGER.info("Registered Beyond the Five Horizons items.");
    }
}
