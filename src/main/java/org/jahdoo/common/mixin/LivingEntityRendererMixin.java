package org.jahdoo.common.mixin;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.trial_nexus.attachments.player_abilities.Blink;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Shadow protected EntityModel<LivingEntity> model;

    @Inject(
        method = "getShadowRadius(Lnet/minecraft/world/entity/Entity;)F",
        at = @At("HEAD"),
        cancellable = true
    )
    private void shadowCancel(Entity par1, CallbackInfoReturnable<Float> cir){
        if(par1 instanceof Player player){
            if(Blink.isActive(player)){
                cir.setReturnValue(0f);
            }
        }
    }


    @Inject(method = "getBob", at = @At("HEAD"), cancellable = true)
    private void freezeAnimation(LivingEntity livingBase, float partialTick, CallbackInfoReturnable<Float> cir) {
        if(livingBase.hasData(AttachmentReg.FROST_EFFECT)){
            cir.setReturnValue(0f);
        }
    }


}
