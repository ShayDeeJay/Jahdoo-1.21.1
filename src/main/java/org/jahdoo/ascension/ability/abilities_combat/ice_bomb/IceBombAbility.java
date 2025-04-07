package org.jahdoo.ascension.ability.abilities_combat.ice_bomb;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.ability.abilities_combat.elemental_shooter.FrostMissile;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.EntityDataReg;
import org.jahdoo.common.registers.EntityReg;

public class IceBombAbility extends Ability {

    public static final ResourceLocation abilityId = Helpers.res("ice_bomb");

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
    public AbstractElement getElemenType() {
        return ElementReg.frost();
    }

    @Override
    public String requiredUnlock() {
        return FrostMissile.abilityId.getPath();
    }

    @Override
    public void invokeAbility(Player player) {
        fireProjectile(
            new ElementProjectile(
                EntityReg.FROST_ELEMENT_PROJECTILE.get(), player,
                EntityDataReg.ICE_BOMB.get().setAbilityId(),
                offsetShoot(player),
                abilityId.getPath().intern()
            ),
            player, 0.35f
        );
    }

    @Override
    public int getAbilityCost() {
        return 5;
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(45)
            .setStaticCooldown(400)
            .setDamage(20, 10, 2, 1, 1.5)
            .setEffectStrength(10, 5, 1, 1, 1.5)
            .setEffectDuration(600,200,100, 1, 1.5)
            .buildAndReturn();
    }

}
