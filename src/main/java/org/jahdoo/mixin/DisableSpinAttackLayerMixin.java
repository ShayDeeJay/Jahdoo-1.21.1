package org.jahdoo.mixin;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.items.wand.WandItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpinAttackEffectLayer.class)
public abstract class DisableSpinAttackLayerMixin {
    @Inject(
        method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
            remap = false
        ),
        cancellable = true
    )
    private void cancelRender(PoseStack poseStack, MultiBufferSource buffer, int packedLight, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci){
        if(livingEntity.getItemInHand(livingEntity.getUsedItemHand()).getItem() instanceof WandItem){
            ci.cancel();
        }
    }
}
