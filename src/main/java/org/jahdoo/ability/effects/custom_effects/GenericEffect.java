package org.jahdoo.ability.effects.custom_effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class GenericEffect extends MobEffect {

    public GenericEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean isBeneficial() {
        return true;
    }
}
