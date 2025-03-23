package org.jahdoo.ascension.ability.abilities.elemental_shooter;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;

import static org.jahdoo.ascension.ability.AbilityBuilder.SET_ELEMENT_TYPE;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.common.registers.EntityDataReg.ELEMENTAL_SHOOTER;

public class ElementalShooterAbility extends AbilityRegistrar {

    public static final ResourceLocation abilityId = Helpers.res("elemental_shooter");
    public static final String SHOT_MULTIPLIER = "Shot Multiplier";
    public static final String NUMBER_OF_RICOCHET = "Ricochets";

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
        var wandAbilityHolder = WandAbilityHolder.getHolderFromWand(player);
        var id = abilityId.getPath().intern();
        return Helpers.getModifierValue(wandAbilityHolder, id).get(name).actualValue();
    }

    @Override
    public void invokeAbility(Player player) {
        var projectileCount = getTag(player, (ElementalShooterAbility.SHOT_MULTIPLIER));
        var index = ELEMENTAL_SHOOTER.get().setAbilityId();
        var aId = abilityId.getPath().intern();
        fireMultiShotProjectile((int) projectileCount , 1.2f, player, 0.1, () -> new GenericProjectile(player, 0, index, aId));
        Helpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundEvents.ENDER_EYE_DEATH, 0.25f);
    }

    public int setTypeManually(int type){
        return type;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setStaticMana(15)
            .setStaticCooldown(0)
            .setDamage(10, 5, 1)
            .setEffectChance(50, 10, 10)
            .setEffectStrength(10, 1, 1)
            .setEffectDuration(300, 100, 50)
            .setAbilityTagModifiersRandom(SHOT_MULTIPLIER, 3, 1, true, 1)
            .setAbilityTagModifiersRandom(NUMBER_OF_RICOCHET, 6, 1, true, 1)
            .setModifier(SET_ELEMENT_TYPE, 0, 0, false, Random.nextInt(1,5))
            .build();
    }

}
