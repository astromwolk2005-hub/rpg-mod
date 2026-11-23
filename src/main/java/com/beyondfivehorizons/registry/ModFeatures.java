package com.beyondfivehorizons.registry;

import com.beyondfivehorizons.BeyondFiveHorizons;
import com.beyondfivehorizons.worldgen.feature.BlightedTreeFeature;
import com.beyondfivehorizons.worldgen.feature.ColossalElysiumTreeFeature;
import com.beyondfivehorizons.worldgen.feature.GiantElysiumFlowerFeature;
import com.beyondfivehorizons.worldgen.feature.GlowingCrystalClusterFeature;
import com.beyondfivehorizons.worldgen.feature.GlowingLakeFeature;
import com.beyondfivehorizons.worldgen.feature.RootBridgeFeature;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public final class ModFeatures {
    public static final Feature<DefaultFeatureConfig> COLOSSAL_ELYSIUM_TREE = register("colossal_elysium_tree",
            new ColossalElysiumTreeFeature(DefaultFeatureConfig.CODEC));

    public static final Feature<DefaultFeatureConfig> GIANT_ELYSIUM_FLOWER = register("giant_elysium_flower",
            new GiantElysiumFlowerFeature(DefaultFeatureConfig.CODEC));

    public static final Feature<DefaultFeatureConfig> GLOWING_CRYSTAL_CLUSTER = register("glowing_crystal_cluster",
            new GlowingCrystalClusterFeature(DefaultFeatureConfig.CODEC));

    public static final Feature<DefaultFeatureConfig> BLIGHTED_TREE = register("blighted_tree",
            new BlightedTreeFeature(DefaultFeatureConfig.CODEC));

    public static final Feature<DefaultFeatureConfig> ROOT_BRIDGE = register("root_bridge",
            new RootBridgeFeature(DefaultFeatureConfig.CODEC));

    public static final Feature<DefaultFeatureConfig> GLOWING_LAKE = register("glowing_lake",
            new GlowingLakeFeature(DefaultFeatureConfig.CODEC));

    private ModFeatures() {
    }

    private static Feature<DefaultFeatureConfig> register(String name, Feature<DefaultFeatureConfig> feature) {
        return Registry.register(Registries.FEATURE, BeyondFiveHorizons.id(name), feature);
    }

    public static void register() {
        BeyondFiveHorizons.LOGGER.info("Registered 6 Elysium worldgen features.");
    }
}
