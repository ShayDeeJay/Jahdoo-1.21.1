package org.jahdoo.trial_nexus.ability.abilities_combat.mystical_semtex;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.trial_nexus.ability.Ability;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.ability.abilities_combat.BurningSkullsAbility;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.GlobalStrings;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.registers.EntityReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.EntityDataReg;

public class MysticalSemtexAbility extends Ability {

    public static final ResourceLocation abilityId = Helpers.res("mystical_semtex");
    public static final String CLUSTER_COUNT = "Cluster Count";
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
    public int levelRequirement() {
        return 30;
    }

    @Override
    public void invokeAbility(Player player) {
        var elementProjectile = new ElementProjectile(
            EntityReg.MYSTIC_ELEMENT_PROJECTILE.get(),
            player,
            EntityDataReg.MYSTICAL_SEMTEX.get().setAbilityId(),
            0,
            abilityId.getPath().intern()
        );
        elementProjectile.setPredicate(1);
        fireProjectileNoSound(elementProjectile, player, 0.8f);
        BurningSkullsAbility.mysticSoundEffect(player);
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
            .setAbilityTagModifiersRandom(CLUSTER_COUNT, 10, 4, true, 2, 2)
            .setAbilityTagModifiersRandom(CLUSTER_CHANCE, 10, 2, false, 2, 2)
            .setAbilityTagModifiersRandom(EXPLOSION_RADIUS, 8, 4, true, 1, 2)
            .buildAndReturn();
    }

}
