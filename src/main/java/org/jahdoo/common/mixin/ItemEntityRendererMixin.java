package org.jahdoo.common.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin {

    @Shadow @Final private ItemRenderer itemRenderer;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void injectScale(ItemEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
//        test(entity, entityYaw, partialTicks, poseStack, buffer, packedLight, ci);
    }

}
