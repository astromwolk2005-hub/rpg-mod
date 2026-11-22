package com.beyondfivehorizons.dimension;

import com.beyondfivehorizons.BeyondFiveHorizons;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

import java.util.Optional;

/**
 * Central descriptor for the five dimensions of Beyond the Five Horizons.
 */
public enum HorizonDimension {
    ELYSIUM("elysium", 1, false, false),
    CHRONOS("chronos", 2, false, false),
    ASTRALIA("astralia", 3, false, true),
    UNDERROOT("underroot", 4, true, false),
    ASHEN_CROWN("ashen_crown", 5, false, false);

    private final String id;
    private final int recommendedOrder;
    private final boolean hasCeiling;
    private final boolean floatingIslands;
    private final RegistryKey<World> worldKey;
    private final RegistryKey<DimensionType> typeKey;
    private final RegistryKey<DimensionOptions> stemKey;

    HorizonDimension(String id, int recommendedOrder, boolean hasCeiling, boolean floatingIslands) {
        this.id = id;
        this.recommendedOrder = recommendedOrder;
        this.hasCeiling = hasCeiling;
        this.floatingIslands = floatingIslands;
        Identifier identifier = BeyondFiveHorizons.id(id);
        this.worldKey = RegistryKey.of(RegistryKeys.WORLD, identifier);
        this.typeKey = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, identifier);
        this.stemKey = RegistryKey.of(RegistryKeys.DIMENSION, identifier);
    }

    public String getId() {
        return id;
    }

    public int getRecommendedOrder() {
        return recommendedOrder;
    }

    public boolean hasCeiling() {
        return hasCeiling;
    }

    public boolean isFloatingIslands() {
        return floatingIslands;
    }

    public RegistryKey<World> getWorldKey() {
        return worldKey;
    }

    public RegistryKey<DimensionType> getTypeKey() {
        return typeKey;
    }

    public RegistryKey<DimensionOptions> getStemKey() {
        return stemKey;
    }

    public String getTranslationKey() {
        return "dimension." + BeyondFiveHorizons.MOD_ID + "." + id;
    }

    public static Optional<HorizonDimension> fromId(String id) {
        for (HorizonDimension dim : values()) {
            if (dim.id.equalsIgnoreCase(id)) {
                return Optional.of(dim);
            }
        }
        return Optional.empty();
    }

    public static Optional<HorizonDimension> fromWorldKey(RegistryKey<World> key) {
        for (HorizonDimension dim : values()) {
            if (dim.worldKey.equals(key)) {
                return Optional.of(dim);
            }
        }
        return Optional.empty();
    }
}
