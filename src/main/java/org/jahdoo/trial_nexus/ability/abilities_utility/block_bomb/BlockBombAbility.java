package org.jahdoo.trial_nexus.ability.abilities_utility.block_bomb;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.ability.AbstractBlockAbility;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.GlobalStrings;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.EntityDataReg;

public class BlockBombAbility extends AbstractBlockAbility {

    public static final ResourceLocation abilityId = JahdooHelpers.res("block_bomb");
    public static final String EXPLOSION_RANGE = "Explosion Radius";
    public static final String BLOCK_DROP_CHANCE = "Block Drop Chance";

    @Override
    public String projectileKey() {
        return EntityDataReg.BLOCK_EXPLODER.get().setAbilityId();
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.MYTHIC;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_PLACER;
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
        return ElementReg.utility();
    }

    @Override
    public void invokeAbility(Player player) {
        var genericProjectile = new GenericProjectile(
            player, 0,
            projectileKey(),
            abilityId.getPath().intern()
        );
        fireUtilityProjectile(genericProjectile, player);
    }

    @Override
    public int getAbilityCost() {
        return 10;
    }

    @Override
    public int levelRequirement() {
        return 35;
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(20)
            .setStaticCooldown(400)
            .setAbilityTagModifiersRandom(EXPLOSION_RANGE, 30, 10, true, 5, 3)
//            .setAbilityTagModifiersRandom(BLOCK_DROP_CHANCE, 100, 40, false, 20, 3)
            .buildAndReturn();
    }

}
