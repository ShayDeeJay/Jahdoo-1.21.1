package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.JahdooRarity;
import org.jahdoo.components.AbilityHolder;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ProjectilePropertyRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.all_magic.AbilityBuilder;

import java.util.Map;

import static org.jahdoo.all_magic.all_abilities.ability_components.LightningTrail.getLightningTrailModifiers;
import static org.jahdoo.registers.AttributesRegister.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.all_magic.AbilityBuilder.DAMAGE;

public class ThunderBurstAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("thunder_burst");
    public static final String NUMBER_OF_THUNDERBOLTS = "Number of Thunderbolts";

    private Map<String, AbilityHolder.AbilityModifiers> tagModifierHelper(Player player){
        WandAbilityHolder wandAbilityHolder = player.getMainHandItem().get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
        return GeneralHelpers.getModifierValue(wandAbilityHolder, abilityId.getPath().intern());
    }

    @Override
    public void invokeAbility(Player player) {
        var modifiers = this.tagModifierHelper(player);
        double damage = GeneralHelpers.attributeModifierCalculator(
            player,
            (float) modifiers.get(DAMAGE).actualValue(),
            this.getElemenType(),
            MAGIC_DAMAGE_MULTIPLIER,
            true
        );

        double numberOfBolts = modifiers.get(NUMBER_OF_THUNDERBOLTS).actualValue();

        Vec3 direction = player.getLookAngle();
        WandAbilityHolder lightningTrailModifiers = getLightningTrailModifiers(damage, 0.2, 10, 0);
        GeneralHelpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundRegister.BOLT.get(), 2f,1f);

        for(int i = 0; i < numberOfBolts; i++){
            GenericProjectile genericProjectile = new GenericProjectile(
                player,
                0,
                ProjectilePropertyRegister.LIGHTNING_TRAIL.get().setAbilityId(),
                lightningTrailModifiers,
                -2,
                abilityId.getPath().intern()
            );
            genericProjectile.shoot(direction.x(), direction.y(), direction.z(), 1f, 0);
            genericProjectile.setOwner(player);
            player.level().addFreshEntity(genericProjectile);
        }
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.UNCOMMON;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setMana(50, 20,  1)
            .setCooldown(600, 200, 100)
            .setDamage(25, 10, 1)
            .setAbilityTagModifiersRandom(NUMBER_OF_THUNDERBOLTS, 30,5, true, 5)
            .build();
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
        return ElementRegistry.LIGHTNING.get();
    }
}
