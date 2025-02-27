package org.jahdoo.ascension.ability.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.entities.decoy.Decoy;
import org.jahdoo.common.registers.EffectsRegister;
import org.jahdoo.common.registers.ElementRegistry;
import org.jahdoo.ascension.ability.effects.JahdooMobEffect;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.ability.AbilityBuilder;

import static org.jahdoo.common.particle.ParticleHandlers.getAllParticleTypes;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.ascension.ability.AbilityBuilder.*;


public class EscapeDecoyAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = Helpers.res("escape_decoy");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setStaticMana(50)
            .setStaticCooldown(1200)
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
        return ElementRegistry.utility();
    }

    @Override
    public void invokeAbility(Player player) {
        var tagModifier = Helpers.getModifierValue(WandAbilityHolder.getHolderFromWand(player), abilityId.getPath().intern());
        var range = tagModifier.get(RANGE);
        if(range != null){
            var decoy = new Decoy(player.level(), player, (int) range.setValue());
            decoy.setMaxLifetime((int) tagModifier.get(LIFETIME).actualValue());
            var duration = (int) tagModifier.get(EFFECT_DURATION).actualValue();

            var lookVector = player.getLookAngle();
            player.addEffect(new JahdooMobEffect(MobEffects.MOVEMENT_SPEED, duration, 6));
            player.addEffect(new JahdooMobEffect(MobEffects.REGENERATION, duration, 0));
            player.addEffect(new JahdooMobEffect(EffectsRegister.STEP_BOOST, duration, 1));

            Helpers.getSoundWithPositionV(player.level(), player.position(), SoundEvents.WARDEN_ATTACK_IMPACT, 1,0.6f);
            Helpers.getSoundWithPositionV(player.level(), player.position(), SoundEvents.CAMEL_DASH, 1,1.6f);

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
            sendParticles(livingEntity.level(), getAllParticleTypes(element, 10, 1.6f), livingEntity.position().add(0, 1f, 0), 5, 0, 0.5, 0, 0.15f);
        }
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.RARE;
    }
}
