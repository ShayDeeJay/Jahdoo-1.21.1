//package org.jahdoo.mixin;
//
//import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
//import net.minecraft.core.Vec3i;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.Mob;
//import net.minecraft.world.entity.item.ItemEntity;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import org.jahdoo.utils.ItemEntityBehaviour;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(Mob.class)
//public abstract class MobMixin extends LivingEntity {
//    @Shadow
//    protected abstract Vec3i getPickupReach();
//
//    protected MobMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
//        super(pEntityType, pLevel);
//    }
//
//    @Inject(
//        method = "aiStep",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/world/entity/Mob;getPickupReach()Lnet/minecraft/core/Vec3i;",
//            shift = At.Shift.AFTER
//        ),
//        cancellable = true
//    )
//    private void MobPickup(CallbackInfo ci){
//        if(!this.level().isClientSide){
//            Vec3i reach = this.getPickupReach();
//            for(ItemEntity itementity : this.level().getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(reach.getX(), reach.getY(), reach.getZ()))) {
//                if (!itementity.isRemoved() && !itementity.getItem().isEmpty() && !itementity.hasPickUpDelay()) {
//                    ItemStack itemStack = itementity.getItem();
//                    if(itemStack.getEntityLifespan(this.level()) > 50){
//                        if (itemStack.getItem() instanceof ItemEntityBehaviour itemEntityBehaviour) {
//                            if (itemEntityBehaviour.onItemInteraction(itementity, this)) {
//                                ci.cancel();
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        if (ci.isCancelled()) this.level().getProfiler().pop();
//    }
//
//}
