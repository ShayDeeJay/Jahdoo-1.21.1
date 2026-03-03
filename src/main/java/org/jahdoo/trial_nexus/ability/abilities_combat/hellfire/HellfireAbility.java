package org.jahdoo.trial_nexus.ability.abilities_combat.hellfire;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.trial_nexus.ability.Ability;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.ability.abilities_combat.BurningSkullsAbility;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.registers.mod.ElementReg;

import static org.jahdoo.common.registers.mod.EntityDataReg.HELLFIRE;

public class HellfireAbility extends Ability {

    public static final ResourceLocation abilityId = JahdooHelpers.res("hellfire");

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
        return AREA_CAST;
    }

    @Override
    public int getCastDuration(Player player) {
        return 0;

    }
    @Override
    public AbstractElement getElemenType() {
        return ElementReg.inferno();
    }

    @Override
    public int levelRequirement() {
        return 35;
    }

    @Override
    public void invokeAbility(Player player) {
        AoeCloud aoeCloud = new AoeCloud(player.level(), player, 0.3f, HELLFIRE.get().setAbilityId(), abilityId.getPath().intern());
        aoeCloud.setPos(player.getX(), player.getY(), player.getZ());
        BurningSkullsAbility.infernoSoundEffect(player);
        player.level().addFreshEntity(aoeCloud);
    }

    @Override
    public int getAbilityCost() {
        return 10;
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(80)
            .setStaticCooldown(400)
            .setDamage(40, 25, 5, 2)
            .setEffectDuration(300, 100, 50, 1)
            .setEffectStrength(10, 4, 2, 2)
            .setRange(20, 10, 2, 1)
            .buildAndReturn();
    }

}
