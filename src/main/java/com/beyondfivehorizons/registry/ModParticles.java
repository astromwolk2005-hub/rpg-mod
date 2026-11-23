package com.beyondfivehorizons.registry;

import com.beyondfivehorizons.BeyondFiveHorizons;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModParticles {
    public static final SimpleParticleType LIVING_SPORE = Registry.register(
            Registries.PARTICLE_TYPE, BeyondFiveHorizons.id("living_spore"), FabricParticleTypes.simple());

    public static final SimpleParticleType BLIGHT_SPORE = Registry.register(
            Registries.PARTICLE_TYPE, BeyondFiveHorizons.id("blight_spore"), FabricParticleTypes.simple());

    public static final SimpleParticleType CRYSTAL_SHIMMER = Registry.register(
            Registries.PARTICLE_TYPE, BeyondFiveHorizons.id("crystal_shimmer"), FabricParticleTypes.simple());

    private ModParticles() {
    }

    public static void register() {
        BeyondFiveHorizons.LOGGER.info("Registered Beyond the Five Horizons particle types.");
    }
}
