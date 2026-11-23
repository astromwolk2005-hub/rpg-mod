package com.beyondfivehorizons.client.model;

import com.beyondfivehorizons.entity.mobs.FireflyEntity;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;

public class FireflyModel extends SinglePartEntityModel<FireflyEntity> {
    private final ModelPart root;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public FireflyModel(ModelPart root) {
        this.root = root;
        this.leftWing = root.getChild("left_wing");
        this.rightWing = root.getChild("right_wing");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();
        root.addChild("body",
                ModelPartBuilder.create().uv(0, 0).cuboid(-2.0f, -2.0f, -3.0f, 4.0f, 4.0f, 6.0f),
                ModelTransform.pivot(0.0f, 20.0f, 0.0f));
        root.addChild("left_wing",
                ModelPartBuilder.create().uv(0, 10).cuboid(0.0f, 0.0f, -2.0f, 5.0f, 1.0f, 4.0f),
                ModelTransform.pivot(2.0f, 18.5f, 0.0f));
        root.addChild("right_wing",
                ModelPartBuilder.create().uv(0, 15).cuboid(-5.0f, 0.0f, -2.0f, 5.0f, 1.0f, 4.0f),
                ModelTransform.pivot(-2.0f, 18.5f, 0.0f));
        return TexturedModelData.of(data, 32, 32);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(FireflyEntity entity, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
        float flap = MathHelper.cos(animationProgress * 1.6f) * 0.7f;
        this.leftWing.roll = flap;
        this.rightWing.roll = -flap;
    }
}
