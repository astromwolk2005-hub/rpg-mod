package com.beyondfivehorizons;

import com.beyondfivehorizons.registry.ModBlocks;
import com.beyondfivehorizons.registry.ModCommands;
import com.beyondfivehorizons.registry.ModDimensions;
import com.beyondfivehorizons.registry.ModItemGroups;
import com.beyondfivehorizons.registry.ModItems;
import com.beyondfivehorizons.registry.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entrypoint for Beyond the Five Horizons (Minecraft 1.21.1, Fabric).
 * Stage 1 initializes core registries, progression storage, and the 5 dimension keys.
 */
public class BeyondFiveHorizons implements ModInitializer {
    public static final String MOD_ID = "beyondfivehorizons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Beyond the Five Horizons (Stage 1 Foundation)...");

        ModSounds.register();
        ModBlocks.register();
        ModItems.register();
        ModItemGroups.register();
        ModDimensions.register();
        ModCommands.register();

        LOGGER.info("Beyond the Five Horizons initialized successfully.");
    }
}
