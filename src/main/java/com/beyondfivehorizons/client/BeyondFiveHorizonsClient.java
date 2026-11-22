package com.beyondfivehorizons.client;

import com.beyondfivehorizons.BeyondFiveHorizons;
import net.fabricmc.api.ClientModInitializer;

/**
 * Client entrypoint for rendering, custom sky effects, particles, and GUI screens.
 */
public class BeyondFiveHorizonsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BeyondFiveHorizons.LOGGER.info("Initializing Beyond the Five Horizons client...");
    }
}
