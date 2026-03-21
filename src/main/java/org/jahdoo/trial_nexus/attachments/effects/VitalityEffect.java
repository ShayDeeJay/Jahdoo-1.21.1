package org.jahdoo.trial_nexus.attachments.effects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.common.entities.ITamableEntity;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.element.Vitality;
import org.jahdoo.trial_nexus.magic.effects.EffectHelpers;
import org.shaydee.shaydeeapi.helpers.MathHelpers;

public class VitalityEffect extends AbstractElementEffect {

    private boolean shouldSiphon;

    @Override
    public AbstractElement getElement() {
        return ElementReg.vitality();
    }

    @Override
    public String getName() {
        return Vitality.abilityId;
    }

    @Override
    public void setEffect(LivingEntity applier, LivingEntity target, int time) {
        applyVitalityEffect(applier, target, time);
    }

    @Override
    public void setGreaterEffect(LivingEntity applier, LivingEntity target, int time) {
        applyGreaterVitalityEffect(applier, target, time);
    }

    @Override
    public void onActive(LivingEntity livingEntity) {
        if(livingEntity.level() instanceof ServerLevel serverLevel){
            shouldSiphon = MathHelpers.percentageChance(2.5);
            if(shouldSiphon) {
                var owner = getOwner(serverLevel);
                if(owner != null) owner.heal((float) getSecondaryValue());

                doDamage(livingEntity, serverLevel);
                EffectHelpers.setEffectParticle(0, livingEntity, serverLevel, ElementReg.vitality(), SoundReg.VITALITY_ABILITY.get());

                shouldSiphon = false;
            }
        }
    }

    @Override
    public void greaterEffect(LivingEntity entity) {
        if(!MathHelpers.percentageChance(2.5)) return;

        var level = entity.level();
        if(!(level instanceof ServerLevel serverLevel)) return;

        var applierMobs = serverLevel
            .getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox())
            .stream()
            .filter(s -> s instanceof ITamableEntity iT && iT.getOwner() == getOwner(serverLevel))
            .toList();

        for (var tamable : applierMobs)
            if(shouldSiphon) tamable.heal((float) getSecondaryValue());
    }

}
