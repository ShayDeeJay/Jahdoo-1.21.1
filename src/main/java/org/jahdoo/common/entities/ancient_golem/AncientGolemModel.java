package org.jahdoo.common.entities.ancient_golem;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import static org.jahdoo.common.entities.ancient_golem.AncientGolemRenderer.*;

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

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        this.rightLeg.xRot = -1.5F * Mth.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
        this.leftLeg.xRot = 1.5F * Mth.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
        this.body.xRot = 0.0f;
        this.body.yRot = 0.0f;
        this.animateWalk(limbSwing, limbSwingAmount);
        this.animateIdlePose(ageInTicks);
        this.animate(entity.smash, SMASH_ANIM, ageInTicks);
        this.animate(entity.jump, MODEL_NEW_ANIMATION, ageInTicks);
        this.animate(entity.normal, NORMAL_ATTACK, ageInTicks);
    }

    private void animateWalk(float limbSwing, float limbSwingAmount) {
        float speed = 0.46F; // slower = heavier
        float swing = limbSwing * speed;
        float amt = Math.min(limbSwingAmount, 1.0F);

        float step = Mth.sin(swing);
        float stepAbs = Math.abs(step);

        // Legs (short, heavy)
        float legRot = Mth.cos(swing) * 0.9F * amt;
        this.rightLeg.xRot = legRot;
        this.leftLeg.xRot = -legRot;

        // Hip shove
        float hipForward = step * 1.6F * amt;
        this.rightLeg.z = -hipForward;
        this.leftLeg.z = hipForward;

        // Arms (delayed, minimal)
        float armLag = Mth.cos(swing - 0.6F) * 0.6F * amt;
        this.rightArm.xRot = armLag;
        this.leftArm.xRot = -armLag;

        // Body slam + lean
        this.body.xRot = -0.25F + (stepAbs * 0.12F * amt);
        this.body.y += stepAbs * 0.35F * amt;

        // Subtle torso yaw
        this.body.yRot = step * 0.08F * amt;

        // Head inertia
        this.head.xRot -= step * 0.05F * amt;
        this.head.yRot -= step * 0.04F * amt;

        this.resetArmPoses();
    }

    private void animateIdlePose(float ageInTicks) {
        float t = ageInTicks * 0.08F;
        float sway = Mth.sin(t);
        float breathe = Mth.cos(t * 0.7F);

        // Head
        this.head.xRot += 0.06F * breathe;
        this.head.zRot += 0.04F * sway;

        // Body (slouched + swaying)
        this.body.xRot += -0.28F + 0.025F * breathe;
        this.body.zRot += 0.04F * sway;
        this.body.yRot += 0.06F * sway;

        // Head counterbalance
        this.head.yRot -= this.body.yRot * 0.7F;
        this.head.zRot -= this.body.zRot * 0.6F;

        // Arms (relaxed)
        this.rightArm.xRot += 0.1F * breathe;
        this.leftArm.xRot += -0.1F * breathe;
        this.rightArm.zRot = 0.1F + 0.01F * sway;
        this.leftArm.zRot = -0.1F - 0.01F * sway;

        // Hips shift weight
        this.rightLeg.z = 0.9F * sway;
        this.leftLeg.z = -0.9F * sway;
    }

}
