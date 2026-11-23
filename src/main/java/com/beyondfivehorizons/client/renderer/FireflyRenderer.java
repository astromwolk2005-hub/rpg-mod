package com.beyondfivehorizons.client.renderer;

import com.beyondfivehorizons.BeyondFiveHorizons;
import com.beyondfivehorizons.client.model.FireflyModel;
import com.beyondfivehorizons.client.model.ModModelLayers;
import com.beyondfivehorizons.entity.mobs.FireflyEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class FireflyRenderer extends MobEntityRenderer<FireflyEntity, FireflyModel> {
    private static final Identifier TEXTURE = BeyondFiveHorizons.id("textures/entity/firefly.png");

    public FireflyRenderer(EntityRendererFactory.Context context) {
        super(context, new FireflyModel(context.getPart(ModModelLayers.FIREFLY)), 0.25f);
    }

    @Override
    public Identifier getTexture(FireflyEntity entity) {
        return TEXTURE;
    }
}
