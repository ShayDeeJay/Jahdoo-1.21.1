package org.jahdoo.client.curio_renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.registers.ItemsRegister;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class GloveRenderer implements ICurioRenderer {
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
        if (!(renderLayerParent.getModel() instanceof HumanoidModel<?> humanoidModel)) return;

        var scale = 0.6f;
        var itemRenderer = Minecraft.getInstance().getItemRenderer();
        poseStack.pushPose();

        humanoidModel.leftArm.translateAndRotate(poseStack);
        poseStack.translate(0.088,0.5,0.015);
        poseStack.mulPose(Axis.XP.rotation(1.57f));
        poseStack.mulPose(Axis.ZP.rotation(18.85f));
        poseStack.scale(scale, scale, scale);
        itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, poseStack, renderTypeBuffer, null, 0);
        poseStack.popPose();


//        poseStack.pushPose();
//        var scale1 = 0.2f;
//        humanoidModel.rightArm.translateAndRotate(poseStack);
//        poseStack.translate(-0.21,0.5,0.009);
//        poseStack.mulPose(Axis.XP.rotation(1.6f));
//        poseStack.mulPose(Axis.ZP.rotation(15.7f));
//        poseStack.mulPose(Axis.YP.rotation(1.55f));
//        poseStack.scale(scale1, scale1, scale1);
//        itemRenderer.renderStatic(
//            new ItemStack(ItemsRegister.RUNE.get()),
//            ItemDisplayContext.FIXED,
//            light,
//            OverlayTexture.NO_OVERLAY,
//            poseStack,
//            renderTypeBuffer,
//            Minecraft.getInstance().level,
//            1
//        );
//        poseStack.popPose();
    }
}
