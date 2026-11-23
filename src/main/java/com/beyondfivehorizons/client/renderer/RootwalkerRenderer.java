package com.beyondfivehorizons.client.renderer;

import com.beyondfivehorizons.BeyondFiveHorizons;
import com.beyondfivehorizons.client.model.RootwalkerModel;
import com.beyondfivehorizons.client.model.ModModelLayers;
import com.beyondfivehorizons.entity.mobs.RootwalkerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class RootwalkerRenderer extends MobEntityRenderer<RootwalkerEntity, RootwalkerModel> {
    private static final Identifier TEXTURE = BeyondFiveHorizons.id("textures/entity/rootwalker.png");

    public RootwalkerRenderer(EntityRendererFactory.Context context) {
        super(context, new RootwalkerModel(context.getPart(ModModelLayers.ROOTWALKER)), 0.45f);
    }

    @Override
    public Identifier getTexture(RootwalkerEntity entity) {
        return TEXTURE;
    }
}
