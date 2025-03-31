package org.jahdoo.common.entities.decoy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

import static net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

public class DecoyRenderer extends AbstractDecoyRenderer<Decoy, DecoyModel<Decoy>> {

    public DecoyRenderer(Context context) {
        this(context, ModelLayers.ZOMBIE, ModelLayers.ZOMBIE, ModelLayers.ZOMBIE);
    }

    public DecoyRenderer(
        Context context,
        ModelLayerLocation zombieLayer,
        ModelLayerLocation innerArmor,
        ModelLayerLocation outerArmor
    ) {
        super(
            context,
            new DecoyModel<>(context.bakeLayer(zombieLayer)),
            new DecoyModel<>(context.bakeLayer(innerArmor)),
            new DecoyModel<>(context.bakeLayer(outerArmor))
        );
    }

    @Override
    protected RenderType getRenderType(Decoy entity, boolean visible, boolean translucent, boolean glowing) {
        return RenderType.entityTranslucent(DECOY);
    }

    @Override
    public void render(
        Decoy entity,
        float yaw,
        float partialTicks,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight
    ) {
        poseStack.pushPose();
        if(entity.tickCount < 20){
            entity.setScale((float) Math.min(1, entity.getScale() + 0.06 * partialTicks));
            poseStack.scale(entity.getScale(), entity.getScale(), entity.getScale());
        }

        this.overriddenRenderer(entity, partialTicks, poseStack, buffer);
        poseStack.popPose();
    }

    private void overriddenRenderer(
        Decoy entity,
        float partialTicks,
        PoseStack postStack,
        MultiBufferSource buffer
    ){
        postStack.pushPose();
        this.model.attackTime = this.getAttackAnim(entity, partialTicks);
        this.model.young = entity.isBaby();
        var f = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        var f1 = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
        var f2 = f1 - f;

        var f6 = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        var f7 = this.getBob(entity, partialTicks);

        this.setupRotations(entity, postStack, f7, f, partialTicks, 1);
        postStack.scale(-1.0F, -1.0F, 1.0F);

        this.scale(entity, postStack, partialTicks);
        postStack.translate(0.0F, -1.501F, 0.0F);

        var f8 = 0.0F;
        var f5 = 0.0F;
        this.model.prepareMobModel(entity, f5, f8, partialTicks);
        this.model.setupAnim(entity, f5, f8, f7, f2, f6);
        var minecraft = Minecraft.getInstance();
        var flag = this.isBodyVisible(entity);
        var flag1 = !flag && !entity.isInvisibleTo(minecraft.player);
        var flag2 = minecraft.shouldEntityAppearGlowing(entity);
        RenderType rendertype = this.getRenderType(entity, flag, flag1, flag2);
        if (rendertype != null) {
            VertexConsumer vertexconsumer = buffer.getBuffer(rendertype);
            this.model.renderToBuffer(postStack, vertexconsumer, 255, 0, FastColor.ABGR32.color(200, 255,255,255));
        }
        postStack.popPose();
    }

}
