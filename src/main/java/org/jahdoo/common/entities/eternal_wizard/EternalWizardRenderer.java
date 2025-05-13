package org.jahdoo.common.entities.eternal_wizard;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jahdoo.common.registers.mod.ElementReg;
import org.joml.Matrix4f;

import static org.jahdoo.trial_nexus.utils.Maths.ticksToTime;

@OnlyIn(Dist.CLIENT)
public class EternalWizardRenderer extends EternalWizardBodyRenderer {

    public EternalWizardRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.STRAY, ModelLayers.STRAY_INNER_ARMOR, ModelLayers.STRAY_OUTER_ARMOR);
    }

    @Override
    public void render(EternalWizard entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        entity.setScale(Math.min(1, (entity.getInternalScale() + 0.017f)));
        poseStack.pushPose();
        var scale = entity.getOwner() != null ? entity.getInternalScale() : 1;
        var lifetime = ticksToTime(String.valueOf(entity.getLifetime() - entity.getPrivateTicks()));

        if(entity.getOwner() != null){
            this.renderNameTags(entity, Component.literal(lifetime), poseStack, buffer, 255, 3);
        }

        poseStack.scale(scale, scale, scale);
        super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    protected void renderNameTags(EternalWizard entity, Component displayName, PoseStack pPoseStack, MultiBufferSource bufferSource, int packedLight, float partialTick) {
        var d0 = this.entityRenderDispatcher.distanceToSqr(entity);

        if (d0 < 5) {
            Vec3 vec3 = entity.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, entity.getViewYRot(partialTick));
            if (vec3 != null) {
                pPoseStack.pushPose();
                pPoseStack.translate(vec3.x, vec3.y + (double)0.5F, vec3.z);
                pPoseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
                pPoseStack.scale(0.025F, -0.025F, 0.025F);
                Matrix4f matrix4f = pPoseStack.last().pose();
                Font font = this.getFont();
                float f1 = (float)(-font.width(displayName) / 2);
                font.drawInBatch(displayName, f1, 0, ElementReg.vitality().textColourB(), false, matrix4f, bufferSource, Font.DisplayMode.SEE_THROUGH , 0, packedLight);
                pPoseStack.popPose();
            }
        }
    }


}
