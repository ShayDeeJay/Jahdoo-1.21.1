package org.jahdoo.trial_nexus.ability.abilities_combat.ancient_golem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.ability.Ability;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import static org.jahdoo.common.registers.mod.EntityDataReg.SUMMON_ANCIENT_GOLEM;
import static org.jahdoo.trial_nexus.ability.abilities_combat.eternal_wizard.SummonEternalWizardAbility.summonMinionSound;

public class SummonAncientGolemAbility extends Ability {

    public static final ResourceLocation abilityId = JahdooHelpers.res("ancient_golem");

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.MYTHIC;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public String getDescription() {
        return "description.ability.jahdoo.test";
    }

    @Override
    public int getCastType() {
        return DISTANCE_CAST;
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
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(200)
            .setStaticCooldown(2400)
            .setDamage(40, 10, 10, 2)
            .setEffectStrength(10, 0, 2, 1)
            .setEffectDuration(600, 200, 100, 1)
            .setEffectChance(60, 20, 20, 1)
            .setCastingDistance(30, 10, 10, 2)
            .setLifetime(12000, 2400, 1200, 1)
            .buildAndReturn();
    }

    @Override
    public void invokeAbility(Player player) {
        var location = player.pick(40, 0, false).getLocation();
        var selectedAbility = SUMMON_ANCIENT_GOLEM.get().setAbilityId();
        var intern = abilityId.getPath().intern();
        var aoeCloud = new AoeCloud(player.level(), player, 0f, selectedAbility, intern);

        aoeCloud.setPos(location.x, location.y, location.z);
        player.level().addFreshEntity(aoeCloud);
        summonMinionSound(player, location);
    }

    @Override
    public int levelRequirement() {
        return 65;
    }

    @Override
    public int getAbilityCost() {
        return 16;
    }

}
