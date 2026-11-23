package com.beyondfivehorizons.client.model;

import com.beyondfivehorizons.entity.boss.HeartOfTheForestEntity;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;

public class HeartOfTheForestModel extends SinglePartEntityModel<HeartOfTheForestEntity> {
    private final ModelPart root;
    private final ModelPart heartCore;
    private final ModelPart leftBranch;
    private final ModelPart rightBranch;
    private final ModelPart crown;

    public HeartOfTheForestModel(ModelPart root) {
        this.root = root;
        this.heartCore = root.getChild("heart_core");
        this.leftBranch = root.getChild("left_branch");
        this.rightBranch = root.getChild("right_branch");
        this.crown = root.getChild("crown");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();
        root.addChild("base_roots",
                ModelPartBuilder.create().uv(0, 0).cuboid(-18.0f, -14.0f, -18.0f, 36.0f, 14.0f, 36.0f),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));
        root.addChild("trunk",
                ModelPartBuilder.create().uv(0, 50).cuboid(-13.0f, -38.0f, -13.0f, 26.0f, 38.0f, 26.0f),
                ModelTransform.pivot(0.0f, 10.0f, 0.0f));
        root.addChild("heart_core",
                ModelPartBuilder.create().uv(104, 50).cuboid(-7.0f, -7.0f, -7.0f, 14.0f, 14.0f, 14.0f),
                ModelTransform.pivot(0.0f, -12.0f, -10.0f));
        root.addChild("crown",
                ModelPartBuilder.create().uv(0, 114).cuboid(-22.0f, -16.0f, -22.0f, 44.0f, 16.0f, 44.0f),
                ModelTransform.pivot(0.0f, -28.0f, 0.0f));
        root.addChild("left_branch",
                ModelPartBuilder.create().uv(144, 0).cuboid(0.0f, -4.0f, -4.0f, 22.0f, 8.0f, 8.0f),
                ModelTransform.pivot(12.0f, -18.0f, 0.0f));
        root.addChild("right_branch",
                ModelPartBuilder.create().uv(144, 16).cuboid(-22.0f, -4.0f, -4.0f, 22.0f, 8.0f, 8.0f),
                ModelTransform.pivot(-12.0f, -18.0f, 0.0f));
        return TexturedModelData.of(data, 256, 256);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(HeartOfTheForestEntity entity, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
        float pulseSpeed = entity.getPhase() == 3 ? 0.35f : 0.16f;
        this.heartCore.yaw = MathHelper.sin(animationProgress * pulseSpeed) * 0.25f;
        this.leftBranch.roll = -0.25f + MathHelper.sin(animationProgress * 0.12f) * 0.2f;
        this.rightBranch.roll = 0.25f - MathHelper.sin(animationProgress * 0.12f) * 0.2f;
        this.crown.yaw = MathHelper.sin(animationProgress * 0.05f) * 0.08f;
    }
}
