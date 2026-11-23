package com.beyondfivehorizons.client.renderer;

import com.beyondfivehorizons.BeyondFiveHorizons;
import com.beyondfivehorizons.client.model.PetalmawModel;
import com.beyondfivehorizons.client.model.ModModelLayers;
import com.beyondfivehorizons.entity.mobs.PetalmawEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class PetalmawRenderer extends MobEntityRenderer<PetalmawEntity, PetalmawModel> {
    private static final Identifier TEXTURE = BeyondFiveHorizons.id("textures/entity/petalmaw.png");

    public PetalmawRenderer(EntityRendererFactory.Context context) {
        super(context, new PetalmawModel(context.getPart(ModModelLayers.PETALMAW)), 0.55f);
    }

    @Override
    public Identifier getTexture(PetalmawEntity entity) {
        return TEXTURE;
    }
}
