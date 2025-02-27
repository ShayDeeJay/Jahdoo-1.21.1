package org.jahdoo.ascension.ability.abilities.hammer;

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

import static org.jahdoo.ascension.ability.AbilityBuilder.OFFSET;

public class HammerAbility extends AbstractBlockAbility {
    public static final ResourceLocation abilityId = Helpers.res("hammer");

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
        return JahdooRarity.LEGENDARY;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        int high = 13;
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setMana(30, 15, 5)
            .setBlockSize(high, 3, 2)
            .setModifierWithStepSet(OFFSET, high,0, true, high, 1, 1)
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
        return EntityPropertyRegister.HAMMER.get().setAbilityId();
    }
}
