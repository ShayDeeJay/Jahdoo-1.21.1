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

public class FireballAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = ModHelpers.res("fireball");
    public static final String novaRange = "Explosion Radius";

    @Override
    public void invokeAbility(Player player) {
        ElementProjectile elementProjectile = new ElementProjectile(
            EntitiesRegister.INFERNO_ELEMENT_PROJECTILE.get(),
            player,
            EntityPropertyRegister.FIRE_BALL.get().setAbilityId(),
            -0.3,
            abilityId.getPath().intern()
        );
        this.fireProjectile(elementProjectile, player, 0.5f);
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
            .setMana(30, 10, 5)
            .setCooldown(600, 200, 100)
            .setDamage(45, 20, 5)
            .setEffectDuration(300, 100, 20)
            .setEffectStrength(10, 0, 1)
            .setEffectChance(40, 5, 5)
            .setAbilityTagModifiersRandom(novaRange, 6, 3, true, 1)
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
        return ElementRegistry.INFERNO.get();
    }
}
