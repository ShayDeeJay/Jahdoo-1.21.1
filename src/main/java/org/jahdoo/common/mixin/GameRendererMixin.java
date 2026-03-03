package org.jahdoo.common.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import org.jahdoo.trial_nexus.attachments.player_abilities.Blink;
import org.shaydee.shaydeeapi.helpers.ClientHelpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(
        method = "bobView",
        at = @At("HEAD"),
        cancellable = true
    )
    private void disableViewBobbing(PoseStack matrices, float tickDelta, CallbackInfo ci) {
        var mc = ClientHelpers.getMinecraft();
        var player = mc.player;
        if(player == null) return;

        var active = Blink.isActive(player);

        if(active) ci.cancel();
    }

}
