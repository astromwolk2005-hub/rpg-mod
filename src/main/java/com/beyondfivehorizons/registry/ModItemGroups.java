package com.beyondfivehorizons.registry;

import com.beyondfivehorizons.BeyondFiveHorizons;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

/**
 * Creative inventory tab for Beyond the Five Horizons.
 */
public final class ModItemGroups {
    public static final ItemGroup MAIN_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            BeyondFiveHorizons.id("main"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModItems.HORIZON_COMPASS))
                    .displayName(Text.translatable("itemGroup.beyondfivehorizons.main"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.EXPLORER_JOURNAL);
                        entries.add(ModItems.HORIZON_COMPASS);

                        entries.add(ModItems.LIVING_ESSENCE);
                        entries.add(ModItems.CHRONODUST);
                        entries.add(ModItems.SPATIAL_CRYSTAL);
                        entries.add(ModItems.PRIMAL_RESIN);
                        entries.add(ModItems.ASHEN_EMBER);

                        entries.add(ModBlocks.OBELISK_STONE);
                        entries.add(ModBlocks.OBELISK_CORE);
                        entries.add(ModBlocks.ELYSIUM_MOSS_STONE);
                        entries.add(ModBlocks.CHRONOS_BRASS_BLOCK);
                        entries.add(ModBlocks.ASTRALIA_CRYSTAL_BLOCK);
                        entries.add(ModBlocks.UNDERROOT_BARK_BLOCK);
                        entries.add(ModBlocks.ASHEN_BASALT_BLOCK);
                    })
                    .build()
    );

    private ModItemGroups() {
    }

    public static void register() {
        BeyondFiveHorizons.LOGGER.info("Registered Beyond the Five Horizons creative tab.");
    }
}
