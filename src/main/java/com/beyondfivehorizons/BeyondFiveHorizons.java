package com.beyondfivehorizons;

import com.beyondfivehorizons.registry.ModArmorMaterials;
import com.beyondfivehorizons.registry.ModBlocks;
import com.beyondfivehorizons.registry.ModCommands;
import com.beyondfivehorizons.registry.ModDimensions;
import com.beyondfivehorizons.registry.ModEntities;
import com.beyondfivehorizons.registry.ModFeatures;
import com.beyondfivehorizons.registry.ModItemGroups;
import com.beyondfivehorizons.registry.ModItems;
import com.beyondfivehorizons.registry.ModParticles;
import com.beyondfivehorizons.registry.ModSounds;
import com.beyondfivehorizons.registry.ModStructures;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entrypoint for Beyond the Five Horizons (Minecraft 1.21.1, Fabric).
 * Stage 2 adds full Elysium implementation: 4 biomes, custom worldgen features,
 * 9 structures, Evolution & Blight mechanics, 4 mobs, Heart of the Forest boss,
 * Elysium weapons/armor, custom portal, particles, and advancements.
 */
public class BeyondFiveHorizons implements ModInitializer {
    public static final String MOD_ID = "beyondfivehorizons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Beyond the Five Horizons (Stage 2: Elysium)...");

        ModSounds.register();
        ModParticles.register();
        ModArmorMaterials.register();
        ModBlocks.register();
        ModEntities.register();
        ModItems.register();
        ModItemGroups.register();
        ModDimensions.register();
        ModFeatures.register();
        ModStructures.register();
        ModCommands.register();

        LOGGER.info("Beyond the Five Horizons Stage 2 initialized successfully.");
    }
}
