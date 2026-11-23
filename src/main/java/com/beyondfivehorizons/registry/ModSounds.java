package com.beyondfivehorizons.registry;

import com.beyondfivehorizons.BeyondFiveHorizons;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class ModSounds {
    public static final SoundEvent OBELISK_ACTIVATE = registerSound("obelisk_activate");
    public static final SoundEvent HORIZON_TRAVEL = registerSound("horizon_travel");
    public static final SoundEvent ELYSIUM_PORTAL_AMBIENT = registerSound("elysium_portal_ambient");
    public static final SoundEvent HEART_BOSS_ROAR = registerSound("heart_boss_roar");

    private ModSounds() {
    }

    private static SoundEvent registerSound(String name) {
        Identifier id = BeyondFiveHorizons.id(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void register() {
        BeyondFiveHorizons.LOGGER.info("Registered Beyond the Five Horizons sound events.");
    }
}
