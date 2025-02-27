package org.jahdoo.ascension.ability.abilities.ice_bomb;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.registers.ElementRegistry;
import org.jahdoo.common.registers.EntitiesRegister;
import org.jahdoo.common.registers.EntityPropertyRegister;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.ability.AbilityBuilder;

public class IceBombAbility extends AbilityRegistrar {

    public static final ResourceLocation abilityId = Helpers.res("ice_bomb");

    @Override
    public void invokeAbility(Player player) {
        fireProjectile(
            new ElementProjectile(
                EntitiesRegister.FROST_ELEMENT_PROJECTILE.get(), player,
                EntityPropertyRegister.ICE_BOMB.get().setAbilityId(),
                offsetShoot(player),
                abilityId.getPath().intern()
            ),
            player, 0.35f
        );
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
            .setStaticMana(45)
            .setStaticCooldown(400)
            .setDamage(20, 10, 2)
            .setEffectStrength(10, 5,1)
            .setEffectDuration(600,200,100)
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
        return ElementRegistry.frost();
    }
}
