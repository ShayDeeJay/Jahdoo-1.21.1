package org.jahdoo.ascension.ability.effects;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import static org.jahdoo.ascension.utils.Helpers.sendEffectPacketsToPlayer;

public class JahdooMobEffect extends MobEffectInstance {

    public JahdooMobEffect(Holder<MobEffect> pEffect, int pDuration, int pAmplifier) {
        super(pEffect, pDuration, pAmplifier, false, false, true);
    }

    @Override
    public void onEffectAdded(LivingEntity pLivingEntity) {
        super.onEffectAdded(pLivingEntity);
    }

    @Override
    public void onEffectStarted(LivingEntity entity) {
        var level = entity.level();
        if(level instanceof ServerLevel serverLevel){
            sendEffectPacketsToPlayer(serverLevel, entity.getId(), new JahdooMobEffect(this.getEffect(), this.getDuration(), this.getAmplifier()));
        }
        super.onEffectStarted(entity);
    }

    @Override
    public boolean update(MobEffectInstance other) {
        return super.update(other);
    }


}
