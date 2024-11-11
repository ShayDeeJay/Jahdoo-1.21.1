package org.jahdoo.all_magic.all_abilities.abilities.Utility;

import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.JahdooRarity;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.all_magic.AbilityBuilder;


public class FarmersTouchAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = ModHelpers.modResourceLocation("farmers_touch");
    public static final String GROWTH_CHANCE = "Growth Chance";
    public static final String HARVEST_CHANCE = "Harvest Chance";

    @Override
    public void invokeAbility(Player player) {
        GenericProjectile genericProjectile = new GenericProjectile(
            player, 0,
            EntityPropertyRegister.BONE_MEAL.get().setAbilityId(),
            abilityId.getPath().intern()
        );
        fireUtilityProjectile(genericProjectile, player);
    }

    @Override
    public void invokeAbilityBlock(Vec3i direction, AbstractBEInventory entity) {
        var augment = entity.inputItemHandler.getStackInSlot(0);
        GenericProjectile genericProjectile = new GenericProjectile(
            augment.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get()),
            entity.getBlockPos().getCenter(),
            entity.getLevel(),
            EntityPropertyRegister.BONE_MEAL.get().setAbilityId(),
            abilityId.getPath().intern()
        );
        genericProjectile.setMaxDistance(10);
        fireUtilityProjectile(genericProjectile, entity.getBlockPos(), direction);
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
            .setMana(20, 10, 5)
            .setRange(10, 1, 1)
            .setAbilityTagModifiersRandom(GROWTH_CHANCE, 60, 10, false, 10)
            .setAbilityTagModifiersRandom(HARVEST_CHANCE, 30, 5, false, 5)
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
}
