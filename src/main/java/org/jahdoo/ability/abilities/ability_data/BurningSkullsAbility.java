package org.jahdoo.ability.abilities.ability_data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.AbilityBuilder;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.components.DataComponentHelper;
import org.jahdoo.entities.BurningSkull;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.ability.AbilityBuilder.ENTITY_MULTIPLIER;

public class BurningSkullsAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = ModHelpers.res("burning_skulls");

    @Override
    public void invokeAbility(Player player) {
        var itemInHand = player.getItemInHand(player.getUsedItemHand());
        var numberOfProjectile = DataComponentHelper.getSpecificValue(player, itemInHand, ENTITY_MULTIPLIER);
        var adjustSpread = 1.8 - (numberOfProjectile / 4);
        player.playSound(SoundRegister.ORB_FIRE.get(), 1, 0.85F);
        fireMultiShotProjectile((int) numberOfProjectile, 0.35F, player, adjustSpread, () -> new BurningSkull(player, 0));
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.RARE;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setStaticMana(40)
            .setStaticCooldown(300)
            .setDamage(15, 5, 2)
            .setEffectDuration(200, 100, 20)
            .setEffectStrength(4, 0, 1)
            .setEffectChance(25, 0, 5)
            .shotMultiplier(5, 2, 1)
            .setLifetime(50, 30, 5)
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
        return ElementRegistry.INFERNO.get();
    }

}
