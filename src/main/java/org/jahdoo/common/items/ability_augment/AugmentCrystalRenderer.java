package org.jahdoo.common.items.ability_augment;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class AugmentCrystalRenderer implements ICurioRenderer {
    ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
        ItemStack itemStack,
        SlotContext slotContext,
        PoseStack poseStack,
        RenderLayerParent<T, M> renderLayerParent,
        MultiBufferSource renderTypeBuffer,
        int light,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float ageInTicks,
        float netHeadYaw,
        float headPitch
    ) {
        if (!(renderLayerParent.getModel() instanceof HumanoidModel<?>)) return;

        var scale = 0.5F;
        var mc = Minecraft.getInstance();
        var level = mc.level;
        if(level == null) return;

        var rotate = level.getGameTime() + partialTicks;
        var animate = rotate / 14;
        var bobOff = Math.sin(rotate / 4.0F) * 0.02F + 1.9F - 0.51;

        poseStack.pushPose();
        poseStack.translate(0, Math.min(bobOff, animate) - 2.4, 0);
        poseStack.mulPose(Axis.YP.rotation(rotate/8));
        poseStack.mulPose(Axis.XN.rotationDegrees(180.0F));

        poseStack.scale(scale, scale, scale);
        itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, poseStack, renderTypeBuffer, null, 0);
        poseStack.popPose();
    }
}
