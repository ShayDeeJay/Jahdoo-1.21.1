package org.jahdoo.ability.all_abilities.abilities;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.entities.Decoy;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.ability.effects.CustomMobEffect;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.ability.AbilityBuilder;

import java.util.List;

import static org.jahdoo.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;
import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.utils.ModHelpers.Random;


public class EscapeDecoyAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = ModHelpers.res("escape_decoy");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setMana(60, 20, 10)
            .setCooldown(600, 200, 100)
            .setLifetime(300, 100, 50)
            .setEffectDuration(200, 50, 50)
            .setRange(15, 5, 2)
            .build();
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
        return ElementRegistry.VITALITY.get();
    }

    @Override
    public void invokeAbility(Player player) {
        var tagModifier = ModHelpers.getModifierValue(player.getMainHandItem().get(WAND_ABILITY_HOLDER.get()), abilityId.getPath().intern());
        var range = tagModifier.get(RANGE);
        if(range != null){
            var decoy = new Decoy(player.level(), player, (int) range.setValue());
            decoy.setMaxLifetime((int) tagModifier.get(LIFETIME).actualValue());
            var duration = (int) tagModifier.get(EFFECT_DURATION).actualValue();

            var lookVector = player.getLookAngle();
            player.addEffect(new CustomMobEffect(MobEffects.MOVEMENT_SPEED, duration, 6));
            player.addEffect(new CustomMobEffect(MobEffects.REGENERATION, duration, 0));
            player.addEffect(new CustomMobEffect(EffectsRegister.STEP_BOOST, duration, 1));

            ModHelpers.getSoundWithPositionV(player.level(), player.position(), SoundEvents.WARDEN_ATTACK_IMPACT, 1,0.6f);
            ModHelpers.getSoundWithPositionV(player.level(), player.position(), SoundEvents.CAMEL_DASH, 1,1.6f);

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
    }

    public static void onExistenceChange(LivingEntity livingEntity, AbstractElement element) {
        for (int i = 0; i < 10; i++) {
            sendParticles(livingEntity.level(), getFromAllRandom(element, 10, 1.6f), livingEntity.position().add(0, 1f, 0), 5, 0, 0.5, 0, 0.15f);
        }
    }

    public static ParticleOptions getFromAllRandom(AbstractElement element, int lifetime, float size){
        var baked = ParticleHandlers.bakedParticleOptions(element.getTypeId(), lifetime, size, false);
        var generic = ParticleHandlers.genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, element, lifetime, size);
        var magic = ParticleHandlers.genericParticleOptions(ParticleStore.MAGIC_PARTICLE_SELECTION, element, lifetime, size);
        var soft = ParticleHandlers.genericParticleOptions(ParticleStore.SOFT_PARTICLE_SELECTION, element, lifetime, size);
        var collectTypes = List.of(baked, generic, magic, soft);
        return collectTypes.get(Random.nextInt(collectTypes.size()));
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.RARE;
    }
}
