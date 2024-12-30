package org.jahdoo.client.entity_renderer.ancient_golem;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jahdoo.entities.living.AncientGolem;

import static org.jahdoo.client.entity_renderer.ancient_golem.AncientGolemRenderer.*;

public class AncientGolemModel<T extends AncientGolem> extends HierarchicalModel<T> {
    private final ModelPart body;
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public AncientGolemModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public ModelPart root() { return this.root;}

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        this.rightLeg.xRot = -1.5F * Mth.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
        this.leftLeg.xRot = 1.5F * Mth.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
        this.body.xRot = 0.0f;
        this.body.yRot = 0.0f;
        this.animateWalk(entity, limbSwing, limbSwingAmount);
        this.animateIdlePose(ageInTicks);
        this.animate(entity.smash, SMASH_ANIM, ageInTicks);
        this.animate(entity.jump, MODEL_NEW_ANIMATION, ageInTicks);
        this.animate(entity.normal, NORMAL_ATTACK_ANIM, ageInTicks);
    }


    private void animateIdlePose(float ageInTicks) {
        var f = ageInTicks * 0.1F;
        var f1 = Mth.cos(f);
        var f2 = Mth.sin(f);
        var var10000 = this.head;
        var10000.zRot += 0.04F * f1 ;
        var10000 = this.head;
        var10000.xRot += 0.06F * f2;
        var10000 = this.body;
        var10000.zRot += 0.015F * f2;
        var10000 = this.body;
        var10000.xRot += 0.015F * f1  - 0.2F;
        var10000 = this.body;
        var10000.yRot += 0.115F * f1 ;
        var10000 = this.rightArm;
        var10000.xRot += 0.225F * f1;
        var10000.zRot  = 0.125F ;
        var10000 = this.leftArm;
        var10000.xRot += -0.125F * f1;
        var10000.zRot = -0.125F ;
        var10000 = this.rightLeg;
//        var10000.xRot += -0.125F * f1;
        var10000.zRot = 0.125F ;
        var10000.yRot = 0.425F ;
    }

    private void animateWalk(T entity, float limbSwing, float limbSwingAmount) {
        var f = Math.min(0.5F, 3.0F * limbSwingAmount);
        var f1 = limbSwing * 0.8662F;
        var f2 = Mth.cos(f1);
        var f3 = Mth.sin(f1);
        var f4 = Math.min(0.35F, f);
        var var10000 = this.head;

        var10000.zRot += 0.3F * f3 * f;
        this.body.zRot = 0.1F * f3 * f;
        this.body.xRot = 0.1F * f2 * f4;

        this.rightLeg.xRot = -1.8F * Mth.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
        this.leftLeg.xRot = 1.8F * Mth.triangleWave(limbSwing, 13.0F) * limbSwingAmount;

        this.leftArm.xRot = -(1.3f * f2 * f) * (limbSwingAmount > 0.8 ? 1.5f : limbSwingAmount);
        this.leftArm.zRot = 0.0F;

        this.rightArm.xRot = (1.3f * f2 * f) * (limbSwingAmount > 0.8 ? 1.5f : limbSwingAmount);
        this.rightArm.zRot = 0.0F;
        this.resetArmPoses();
    }

    private void resetArmPoses() {
        this.leftArm.setPos(0, -6f, -1.5F);
        this.rightArm.setPos(0,  -6F, -1.5f);

        this.rightLeg.z = -2f;
        this.rightLeg.yRot = 0.2f;

        this.leftLeg.z = -2f;
        this.leftLeg.yRot = -0.2f;

        this.body.z = -1f;
        this.body.xRot = 0.15f;

        this.head.setPos(0, -7f, -4f);
    }

}
