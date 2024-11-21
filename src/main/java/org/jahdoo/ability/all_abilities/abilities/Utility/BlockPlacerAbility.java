package org.jahdoo.ability.all_abilities.abilities.Utility;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.JahdooRarity;
import org.jahdoo.ability.all_abilities.ability_components.AbstractBlockAbility;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.ability.AbilityBuilder;

public class BlockPlacerAbility extends AbstractBlockAbility {
    public static final ResourceLocation abilityId = ModHelpers.res("block_placer");

    @Override
    public void invokeAbility(Player player) {
        GenericProjectile genericProjectile = new GenericProjectile(
            player, 0,
            EntityPropertyRegister.BLOCK_PLACER.get().setAbilityId(),
            abilityId.getPath().intern()
        );
        fireUtilityProjectile(genericProjectile, player);
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
            .setMana(10, 5, 1)
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
    public boolean isInputUser() {
        return true;
    }

    @Override
    public String projectileKey() {
        return EntityPropertyRegister.BLOCK_PLACER.get().setAbilityId();
    }
}
