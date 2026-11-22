package com.beyondfivehorizons.registry;

import com.beyondfivehorizons.BeyondFiveHorizons;
import com.beyondfivehorizons.block.ObeliskCoreBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

/**
 * Central block registry for Beyond the Five Horizons.
 */
public final class ModBlocks {
    public static final Block OBELISK_STONE = registerBlock("obelisk_stone",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DEEPSLATE_GRAY)
                    .strength(5.0f, 18.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.DEEPSLATE_BRICKS)));

    public static final Block OBELISK_CORE = registerBlock("obelisk_core",
            new ObeliskCoreBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.CYAN)
                    .strength(6.0f, 24.0f)
                    .requiresTool()
                    .luminance(state -> 12)
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK)));

    public static final Block ELYSIUM_MOSS_STONE = registerBlock("elysium_moss_stone",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.EMERALD_GREEN)
                    .strength(2.5f, 6.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.MOSS_BLOCK)));

    public static final Block CHRONOS_BRASS_BLOCK = registerBlock("chronos_brass_block",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.GOLD)
                    .strength(4.0f, 10.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.COPPER)));

    public static final Block ASTRALIA_CRYSTAL_BLOCK = registerBlock("astralia_crystal_block",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.LIGHT_BLUE)
                    .strength(3.0f, 8.0f)
                    .requiresTool()
                    .luminance(state -> 10)
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK)));

    public static final Block UNDERROOT_BARK_BLOCK = registerBlock("underroot_bark_block",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .strength(3.0f, 7.0f)
                    .sounds(BlockSoundGroup.WOOD)));

    public static final Block ASHEN_BASALT_BLOCK = registerBlock("ashen_basalt_block",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BLACK)
                    .strength(4.5f, 12.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.BASALT)));

    private ModBlocks() {
    }

    private static Block registerBlock(String name, Block block) {
        Identifier id = BeyondFiveHorizons.id(name);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void register() {
        BeyondFiveHorizons.LOGGER.info("Registered Beyond the Five Horizons blocks.");
    }
}
