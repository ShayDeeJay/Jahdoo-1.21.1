package org.jahdoo.client.entity_renderer.etneral_wizzard;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jahdoo.entities.EternalWizard;
import org.jetbrains.annotations.NotNull;

public class WizardModel <T extends Mob & RangedAttackMob> extends HumanoidModel<T> {
    public WizardModel(ModelPart pRoot) {
        super(pRoot);
    }

    public void prepareMobModel(@NotNull T pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick) {
        this.rightArmPose = ArmPose.EMPTY;
        this.leftArmPose = ArmPose.EMPTY;
        super.prepareMobModel(pEntity, pLimbSwing, pLimbSwingAmount, pPartialTick);
    }

    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);

        float f = Mth.sin(this.attackTime * (float)Math.PI);
        float f1 = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * (float)Math.PI);
        this.rightArm.zRot = 0.0F;
        this.leftArm.zRot = 0.0F;
        this.rightArm.yRot = -(0.1F - f * 0.3F);
        this.rightArm.xRot = (-(float)Math.PI / 2F) + 0.8f;
        this.rightArm.xRot -= f * 1.2F - f1 * 0.4F;

        if(pEntity instanceof EternalWizard eternalWizard && eternalWizard.isNoAi()){
            this.rightArm.zRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.rightArm.yRot = (0.8F - f * 0.3F);
            this.leftArm.yRot = -(0.8F - f * 0.3F);
            this.rightArm.xRot = (-(float)Math.PI / 2F) - 1.1f;
            this.leftArm.xRot = (-(float)Math.PI / 2F) - 1.1f;
        }

        if (pEntity.isAggressive()) {
            this.rightArm.xRot = (-(float)Math.PI / 2F) - 0.1f;
            this.leftArm.xRot = (-(float)Math.PI / 2F)+ 0.8f;
        }

        AnimationUtils.bobArms(this.rightArm, this.leftArm, pAgeInTicks);
    }

    public void translateToHand(HumanoidArm pSide, PoseStack pPoseStack) {
        float f = pSide == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        ModelPart modelpart = this.getArm(pSide);
        modelpart.x += f;
        modelpart.translateAndRotate(pPoseStack);
        modelpart.x -= f;
    }
}
