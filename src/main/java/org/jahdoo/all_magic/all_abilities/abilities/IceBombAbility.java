package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.JahdooRarity;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.all_magic.AbilityBuilder;

public class IceBombAbility extends AbstractAbility {

    public static final ResourceLocation abilityId = ModHelpers.modResourceLocation("ice_bomb");

    @Override
    public void invokeAbility(Player player) {
        fireProjectile(
            new ElementProjectile(
                EntitiesRegister.FROST_ELEMENT_PROJECTILE.get(),
                player,
                EntityPropertyRegister.ICE_NEEDLER.get().setAbilityId(),
                0,
                abilityId.getPath().intern()
            ),
            player,
            0.5f
        );
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
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setMana(50, 20,  5)
            .setCooldown(600, 200, 50)
            .setDamage(25, 10, 5)
            .setEffectStrength(10, 5,1)
            .setEffectDuration(400,200,10)
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
        return ElementRegistry.FROST.get();
    }
}
