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
import org.jahdoo.trial_nexus.utils.DamageUtils;
import org.jahdoo.trial_nexus.utils.Icons;

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
        var level = entity.level();
        if(level instanceof ServerLevel serverLevel) {
            for (var allEntity : serverLevel.getAllEntities()) {
                if(allEntity instanceof ITamableEntity entity1){
                    if(entity1.getOwner() == this.getOwner(serverLevel)){
                        if(allEntity instanceof LivingEntity lEntity){
                            if(entity.hurtMarked){
                                lEntity.heal(22);
                            }
                        }
                    }
                }
            }
        }
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
                if(owner != null) owner.heal(1);
                DamageUtils.damageWithJahdoo(livingEntity, getOwner(serverLevel), 1, ElementReg.vitality().damageTypeResourceKey());
            }
            EffectHelpers.setEffectParticle(getRandomChance, livingEntity, serverLevel, ElementReg.vitality(), SoundReg.VITALITY_ABILITY.get());
        }
    }

}
