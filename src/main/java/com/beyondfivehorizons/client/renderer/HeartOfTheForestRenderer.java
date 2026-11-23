package com.beyondfivehorizons.client.renderer;

import com.beyondfivehorizons.BeyondFiveHorizons;
import com.beyondfivehorizons.client.model.HeartOfTheForestModel;
import com.beyondfivehorizons.client.model.ModModelLayers;
import com.beyondfivehorizons.entity.boss.HeartOfTheForestEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class HeartOfTheForestRenderer extends MobEntityRenderer<HeartOfTheForestEntity, HeartOfTheForestModel> {
    private static final Identifier TEXTURE = BeyondFiveHorizons.id("textures/entity/heart_of_the_forest.png");

    public HeartOfTheForestRenderer(EntityRendererFactory.Context context) {
        super(context, new HeartOfTheForestModel(context.getPart(ModModelLayers.HEART_OF_THE_FOREST)), 1.6f);
    }

    @Override
    public Identifier getTexture(HeartOfTheForestEntity entity) {
        return TEXTURE;
    }
}
