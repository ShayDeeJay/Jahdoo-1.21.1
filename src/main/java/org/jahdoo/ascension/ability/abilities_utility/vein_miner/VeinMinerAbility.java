package org.jahdoo.ascension.ability.abilities_utility.vein_miner;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.ability.AbstractBlockAbility;
import org.jahdoo.ascension.ability.abilities_utility.farmers_touch.FarmersTouchAbility;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.EntityDataReg;


public class VeinMinerAbility extends AbstractBlockAbility {

    public static final ResourceLocation abilityId = Helpers.res("vein_miner");
    public static final String VEIN_MINE_SIZE = "Total Vein Size";

    @Override
    public String projectileKey() {
        return EntityDataReg.VEIN_MINER.get().setAbilityId();
    }

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
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setMana(30, 20, 2, 1, 1.5)
            .setAbilityTagModifiersRandom(VEIN_MINE_SIZE, 256,32, true, 32, 1, 1.5)
            .buildAndReturn();
    }

    @Override
    public void invokeAbility(Player player) {
        GenericProjectile genericProjectile = new GenericProjectile(
            player, 0,
            projectileKey(),
            abilityId.getPath().intern()
        );
        fireUtilityProjectile(genericProjectile, player);
    }

    @Override
    public String requiredUnlock() {
        return FarmersTouchAbility.abilityId.getPath();
    }

    @Override
    public int getAbilityCost() {
        return 8;
    }

}
