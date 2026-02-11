package org.jahdoo.trial_nexus.ability.abilities_combat.eternal_wizard;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.trial_nexus.ability.Ability;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;

import static org.jahdoo.trial_nexus.utils.JahdooHelpers.res;
import static org.jahdoo.common.registers.mod.EntityDataReg.SUMMON_ETERNAL_WIZARD;

public class SummonEternalWizardAbility extends Ability {

    public static final ResourceLocation abilityId = res("eternal_wizard");

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.EPIC;
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
    public int levelRequirement() {
        return 35;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementReg.vitality();
    }

    @Override
    public void invokeAbility(Player player) {
        var location = player.pick(40, 0,false).getLocation();
        var aoeCloud = new AoeCloud(player.level(), player, 0f, SUMMON_ETERNAL_WIZARD.get().setAbilityId(), abilityId.getPath().intern());

        aoeCloud.setPos(location.x, location.y, location.z);
        summonMinionSound(player, location);
        player.level().addFreshEntity(aoeCloud);
    }

    public static void summonMinionSound(Player player, Vec3 location) {
        JahdooHelpers.getSoundWithPositionV(player.level(), player.position(), ElementReg.vitality().sound(), 1, 1f);
        JahdooHelpers.getSoundWithPositionV(player.level(), player.position(), SoundReg.TELEPORT.get(), 2, 0.8F);
    }

    @Override
    public int getAbilityCost() {
        return 8;
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(100)
            .setStaticCooldown(6000)
            .setDamage(40, 10, 10, 1)
            .setLifeLeechChance(25, 5, 5, 2)
            .setEffectStrength(4, 0, 2, 3)
            .setEffectDuration(600, 200, 100, 1)
            .setEffectChance(50, 20, 10, 2)
            .setCastingDistance(30, 10, 10, 1)
            .setLifetime(12000, 2400, 1200, 1)
            .buildAndReturn();
    }

}
