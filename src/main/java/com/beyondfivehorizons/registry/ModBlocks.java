package com.beyondfivehorizons.registry;

import com.beyondfivehorizons.BeyondFiveHorizons;
import com.beyondfivehorizons.block.BlightCoreBlock;
import com.beyondfivehorizons.block.BlightedSoilBlock;
import com.beyondfivehorizons.block.ElysiumPortalBlock;
import com.beyondfivehorizons.block.EvolvingSproutBlock;
import com.beyondfivehorizons.block.FirstSeedAltarBlock;
import com.beyondfivehorizons.block.NightbloomFlowerBlock;
import com.beyondfivehorizons.block.ObeliskCoreBlock;
import com.beyondfivehorizons.block.ReactiveBlossomBlock;
import com.beyondfivehorizons.block.RestorativeMossBlock;
import com.beyondfivehorizons.block.VitalityBudBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TransparentBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.WoodType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public final class ModBlocks {
    // --- Core & Stage 1 Foundation Blocks ---
    public static final Block OBELISK_STONE = registerBlock("obelisk_stone",
            new Block(AbstractBlock.Settings.create().mapColor(MapColor.DEEPSLATE_GRAY)
                    .strength(5.0f, 18.0f).requiresTool().sounds(BlockSoundGroup.DEEPSLATE_BRICKS)));

    public static final Block OBELISK_CORE = registerBlock("obelisk_core",
            new ObeliskCoreBlock(AbstractBlock.Settings.create().mapColor(MapColor.CYAN)
                    .strength(6.0f, 24.0f).requiresTool().luminance(s -> 12).sounds(BlockSoundGroup.AMETHYST_BLOCK)));

    public static final Block ELYSIUM_MOSS_STONE = registerBlock("elysium_moss_stone",
            new Block(AbstractBlock.Settings.create().mapColor(MapColor.EMERALD_GREEN)
                    .strength(2.5f, 6.0f).requiresTool().sounds(BlockSoundGroup.MOSS_BLOCK)));

    public static final Block CHRONOS_BRASS_BLOCK = registerBlock("chronos_brass_block",
            new Block(AbstractBlock.Settings.create().mapColor(MapColor.GOLD)
                    .strength(4.0f, 10.0f).requiresTool().sounds(BlockSoundGroup.COPPER)));

    public static final Block ASTRALIA_CRYSTAL_BLOCK = registerBlock("astralia_crystal_block",
            new Block(AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE)
                    .strength(3.0f, 8.0f).requiresTool().luminance(s -> 10).sounds(BlockSoundGroup.AMETHYST_BLOCK)));

    public static final Block UNDERROOT_BARK_BLOCK = registerBlock("underroot_bark_block",
            new Block(AbstractBlock.Settings.create().mapColor(MapColor.DARK_GREEN)
                    .strength(3.0f, 7.0f).sounds(BlockSoundGroup.WOOD)));

    public static final Block ASHEN_BASALT_BLOCK = registerBlock("ashen_basalt_block",
            new Block(AbstractBlock.Settings.create().mapColor(MapColor.BLACK)
                    .strength(4.5f, 12.0f).requiresTool().sounds(BlockSoundGroup.BASALT)));

    // --- Stage 2: Elysium Wood & Decorative Building Set ---
    public static final Block ELYSIUM_LOG = registerBlock("elysium_log",
            new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.DARK_AQUA)
                    .strength(2.5f).sounds(BlockSoundGroup.WOOD)));

    public static final Block STRIPPED_ELYSIUM_LOG = registerBlock("stripped_elysium_log",
            new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.CYAN)
                    .strength(2.5f).sounds(BlockSoundGroup.WOOD)));

    public static final Block ELYSIUM_HOLLOW_LOG = registerBlock("elysium_hollow_log",
            new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.DARK_AQUA)
                    .strength(2.5f).luminance(s -> 4).sounds(BlockSoundGroup.WOOD)));

    public static final Block ELYSIUM_PLANKS = registerBlock("elysium_planks",
            new Block(AbstractBlock.Settings.create().mapColor(MapColor.CYAN)
                    .strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD)));

    public static final Block ELYSIUM_STAIRS = registerBlock("elysium_stairs",
            new StairsBlock(ELYSIUM_PLANKS.getDefaultState(),
                    AbstractBlock.Settings.copy(ELYSIUM_PLANKS)));

    public static final Block ELYSIUM_SLAB = registerBlock("elysium_slab",
            new SlabBlock(AbstractBlock.Settings.copy(ELYSIUM_PLANKS)));

    public static final Block ELYSIUM_FENCE = registerBlock("elysium_fence",
            new FenceBlock(AbstractBlock.Settings.copy(ELYSIUM_PLANKS)));

    public static final Block ELYSIUM_FENCE_GATE = registerBlock("elysium_fence_gate",
            new FenceGateBlock(WoodType.OAK, AbstractBlock.Settings.copy(ELYSIUM_PLANKS)));

    public static final Block ELYSIUM_DOOR = registerBlock("elysium_door",
            new DoorBlock(BlockSetType.OAK, AbstractBlock.Settings.copy(ELYSIUM_PLANKS).nonOpaque()));

    public static final Block ELYSIUM_TRAPDOOR = registerBlock("elysium_trapdoor",
            new TrapdoorBlock(BlockSetType.OAK, AbstractBlock.Settings.copy(ELYSIUM_PLANKS).nonOpaque()));

    public static final Block ELYSIUM_LEAVES = registerBlock("elysium_leaves",
            new LeavesBlock(AbstractBlock.Settings.create().mapColor(MapColor.EMERALD_GREEN)
                    .strength(0.2f).ticksRandomly().nonOpaque().luminance(s -> 3).sounds(BlockSoundGroup.GRASS)));

    public static final Block ELYSIUM_ROOT_BLOCK = registerBlock("elysium_root_block",
            new Block(AbstractBlock.Settings.create().mapColor(MapColor.DARK_GREEN)
                    .strength(2.2f, 5.0f).sounds(BlockSoundGroup.ROOTS)));

    public static final Block ELYSIUM_MOSS_BRICKS = registerBlock("elysium_moss_bricks",
            new Block(AbstractBlock.Settings.create().mapColor(MapColor.EMERALD_GREEN)
                    .strength(3.0f, 8.0f).requiresTool().sounds(BlockSoundGroup.STONE)));

    public static final Block ELYSIUM_MOSS_BRICK_STAIRS = registerBlock("elysium_moss_brick_stairs",
            new StairsBlock(ELYSIUM_MOSS_BRICKS.getDefaultState(),
                    AbstractBlock.Settings.copy(ELYSIUM_MOSS_BRICKS)));

    public static final Block ELYSIUM_MOSS_BRICK_SLAB = registerBlock("elysium_moss_brick_slab",
            new SlabBlock(AbstractBlock.Settings.copy(ELYSIUM_MOSS_BRICKS)));

    public static final Block ELYSIUM_CRYSTAL_BLOCK = registerBlock("elysium_crystal_block",
            new Block(AbstractBlock.Settings.create().mapColor(MapColor.DIAMOND_BLUE)
                    .strength(3.0f, 9.0f).requiresTool().luminance(s -> 13).sounds(BlockSoundGroup.AMETHYST_BLOCK)));

    public static final Block ELYSIUM_CRYSTAL_ORE = registerBlock("elysium_crystal_ore",
            new Block(AbstractBlock.Settings.create().mapColor(MapColor.EMERALD_GREEN)
                    .strength(3.5f, 8.0f).requiresTool().luminance(s -> 6).sounds(BlockSoundGroup.STONE)));

    public static final Block LUMINOUS_LANTERN = registerBlock("luminous_lantern",
            new Block(AbstractBlock.Settings.create().mapColor(MapColor.PALE_GREEN)
                    .strength(1.5f).luminance(s -> 15).sounds(BlockSoundGroup.LANTERN)));

    public static final Block ELYSIUM_GLASS = registerBlock("elysium_glass",
            new TransparentBlock(AbstractBlock.Settings.create().mapColor(MapColor.CYAN)
                    .strength(0.4f).nonOpaque().luminance(s -> 2).sounds(BlockSoundGroup.GLASS)));

    // --- Stage 2: Elysium Evolution & Blight Mechanics Blocks ---
    public static final Block EVOLVING_SPROUT = registerBlock("evolving_sprout",
            new EvolvingSproutBlock(AbstractBlock.Settings.create().mapColor(MapColor.LIME)
                    .noCollision().ticksRandomly().breakInstantly().luminance(s -> 4).sounds(BlockSoundGroup.GRASS)));

    public static final Block NIGHTBLOOM_FLOWER = registerBlock("nightbloom_flower",
            new NightbloomFlowerBlock(AbstractBlock.Settings.create().mapColor(MapColor.PINK)
                    .noCollision().ticksRandomly().breakInstantly()
                    .luminance(s -> s.get(NightbloomFlowerBlock.OPEN) ? 12 : 2)
                    .sounds(BlockSoundGroup.GRASS)));

    public static final Block REACTIVE_BLOSSOM = registerBlock("reactive_blossom",
            new ReactiveBlossomBlock(AbstractBlock.Settings.create().mapColor(MapColor.MAGENTA)
                    .noCollision().breakInstantly()
                    .luminance(s -> s.get(ReactiveBlossomBlock.ACTIVE) ? 7 : 1)
                    .sounds(BlockSoundGroup.SPORE_BLOSSOM)));

    public static final Block RESTORATIVE_MOSS = registerBlock("restorative_moss",
            new RestorativeMossBlock(AbstractBlock.Settings.create().mapColor(MapColor.LIME)
                    .strength(1.2f).ticksRandomly().luminance(s -> 5).sounds(BlockSoundGroup.MOSS_BLOCK)));

    public static final Block BLIGHTED_SOIL = registerBlock("blighted_soil",
            new BlightedSoilBlock(AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_PURPLE)
                    .strength(1.5f).sounds(BlockSoundGroup.SOUL_SOIL)));

    public static final Block BLIGHTED_LOG = registerBlock("blighted_log",
            new PillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.BLACK)
                    .strength(2.5f).sounds(BlockSoundGroup.WOOD)));

    public static final Block BLIGHT_CORE = registerBlock("blight_core",
            new BlightCoreBlock(AbstractBlock.Settings.create().mapColor(MapColor.PURPLE)
                    .strength(4.0f, 10.0f).ticksRandomly().luminance(s -> 8).sounds(BlockSoundGroup.SCULK_CATALYST)));

    // --- Stage 2: Elysium Portal & Boss Arena Blocks ---
    public static final Block ELYSIUM_PORTAL = registerBlockNoItem("elysium_portal",
            new ElysiumPortalBlock(AbstractBlock.Settings.create().mapColor(MapColor.CYAN)
                    .noCollision().nonOpaque().luminance(s -> 13).strength(-1.0f, 3600000.0f).dropsNothing()
                    .sounds(BlockSoundGroup.GLASS)));

    public static final Block FIRST_SEED_ALTAR = registerBlock("first_seed_altar",
            new FirstSeedAltarBlock(AbstractBlock.Settings.create().mapColor(MapColor.EMERALD_GREEN)
                    .strength(8.0f, 3600.0f).requiresTool().luminance(s -> 14).sounds(BlockSoundGroup.AMETHYST_BLOCK)));

    public static final Block VITALITY_BUD = registerBlock("vitality_bud",
            new VitalityBudBlock(AbstractBlock.Settings.create().mapColor(MapColor.LIME)
                    .strength(1.2f).luminance(s -> 12).sounds(BlockSoundGroup.SPORE_BLOSSOM)));

    private ModBlocks() {
    }

    private static Block registerBlock(String name, Block block) {
        Identifier id = BeyondFiveHorizons.id(name);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
        return Registry.register(Registries.BLOCK, id, block);
    }

    private static Block registerBlockNoItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, BeyondFiveHorizons.id(name), block);
    }

    public static void register() {
        BeyondFiveHorizons.LOGGER.info("Registered Beyond the Five Horizons blocks (Stage 2).");
    }
}
