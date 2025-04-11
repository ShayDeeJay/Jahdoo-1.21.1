package org.jahdoo.ascension.ability.abilities_combat.arcane_shift;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.ability.abilities_combat.elemental_shooter.MysticMissile;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.registers.mod.ElementReg;

public class ArcaneShiftAbility extends Ability {

    public static final ResourceLocation abilityId = Helpers.res("arcane_shift");
    public static final String distance = "Teleport Distance";

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
        return ElementReg.mystic();
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_MINER_DESCRIPTION;
    }

    @Override
    public void invokeAbility(Player player) {
        new ArcaneShift(player).shift();
    }

    @Override
    public String requiredUnlock() {
        return MysticMissile.abilityId.getPath();
    }

    @Override
    public int getAbilityCost() {
        return 5;
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.RARE;
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(80)
            .setStaticCooldown(800)
            .setAoe(10, 3, 1, 2)
            .setEffectDuration(200, 50, 50, 1)
            .setCastingDistance(50, 25, 5, 1)
            .buildAndReturn();
    }

}
