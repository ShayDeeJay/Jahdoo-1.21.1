package org.jahdoo.ascension.ability.abilities_combat.elemental_shooter;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.attachments.CastingData;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;

import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.common.registers.EntityDataReg.ELEMENTAL_SHOOTER;

public class ElementalMissile extends Ability {

    public static final ResourceLocation abilityId = Helpers.res("elemental_shooter");

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

    private double getTag(Player player, String name){
        var holder = CastingData.entityHolderWithSelected(player);
        return holder.data().abilityProperties().get(name).setValue();
    }

    @Override
    public void invokeAbility(Player player) {
        var projectileCount = getTag(player, SHOT_MULTIPLIER);
        var index = ELEMENTAL_SHOOTER.get().setAbilityId();
        var aId = abilityId.getPath().intern();

        fireMultiShotProjectile((int) projectileCount , 1.2f, player, 0.1, () -> new GenericProjectile(player, 0, index, aId));
        Helpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundEvents.ENDER_EYE_DEATH, 0.25f);
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(15)
            .setStaticCooldown(0)
            .setDamage(20, 10, 2)
            .setEffectChance(50, 10, 10)
            .setEffectStrength(10, 1, 1)
            .setEffectDuration(300, 100, 50)
            .setAbilityTagModifiersRandom(SHOT_MULTIPLIER, 3, 1, true, 1)
            .setAbilityTagModifiersRandom(NUMBER_OF_RICOCHET, 6, 1, true, 1)
            .setModifier(SET_ELEMENT_TYPE, 0, 0, false, Random.nextInt(1,5))
            .buildAndReturn();
    }

}
