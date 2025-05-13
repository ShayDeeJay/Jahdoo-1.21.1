package org.jahdoo.trial_nexus.ability.abilities_combat.nova_smash;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.trial_nexus.ability.Ability;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.GlobalStrings;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.registers.mod.ElementReg;

import static org.jahdoo.common.items.caster_item.CastHelper.chargeManaAndCooldown;
import static org.jahdoo.common.registers.AttachmentReg.NOVA_SMASH;

public class NovaSmashAbility extends Ability {

    public static final ResourceLocation abilityId = Helpers.res("nova_smash");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.RARE;
    }

    @Override
    public boolean selfChargeAbility() {
        return true;
    }

    @Override
    public int getAbilityCost() {
        return 6;
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
    public int levelRequirement() {
        return 15;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementReg.mystic();
    }

    @Override
    public void invokeAbility(Player player) {
        if(!player.onGround()) {
            var novaSmash = player.getData(NOVA_SMASH);
            novaSmash.setCanSmash(true);
            chargeManaAndCooldown(abilityId.getPath().intern(), player);
        }
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(40)
            .setStaticCooldown(400)
            .setDamage(12, 6, 2, 1)
            .buildAndReturn();
    }

}
