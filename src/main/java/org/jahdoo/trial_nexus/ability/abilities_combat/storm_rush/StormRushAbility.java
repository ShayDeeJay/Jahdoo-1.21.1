package org.jahdoo.trial_nexus.ability.abilities_combat.storm_rush;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.trial_nexus.ability.Ability;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.GlobalStrings;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.registers.mod.ElementReg;

public class StormRushAbility extends Ability {

    public static final ResourceLocation abilityId = JahdooHelpers.res("storm_rush");
    public static final String LAUNCH_DISTANCE = "Launch Distance";

    @Override
    public void invokeAbility(Player player) {
        new StormRush(player).launchPlayerDirection();
    }

    @Override
    public int getAbilityCost() {
        return 4;
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.RARE;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_PLACER;
    }

    @Override
    public int levelRequirement() {
        return 20;
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
        return ElementReg.frost();
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(30)
            .setStaticCooldown(300)
            .setEffectChance(10, 2, 2, 2)
            .setEffectDuration(300, 100, 50, 1)
            .setEffectStrength(4, 0, 1, 1)
            .setAbilityTagModifiersRandom(LAUNCH_DISTANCE, 3, 1, true, 1, 2)
            .buildAndReturn();
    }

}
