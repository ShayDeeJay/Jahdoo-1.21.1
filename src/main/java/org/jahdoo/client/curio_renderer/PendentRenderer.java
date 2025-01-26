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
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import static net.minecraft.client.renderer.texture.OverlayTexture.*;
import static net.minecraft.world.item.ItemDisplayContext.*;

public class PendentRenderer implements ICurioRenderer {
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
        var scale = .45f;
        var isChestEmpty = slotContext.entity().getItemBySlot(EquipmentSlot.CHEST).isEmpty();

        poseStack.pushPose();
        humanoidModel.body.translateAndRotate(poseStack);
        poseStack.translate(0, 0.4 * .0625f + 0.2, isChestEmpty ? -0.13 : - 0.2);
        poseStack.mulPose(Axis.ZP.rotation((Mth.PI - 0.1F * Mth.DEG_TO_RAD)));
        poseStack.scale(scale, scale, scale);
        itemRenderer.renderStatic(itemStack, FIXED, light, NO_OVERLAY, poseStack, renderTypeBuffer, null, 0);
        poseStack.popPose();
    }


}
