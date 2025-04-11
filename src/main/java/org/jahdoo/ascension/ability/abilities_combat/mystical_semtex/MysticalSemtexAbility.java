package org.jahdoo.ascension.ability.abilities_combat.mystical_semtex;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.ability.abilities_combat.nova_smash.NovaSmashAbility;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.EntityDataReg;
import org.jahdoo.common.registers.EntityReg;

public class MysticalSemtexAbility extends Ability {

    public static final ResourceLocation abilityId = Helpers.res("mystical_semtex");
    public static final String ADDITIONAL_PROJECTILE = "Additional Projectiles";
    public static final String EXPLOSION_DELAYS = "Explosion Delay";
    public static final String CLUSTER_CHANCE = "Cluster Chance";
    public static final String EXPLOSION_RADIUS = "Explosion Radius";

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.EPIC;
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
        return ElementReg.mystic();
    }

    @Override
    public String requiredUnlock() {
        return NovaSmashAbility.abilityId.getPath();
    }

    @Override
    public void invokeAbility(Player player) {
        var elementProjectile = new ElementProjectile(
            EntityReg.MYSTIC_ELEMENT_PROJECTILE.get(),
            player,
            EntityDataReg.MYSTICAL_SEMTEX.get().setAbilityId(),
            offsetShoot(player),
            abilityId.getPath().intern()
        );
        elementProjectile.setPredicate(1);
        fireProjectile(elementProjectile, player, 0.8f);
    }

    @Override
    public int getAbilityCost() {
        return 8;
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(60)
            .setStaticCooldown(500)
            .setDamage(45, 25, 5, 1)
            .setAbilityTagModifiersRandom(ADDITIONAL_PROJECTILE, 10,4, true, 1, 1)
            .setAbilityTagModifiersRandom(EXPLOSION_DELAYS, 50,20, false, 5, 1)
            .setAbilityTagModifiersRandom(CLUSTER_CHANCE, 10,1, false, 1, 1)
            .setAbilityTagModifiersRandom(EXPLOSION_RADIUS, 8,3, true, 1, 1)
            .buildAndReturn();
    }

}
