package org.jahdoo.trial_nexus.ability.abilities_combat.dimensional_recall;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.trial_nexus.ability.Ability;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.GlobalStrings;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.registers.mod.ElementReg;

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
    public int levelRequirement() {
        return 100;
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
