package org.jahdoo.ascension.ability.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import static net.minecraft.world.effect.MobEffectCategory.BENEFICIAL;
import static org.jahdoo.common.registers.AttachmentReg.CASTER_DATA;

public class ReplenishManaEffect extends MobEffect {

    public ReplenishManaEffect() {
        super(BENEFICIAL, 3436524);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public boolean isBeneficial() {
        return true;
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

}
