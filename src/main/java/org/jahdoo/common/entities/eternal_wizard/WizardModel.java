package org.jahdoo.common.entities.eternal_wizard;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.client.model.AnimationUtils.*;

public class WizardModel <T extends Mob & RangedAttackMob> extends HumanoidModel<T> {
    public WizardModel(ModelPart pRoot) {
        super(pRoot);
    }

    public void prepareMobModel(@NotNull T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        this.rightArmPose = ArmPose.EMPTY;
        this.leftArmPose = ArmPose.EMPTY;
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    }

    public void translateToHand(HumanoidArm side, PoseStack poseStack) {
        float f = side == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        ModelPart modelpart = this.getArm(side);
        modelpart.x += f;
        modelpart.translateAndRotate(poseStack);
        modelpart.x -= f;
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        float f = Mth.sin(this.attackTime * (float)Math.PI);
        float f1 = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * (float)Math.PI);
        this.rightArm.zRot = 0.0F;
        this.leftArm.zRot = 0.0F;
        this.rightArm.yRot = -(0.1F - f * 0.3F);
        this.rightArm.xRot = (-(float)Math.PI / 2F) + 0.8f;
        this.rightArm.xRot -= f * 1.2F - f1 * 0.4F;

        if(entity instanceof EternalWizard eWiz && eWiz.isNoAi()){
            this.rightArm.zRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.rightArm.yRot = (0.8F - f * 0.3F);
            this.leftArm.yRot = -(0.8F - f * 0.3F);
            this.rightArm.xRot = (-(float)Math.PI / 2F) - 1.1f;
            this.leftArm.xRot = (-(float)Math.PI / 2F) - 1.1f;
        }

        if (entity.isAggressive()) {
            this.rightArm.xRot = (-(float)Math.PI / 2F) - 0.1f;
            this.leftArm.xRot = (-(float)Math.PI / 2F)+ 0.8f;
        }

        bobArms(this.rightArm, this.leftArm, ageInTicks);
    }

}
