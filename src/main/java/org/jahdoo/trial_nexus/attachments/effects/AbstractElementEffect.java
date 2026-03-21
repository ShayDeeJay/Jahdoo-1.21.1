package org.jahdoo.trial_nexus.attachments.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.utils.DamageUtils;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import javax.annotation.Nullable;

import static org.jahdoo.common.registers.AttributeReg.MAGIC_DAMAGE_MULTIPLIER;

public abstract class AbstractElementEffect extends AbstractEntityEffect {

    public abstract AbstractElement getElement();

    public abstract String getName();

    public abstract void setEffect(LivingEntity applier, LivingEntity target, int time);

    public abstract void setGreaterEffect(LivingEntity applier, LivingEntity target, int time);

    @Override
    public String id() {
        return getName() + "_element";
    }

    @Override
    public AttachmentType<AbstractEntityEffect> getAttachment() {
        return getElement().attachmentType();
    }

    @Override
    public ResourceLocation icon() {
        return getElement().iconTexture();
    }

    @Override
    public void doDamage(LivingEntity target, DamageSource source, float amount) {
        if(target.level() instanceof ServerLevel level) {
            DamageUtils.damageWithJahdoo(
                target,
                getOwner(level),
                amount,
                getElement().damageTypeResourceKey()
            );
        }
    }

    @Override
    public void createEffect(@Nullable LivingEntity applier, boolean secondary, int maxTime, float damage, double secondaryValue) {
        super.createEffect(applier, secondary, maxTime, damage, secondaryValue);

        if(applier instanceof Player player) {
            this.setScaledDamage(player, damage);
        }
    }

    //Mysti Effects
    public static void applyMysticEffect(LivingEntity applier, LivingEntity target, int time) {
        mysticEffect(applier, target, time, false);
    }
    public static void applyGreaterMysticEffect(LivingEntity applier, LivingEntity target, int time) {
        mysticEffect(applier, target, time, true);
    }
    private static void mysticEffect(LivingEntity applier, LivingEntity target, int time, boolean isSecondary) {
        var attributeValue = JahdooHelpers.getAttributeValue((Player) applier, AttributeReg.MYSTIC_EFFECT_EXPLOSION_CHANCE);
        AbstractEntityEffect.setTypeEffect(MysticEffect::new, applier, target, isSecondary, time, 5, attributeValue);
    }


    //Frost Effects
    public static void applyFrostEffect(LivingEntity applier, LivingEntity target, int time) {
        frostEffect(applier, target, time, false);
    }
    public static void applyGreaterFrostEffect(LivingEntity applier, LivingEntity target, int time) {
        frostEffect(applier, target, time, true);
    }
    private static void frostEffect(LivingEntity applier, LivingEntity target, int time, boolean isSecondary) {
        var attributeValue = JahdooHelpers.getAttributeValue((Player) applier, AttributeReg.FROST_DOUBLED_DAMAGE_CHANCE);
        AbstractEntityEffect.setTypeEffect(FrostEffect::new, applier, target, isSecondary, time, 1, attributeValue);
    }


    //Inferno Effects
    public static void applyInfernoEffect(LivingEntity applier, LivingEntity target, int time) {
        infernoEffect(applier, target, time, false);
    }
    public static void applyGreaterInfernoEffect(LivingEntity applier, LivingEntity target, int time) {
        infernoEffect(applier, target, time, true);
    }
    private static void infernoEffect(LivingEntity applier, LivingEntity target, int time, boolean isSecondary) {
        var attributeValue = JahdooHelpers.getAttributeValue((Player) applier, AttributeReg.INFERNO_BURN_RADIUS);
        AbstractEntityEffect.setTypeEffect(InfernoEffect::new, applier, target, isSecondary, time, 10, attributeValue);
    }


    //Vitality Effects
    public static void applyVitalityEffect(LivingEntity applier, LivingEntity target, int time) {
        vitalityEffect(applier, target, time, false);
    }
    public static void applyGreaterVitalityEffect(LivingEntity applier, LivingEntity target, int time) {
        vitalityEffect(applier, target, time, true);
    }
    private static void vitalityEffect(LivingEntity applier, LivingEntity target, int time, boolean isSecondary) {
        var attributeValue = JahdooHelpers.getAttributeValue((Player) applier, AttributeReg.VITALITY_EFFECT_HEAL_VALUE);
        AbstractEntityEffect.setTypeEffect(VitalityEffect::new, applier, target, isSecondary, time, 1, attributeValue);
    }


    public void doDamage(LivingEntity target, ServerLevel serverLevel) {
        DamageUtils.damageWithJahdoo(
            target,
            getOwner(serverLevel),
            getDamage(),
            getElement().damageTypeResourceKey()
        );
    }

    public void doDamage(LivingEntity target, ServerLevel serverLevel, double damage) {
        DamageUtils.damageWithJahdoo(
            target,
            getOwner(serverLevel),
            damage,
            getElement().damageTypeResourceKey()
        );
    }

    protected void setScaledDamage(Player player, float damage) {
        this.setDamage(
            JahdooHelpers.attributeModifierCalculator(
                player,
                damage,
                true,
                getElement().damageAmplifier(),
                MAGIC_DAMAGE_MULTIPLIER
            )
        );
    }
}