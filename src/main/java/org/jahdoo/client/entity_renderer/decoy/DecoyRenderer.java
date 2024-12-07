package org.jahdoo.client.entity_renderer.decoy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.jahdoo.entities.Decoy;
import org.jetbrains.annotations.Nullable;

public class DecoyRenderer extends AbstractDecoyRenderer<Decoy, DecoyModel<Decoy>> {
    public DecoyRenderer(EntityRendererProvider.Context p_174456_) {
        this(p_174456_, ModelLayers.ZOMBIE, ModelLayers.ZOMBIE, ModelLayers.ZOMBIE);
    }

    public DecoyRenderer(EntityRendererProvider.Context pContext, ModelLayerLocation pZombieLayer, ModelLayerLocation pInnerArmor, ModelLayerLocation pOuterArmor) {
        super(pContext, new DecoyModel<>(pContext.bakeLayer(pZombieLayer)), new DecoyModel<>(pContext.bakeLayer(pInnerArmor)), new DecoyModel<>(pContext.bakeLayer(pOuterArmor)));
    }

    @Nullable
    @Override
    protected RenderType getRenderType(Decoy pLivingEntity, boolean pBodyVisible, boolean pTranslucent, boolean pGlowing) {
        return RenderType.entityTranslucent(DECOY);
    }

    @Override
    public void render(Decoy pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        if(pEntity.tickCount < 20){
            pEntity.setScale((float) Math.min(1, pEntity.getScale() + 0.06 * pPartialTicks));
            pPoseStack.scale(pEntity.getScale(), pEntity.getScale(), pEntity.getScale());
        }
        this.overriddenRenderer(pEntity, pPartialTicks, pPoseStack, pBuffer);

        pPoseStack.popPose();
    }

    private void overriddenRenderer(Decoy pEntity, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer){
        pPoseStack.pushPose();
        this.model.attackTime = this.getAttackAnim(pEntity, pPartialTicks);
        this.model.young = pEntity.isBaby();
        float f = Mth.rotLerp(pPartialTicks, pEntity.yBodyRotO, pEntity.yBodyRot);
        float f1 = Mth.rotLerp(pPartialTicks, pEntity.yHeadRotO, pEntity.yHeadRot);
        float f2 = f1 - f;

        float f6 = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());
        float f7 = this.getBob(pEntity, pPartialTicks);

        this.setupRotations(pEntity, pPoseStack, f7, f, pPartialTicks,1);
        pPoseStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(pEntity, pPoseStack, pPartialTicks);
        pPoseStack.translate(0.0F, -1.501F, 0.0F);
        float f8 = 0.0F;
        float f5 = 0.0F;
        this.model.prepareMobModel(pEntity, f5, f8, pPartialTicks);
        this.model.setupAnim(pEntity, f5, f8, f7, f2, f6);
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = this.isBodyVisible(pEntity);
        boolean flag1 = !flag && !pEntity.isInvisibleTo(minecraft.player);
        boolean flag2 = minecraft.shouldEntityAppearGlowing(pEntity);
        RenderType rendertype = this.getRenderType(pEntity, flag, flag1, flag2);
        if (rendertype != null) {
            VertexConsumer vertexconsumer = pBuffer.getBuffer(rendertype);
            this.model.renderToBuffer(pPoseStack, vertexconsumer, 255, 0, FastColor.ABGR32.color(200, 255,255,255));
        }
        pPoseStack.popPose();
    }

}
