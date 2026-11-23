package com.beyondfivehorizons.client.model;

import com.beyondfivehorizons.entity.mobs.ArborealGuardianEntity;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;

public class ArborealGuardianModel extends SinglePartEntityModel<ArborealGuardianEntity> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public ArborealGuardianModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();
        root.addChild("head",
                ModelPartBuilder.create().uv(0, 0).cuboid(-6.0f, -10.0f, -6.0f, 12.0f, 10.0f, 12.0f),
                ModelTransform.pivot(0.0f, -10.0f, 0.0f));
        root.addChild("torso",
                ModelPartBuilder.create().uv(0, 22).cuboid(-9.0f, 0.0f, -6.0f, 18.0f, 18.0f, 12.0f),
                ModelTransform.pivot(0.0f, -10.0f, 0.0f));
        root.addChild("left_arm",
                ModelPartBuilder.create().uv(60, 0).cuboid(0.0f, -2.0f, -4.0f, 6.0f, 24.0f, 8.0f),
                ModelTransform.pivot(9.0f, -8.0f, 0.0f));
        root.addChild("right_arm",
                ModelPartBuilder.create().uv(60, 32).cuboid(-6.0f, -2.0f, -4.0f, 6.0f, 24.0f, 8.0f),
                ModelTransform.pivot(-9.0f, -8.0f, 0.0f));
        root.addChild("left_leg",
                ModelPartBuilder.create().uv(0, 52).cuboid(-3.5f, 0.0f, -4.0f, 7.0f, 16.0f, 8.0f),
                ModelTransform.pivot(4.5f, 8.0f, 0.0f));
        root.addChild("right_leg",
                ModelPartBuilder.create().uv(30, 52).cuboid(-3.5f, 0.0f, -4.0f, 7.0f, 16.0f, 8.0f),
                ModelTransform.pivot(-4.5f, 8.0f, 0.0f));
        return TexturedModelData.of(data, 128, 128);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(ArborealGuardianEntity entity, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
        this.head.yaw = headYaw * ((float) Math.PI / 180.0f);
        this.leftLeg.pitch = MathHelper.cos(limbAngle * 0.5f) * 0.9f * limbDistance;
        this.rightLeg.pitch = MathHelper.cos(limbAngle * 0.5f + (float) Math.PI) * 0.9f * limbDistance;
        this.leftArm.pitch = MathHelper.cos(limbAngle * 0.5f + (float) Math.PI) * 0.85f * limbDistance;
        this.rightArm.pitch = MathHelper.cos(limbAngle * 0.5f) * 0.85f * limbDistance;
    }
}
