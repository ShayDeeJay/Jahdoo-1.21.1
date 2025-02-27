package org.jahdoo.ascension.ability.abilities.fetch;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.ability.AbstractBlockAbility;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.registers.ElementRegistry;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;

import static org.jahdoo.common.registers.EntityPropertyRegister.FETCH;

public class FetchAbility extends AbstractBlockAbility {
    public static final ResourceLocation abilityId = Helpers.res("fetch");

    @Override
    public void invokeAbility(Player player) {
        var genericProjectile = new GenericProjectile(player, 0, projectileKey(), abilityId.getPath().intern());
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
            .setMana(20, 10, 2)
            .setRange(10,  3, 1)
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
        return ElementRegistry.UTILITY.get();
    }

    @Override
    public boolean isOutputUser() {
        return true;
    }

    @Override
    public String projectileKey() {
        return FETCH.get().setAbilityId();
    }
}
