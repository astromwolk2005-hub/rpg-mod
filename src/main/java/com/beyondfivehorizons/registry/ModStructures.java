package com.beyondfivehorizons.registry;

import com.beyondfivehorizons.BeyondFiveHorizons;
import com.beyondfivehorizons.worldgen.structure.ElysiumStructure;
import com.beyondfivehorizons.worldgen.structure.ElysiumStructurePiece;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.world.gen.structure.StructureType;

public final class ModStructures {
    public static final StructureType<ElysiumStructure> ELYSIUM_STRUCTURE_TYPE = Registry.register(
            Registries.STRUCTURE_TYPE,
            BeyondFiveHorizons.id("elysium_structure"),
            () -> ElysiumStructure.CODEC
    );

    public static final StructurePieceType ELYSIUM_PIECE_TYPE = Registry.register(
            Registries.STRUCTURE_PIECE,
            BeyondFiveHorizons.id("elysium_piece"),
            ElysiumStructurePiece::new
    );

    private ModStructures() {
    }

    public static void register() {
        BeyondFiveHorizons.LOGGER.info("Registered Elysium structure & piece types.");
    }
}
