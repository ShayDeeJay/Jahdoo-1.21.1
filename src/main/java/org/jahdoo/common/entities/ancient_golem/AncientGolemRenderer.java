package org.jahdoo.common.entities.ancient_golem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import static net.minecraft.client.animation.AnimationChannel.Interpolations.CATMULLROM;
import static net.minecraft.client.animation.AnimationChannel.Interpolations.LINEAR;
import static net.minecraft.client.animation.AnimationChannel.Targets;
import static net.minecraft.client.animation.KeyframeAnimations.degreeVec;
import static net.minecraft.client.animation.KeyframeAnimations.posVec;
import static net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

public class AncientGolemRenderer extends MobRenderer<AncientGolem, AncientGolemModel<AncientGolem>> {

    private static final ResourceLocation GOLEM_LOCATION =
        JahdooHelpers.res("textures/entity/ancient_golem/ancient_golem.png");

    public static final AnimationDefinition MODEL_NEW_ANIMATION = AnimationDefinition.Builder
        .withLength(1.0F) // Animation length

        // Body Main Animation
        .addAnimation("body_main", new AnimationChannel(
            Targets.POSITION,
            new Keyframe(0.0F, posVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.125F, posVec(0.0F, -2.0F, 0.0F), LINEAR),
            new Keyframe(0.1667F, posVec(0.0F, -2.0F, 0.0F), LINEAR),
            new Keyframe(0.3333F, posVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.5417F, posVec(0.0F, 1.0F, 0.0F), LINEAR),
            new Keyframe(0.75F, posVec(0.0F, 0.0F, 0.0F), LINEAR)
        ))

        // Body Lower Animation
        .addAnimation("body_lower", new AnimationChannel(
            Targets.POSITION,
            new Keyframe(0.0F, posVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.125F, posVec(0.0F, -2.0F, 0.0F), LINEAR),
            new Keyframe(0.1667F, posVec(0.0F, -2.0F, 0.0F), LINEAR),
            new Keyframe(0.3333F, posVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.5417F, posVec(0.0F, 1.0F, 0.0F), LINEAR),
            new Keyframe(0.75F, posVec(0.0F, 0.0F, 0.0F), LINEAR)
        ))

        // Head Animation
        .addAnimation("head", new AnimationChannel(
            Targets.POSITION,
            new Keyframe(0.0F, posVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.125F, posVec(0.0F, -3.0F, 0.0F), LINEAR),
            new Keyframe(0.1667F, posVec(0.0F, -3.0F, 0.0F), LINEAR),
            new Keyframe(0.3333F, posVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.5417F, posVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.75F, posVec(0.0F, 0.0F, 0.0F), LINEAR)
        ))

        // Right Arm Animation
        .addAnimation("right_arm", new AnimationChannel(
            Targets.ROTATION,
            new Keyframe(0.0F, degreeVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.5417F, degreeVec(-22.5F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.9583F, degreeVec(0.0F, 0.0F, 0.0F), LINEAR)
        ))
        .addAnimation("right_arm", new AnimationChannel(
            Targets.POSITION,
            new Keyframe(0.0F, posVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.125F, posVec(0.0F, -4.0F, 0.0F), LINEAR),
            new Keyframe(0.1667F, posVec(0.0F, -4.0F, 0.0F), LINEAR),
            new Keyframe(0.3333F, posVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.5417F, posVec(0.0F, 2.0F, 0.0F), LINEAR),
            new Keyframe(0.75F, posVec(0.0F, 0.0F, 0.0F), LINEAR)
        ))

        // Left Arm Animation
        .addAnimation("left_arm", new AnimationChannel(
            Targets.ROTATION,
            new Keyframe(0.0F, degreeVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.5417F, degreeVec(-20.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.9583F, degreeVec(0.0F, 0.0F, 0.0F), LINEAR)
        ))
        .addAnimation("left_arm", new AnimationChannel(
            Targets.POSITION,
            new Keyframe(0.0F, posVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.125F, posVec(0.0F, -4.0F, 0.0F), LINEAR),
            new Keyframe(0.1667F, posVec(0.0F, -4.0F, 0.0F), LINEAR),
            new Keyframe(0.3333F, posVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.5417F, posVec(0.0F, 2.0F, 0.0F), LINEAR),
            new Keyframe(0.75F, posVec(0.0F, 0.0F, 0.0F), LINEAR)
        ))

        // Right Leg Animation
        .addAnimation("right_leg", new AnimationChannel(
            Targets.POSITION,
            new Keyframe(0.0F, posVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.5417F, posVec(0.0F, 3.0F, 0.0F), LINEAR),
            new Keyframe(0.75F, posVec(0.0F, 0.0F, 0.0F), LINEAR)
        ))

        // Left Leg Animation
        .addAnimation("left_leg", new AnimationChannel(
            Targets.POSITION,
            new Keyframe(0.0F, posVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.5417F, posVec(0.0F, 2.0F, 0.0F), LINEAR),
            new Keyframe(0.75F, posVec(0.0F, 0.0F, 0.0F), LINEAR)
        ))

        // All Bones Animation
        .addAnimation("root", new AnimationChannel(
            Targets.POSITION,
            new Keyframe(0.25F, posVec(0.0F, 1.0F, 0.0F), LINEAR),
            new Keyframe(0.5833F, posVec(0.0F, 30.0F, 0.0F), CATMULLROM),
            new Keyframe(1.0F, posVec(0.0F, 0.0F, 0.0F), CATMULLROM)
        ))

        .build();


    public static final AnimationDefinition SMASH_ANIM = AnimationDefinition.Builder
        .withLength(1.125F) // Animation length
        // Body Rotation Animation
        .addAnimation("body", new AnimationChannel(
            Targets.ROTATION,
            new Keyframe(0.0F, degreeVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.2917F, degreeVec(-22.5F, 0.0F, 0.0F), CATMULLROM),
            new Keyframe(0.4583F, degreeVec(40.0F, 0.0F, 0.0F), CATMULLROM),
            new Keyframe(0.625F, degreeVec(45.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.75F, degreeVec(40.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(1.125F, degreeVec(0.0F, 0.0F, 0.0F), CATMULLROM)
        ))
        .addAnimation("body", new AnimationChannel(
            Targets.POSITION,
            new Keyframe(0.0F, posVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.2917F, posVec(0.0F, 0.0F, 3.0F), CATMULLROM),
            new Keyframe(0.4583F, posVec(0.0F, -1.0F, -7.0F), CATMULLROM),
            new Keyframe(0.625F, posVec(0.0F, -1.0F, -9.0F), LINEAR),
            new Keyframe(0.75F, posVec(0.0F, -1.0F, -7.0F), LINEAR),
            new Keyframe(1.125F, posVec(0.0F, 0.0F, 0.0F), CATMULLROM)
        ))

        // Head Rotation Animation
        .addAnimation("head", new AnimationChannel(
            Targets.ROTATION,
            new Keyframe(0.0F, degreeVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.2917F, degreeVec(-7.5F, 0.0F, 0.0F), CATMULLROM),
            new Keyframe(0.4583F, degreeVec(12.5F, 0.0F, 0.0F), CATMULLROM),
            new Keyframe(0.75F, degreeVec(12.5F, 0.0F, 0.0F), LINEAR),
            new Keyframe(1.125F, degreeVec(0.0F, 0.0F, 0.0F), CATMULLROM)
        ))
        .addAnimation("head", new AnimationChannel(
            Targets.POSITION,
            new Keyframe(0.0F, posVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.2917F, posVec(0.0F, 0.0F, 3.0F), CATMULLROM),
            new Keyframe(0.4583F, posVec(0.0F, -5.0F, -10.0F), CATMULLROM),
            new Keyframe(0.625F, posVec(0.0F, -5.0F, -12.0F), LINEAR),
            new Keyframe(0.75F, posVec(0.0F, -5.0F, -10.0F), LINEAR),
            new Keyframe(1.125F, posVec(0.0F, 0.0F, 0.0F), CATMULLROM)
        ))

        // Right Arm Rotation Animation
        .addAnimation("right_arm", new AnimationChannel(
            Targets.ROTATION,
            new Keyframe(0.0F, degreeVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.2917F, degreeVec(-217.5F, 0.0F, 0.0F), CATMULLROM),
            new Keyframe(0.4583F, degreeVec(-35.0F, 0.0F, 0.0F), CATMULLROM),
            new Keyframe(0.625F, degreeVec(-27.5F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.75F, degreeVec(-35.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(1.125F, degreeVec(0.0F, 0.0F, 0.0F), CATMULLROM)
        ))
        .addAnimation("right_arm", new AnimationChannel(
            Targets.POSITION,
            new Keyframe(0.0F, posVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.2917F, posVec(0.0F, -0.36F, 1.18F), CATMULLROM),
            new Keyframe(0.4583F, posVec(0.0F, -6.0F, -8.0F), CATMULLROM),
            new Keyframe(0.75F, posVec(0.0F, -6.0F, -8.0F), LINEAR),
            new Keyframe(1.125F, posVec(0.0F, 0.0F, 0.0F), CATMULLROM)
        ))

        // Left Arm Rotation Animation
        .addAnimation("left_arm", new AnimationChannel(
            Targets.ROTATION,
            new Keyframe(0.0F, degreeVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.2917F, degreeVec(-217.5F, 0.0F, 0.0F), CATMULLROM),
            new Keyframe(0.4583F, degreeVec(-32.5F, 0.0F, 0.0F), CATMULLROM),
            new Keyframe(0.625F, degreeVec(-25.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.75F, degreeVec(-32.5F, 0.0F, 0.0F), LINEAR),
            new Keyframe(1.125F, degreeVec(-2.41261F, -0.60918F, -0.22895F), CATMULLROM)
        ))
        .addAnimation("left_arm", new AnimationChannel(
            Targets.POSITION,
            new Keyframe(0.0F, posVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.2917F, posVec(0.0F, -1.64F, -0.27F), CATMULLROM),
            new Keyframe(0.4583F, posVec(0.0F, -5.0F, -10.0F), CATMULLROM),
            new Keyframe(0.75F, posVec(0.0F, -5.0F, -10.0F), LINEAR),
            new Keyframe(1.125F, posVec(0.0F, 0.0F, -1.0F), CATMULLROM)
        ))

        // Right Leg Rotation Animation
        .addAnimation("right_leg", new AnimationChannel(
            Targets.ROTATION,
            new Keyframe(0.0F, degreeVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.2917F, degreeVec(-5.0F, 0.0F, 0.0F), CATMULLROM),
            new Keyframe(0.4583F, degreeVec(20.0F, 0.0F, 0.0F), CATMULLROM),
            new Keyframe(0.75F, degreeVec(20.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(1.125F, degreeVec(0.0F, 0.0F, 0.0F), CATMULLROM)
        ))

        // Left Leg Rotation Animation
        .addAnimation("left_leg", new AnimationChannel(
            Targets.ROTATION,
            new Keyframe(0.0F, degreeVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.2917F, degreeVec(-5.0F, 0.0F, 0.0F), CATMULLROM),
            new Keyframe(0.4583F, degreeVec(20.0F, 0.0F, 0.0F), CATMULLROM),
            new Keyframe(0.75F, degreeVec(20.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(1.125F, degreeVec(0.0F, 0.0F, 0.0F), CATMULLROM)
        ))

        .build();

    public static final AnimationDefinition NORMAL_ATTACK = AnimationDefinition.Builder.withLength(0.75F)

        // LEFT LEG ROTATION
        .addAnimation("left_leg", new AnimationChannel(
            Targets.ROTATION,
            new Keyframe(0.0F,    degreeVec(0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.4583F, degreeVec(0.0F, 0.0F, 0.0F), LINEAR)
        ))

        // BODY ROTATION
        .addAnimation("body", new AnimationChannel(
            Targets.ROTATION,
            new Keyframe(0.0F,    degreeVec( 0.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.125F,  degreeVec(-10.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.2083F, degreeVec(-10.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.2917F, degreeVec(-10.0F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.375F,  degreeVec( 7.5F, 0.0F, 0.0F), LINEAR),
            new Keyframe(0.7083F, degreeVec( 0.0F, 0.0F, 0.0F), CATMULLROM)
        ))

        // BODY POSITION
        .addAnimation("body", new AnimationChannel(
            Targets.POSITION,
            new Keyframe(0.0F,    posVec(0.0F, 0.0F,  0.0F), LINEAR),
            new Keyframe(0.125F,  posVec(0.0F, 0.0F,  2.0F), LINEAR),
            new Keyframe(0.2083F, posVec(0.0F, 0.0F,  2.0F), LINEAR),
            new Keyframe(0.2917F, posVec(0.0F, 0.0F,  2.0F), LINEAR),
            new Keyframe(0.375F,  posVec(0.0F, 0.0F, -2.0F), LINEAR),
            new Keyframe(0.7083F, posVec(0.0F, 0.0F,  0.0F), CATMULLROM)
        ))

        // HEAD POSITION
        .addAnimation("head", new AnimationChannel(
            Targets.POSITION,
            new Keyframe(0.0F,    posVec(0.0F, 0.0F,  0.0F), LINEAR),
            new Keyframe(0.125F,  posVec(0.0F, 0.0F,  4.0F), LINEAR),
            new Keyframe(0.2083F, posVec(0.0F, 0.0F,  4.0F), LINEAR),
            new Keyframe(0.2917F, posVec(0.0F, 0.0F,  4.0F), LINEAR),
            new Keyframe(0.4167F, posVec(0.0F, 0.0F, -4.0F), LINEAR),
            new Keyframe(0.7083F, posVec(0.0F, 0.0F,  0.0F), CATMULLROM)
        ))

        // RIGHT ARM ROTATION
        .addAnimation("right_arm", new AnimationChannel(
            Targets.ROTATION,
            new Keyframe(0.0F,    degreeVec(  0.0F,   0.0F, 0.0F), LINEAR),
            new Keyframe(0.2917F, degreeVec(-90.0F,  45.0F, 0.0F), LINEAR),
            new Keyframe(0.4167F, degreeVec(-90.0F, -17.5F, 0.0F), CATMULLROM),
            new Keyframe(0.7083F, degreeVec(  0.0F,   0.0F, 0.0F), CATMULLROM)
        ))

        // RIGHT ARM POSITION
        .addAnimation("right_arm", new AnimationChannel(
            Targets.POSITION,
            new Keyframe(0.0F,    posVec(0.0F, 0.0F,  0.0F), LINEAR),
            new Keyframe(0.125F,  posVec(0.0F, 0.0F,  2.0F), LINEAR),
            new Keyframe(0.2083F, posVec(0.0F, 0.0F,  2.0F), LINEAR),
            new Keyframe(0.2917F, posVec(0.0F, 0.0F,  2.0F), LINEAR),
            new Keyframe(0.375F,  posVec(0.0F, 0.0F, -2.0F), LINEAR),
            new Keyframe(0.4167F, posVec(0.0F, 0.0F, -9.0F), LINEAR),
            new Keyframe(0.7083F, posVec(0.0F, 0.0F,  0.0F), CATMULLROM)
        ))

        // LEFT ARM ROTATION
        .addAnimation("left_arm", new AnimationChannel(
            Targets.ROTATION,
            new Keyframe(0.0F,    degreeVec(  0.0F,   0.0F, 0.0F), LINEAR),
            new Keyframe(0.2917F, degreeVec(-90.0F, -45.0F, 0.0F), LINEAR),
            new Keyframe(0.4167F, degreeVec(-90.0F,  15.0F, 0.0F), CATMULLROM),
            new Keyframe(0.7083F, degreeVec(  0.0F,   0.0F, 0.0F), CATMULLROM)
        ))

        // LEFT ARM POSITION
        .addAnimation("left_arm", new AnimationChannel(
            Targets.POSITION,
            new Keyframe(0.0F,    posVec(0.0F, 0.0F,  0.0F), LINEAR),
            new Keyframe(0.125F,  posVec(0.0F, 0.0F,  2.0F), LINEAR),
            new Keyframe(0.2083F, posVec(0.0F, 0.0F,  2.0F), LINEAR),
            new Keyframe(0.2917F, posVec(0.0F, 0.0F,  2.0F), LINEAR),
            new Keyframe(0.375F,  posVec(0.0F, 0.0F, -2.0F), LINEAR),
            new Keyframe(0.4167F, posVec(0.0F, 0.0F, -9.0F), LINEAR),
            new Keyframe(0.7083F, posVec(0.0F, 0.0F,  0.0F), CATMULLROM)
        ))

        .build();
    @Override
    public void render(
        AncientGolem entity,
        float entityYaw,
        float partialTicks,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight
    ) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    public AncientGolemRenderer(Context context) {
        super(context, new AncientGolemModel<>(context.bakeLayer(ModelLayers.IRON_GOLEM)), 0.7F);
    }

    public ResourceLocation getTextureLocation(AncientGolem entity) {
        return GOLEM_LOCATION;
    }

    protected void setupRotations(
        AncientGolem entity,
        PoseStack poseStack,
        float bob,
        float yBodyRot,
        float partialTick,
        float scale
    ) {
        super.setupRotations(entity, poseStack, bob, yBodyRot, partialTick, scale);
        var walkAnimation = entity.walkAnimation;

        if (!((double) walkAnimation.speed() < 0.01)) {
            var f1 = walkAnimation.position(partialTick) + 6.0F;
            var f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
            poseStack.mulPose(Axis.ZP.rotationDegrees(6.5F * f2));
        }
    }

}
