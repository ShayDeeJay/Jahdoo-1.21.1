package org.jahdoo.ability.abilities.ability_data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.entities.GenericProjectile;
import static org.jahdoo.ability.AbilityBuilder.*;

import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.ability.AbilityBuilder;

import static org.jahdoo.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;
import static org.jahdoo.registers.EntityPropertyRegister.ELEMENTAL_SHOOTER;
import static org.jahdoo.utils.ModHelpers.Random;

public class ElementalShooterAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = ModHelpers.res("elemental_shooter");
    public static final String numberOfProjectiles = "Shot Multiplier";
    public static final String numberOfRicochet = "Ricochets";
    public static final String velocity = "Projectile Velocity";

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
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
            .setAbilityTagModifiersRandom(numberOfProjectiles, 3, 1, true, 1)
            .setAbilityTagModifiersRandom(numberOfRicochet, 6, 1, true, 1)
            .setModifier(SET_ELEMENT_TYPE, 0, 0, false, Random.nextInt(1,6))
            .build();
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


    private double getTag(Player player, String name){
        return ModHelpers.getModifierValue(player.getMainHandItem().get(WAND_ABILITY_HOLDER.get()), abilityId.getPath().intern()).get(name).actualValue();
    }

    @Override
    public void invokeAbility(Player player) {
        var numberOfProjectile = getTag(player, (ElementalShooterAbility.numberOfProjectiles));
        this.fireMultiShotProjectile((int) numberOfProjectile , 1.2f, player, 0.1, () ->
            new GenericProjectile(player, 0, ELEMENTAL_SHOOTER.get().setAbilityId(), abilityId.getPath().intern())
        );
        ModHelpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundEvents.ENDER_EYE_DEATH, 0.25f);
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.COMMON;
    }

    @Override
    public boolean isMultiType() {
        return true;
    }
}
