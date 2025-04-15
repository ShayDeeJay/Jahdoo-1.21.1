package org.jahdoo.common.mixin;


import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jahdoo.ascension.ability.effects.JahdooMobEffect;
import org.jahdoo.ascension.attachments.CasterData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.common.registers.EffectReg.FROST_EFFECT;

@Mixin(Player.class)
public abstract class PlayerSpinAttackMixin extends LivingEntity {

    @Shadow public abstract void playSound(SoundEvent sound, float volume, float pitch);
    protected PlayerSpinAttackMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
        method = "attack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;isAutoSpinAttack()Z",
            remap = false
        )
    )
    private void attackEvent(Entity target, CallbackInfo ci){
        if(this.isAutoSpinAttack()){
            var holder = CasterData.entityHolderWithSelected(this);
            var chance = CasterData.getSpecificValue(holder, EFFECT_CHANCE);
            var duration = CasterData.getSpecificValue(holder, EFFECT_DURATION);
            var strength = CasterData.getSpecificValue(holder, EFFECT_STRENGTH);
            if (target instanceof LivingEntity livingEntity) {
                if (Random.nextInt(0, (int) chance) == 0) {
                    var effect = new JahdooMobEffect(FROST_EFFECT, (int) duration, (int) strength);
                    livingEntity.addEffect(effect);
                }
            }
        }
    }

}
