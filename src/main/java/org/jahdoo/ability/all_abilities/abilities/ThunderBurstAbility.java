package org.jahdoo.ability.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.ability.JahdooRarity;
import org.jahdoo.components.AbilityHolder;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.ability.AbilityBuilder;

import java.util.Map;

import static org.jahdoo.ability.all_abilities.ability_components.LightningTrail.getLightningTrailModifiers;
import static org.jahdoo.registers.AttributesRegister.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.ability.AbilityBuilder.DAMAGE;

public class ThunderBurstAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = ModHelpers.res("thunder_burst");
    public static final String NUMBER_OF_THUNDERBOLTS = "Number of Thunderbolts";

    private Map<String, AbilityHolder.AbilityModifiers> tagModifierHelper(Player player){
        var wandAbilityHolder = player.getMainHandItem().get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
        return ModHelpers.getModifierValue(wandAbilityHolder, abilityId.getPath().intern());
    }

    @Override
    public void invokeAbility(Player player) {
        var modifiers = this.tagModifierHelper(player);
        var damage = ModHelpers.attributeModifierCalculator(
            player, (float) modifiers.get(DAMAGE).actualValue(), this.getElemenType(),
            MAGIC_DAMAGE_MULTIPLIER, true
        );
        var numberOfBolts = modifiers.get(NUMBER_OF_THUNDERBOLTS).actualValue();
        var direction = player.getLookAngle();
        var lightningTrailModifiers = getLightningTrailModifiers(damage, 0.2, 10, 0);

        ModHelpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundRegister.BOLT.get(), 2f,1f);

        for(int i = 0; i < numberOfBolts; i++){
            GenericProjectile genericProjectile = new GenericProjectile(
                player, -0.3,
                EntityPropertyRegister.LIGHTNING_TRAIL.get().setAbilityId(),
                lightningTrailModifiers, -1,
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
            .setMana(60, 30,  5)
            .setCooldown(400, 100, 100)
            .setDamage(40, 15, 5)
            .setAbilityTagModifiersRandom(NUMBER_OF_THUNDERBOLTS, 30,10, true, 5)
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
