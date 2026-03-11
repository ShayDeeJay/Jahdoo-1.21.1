package org.jahdoo.trial_nexus.attachments.effects;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED;

public class FrostEffect extends AbstractEntityEffect {
    public static final String FROST_EFFECT = "frost_effect";

    @Override
    public String id() {
        return FROST_EFFECT;
    }

    @Override
    public AbstractElement getElement() {
        return ElementReg.frost();
    }

    @Override
    public void greaterEffect(LivingEntity entity) {
    }

    @Override
    public AttachmentType<AbstractEntityEffect> getAttachment() {
        return AttachmentReg.FROST_EFFECT.get();
    }

    @Override
    public ResourceLocation icon() {
        return Icons.FROST_ICON;
    }

    @Override
    public void onStarted(LivingEntity livingEntity) {
        super.onStarted(livingEntity);
        livingEntity.playSound(SoundReg.FROST_ABILITY.get());
        JahdooHelpers.addTransientAttribute(livingEntity, -(livingEntity.getSpeed()/4), "slow_entity", MOVEMENT_SPEED);
        if(isSecondary()){
            if(livingEntity instanceof Mob mob){


            }
        }
    }

    @Override
    public void onEnd(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        var multiMap = getHolderAttributeModifierMultimap();
        attributes.removeAttributeModifiers(multiMap);
        if(livingEntity instanceof Mob mob){
            mob.setNoAi(false);
        }
    }

    @Override
    public void onActive(LivingEntity livingEntity) {
        if(isSecondary()){
           if(livingEntity instanceof Mob mob){
           }
        }
    }

    private static @NotNull Multimap<Holder<Attribute>, AttributeModifier> getHolderAttributeModifierMultimap() {
        Multimap<Holder<Attribute>, AttributeModifier> multiMap = HashMultimap.create();
        var modifier = new AttributeModifier(JahdooHelpers.res("slow_entity"), 0, AttributeModifier.Operation.ADD_VALUE);
        multiMap.put(MOVEMENT_SPEED, modifier);
        return multiMap;
    }
}
