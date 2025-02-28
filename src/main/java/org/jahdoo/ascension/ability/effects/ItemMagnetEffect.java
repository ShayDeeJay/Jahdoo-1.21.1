package org.jahdoo.ascension.ability.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import org.jahdoo.common.entities.EntityMovers;

import static net.minecraft.world.effect.MobEffectCategory.BENEFICIAL;

public class ItemMagnetEffect extends MobEffect {

    public ItemMagnetEffect() {
        super(BENEFICIAL, 3436524);
    }

    @Override
    public boolean isBeneficial() {
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity targetEntity, int pAmplifier) {

        var items = targetEntity.level().getEntitiesOfClass(
            ItemEntity.class,
            targetEntity.getBoundingBox().inflate(pAmplifier),
            entity -> true
        );

        var experienceOrbs = targetEntity.level().getEntitiesOfClass(
            ExperienceOrb.class,
            targetEntity.getBoundingBox().inflate(pAmplifier),
            entity -> true
        );

        for (ItemEntity item : items) {
            EntityMovers.entityMover(targetEntity, item, pAmplifier);
        }

        for (ExperienceOrb experience : experienceOrbs) {
            EntityMovers.entityMover(targetEntity, experience, pAmplifier);
        }

        return true;
    }

}
