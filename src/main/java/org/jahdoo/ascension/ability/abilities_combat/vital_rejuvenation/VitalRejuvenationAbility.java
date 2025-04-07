package org.jahdoo.ascension.ability.abilities_combat.vital_rejuvenation;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.ability.abilities_combat.EscapeDecoyAbility;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.registers.ElementReg;

import static org.jahdoo.common.registers.AttachmentReg.VITAL_REJUVENATION;

public class VitalRejuvenationAbility extends Ability {

    public static final ResourceLocation abilityId = Helpers.res("vital_rejuvenation");
    public static final String MAX_ABSORPTION = "Max Absorption hearts";
    public static final String CAST_DELAY = "Cast Charge Delay";

    @Override
    public int getCastType() {
        return HOLD_CAST;
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
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_MINER_DESCRIPTION;
    }

    @Override
    public void invokeAbility(Player player) {
        player.getData(VITAL_REJUVENATION).setStartedUsing(true);
    }

    @Override
    public String requiredUnlock() {
        return EscapeDecoyAbility.abilityId.getPath();
    }

    @Override
    public boolean selfChargeAbility() {
        return true;
    }

    @Override
    public int getAbilityCost() {
        return 35;
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.EPIC;
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(100)
            .setAbilityTagModifiersRandom(MAX_ABSORPTION, 10, 2, true, 1, 1, 1.5)
            .setAbilityTagModifiersRandom(CAST_DELAY, 15, 5, false, 2, 1, 1.5)
            .buildAndReturn();
    }

}
