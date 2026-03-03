package org.jahdoo.common.mixin;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.trial_nexus.attachments.player_abilities.Blink;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

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

}
