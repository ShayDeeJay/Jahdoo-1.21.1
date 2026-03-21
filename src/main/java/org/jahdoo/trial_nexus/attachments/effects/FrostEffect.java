package org.jahdoo.trial_nexus.attachments.effects;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.element.Frost;
import org.jahdoo.trial_nexus.magic.effects.EffectHelpers;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED;

public class FrostEffect extends AbstractElementEffect {

    @Override
    public AbstractElement getElement() {
        return ElementReg.frost();
    }

    @Override
    public String getName() {
        return Frost.abilityId;
    }

    @Override
    public void setEffect(LivingEntity applier, LivingEntity target, int time) {
        applyFrostEffect(applier, target, time);
    }

    @Override
    public void setGreaterEffect(LivingEntity applier, LivingEntity target, int time) {
        applyGreaterFrostEffect(applier, target, time);
    }

    @Override
    public void onStarted(LivingEntity livingEntity) {
        livingEntity.playSound(SoundReg.FROST_ABILITY.get());
        if(isSecondary()){
            if(livingEntity instanceof Mob mob) mob.setNoAi(true);
        } else {
            JahdooHelpers.addTransientAttribute(livingEntity, -(livingEntity.getSpeed()/1.5), "slow_entity", MOVEMENT_SPEED);
        }
    }

    @Override
    public void onEnd(LivingEntity livingEntity) {
        if(isSecondary()){
            if (livingEntity instanceof Mob mob) mob.setNoAi(false);
        } else {
            var attributes = livingEntity.getAttributes();
            var multiMap = getHolderAttributeModifierMultimap();
            attributes.removeAttributeModifiers(multiMap);
        }
    }

    @Override
    public void onActive(LivingEntity livingEntity) {
        int getRandomChance = JahdooHelpers.Random.nextInt(0,20);
        if(livingEntity.level() instanceof  ServerLevel serverLevel){
            EffectHelpers.setEffectParticle(getRandomChance, livingEntity, serverLevel, ElementReg.frost(), SoundReg.FROST_ABILITY.get());
        }
    }

    private static @NotNull Multimap<Holder<Attribute>, AttributeModifier> getHolderAttributeModifierMultimap() {
        Multimap<Holder<Attribute>, AttributeModifier> multiMap = HashMultimap.create();
        var modifier = new AttributeModifier(JahdooHelpers.res("slow_entity"), 0, AttributeModifier.Operation.ADD_VALUE);
        multiMap.put(MOVEMENT_SPEED, modifier);
        return multiMap;
    }


}
