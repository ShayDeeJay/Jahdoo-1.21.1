package org.jahdoo.ascension.ability.abilities_combat.life_siphon;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.ability.abilities_combat.vital_rejuvenation.VitalRejuvenationAbility;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.registers.ElementReg;

import static org.jahdoo.common.registers.EntityDataReg.OVERCHARGED;
import static org.jahdoo.common.registers.EntityReg.VITALITY_ELEMENT_PROJECTILE;

public class LifeSiphonAbility extends Ability {

    public static final ResourceLocation abilityId = Helpers.res("life_siphon");
    public static final String HEAL_VALUE = "Heal Value";
    public static final String PULSES = "Pulse Multiplier";

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.LEGENDARY;
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
    public int getCastType() {
        return PROJECTILE_CAST;
    }

    @Override
    public int getCastDuration(Player player) {
        return 0;
    }

    @Override
    public String requiredUnlock() {
        return VitalRejuvenationAbility.abilityId.getPath();
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementReg.vitality();
    }

    @Override
    public void invokeAbility(Player player) {
        var projectile = new ElementProjectile(
            VITALITY_ELEMENT_PROJECTILE.get(), player, OVERCHARGED.get().setAbilityId(),
            offsetShoot(player), abilityId.getPath().intern()
        );
        fireProjectile(projectile, player, 0.8f);
    }

    @Override
    public int getAbilityCost() {
        return 14;
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(100)
            .setStaticCooldown(1200)
            .setDamage(20, 10, 2, 1)
            .setRange(2.5, 1.5, 0.2, 2)
            .setAbilityTagModifiersRandom(HEAL_VALUE, 1.5, 0.5, true, 0.2, 3)
            .setAbilityTagModifiersRandom(PULSES, 5,1, true, 1, 3)
            .buildAndReturn();
    }

}
