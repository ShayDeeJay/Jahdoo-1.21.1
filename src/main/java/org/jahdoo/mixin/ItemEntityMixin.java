package org.jahdoo.mixin;


import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.items.CoinItem;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ItemEntityBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.UUID;

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

    @Unique
    private boolean lootBeams$hasPlayedSound = false;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        if (!lootBeams$hasPlayedSound && (itemEntity.onGround() || (itemEntity.onGround() && (itemEntity.tickCount < 10 && itemEntity.tickCount > 3)))) {
            if (itemEntity.getItem().getItem() instanceof CoinItem) itemEntity.playSound(SoundRegister.COIN.get());
            lootBeams$hasPlayedSound = true;
        }

        if(lootBeams$hasPlayedSound && !itemEntity.onGround()){
            lootBeams$hasPlayedSound = false;
        }
    }
}
