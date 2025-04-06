package org.jahdoo.ascension.ability.abilities_combat.frostbolts;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.registers.EntityDataReg;

import static org.jahdoo.ascension.utils.GlobalStrings.BLOCK_MINER_DESCRIPTION;
import static org.jahdoo.common.registers.ElementReg.frost;

public class FrostboltsAbility extends Ability {

    public static final ResourceLocation abilityId = Helpers.res("frostbolts");
    public static final String NUMBER_OF_PROJECTILES = "Total Arrows";

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public String getDescription() {
        return BLOCK_MINER_DESCRIPTION;
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
        return frost();
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
    public JahdooRarity rarity() {
        return JahdooRarity.EPIC;
    }

    @Override
    public void invokeAbility(Player player) {
        if(player != null){
            var projSelect = EntityDataReg.FROST_BOLT.get().setAbilityId();
            var id = abilityId.getPath().intern();
            var elementProjectile = new GenericProjectile(player, 0, projSelect, id, this.getElemenType());

            elementProjectile.setOwner(player);
            elementProjectile.setInvisible(true);
            fireProjectileNoSound(elementProjectile, player, 100f);
            player.level().addFreshEntity(elementProjectile);
        }
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(60)
            .setStaticCooldown(600)
            .setDamage(25, 15, 2, 1, 1.5)
            .setAbilityTagModifiersRandom(NUMBER_OF_PROJECTILES, 30,10, true, 5, 1, 1.5)
            .setEffectDuration(300, 100, 20, 1, 1.5)
            .setEffectStrength(10, 0, 1, 1, 1.5)
            .setEffectChance(40, 5, 5, 1, 1.5)
            .setCastingDistance(30, 5, 5, 1, 1.5)
            .buildAndReturn();
    }

}
