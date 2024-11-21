package org.jahdoo.ability.effects.custom_effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import static org.jahdoo.registers.AttachmentRegister.CASTER_DATA;

public class ReplenishManaEffect extends MobEffect {

    public ReplenishManaEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
        if(livingEntity instanceof Player player){
            var casterData = player.getData(CASTER_DATA);
            casterData.refillMana(player);
        }
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public boolean isBeneficial() {
        return true;
    }


}
