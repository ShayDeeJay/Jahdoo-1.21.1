package org.jahdoo.client.entity_renderer.etneral_wizzard;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jahdoo.entities.EternalWizard;
import org.jahdoo.items.augments.AugmentItemHelper;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class EternalWizardRenderer extends EternalWizardBodyRenderer {

    public EternalWizardRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.STRAY, ModelLayers.STRAY_INNER_ARMOR, ModelLayers.STRAY_OUTER_ARMOR);
        this.addLayer(new WizardClothingLayerRenderer<>(this, context.getModelSet()));
    }

    @Override
    public void render(EternalWizard pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pEntity.setScale(Math.min(1, (pEntity.getInternalScale() + 0.017f)));
        pPoseStack.pushPose();
        var scale = pEntity.getInternalScale();

//        var itemRenderer = Minecraft.getInstance().getItemRenderer();
//        pPoseStack.pushPose();
//
//        pPoseStack.rotateAround(Axis.YN.rotationDegrees(pEntity.yHeadRot), 0, 0, 0);
//        pPoseStack.translate(0,2.5,0);
//        itemRenderer.renderStatic(
//            new ItemStack(Items.DIAMOND_BLOCK),
//            ItemDisplayContext.FIXED,
//            255,
//            OverlayTexture.NO_OVERLAY,
//            pPoseStack,
//            pBuffer,
//            pEntity.level(),
//            1
//        );
//        pPoseStack.popPose();


        var lifetime = AugmentItemHelper.ticksToTime(String.valueOf(pEntity.getLifetime() - pEntity.getPrivateTicks()));
        if(pEntity.getOwner() != null){
            this.renderNameTags(pEntity, Component.literal(lifetime), pPoseStack, pBuffer, 255, 3);
        }
        pPoseStack.scale(scale, scale, scale);
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
        pPoseStack.popPose();
    }

    protected void renderNameTags(EternalWizard entity, Component displayName, PoseStack pPoseStack, MultiBufferSource bufferSource, int packedLight, float partialTick) {
        double d0 = this.entityRenderDispatcher.distanceToSqr(entity);
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
                font.drawInBatch(displayName, f1, 0, ElementRegistry.VITALITY.get().textColourSecondary(), false, matrix4f, bufferSource, Font.DisplayMode.SEE_THROUGH , 0, packedLight);
                pPoseStack.popPose();
            }
        }
    }


}
