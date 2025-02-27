package org.jahdoo.ascension.ability.abilities.fireball;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.ability.abilities.BurningSkullsAbility;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.registers.ElementRegistry;
import org.jahdoo.common.registers.EntitiesRegister;
import org.jahdoo.common.registers.EntityPropertyRegister;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.ability.AbilityBuilder;

public class FireballAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = Helpers.res("fireball");
    public static final String novaRange = "Explosion Radius";

    @Override
    public void invokeAbility(Player player) {
        var projCount = 1;
        fireMultiShotProjectile(projCount, 0.5f, player, 0.4,
            () -> new ElementProjectile(
                EntitiesRegister.INFERNO_ELEMENT_PROJECTILE.get(), player,
                EntityPropertyRegister.FIRE_BALL.get().setAbilityId(), projCount == 1 ? offsetShoot(player) : 0,
                abilityId.getPath().intern()
            )
        );
        BurningSkullsAbility.infernoSoundEffect(player);
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
            .setStaticMana(60)
            .setStaticCooldown(600)
            .setDamage(45, 20, 5)
            .setEffectDuration(300, 100, 20)
            .setEffectStrength(10, 0, 1)
            .setEffectChance(50, 10, 10)
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
