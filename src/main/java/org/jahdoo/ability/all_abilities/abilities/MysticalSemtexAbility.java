package org.jahdoo.ability.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.JahdooRarity;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.ability.AbilityBuilder;

public class MysticalSemtexAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = ModHelpers.res("mystical_semtex");
    public static final String additionalProjectile = "Additional Projectiles";
    public static final String explosionDelays = "Explosion Delay";
    public static final String clusterChance = "Cluster Chance";
    public static final String explosionRadius = "Explosion Radius";

    @Override
    public void invokeAbility(Player player) {

        var elementProjectile = new ElementProjectile(
            EntitiesRegister.MYSTIC_ELEMENT_PROJECTILE.get(),
            player,
            EntityPropertyRegister.MYSTICAL_SEMTEX.get().setAbilityId(),
            -0.3,
            abilityId.getPath().intern()
        );
        elementProjectile.setPredicate(1);
        fireProjectile(elementProjectile, player, 0.8f);
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
            .setMana(60, 20,  10)
            .setCooldown(600, 100, 100)
            .setDamage(45, 20, 5)
            .setAbilityTagModifiersRandom(additionalProjectile, 10,4, true, 1)
            .setAbilityTagModifiersRandom(explosionDelays, 50,20, false, 5)
            .setAbilityTagModifiersRandom(clusterChance, 10,1, false, 1)
            .setAbilityTagModifiersRandom(explosionRadius, 8,3, true, 1)
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
        return ElementRegistry.MYSTIC.get();
    }
}
