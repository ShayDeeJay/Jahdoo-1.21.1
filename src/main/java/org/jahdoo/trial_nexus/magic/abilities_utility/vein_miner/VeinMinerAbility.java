package org.jahdoo.trial_nexus.magic.abilities_utility.vein_miner;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.trial_nexus.magic.AbilityBuilder;
import org.jahdoo.trial_nexus.magic.AbstractBlockAbility;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.EntityDataReg;


public class VeinMinerAbility extends AbstractBlockAbility {

    public static final ResourceLocation abilityId = JahdooHelpers.res("vein_miner");
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
        return "description.ability.jahdoo.test";
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
            .setStaticMana(10)
            .setAbilityTagModifiersRandom(VEIN_MINE_SIZE, 256, 32, true, 32, 2)
            .toggleVoid()
            .toggleSilk()
            .toggleSmelt()
            .toggleCollect()
            .toggleReinforced()
            .setFortune()
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
    public int levelRequirement() {
        return 25;
    }

    @Override
    public int getAbilityCost() {
        return 2;
    }

    @Override
    public boolean isInputUser() {
        return true;
    }
}
