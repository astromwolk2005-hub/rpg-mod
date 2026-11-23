package com.beyondfivehorizons.registry;

import com.beyondfivehorizons.BeyondFiveHorizons;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public final class ModItemGroups {
    public static final ItemGroup MAIN_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            BeyondFiveHorizons.id("main"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModItems.HEART_OF_ELYSIUM))
                    .displayName(Text.translatable("itemGroup.beyondfivehorizons.main"))
                    .entries((displayContext, entries) -> {
                        // Core tools & Horizon resources
                        entries.add(ModItems.EXPLORER_JOURNAL);
                        entries.add(ModItems.HORIZON_COMPASS);
                        entries.add(ModItems.LIVING_ESSENCE);
                        entries.add(ModItems.CHRONODUST);
                        entries.add(ModItems.SPATIAL_CRYSTAL);
                        entries.add(ModItems.PRIMAL_RESIN);
                        entries.add(ModItems.ASHEN_EMBER);

                        // Elysium resources, artifact & gear
                        entries.add(ModItems.LUMINOUS_POLLEN);
                        entries.add(ModItems.VERDANT_CRYSTAL);
                        entries.add(ModItems.ANCIENT_SAP);
                        entries.add(ModItems.BLIGHTED_SPORE);
                        entries.add(ModItems.PURIFYING_NECTAR);
                        entries.add(ModItems.HEART_OF_ELYSIUM);

                        entries.add(ModItems.LIVING_BLADE);
                        entries.add(ModItems.ROOT_STAFF);
                        entries.add(ModItems.ELYSIUM_HELMET);
                        entries.add(ModItems.ELYSIUM_CHESTPLATE);
                        entries.add(ModItems.ELYSIUM_LEGGINGS);
                        entries.add(ModItems.ELYSIUM_BOOTS);

                        // Core & 5-horizon blocks
                        entries.add(ModBlocks.OBELISK_STONE);
                        entries.add(ModBlocks.OBELISK_CORE);
                        entries.add(ModBlocks.CHRONOS_BRASS_BLOCK);
                        entries.add(ModBlocks.ASTRALIA_CRYSTAL_BLOCK);
                        entries.add(ModBlocks.UNDERROOT_BARK_BLOCK);
                        entries.add(ModBlocks.ASHEN_BASALT_BLOCK);

                        // Elysium wood, building & flora blocks
                        entries.add(ModBlocks.ELYSIUM_LOG);
                        entries.add(ModBlocks.STRIPPED_ELYSIUM_LOG);
                        entries.add(ModBlocks.ELYSIUM_HOLLOW_LOG);
                        entries.add(ModBlocks.ELYSIUM_PLANKS);
                        entries.add(ModBlocks.ELYSIUM_STAIRS);
                        entries.add(ModBlocks.ELYSIUM_SLAB);
                        entries.add(ModBlocks.ELYSIUM_FENCE);
                        entries.add(ModBlocks.ELYSIUM_FENCE_GATE);
                        entries.add(ModBlocks.ELYSIUM_DOOR);
                        entries.add(ModBlocks.ELYSIUM_TRAPDOOR);
                        entries.add(ModBlocks.ELYSIUM_LEAVES);
                        entries.add(ModBlocks.ELYSIUM_ROOT_BLOCK);
                        entries.add(ModBlocks.ELYSIUM_MOSS_STONE);
                        entries.add(ModBlocks.ELYSIUM_MOSS_BRICKS);
                        entries.add(ModBlocks.ELYSIUM_MOSS_BRICK_STAIRS);
                        entries.add(ModBlocks.ELYSIUM_MOSS_BRICK_SLAB);
                        entries.add(ModBlocks.ELYSIUM_CRYSTAL_BLOCK);
                        entries.add(ModBlocks.ELYSIUM_CRYSTAL_ORE);
                        entries.add(ModBlocks.LUMINOUS_LANTERN);
                        entries.add(ModBlocks.ELYSIUM_GLASS);

                        // Elysium mechanics & boss blocks
                        entries.add(ModBlocks.EVOLVING_SPROUT);
                        entries.add(ModBlocks.NIGHTBLOOM_FLOWER);
                        entries.add(ModBlocks.REACTIVE_BLOSSOM);
                        entries.add(ModBlocks.RESTORATIVE_MOSS);
                        entries.add(ModBlocks.BLIGHTED_SOIL);
                        entries.add(ModBlocks.BLIGHTED_LOG);
                        entries.add(ModBlocks.BLIGHT_CORE);
                        entries.add(ModBlocks.FIRST_SEED_ALTAR);
                        entries.add(ModBlocks.VITALITY_BUD);

                        // Spawn eggs
                        entries.add(ModItems.FIREFLY_SPAWN_EGG);
                        entries.add(ModItems.ROOTWALKER_SPAWN_EGG);
                        entries.add(ModItems.PETALMAW_SPAWN_EGG);
                        entries.add(ModItems.ARBOREAL_GUARDIAN_SPAWN_EGG);
                        entries.add(ModItems.HEART_OF_THE_FOREST_SPAWN_EGG);
                    })
                    .build()
    );

    private ModItemGroups() {
    }

    public static void register() {
        BeyondFiveHorizons.LOGGER.info("Registered Beyond the Five Horizons creative tab.");
    }
}
