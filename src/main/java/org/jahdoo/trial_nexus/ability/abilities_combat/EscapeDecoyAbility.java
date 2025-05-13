package org.jahdoo.trial_nexus.ability.abilities_combat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.trial_nexus.ability.Ability;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.ability.effects.JahdooMobEffect;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.GlobalStrings;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.decoy.Decoy;
import org.jahdoo.common.registers.EffectReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;

import static org.jahdoo.trial_nexus.ability.AbilityBuilder.*;
import static org.jahdoo.common.particle.ParticleHandlers.getAllParticleTypes;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;


public class EscapeDecoyAbility extends Ability {
    public static final ResourceLocation abilityId = Helpers.res("escape_decoy");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_PLACER;
    }

    @Override
    public int getCastType() {
        return AREA_CAST;
    }

    @Override
    public int getCastDuration(Player player) {
        return 0;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementReg.vitality();
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.RARE;
    }

    public static void onExistenceChange(LivingEntity livingEntity, AbstractElement element) {
        for (int i = 0; i < 10; i++) {
            sendParticles(livingEntity.level(), getAllParticleTypes(element, 10, 1.6f), livingEntity.position().add(0, 1f, 0), 5, 0, 0.5, 0, 0.15f);
        }
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(50)
            .setStaticCooldown(1200)
            .setLifetime(300, 100, 50, 1)
            .setEffectDuration(200, 50, 50, 1)
            .setRange(25, 5, 5, 2)
            .buildAndReturn();
    }

    @Override
    public int levelRequirement() {
        return 15;
    }

    public static double getTag(Player player, String id){
        return CasterData.entityHolderWithSelected(player).data().abilityProperties().get(id).setValue();
    }

    @Override
    public void invokeAbility(Player player) {
        var range = getTag(player, RANGE);
        var decoy = new Decoy(player.level(), player, (int) range);
        var duration = (int) getTag(player, EFFECT_DURATION);
        var lookVector = player.getLookAngle();

        decoy.setMaxLifetime((int) getTag(player, LIFETIME));
        player.addEffect(new JahdooMobEffect(MobEffects.MOVEMENT_SPEED, duration, 6));
        player.addEffect(new JahdooMobEffect(MobEffects.REGENERATION, duration, 0));
        player.addEffect(new JahdooMobEffect(EffectReg.CLIMBER, duration, 1));

        Helpers.getSoundWithPositionV(player.level(), player.position(), getElemenType().sound(), 1, 0.8f);
        Helpers.getSoundWithPositionV(player.level(), player.position(), SoundReg.TELEPORT.get(), 2, 0.6F);

        var yaw = Math.toDegrees(Math.atan2(lookVector.z, lookVector.x)) + 270.0;
        decoy.setYRot((float) yaw);
        decoy.setYHeadRot((float) yaw);
        decoy.setYBodyRot((float) yaw);
        decoy.setPos(player.getX(), player.getY(), player.getY());
        decoy.yRotO = (float) yaw;
        decoy.yHeadRotO = (float) yaw;
        decoy.setNoAi(true);
        decoy.moveTo(player.position());
        player.level().addFreshEntity(decoy);

        onExistenceChange(decoy, getElemenType());
    }

    @Override
    public int getAbilityCost() {
        return 8;
    }
}
