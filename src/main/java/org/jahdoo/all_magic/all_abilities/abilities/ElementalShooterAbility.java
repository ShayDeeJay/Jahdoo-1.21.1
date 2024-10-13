package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.JahdooRarity;
import org.jahdoo.entities.GenericProjectile;
import static org.jahdoo.all_magic.AbilityBuilder.*;

import org.jahdoo.registers.ProjectilePropertyRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.all_magic.AbilityBuilder;

import static org.jahdoo.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;
import static org.jahdoo.utils.GeneralHelpers.Random;

public class ElementalShooterAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("elemental_shooter");
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
            .setMana(20, 10, 1)
            .setCooldown(100, 0, 10)
            .setDamage(10, 5, 1)
            .setEffectChance(50, 10, 5)
            .setEffectStrength(10, 1, 1)
            .setEffectDuration(300, 100, 20)
            .setAbilityTagModifiersRandom(numberOfProjectiles, 3, 1, true, 1)
            .setAbilityTagModifiersRandom(numberOfRicochet, 6, 1, true, 1)
            .setAbilityTagModifiersRandom(velocity, 2, 0.8, true, 0.1)
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

    private Vec3 calculateDirectionOffset(LivingEntity player, double offset) {
        Vec3 lookDirection = player.getLookAngle();
        Vec3 rightVector = new Vec3(-lookDirection.z(), 0, lookDirection.x()).normalize(); // Perpendicular to look direction
        return rightVector.scale(offset);
    }

    private double getTag(Player player, String name){
        return GeneralHelpers.getModifierValue(player.getMainHandItem().get(WAND_ABILITY_HOLDER.get()), abilityId.getPath().intern()).get(name).actualValue();
    }

    @Override
    public void invokeAbility(Player player) {
        double numberOfProjectile = getTag(player, (ElementalShooterAbility.numberOfProjectiles));
        double velocities = getTag(player, ElementalShooterAbility.velocity);
        double totalWidth = (numberOfProjectile - 1) * 0.1; // Adjust the total width as needed
        double startOffset = -totalWidth / 2.0;

        for (int i = 0; i < numberOfProjectile; i++) {
            double offset = numberOfProjectile == 1 ? 0 : startOffset + i * (totalWidth / (numberOfProjectile - 1));
            GenericProjectile genericProjectile = new GenericProjectile(
                player,
                0,
                ProjectilePropertyRegister.ELEMENTAL_SHOOTER.get().setAbilityId(),
                abilityId.getPath().intern()
            );
            Vec3 directionOffset = calculateDirectionOffset(player, offset);
            Vec3 direction = player.getLookAngle().add(directionOffset).normalize();
            genericProjectile.shoot(direction.x(), direction.y(), direction.z(), (float) velocities, 0);
            genericProjectile.setOwner(player);
            player.level().addFreshEntity(genericProjectile);
        }
        GeneralHelpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundEvents.ENDER_EYE_DEATH, 0.25f);
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
