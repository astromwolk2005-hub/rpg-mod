package com.beyondfivehorizons.client.model;

import com.beyondfivehorizons.BeyondFiveHorizons;
import net.minecraft.client.render.entity.model.EntityModelLayer;

public final class ModModelLayers {
    public static final EntityModelLayer FIREFLY =
            new EntityModelLayer(BeyondFiveHorizons.id("firefly"), "main");
    public static final EntityModelLayer ROOTWALKER =
            new EntityModelLayer(BeyondFiveHorizons.id("rootwalker"), "main");
    public static final EntityModelLayer PETALMAW =
            new EntityModelLayer(BeyondFiveHorizons.id("petalmaw"), "main");
    public static final EntityModelLayer ARBOREAL_GUARDIAN =
            new EntityModelLayer(BeyondFiveHorizons.id("arboreal_guardian"), "main");
    public static final EntityModelLayer HEART_OF_THE_FOREST =
            new EntityModelLayer(BeyondFiveHorizons.id("heart_of_the_forest"), "main");

    private ModModelLayers() {
    }
}
