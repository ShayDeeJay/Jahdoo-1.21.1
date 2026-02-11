package org.jahdoo.trial_nexus.ability.abilities_combat.frostbolts;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.trial_nexus.ability.Ability;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.registers.mod.EntityDataReg;
import static org.jahdoo.common.registers.mod.ElementReg.frost;

public class FrostboltsAbility extends Ability {

    public static final ResourceLocation abilityId = JahdooHelpers.res("frostbolts");
    public static final String NUMBER_OF_PROJECTILES = "Total Arrows";

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public String getDescription() {
        return "dsd";
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
    public int levelRequirement() {
        return 30;
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
            var level = player.level();

            elementProjectile.setOwner(player);
            elementProjectile.setInvisible(true);
            fireProjectileNoSound(elementProjectile, player, 100f);
            level.addFreshEntity(elementProjectile);
        }
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(60)
            .setStaticCooldown(600)
            .setDamage(35, 15, 5, 1)
            .shotMultiplier(30, 10, 5, 1)
            .setEffectDuration(300, 100, 100, 2)
            .setEffectStrength(10, 0, 2, 1)
            .setEffectChance(40, 5, 5, 2)
            .setCastingDistance(30, 5, 5, 2)
            .buildAndReturn();
    }

}
