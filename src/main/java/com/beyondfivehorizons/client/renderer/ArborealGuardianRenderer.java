package com.beyondfivehorizons.client.renderer;

import com.beyondfivehorizons.BeyondFiveHorizons;
import com.beyondfivehorizons.client.model.ArborealGuardianModel;
import com.beyondfivehorizons.client.model.ModModelLayers;
import com.beyondfivehorizons.entity.mobs.ArborealGuardianEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class ArborealGuardianRenderer extends MobEntityRenderer<ArborealGuardianEntity, ArborealGuardianModel> {
    private static final Identifier TEXTURE = BeyondFiveHorizons.id("textures/entity/arboreal_guardian.png");

    public ArborealGuardianRenderer(EntityRendererFactory.Context context) {
        super(context, new ArborealGuardianModel(context.getPart(ModModelLayers.ARBOREAL_GUARDIAN)), 0.85f);
    }

    @Override
    public Identifier getTexture(ArborealGuardianEntity entity) {
        return TEXTURE;
    }
}
