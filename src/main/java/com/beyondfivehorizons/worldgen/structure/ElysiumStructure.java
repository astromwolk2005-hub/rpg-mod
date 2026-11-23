package com.beyondfivehorizons.worldgen.structure;

import com.beyondfivehorizons.registry.ModStructures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

/**
 * Data-driven procedural Structure supporting all 9 Elysium structure variants:
 * Small (3): elysium_shrine, glowing_gazebo, root_arch
 * Medium Dungeons (2): overgrown_crypt, blighted_hollow
 * Large Unique (2): ancient_garden, root_temple
 * Boss Structure (1): temple_of_first_seed
 * Secret Structure (1): hollow_titan_vault
 */
public class ElysiumStructure extends Structure {
    public static final MapCodec<ElysiumStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Structure.configCodecBuilder(instance),
                    Codec.STRING.fieldOf("variant").forGetter(s -> s.variant)
            ).apply(instance, ElysiumStructure::new)
    );

    private final String variant;

    public ElysiumStructure(Structure.Config config, String variant) {
        super(config);
        this.variant = variant;
    }

    @Override
    protected Optional<StructurePosition> getStructurePosition(Context context) {
        return getStructurePosition(context, Heightmap.Type.WORLD_SURFACE_WG,
                collector -> addPieces(collector, context));
    }

    private void addPieces(StructurePiecesCollector collector, Context context) {
        int x = context.chunkPos().getStartX() + 8;
        int z = context.chunkPos().getStartZ() + 8;
        int y = context.chunkGenerator().getHeightInGround(
                x, z, Heightmap.Type.WORLD_SURFACE_WG, context.world(), context.noiseConfig());
        if (y < 60) {
            return;
        }
        collector.addPiece(new ElysiumStructurePiece(variant, new BlockPos(x, y, z)));
    }

    @Override
    public StructureType<?> getType() {
        return ModStructures.ELYSIUM_STRUCTURE_TYPE;
    }
}
