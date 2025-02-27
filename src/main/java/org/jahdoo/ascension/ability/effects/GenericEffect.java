package org.jahdoo.ascension.ability.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import static net.minecraft.world.effect.MobEffectCategory.BENEFICIAL;

public class GenericEffect extends MobEffect {

    public GenericEffect() {
        super(BENEFICIAL, 3436524);
    }

    @Override
    public boolean isBeneficial() {
        return true;
    }
}
