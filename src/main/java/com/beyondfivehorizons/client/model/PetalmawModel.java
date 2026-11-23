package com.beyondfivehorizons.client.model;

import com.beyondfivehorizons.entity.mobs.PetalmawEntity;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;

public class PetalmawModel extends SinglePartEntityModel<PetalmawEntity> {
    private final ModelPart root;
    private final ModelPart petalNorth;
    private final ModelPart petalSouth;
    private final ModelPart petalEast;
    private final ModelPart petalWest;

    public PetalmawModel(ModelPart root) {
        this.root = root;
        this.petalNorth = root.getChild("petal_north");
        this.petalSouth = root.getChild("petal_south");
        this.petalEast = root.getChild("petal_east");
        this.petalWest = root.getChild("petal_west");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();
        root.addChild("stem",
                ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, 0.0f, -4.0f, 8.0f, 10.0f, 8.0f),
                ModelTransform.pivot(0.0f, 14.0f, 0.0f));
        root.addChild("maw",
                ModelPartBuilder.create().uv(0, 18).cuboid(-5.0f, -6.0f, -5.0f, 10.0f, 6.0f, 10.0f),
                ModelTransform.pivot(0.0f, 14.0f, 0.0f));
        root.addChild("petal_north",
                ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -10.0f, -1.0f, 8.0f, 10.0f, 2.0f),
                ModelTransform.pivot(0.0f, 12.0f, -5.0f));
        root.addChild("petal_south",
                ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -10.0f, -1.0f, 8.0f, 10.0f, 2.0f),
                ModelTransform.pivot(0.0f, 12.0f, 5.0f));
        root.addChild("petal_east",
                ModelPartBuilder.create().uv(32, 12).cuboid(-1.0f, -10.0f, -4.0f, 2.0f, 10.0f, 8.0f),
                ModelTransform.pivot(5.0f, 12.0f, 0.0f));
        root.addChild("petal_west",
                ModelPartBuilder.create().uv(32, 12).cuboid(-1.0f, -10.0f, -4.0f, 2.0f, 10.0f, 8.0f),
                ModelTransform.pivot(-5.0f, 12.0f, 0.0f));
        return TexturedModelData.of(data, 64, 64);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(PetalmawEntity entity, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
        float openAngle = entity.isAwakened()
                ? 0.85f + MathHelper.sin(animationProgress * 0.35f) * 0.2f
                : 0.12f;
        this.petalNorth.pitch = -openAngle;
        this.petalSouth.pitch = openAngle;
        this.petalEast.roll = openAngle;
        this.petalWest.roll = -openAngle;
    }
}
