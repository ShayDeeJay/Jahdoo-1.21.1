package org.jahdoo.mixin;


import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.utils.ItemEntityBehaviour;
import org.jahdoo.utils.PositionGetters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import static org.jahdoo.registers.ElementRegistry.getElementByWandType;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow
    public abstract ItemStack getItem();
    @Shadow private int pickupDelay;
    @Shadow @Nullable
    private UUID target;

    @Inject(
        method = "playerTouch",
        at = @At(
            value = "INVOKE",
            target = "Lnet/neoforged/neoforge/event/EventHooks;fireItemPickupPre(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/entity/player/Player;)Lnet/neoforged/neoforge/event/entity/player/ItemEntityPickupEvent$Pre;",
            remap = false
        ),
        cancellable = true
    )

    private void onItemInteraction(Player player, CallbackInfo ci){
        ItemStack item = this.getItem();
        if(this.pickupDelay == 0 && (this.target == null || this.target.equals(player.getUUID()))){
            if (item.getItem() instanceof ItemEntityBehaviour itemEntityBehaviour) {
                if (itemEntityBehaviour.onItemInteraction((ItemEntity) ((Object) this), player)) {
                    ci.cancel();
                }
            }
        }

    }
}
