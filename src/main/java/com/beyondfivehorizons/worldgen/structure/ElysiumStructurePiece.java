package com.beyondfivehorizons.worldgen.structure;

import com.beyondfivehorizons.BeyondFiveHorizons;
import com.beyondfivehorizons.registry.ModBlocks;
import com.beyondfivehorizons.registry.ModStructures;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

/**
 * Generates all 9 distinct Elysium architectural & natural structures safely within chunkBox bounds.
 */
public class ElysiumStructurePiece extends StructurePiece {
    private static final RegistryKey<LootTable> LOOT_SHRINE = RegistryKey.of(
            RegistryKeys.LOOT_TABLE, BeyondFiveHorizons.id("chests/elysium_shrine"));
    private static final RegistryKey<LootTable> LOOT_DUNGEON = RegistryKey.of(
            RegistryKeys.LOOT_TABLE, BeyondFiveHorizons.id("chests/elysium_dungeon"));
    private static final RegistryKey<LootTable> LOOT_VAULT = RegistryKey.of(
            RegistryKeys.LOOT_TABLE, BeyondFiveHorizons.id("chests/elysium_vault"));

    private final String variant;
    private final BlockPos origin;

    public ElysiumStructurePiece(String variant, BlockPos origin) {
        super(ModStructures.ELYSIUM_PIECE_TYPE, 0, computeBox(variant, origin));
        this.variant = variant;
        this.origin = origin.toImmutable();
    }

    public ElysiumStructurePiece(StructureContext context, NbtCompound nbt) {
        super(ModStructures.ELYSIUM_PIECE_TYPE, nbt);
        this.variant = nbt.getString("Variant");
        this.origin = new BlockPos(nbt.getInt("OriginX"), nbt.getInt("OriginY"), nbt.getInt("OriginZ"));
    }

    private static BlockBox computeBox(String variant, BlockPos o) {
        int half = switch (variant) {
            case "temple_of_first_seed" -> 16;
            case "root_temple", "ancient_garden", "hollow_titan_vault" -> 12;
            case "overgrown_crypt", "blighted_hollow" -> 9;
            default -> 6;
        };
        int down = variant.equals("overgrown_crypt") ? 10 : 4;
        int up = variant.equals("hollow_titan_vault") ? 36 : 20;
        return new BlockBox(o.getX() - half, o.getY() - down, o.getZ() - half,
                o.getX() + half, o.getY() + up, o.getZ() + half);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        nbt.putString("Variant", variant);
        nbt.putInt("OriginX", origin.getX());
        nbt.putInt("OriginY", origin.getY());
        nbt.putInt("OriginZ", origin.getZ());
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor,
                         ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox,
                         ChunkPos chunkPos, BlockPos pivot) {
        switch (variant) {
            case "elysium_shrine" -> buildShrine(world, chunkBox, random);
            case "glowing_gazebo" -> buildGazebo(world, chunkBox, random);
            case "root_arch" -> buildRootArch(world, chunkBox, random);
            case "overgrown_crypt" -> buildOvergrownCrypt(world, chunkBox, random);
            case "blighted_hollow" -> buildBlightedHollow(world, chunkBox, random);
            case "ancient_garden" -> buildAncientGarden(world, chunkBox, random);
            case "root_temple" -> buildRootTemple(world, chunkBox, random);
            case "temple_of_first_seed" -> buildTempleOfFirstSeed(world, chunkBox, random);
            case "hollow_titan_vault" -> buildHollowTitanVault(world, chunkBox, random);
            default -> buildShrine(world, chunkBox, random);
        }
    }

    private void put(StructureWorldAccess world, BlockBox box, int dx, int dy, int dz, BlockState state) {
        BlockPos p = origin.add(dx, dy, dz);
        if (box.contains(p)) {
            world.setBlockState(p, state, Block.NOTIFY_LISTENERS);
        }
    }

    private void putChest(StructureWorldAccess world, BlockBox box, int dx, int dy, int dz,
                          RegistryKey<LootTable> loot, Random random) {
        BlockPos p = origin.add(dx, dy, dz);
        if (box.contains(p)) {
            world.setBlockState(p, Blocks.CHEST.getDefaultState(), Block.NOTIFY_LISTENERS);
            BlockEntity be = world.getBlockEntity(p);
            if (be instanceof ChestBlockEntity chest) {
                chest.setLootTable(loot, random.nextLong());
            }
        }
    }

    // 1. Small: Forest Shrine
    private void buildShrine(StructureWorldAccess world, BlockBox box, Random random) {
        BlockState brick = ModBlocks.ELYSIUM_MOSS_BRICKS.getDefaultState();
        BlockState moss = ModBlocks.ELYSIUM_MOSS_STONE.getDefaultState();
        BlockState lamp = ModBlocks.LUMINOUS_LANTERN.getDefaultState();

        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                put(world, box, x, -1, z, moss);
                for (int y = 0; y <= 5; y++) put(world, box, x, y, z, Blocks.AIR.getDefaultState());
            }
        }
        int[][] corners = {{-3, -3}, {3, -3}, {-3, 3}, {3, 3}};
        for (int[] c : corners) {
            for (int y = 0; y <= 4; y++) put(world, box, c[0], y, c[1], brick);
        }
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                put(world, box, x, 5, z, brick);
            }
        }
        put(world, box, 0, 0, 0, ModBlocks.RESTORATIVE_MOSS.getDefaultState());
        put(world, box, 0, 1, 0, ModBlocks.NIGHTBLOOM_FLOWER.getDefaultState());
        put(world, box, 0, 4, 0, lamp);
        putChest(world, box, 0, 0, -2, LOOT_SHRINE, random);
    }

    // 2. Small: Glowing Gazebo
    private void buildGazebo(StructureWorldAccess world, BlockBox box, Random random) {
        BlockState planks = ModBlocks.ELYSIUM_PLANKS.getDefaultState();
        BlockState log = ModBlocks.ELYSIUM_LOG.getDefaultState();
        BlockState glass = ModBlocks.ELYSIUM_GLASS.getDefaultState();
        BlockState lamp = ModBlocks.LUMINOUS_LANTERN.getDefaultState();

        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                if (x * x + z * z <= 16) {
                    put(world, box, x, -1, z, planks);
                    for (int y = 0; y <= 5; y++) put(world, box, x, y, z, Blocks.AIR.getDefaultState());
                    put(world, box, x, 6, z, (x * x + z * z <= 4) ? glass : planks);
                }
            }
        }
        for (int[] c : new int[][]{{3, 0}, {-3, 0}, {0, 3}, {0, -3}}) {
            for (int y = 0; y <= 5; y++) put(world, box, c[0], y, c[1], log);
        }
        put(world, box, 0, 5, 0, lamp);
        putChest(world, box, 0, 0, 0, LOOT_SHRINE, random);
    }

    // 3. Small: Ancient Root Arch
    private void buildRootArch(StructureWorldAccess world, BlockBox box, Random random) {
        BlockState root = ModBlocks.ELYSIUM_ROOT_BLOCK.getDefaultState();
        BlockState crystal = ModBlocks.ELYSIUM_CRYSTAL_BLOCK.getDefaultState();
        for (int x = -5; x <= 5; x++) {
            int h = 6 - Math.abs(x);
            for (int y = -2; y <= h; y++) {
                if (Math.abs(x) >= 4 || y >= h - 1) {
                    put(world, box, x, y, 0, root);
                    put(world, box, x, y, 1, root);
                }
            }
        }
        put(world, box, 0, 5, 0, crystal);
        putChest(world, box, 0, 0, 0, LOOT_SHRINE, random);
    }

    // 4. Medium Dungeon: Overgrown Crypt
    private void buildOvergrownCrypt(StructureWorldAccess world, BlockBox box, Random random) {
        BlockState brick = ModBlocks.ELYSIUM_MOSS_BRICKS.getDefaultState();
        BlockState moss = ModBlocks.ELYSIUM_MOSS_STONE.getDefaultState();
        BlockState lamp = ModBlocks.LUMINOUS_LANTERN.getDefaultState();

        // Underground vaulted chamber
        for (int x = -7; x <= 7; x++) {
            for (int z = -7; z <= 7; z++) {
                for (int y = -8; y <= -1; y++) {
                    boolean wall = (Math.abs(x) == 7 || Math.abs(z) == 7 || y == -8 || y == -1);
                    put(world, box, x, y, z, wall ? brick : Blocks.AIR.getDefaultState());
                }
            }
        }
        // Surface entrance archway + stairs down
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                put(world, box, x, -1, z, Blocks.AIR.getDefaultState());
                if (Math.abs(x) == 2 || Math.abs(z) == 2) {
                    for (int y = 0; y <= 3; y++) put(world, box, x, y, z, moss);
                }
            }
        }
        for (int s = 0; s < 7; s++) {
            put(world, box, 0, -1 - s, -2 + s, brick);
            put(world, box, 1, -1 - s, -2 + s, brick);
        }
        put(world, box, -5, -4, -5, lamp);
        put(world, box, 5, -4, -5, lamp);
        put(world, box, -5, -4, 5, lamp);
        put(world, box, 5, -4, 5, lamp);
        putChest(world, box, -5, -7, 0, LOOT_DUNGEON, random);
        putChest(world, box, 5, -7, 0, LOOT_DUNGEON, random);
        put(world, box, 0, -7, 0, ModBlocks.ELYSIUM_CRYSTAL_BLOCK.getDefaultState());
    }

    // 5. Medium Dungeon: Blighted Hollow
    private void buildBlightedHollow(StructureWorldAccess world, BlockBox box, Random random) {
        BlockState blightedLog = ModBlocks.BLIGHTED_LOG.getDefaultState();
        BlockState blightedSoil = ModBlocks.BLIGHTED_SOIL.getDefaultState();
        BlockState core = ModBlocks.BLIGHT_CORE.getDefaultState();

        for (int x = -7; x <= 7; x++) {
            for (int z = -7; z <= 7; z++) {
                int distSq = x * x + z * z;
                if (distSq <= 49) {
                    put(world, box, x, -1, z, blightedSoil);
                    for (int y = 0; y <= 8; y++) {
                        boolean shell = distSq >= 36 || y == 8;
                        if (shell && !(z > 4 && Math.abs(x) <= 1 && y <= 3)) {
                            put(world, box, x, y, z, blightedLog);
                        } else {
                            put(world, box, x, y, z, Blocks.AIR.getDefaultState());
                        }
                    }
                }
            }
        }
        put(world, box, 0, 0, 0, core);
        put(world, box, 3, 0, -2, core);
        put(world, box, -3, 0, -2, core);
        putChest(world, box, 0, 0, -4, LOOT_DUNGEON, random);
    }

    // 6. Large Unique: Ancient Garden
    private void buildAncientGarden(StructureWorldAccess world, BlockBox box, Random random) {
        BlockState brick = ModBlocks.ELYSIUM_MOSS_BRICKS.getDefaultState();
        BlockState rest = ModBlocks.RESTORATIVE_MOSS.getDefaultState();
        BlockState water = Blocks.WATER.getDefaultState();
        BlockState lamp = ModBlocks.LUMINOUS_LANTERN.getDefaultState();

        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                put(world, box, x, -1, z, (Math.abs(x) <= 2 || Math.abs(z) <= 2) ? brick : rest);
                for (int y = 0; y <= 7; y++) put(world, box, x, y, z, Blocks.AIR.getDefaultState());

                if (Math.abs(x) == 10 || Math.abs(z) == 10) {
                    if (Math.abs(x) > 2 && Math.abs(z) > 2) {
                        put(world, box, x, 0, z, brick);
                        put(world, box, x, 1, z, brick);
                    }
                }
            }
        }
        // Central fountain + garden blooms
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                put(world, box, x, 0, z, (Math.abs(x) == 2 || Math.abs(z) == 2) ? brick : water);
            }
        }
        put(world, box, 0, 1, 0, lamp);
        put(world, box, 0, 2, 0, ModBlocks.ELYSIUM_CRYSTAL_BLOCK.getDefaultState());
        for (int[] p : new int[][]{{6, 6}, {-6, 6}, {6, -6}, {-6, -6}}) {
            put(world, box, p[0], 0, p[1], ModBlocks.EVOLVING_SPROUT.getDefaultState());
            put(world, box, p[0] + 1, 0, p[1], ModBlocks.NIGHTBLOOM_FLOWER.getDefaultState());
            put(world, box, p[0], 0, p[1] + 1, ModBlocks.REACTIVE_BLOSSOM.getDefaultState());
        }
        putChest(world, box, 0, 0, -7, LOOT_DUNGEON, random);
        putChest(world, box, 0, 0, 7, LOOT_SHRINE, random);
    }

    // 7. Large Unique: Root Temple
    private void buildRootTemple(StructureWorldAccess world, BlockBox box, Random random) {
        BlockState brick = ModBlocks.ELYSIUM_MOSS_BRICKS.getDefaultState();
        BlockState root = ModBlocks.ELYSIUM_ROOT_BLOCK.getDefaultState();
        BlockState crystal = ModBlocks.ELYSIUM_CRYSTAL_BLOCK.getDefaultState();
        BlockState lamp = ModBlocks.LUMINOUS_LANTERN.getDefaultState();

        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                put(world, box, x, -1, z, brick);
                for (int y = 0; y <= 10; y++) put(world, box, x, y, z, Blocks.AIR.getDefaultState());
                if (Math.abs(x) == 10 || Math.abs(z) == 10) {
                    boolean doorway = (Math.abs(x) <= 2 || Math.abs(z) <= 2);
                    for (int y = 0; y <= 8; y++) {
                        if (!doorway || y >= 5) {
                            put(world, box, x, y, z, (y % 3 == 0) ? root : brick);
                        }
                    }
                }
                put(world, box, x, 9, z, (x * x + z * z <= 16) ? ModBlocks.ELYSIUM_GLASS.getDefaultState() : brick);
            }
        }
        for (int[] p : new int[][]{{5, 5}, {-5, 5}, {5, -5}, {-5, -5}}) {
            for (int y = 0; y <= 8; y++) put(world, box, p[0], y, p[1], root);
            put(world, box, p[0], 4, p[1], lamp);
        }
        put(world, box, 0, 0, 0, crystal);
        put(world, box, 0, 1, 0, crystal);
        putChest(world, box, -8, 0, 0, LOOT_DUNGEON, random);
        putChest(world, box, 8, 0, 0, LOOT_DUNGEON, random);
    }

    // 8. Boss Structure: Temple of the First Seed (Храм Первого Семени)
    private void buildTempleOfFirstSeed(StructureWorldAccess world, BlockBox box, Random random) {
        BlockState brick = ModBlocks.ELYSIUM_MOSS_BRICKS.getDefaultState();
        BlockState moss = ModBlocks.ELYSIUM_MOSS_STONE.getDefaultState();
        BlockState root = ModBlocks.ELYSIUM_ROOT_BLOCK.getDefaultState();
        BlockState crystal = ModBlocks.ELYSIUM_CRYSTAL_BLOCK.getDefaultState();
        BlockState lamp = ModBlocks.LUMINOUS_LANTERN.getDefaultState();

        // Circular open-air boss coliseum (radius 15)
        for (int x = -15; x <= 15; x++) {
            for (int z = -15; z <= 15; z++) {
                int distSq = x * x + z * z;
                if (distSq <= 225) {
                    put(world, box, x, -2, z, moss);
                    put(world, box, x, -1, z, (distSq % 7 == 0) ? moss : brick);
                    for (int y = 0; y <= 14; y++) {
                        put(world, box, x, y, z, Blocks.AIR.getDefaultState());
                    }
                    // Perimeter pillars & arches
                    if (distSq >= 196) {
                        boolean entrance = Math.abs(x) <= 2 || Math.abs(z) <= 2;
                        if (!entrance) {
                            int wallH = ((x + z) % 3 == 0) ? 9 : 4;
                            for (int y = 0; y <= wallH; y++) {
                                put(world, box, x, y, z, (y == wallH) ? lamp : (y % 2 == 0 ? brick : root));
                            }
                        }
                    }
                }
            }
        }

        // 4 monumental inner root columns around the altar
        for (int[] p : new int[][]{{7, 7}, {-7, 7}, {7, -7}, {-7, -7}}) {
            for (int y = 0; y <= 11; y++) {
                put(world, box, p[0], y, p[1], root);
                put(world, box, p[0] + (p[0] > 0 ? -1 : 1), y, p[1], brick);
            }
            put(world, box, p[0], 12, p[1], crystal);
        }

        // Central dais with First Seed Altar (awakens Heart of the Forest)
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                put(world, box, x, 0, z, brick);
            }
        }
        put(world, box, 0, 1, 0, ModBlocks.FIRST_SEED_ALTAR.getDefaultState());
        put(world, box, 2, 1, 2, lamp);
        put(world, box, -2, 1, 2, lamp);
        put(world, box, 2, 1, -2, lamp);
        put(world, box, -2, 1, -2, lamp);
    }

    // 9. Secret Structure: Hollow Titan Vault (Секретное хранилище внутри гигантского дерева)
    private void buildHollowTitanVault(StructureWorldAccess world, BlockBox box, Random random) {
        BlockState log = ModBlocks.ELYSIUM_LOG.getDefaultState();
        BlockState hollow = ModBlocks.ELYSIUM_HOLLOW_LOG.getDefaultState();
        BlockState leaves = ModBlocks.ELYSIUM_LEAVES.getDefaultState();
        BlockState planks = ModBlocks.ELYSIUM_PLANKS.getDefaultState();
        BlockState crystal = ModBlocks.ELYSIUM_CRYSTAL_BLOCK.getDefaultState();

        // Massive 32-block tall hollow titan tree hiding an interior secret treasure chamber
        for (int y = -1; y <= 32; y++) {
            int rad = y < 4 ? 6 : (y < 24 ? 5 : 4);
            for (int x = -rad; x <= rad; x++) {
                for (int z = -rad; z <= rad; z++) {
                    int distSq = x * x + z * z;
                    if (distSq <= rad * rad) {
                        boolean wall = distSq >= (rad - 1) * (rad - 1) || y == -1 || y == 12 || y == 24;
                        if (wall) {
                            if (z == rad && Math.abs(x) <= 1 && y >= 0 && y <= 2) {
                                put(world, box, x, y, z, leaves); // camouflaged secret entrance!
                            } else {
                                put(world, box, x, y, z, (y == 12 || y == -1) ? planks : (y % 4 == 0 ? hollow : log));
                            }
                        } else {
                            put(world, box, x, y, z, Blocks.AIR.getDefaultState());
                        }
                    }
                }
            }
        }
        // Canopy top
        for (int y = 26; y <= 34; y++) {
            int r = 10 - Math.abs(y - 30);
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    if (x * x + z * z <= r * r) {
                        put(world, box, x, y, z, leaves);
                    }
                }
            }
        }
        // Secret vault loot inside
        put(world, box, 0, 0, 0, crystal);
        put(world, box, 0, 1, 0, ModBlocks.LUMINOUS_LANTERN.getDefaultState());
        putChest(world, box, -2, 0, -2, LOOT_VAULT, random);
        putChest(world, box, 2, 0, -2, LOOT_VAULT, random);
        putChest(world, box, 0, 13, 0, LOOT_VAULT, random);
    }
}
