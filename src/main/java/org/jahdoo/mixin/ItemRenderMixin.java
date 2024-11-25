package org.jahdoo.mixin;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.items.augments.Augment;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.ItemEntityBehaviour;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ItemRenderer.class)
public abstract class ItemRenderMixin {

    @Shadow public abstract void renderStatic(ItemStack stack, ItemDisplayContext displayContext, int combinedLight, int combinedOverlay, PoseStack poseStack, MultiBufferSource bufferSource, @Nullable Level level, int seed);

    @Shadow @Final private Minecraft minecraft;

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
            shift = At.Shift.BEFORE
        )
    )
    public void overlayStuff(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel p_model, CallbackInfo ci){
        ModHelpers.itemOverlay(itemStack, displayContext,poseStack,
            (itemStack1) -> this.renderStatic(itemStack1, displayContext, combinedLight, combinedOverlay, poseStack, bufferSource, Minecraft.getInstance().level, 0)
        );
    }

}
