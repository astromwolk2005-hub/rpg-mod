package com.beyondfivehorizons.registry;

import com.beyondfivehorizons.BeyondFiveHorizons;
import com.beyondfivehorizons.dimension.HorizonDimension;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

public final class ModDimensions {
    public static final RegistryKey<World> ELYSIUM = HorizonDimension.ELYSIUM.getWorldKey();
    public static final RegistryKey<World> CHRONOS = HorizonDimension.CHRONOS.getWorldKey();
    public static final RegistryKey<World> ASTRALIA = HorizonDimension.ASTRALIA.getWorldKey();
    public static final RegistryKey<World> UNDERROOT = HorizonDimension.UNDERROOT.getWorldKey();
    public static final RegistryKey<World> ASHEN_CROWN = HorizonDimension.ASHEN_CROWN.getWorldKey();

    private ModDimensions() {
    }

    public static void register() {
        BeyondFiveHorizons.LOGGER.info("Registered {} Horizon dimension keys.", HorizonDimension.values().length);
    }
}
