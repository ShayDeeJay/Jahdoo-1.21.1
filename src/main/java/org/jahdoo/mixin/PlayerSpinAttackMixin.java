package org.jahdoo.mixin;


import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jahdoo.ability.abilities.ability_data.StormRushAbility;
import org.jahdoo.ability.effects.JahdooMobEffect;
import org.jahdoo.registers.DataComponentRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.components.DataComponentHelper.getSpecificValue;
import static org.jahdoo.registers.EffectsRegister.LIGHTNING_EFFECT;
import static org.jahdoo.utils.ModHelpers.Random;

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
            var wandAbilityHolder = this.getItemInHand(this.getUsedItemHand()).get(DataComponentRegistry.WAND_ABILITY_HOLDER);
            if(wandAbilityHolder != null){
                var ability = StormRushAbility.abilityId.getPath().intern();
                if(wandAbilityHolder.abilityProperties().containsKey(ability)){
                    var chance = getSpecificValue(ability, wandAbilityHolder, EFFECT_CHANCE);
                    var duration = getSpecificValue(ability, wandAbilityHolder, EFFECT_DURATION);
                    var strength = getSpecificValue(ability, wandAbilityHolder, EFFECT_STRENGTH);
                    if (target instanceof LivingEntity livingEntity) {
                        if (Random.nextInt(0, (int) chance) == 0) {
                            livingEntity.addEffect(new JahdooMobEffect(LIGHTNING_EFFECT, (int) duration, (int) strength));
                        }
                    }
                }
            }
        }
    }

}
