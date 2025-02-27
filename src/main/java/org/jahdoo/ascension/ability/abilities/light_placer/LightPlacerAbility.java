package org.jahdoo.ascension.ability.abilities.light_placer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.ability.AbstractBlockAbility;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.registers.ElementRegistry;
import org.jahdoo.common.registers.EntityPropertyRegister;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.ability.AbilityBuilder;


public class LightPlacerAbility extends AbstractBlockAbility {
    public static final ResourceLocation abilityId = Helpers.res("light_placer");

    @Override
    public void invokeAbility(Player player) {
        GenericProjectile genericProjectile = new GenericProjectile(
            player, 0,
            projectileKey(),
            abilityId.getPath().intern()
        );
        fireUtilityProjectile(genericProjectile, player);
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.COMMON;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setMana(15, 5, 2)
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
        return ElementRegistry.utility();
    }

    @Override
    public String projectileKey() {
        return EntityPropertyRegister.LIGHT_PLACER.get().setAbilityId();
    }
}
