package org.jahdoo.common.mixin;

import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.items.JahdooItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Redirect(
        method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"
        )
    )
    private void redirectShrink(ItemStack instance, int count) {
        if (instance.getItem() instanceof JahdooItem) return;
        instance.shrink(count);
    }

}