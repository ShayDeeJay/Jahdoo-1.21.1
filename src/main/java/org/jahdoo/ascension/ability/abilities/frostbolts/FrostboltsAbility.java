package org.jahdoo.ascension.ability.abilities.frostbolts;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.registers.ElementRegistry;
import org.jahdoo.common.registers.EntityPropertyRegister;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;

public class FrostboltsAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = Helpers.res("frostbolts");
    public static final String NUMBER_OF_PROJECTILES = "Total Arrows";

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setStaticMana(60)
            .setStaticCooldown(600)
            .setDamage(25, 15, 2)
            .setAbilityTagModifiersRandom(NUMBER_OF_PROJECTILES, 30,10, true, 5)
            .setEffectDuration(300, 100, 20)
            .setEffectStrength(10, 0,1)
            .setEffectChance(40,5,5)
            .setCastingDistance(30,5,5)
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

    @Override
    public boolean internallyChargeManaAndCooldown() {
        return true;
    }

    @Override
    public void invokeAbility(Player player) {
        if(player != null){
            var projSelect = EntityPropertyRegister.FROST_BOLT.get().setAbilityId();
            var id = abilityId.getPath().intern();
            var elementProjectile = new GenericProjectile(player, 0, projSelect, id, this.getElemenType());

            elementProjectile.setOwner(player);
            elementProjectile.setInvisible(true);
            fireProjectileNoSound(elementProjectile, player, 100f);
            player.level().addFreshEntity(elementProjectile);
        }
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.EPIC;
    }
}
