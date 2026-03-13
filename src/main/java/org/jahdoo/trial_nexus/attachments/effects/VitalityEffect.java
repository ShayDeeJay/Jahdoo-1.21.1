package org.jahdoo.trial_nexus.attachments.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jahdoo.common.entities.ITamableEntity;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.magic.effects.EffectHelpers;
import org.jahdoo.trial_nexus.utils.Icons;
import org.shaydee.shaydeeapi.helpers.MathHelpers;

import static org.jahdoo.trial_nexus.magic.effects.EffectHelpers.getGetRandomChance;

public class VitalityEffect extends AbstractEntityEffect {

    public static final String VITALITY_EFFECT = "vitality_effect";

    @Override
    public String id() {
        return VITALITY_EFFECT;
    }

    @Override
    public AbstractElement getElement() {
        return ElementReg.vitality();
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
            if(entity.hurtMarked) tamable.heal(0.5F);
    }

    @Override
    public AttachmentType<AbstractEntityEffect> getAttachment() {
        return AttachmentReg.VITALITY_EFFECT.get();
    }

    @Override
    public ResourceLocation icon() {
        return Icons.VITALITY_ICON;
    }

    @Override
    public void onStarted(LivingEntity livingEntity) {
        super.onStarted(livingEntity);
    }

    @Override
    public void onActive(LivingEntity livingEntity) {
        if(livingEntity.level() instanceof ServerLevel serverLevel){
            int getRandomChance = getGetRandomChance(4);
            if(getRandomChance == 0) {
                var owner = getOwner(serverLevel);
                if(owner != null) owner.heal(0.5F);
                doDamage(livingEntity, serverLevel);
            }
            EffectHelpers.setEffectParticle(getRandomChance, livingEntity, serverLevel, ElementReg.vitality(), SoundReg.VITALITY_ABILITY.get());
        }
    }

}
