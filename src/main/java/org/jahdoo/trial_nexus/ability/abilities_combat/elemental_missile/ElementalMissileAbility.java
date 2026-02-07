package org.jahdoo.trial_nexus.ability.abilities_combat.elemental_missile;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.ability.Ability;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.GlobalStrings;
import org.shaydee.shaydeeapi.Helpers;

import static org.jahdoo.common.registers.mod.EntityDataReg.ELEMENTAL_SHOOTER;
import static org.jahdoo.trial_nexus.ability.AbilityBuilder.SET_ELEMENT_TYPE;
import static org.jahdoo.trial_nexus.ability.AbilityBuilder.SHOT_MULTIPLIER;

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
        var extraShots = player.getAttribute(AttributeReg.ELEMENTAL_SHOTGUN);
        var shotMulti = extraShots != null ? extraShots.getValue() : 0;
        var projectileCount = getTag(player, SHOT_MULTIPLIER) + shotMulti;
        var index = ELEMENTAL_SHOOTER.get().setAbilityId();
        var isAlt = ElementalMissile.altOnHitCheck(player);

        fireMultiShotProjectile((int) projectileCount, 1.2f, player, 0.1, () -> new GenericProjectile(player, 0, index, name), isAlt ? 15 : 0);
        Helpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundReg.ELEMENTAL_BULLET.get(), SoundSource.NEUTRAL, 0.8F, 1.2F);
    }

    protected AbilityHolder getWithElement(int id, String name){
        return new AbilityBuilder(name)
            .setStaticMana(20)
            .setStaticCooldown(0)
            .setDamage(20, 10, 2.5, 1)
            .setEffectChance(50, 10, 20, 2)
            .setEffectStrength(4, 0, 2, 2)
            .setEffectDuration(300, 100, 50, 1)
            .shotMultiplier(3, 1, 1, 3)
            .ricochets(6, 0, 2, 2)
            .setModifier(SET_ELEMENT_TYPE, 0, 0, false, id)
            .buildAndReturn();
    }
    

}
