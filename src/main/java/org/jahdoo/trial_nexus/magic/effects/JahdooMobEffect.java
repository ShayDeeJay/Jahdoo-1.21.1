package org.jahdoo.trial_nexus.magic.effects;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class JahdooMobEffect extends MobEffectInstance {

    private boolean isSkill;

    public JahdooMobEffect(Holder<MobEffect> pEffect, int pDuration, int pAmplifier) {
        super(pEffect, pDuration, pAmplifier, false, false, true);
    }

    public JahdooMobEffect(Holder<MobEffect> pEffect, int pDuration, int pAmplifier, boolean isSkill) {
        super(pEffect, pDuration, pAmplifier, false, false, true);
        this.isSkill = isSkill;
    }

    public boolean isSkill() {
        return isSkill;
    }

}
