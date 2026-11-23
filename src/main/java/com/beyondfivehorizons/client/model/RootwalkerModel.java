package com.beyondfivehorizons.client.model;

import com.beyondfivehorizons.entity.mobs.RootwalkerEntity;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;

public class RootwalkerModel extends SinglePartEntityModel<RootwalkerEntity> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public RootwalkerModel(ModelPart root) {
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
                ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f),
                ModelTransform.pivot(0.0f, 2.0f, 0.0f));
        root.addChild("body",
                ModelPartBuilder.create().uv(0, 16).cuboid(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f),
                ModelTransform.pivot(0.0f, 2.0f, 0.0f));
        root.addChild("left_arm",
                ModelPartBuilder.create().uv(28, 16).cuboid(0.0f, -1.0f, -2.0f, 3.0f, 14.0f, 4.0f),
                ModelTransform.pivot(4.0f, 3.0f, 0.0f));
        root.addChild("right_arm",
                ModelPartBuilder.create().uv(42, 16).cuboid(-3.0f, -1.0f, -2.0f, 3.0f, 14.0f, 4.0f),
                ModelTransform.pivot(-4.0f, 3.0f, 0.0f));
        root.addChild("left_leg",
                ModelPartBuilder.create().uv(0, 34).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 4.0f),
                ModelTransform.pivot(2.0f, 14.0f, 0.0f));
        root.addChild("right_leg",
                ModelPartBuilder.create().uv(16, 34).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 4.0f),
                ModelTransform.pivot(-2.0f, 14.0f, 0.0f));
        return TexturedModelData.of(data, 64, 64);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(RootwalkerEntity entity, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
        this.head.yaw = headYaw * ((float) Math.PI / 180.0f);
        this.head.pitch = headPitch * ((float) Math.PI / 180.0f);
        this.leftLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.1f * limbDistance;
        this.rightLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + (float) Math.PI) * 1.1f * limbDistance;
        this.leftArm.pitch = -0.35f + MathHelper.cos(limbAngle * 0.6662f + (float) Math.PI) * 0.9f * limbDistance;
        this.rightArm.pitch = -0.35f + MathHelper.cos(limbAngle * 0.6662f) * 0.9f * limbDistance;
    }
}
