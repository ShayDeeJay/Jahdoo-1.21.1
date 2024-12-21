package org.jahdoo.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public class StaffArmPose {
    public static final EnumProxy<HumanoidModel.ArmPose> STAFF_ARM_POSE = new EnumProxy<>(HumanoidModel.ArmPose.class, false, (IArmPoseTransformer) ((model, entity, arm) ->
        (arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm).xRot = Mth.lerp(.85f, (arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm).xRot, ((-(float) Math.PI / 2.5F) + model.head.xRot / 2f))));

}
