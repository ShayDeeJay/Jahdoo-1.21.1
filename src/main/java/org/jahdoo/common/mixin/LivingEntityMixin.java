package org.jahdoo.common.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.level.Level;
import org.jahdoo.common.entities.ITamableEntity;
import org.jahdoo.common.registers.AttachmentReg;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.jahdoo.common.registers.mod.ElementReg.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Shadow public abstract void indicateDamage(double xDistance, double zDistance);

    @Shadow private float speed;

    @Shadow @Final public WalkAnimationState walkAnimation;

    @Shadow protected int lerpSteps;

    @Shadow public float xxa;

    @Shadow public float yya;

    @Shadow public float zza;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public int getTeamColor() {
        int color = super.getTeamColor();

        if (this.hasData(AttachmentReg.MYSTIC_EFFECT)) {
            color = mystic().textColourA();
        } else if (this.hasData(AttachmentReg.INFERNO_EFFECT)) {
            color = inferno().textColourA();
        } else if (this.hasData(AttachmentReg.FROST_EFFECT)) {
            color = frost().textColourA();
        } else if (this.hasData(AttachmentReg.VITALITY_EFFECT)) {
            color = vitality().textColourA();
        }

        return color;
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if(this instanceof ITamableEntity entity){
            if(entity.getOwner() != null && source.getEntity() == entity.getOwner()) {
                cir.setReturnValue(false);
            }

            if(source.getEntity() instanceof ITamableEntity iTamableEntity && iTamableEntity.getOwner() != null && entity.getOwner() != null){
                cir.setReturnValue(false);
            }
        }

        if (source.getEntity() instanceof ITamableEntity entity && entity.getOwnerUUIDOptional().isPresent() && entity.getOwnerUUIDOptional().get().equals(this.getUUID())) {
            cir.setReturnValue(false);
        }
    }

}
