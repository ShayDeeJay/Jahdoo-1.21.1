package org.jahdoo.trial_nexus.ability.abilities_combat.frost_spear;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.trial_nexus.ability.Ability;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.ice_spear.IceSpear;

import static org.jahdoo.trial_nexus.ability.AbilityBuilder.SHOT_MULTIPLIER;
import static org.jahdoo.trial_nexus.ability.AbilityBuilder.VELOCITY;
import static org.jahdoo.common.registers.mod.ElementReg.frost;

public class IceSpearAbility extends Ability {

    public static final ResourceLocation abilityId = JahdooHelpers.res("ice_spear");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public String getDescription() {
        return "dsd";
    }

    @Override
    public int getCastType() {
        return PROJECTILE_CAST;
    }

    @Override
    public int getCastDuration(Player player) {
        return 0;
    }

    @Override
    public AbstractElement getElemenType() {
        return frost();
    }

    @Override
    public int levelRequirement() {
        return 15;
    }

    @Override
    public int getAbilityCost() {
        return 3;
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.RARE;
    }

    @Override
    public void invokeAbility(Player player) {
        var projectileCount = CasterData.getSpecificValue(player, SHOT_MULTIPLIER);
        var velocity = CasterData.getSpecificValue(player, VELOCITY);
        var level = player.level();

        if (!level.isClientSide) {
            JahdooHelpers.getSoundWithPositionV(level, player.position(), SoundEvents.TRIDENT_THROW.value(), 1, 1.4F);
            Ability.fireMultiShotProjectile((int) projectileCount, (float) velocity, player, 0.25, () -> new IceSpear(player), 0);
        }
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(30)
            .setStaticCooldown(300)
            .setDamage(20, 10, 2, 1)
            .setEffectDuration(300, 100, 100, 2)
            .setEffectStrength(8, 2, 2, 1)
            .shotVelocity(4, 1, 1, 1)
            .setRange(5, 2, 1, 1)
            .shotMultiplier(5, 1, 1, 1)
            .buildAndReturn();
    }

}
