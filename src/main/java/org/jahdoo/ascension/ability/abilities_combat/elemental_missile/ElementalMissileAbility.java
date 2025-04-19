package org.jahdoo.ascension.ability.abilities_combat.elemental_missile;

import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.attachments.CasterData;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.registers.SoundReg;

import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.common.registers.mod.EntityDataReg.ELEMENTAL_SHOOTER;

abstract public class ElementalMissileAbility extends Ability {

    @Override
    public int levelRequirement() {
        return 0;
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
        return null;
    }


    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.COMMON;
    }

    @Override
    public boolean isMultiType() {
        return true;
    }

    @Override
    public int getAbilityCost() {
        return 1;
    }

    protected double getTag(Player player, String name){
        var holder = CasterData.entityHolderWithSelected(player);
        return holder.data().abilityProperties().get(name).setValue();
    }

    protected void doOnCast(Player player, String name) {
        var projectileCount = getTag(player, SHOT_MULTIPLIER);
        var index = ELEMENTAL_SHOOTER.get().setAbilityId();

        fireMultiShotProjectile((int) projectileCount , 1.2f, player, 0.1, () -> new GenericProjectile(player, 0, index, name));
        Helpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundReg.ELEMENTAL_BULLET.get(), 0.8F, 1.2F);
    }

    protected AbilityHolder getWithElement(int id, String name){
        return new AbilityBuilder(name)
            .setStaticMana(15)
            .setStaticCooldown(0)
            .setDamage(20, 10, 2, 1)
            .setEffectChance(50, 10, 10, 2)
            .setEffectStrength(10, 1, 1, 1)
            .setEffectDuration(300, 100, 50, 1)
            .setAbilityTagModifiersRandom(SHOT_MULTIPLIER, 3, 1, true, 1, 3)
            .setAbilityTagModifiersRandom(NUMBER_OF_RICOCHET, 6, 1, true, 1, 1)
            .setModifier(SET_ELEMENT_TYPE, 0, 0, false, id)
            .buildAndReturn();
    }
    

}
