package org.jahdoo.ascension.ability.abilities_combat.dimensional_recall;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.ability.abilities_combat.quantum_destroyer.QuantumDestroyerAbility;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.registers.ElementReg;

import static org.jahdoo.common.registers.AttachmentReg.DIMENSIONAL_RECALL;

public class DimensionalRecallAbility extends Ability {

    public static final ResourceLocation abilityId = Helpers.res("dimensional_recall");
    public static final String CASTING_TIME = "Cast Time";

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
    public boolean selfChargeAbility() {
        return true;
    }

    @Override
    public int getAbilityCost() {
        return 50;
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.ETERNAL;
    }

    @Override
    public void invokeAbility(Player player) {
        if(!(player.level() instanceof CustomLevel)){
            player.getData(DIMENSIONAL_RECALL).setStartedUsing(true);
        } else {
            player.displayClientMessage(Helpers.withStyleComponent("You can't use this here", ColourStore.NEGATIVE_RED), true);
        }
    }

    @Override
    public String requiredUnlock() {
        return QuantumDestroyerAbility.abilityId.getPath();
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(120)
            .setStaticCooldown(7200)
            .setAbilityTagModifiersRandom(CASTING_TIME, 400, 100, false, 100, 2)
            .buildAndReturn();
    }

}
