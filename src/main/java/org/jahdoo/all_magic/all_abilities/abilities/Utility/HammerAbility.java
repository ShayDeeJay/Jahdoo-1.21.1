package org.jahdoo.all_magic.all_abilities.abilities.Utility;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.JahdooRarity;
import org.jahdoo.all_magic.all_abilities.ability_components.AbstractBlockAbility;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.all_magic.AbilityBuilder;

import static org.jahdoo.all_magic.AbilityBuilder.OFFSET;
import static org.jahdoo.all_magic.AbilityBuilder.SIZE;

public class HammerAbility extends AbstractBlockAbility {
    public static final ResourceLocation abilityId = ModHelpers.res("hammer");

    @Override
    public void invokeAbility(Player player) {
        GenericProjectile genericProjectile = new GenericProjectile(
            player, 0,
            EntityPropertyRegister.HAMMER.get().setAbilityId(),
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
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setMana(10, 5, 1)
            .setAbilityTagModifiersRandom(SIZE, 12,3, true, 3)
            .setModifierWithStep(OFFSET, 12,-12, true, 12, 1)
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
    public String projectileKey() {
        return EntityPropertyRegister.HAMMER.get().setAbilityId();
    }
}
