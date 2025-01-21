package org.jahdoo.ability.abilities.ability_data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.ability.AbilityBuilder;

public class FireballAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = ModHelpers.res("fireball");
    public static final String novaRange = "Explosion Radius";

    @Override
    public void invokeAbility(Player player) {
        var projCount = 1;
        this.fireMultiShotProjectile(projCount, 0.5f, player, 0.4,
            () -> new ElementProjectile(
                EntitiesRegister.INFERNO_ELEMENT_PROJECTILE.get(), player,
                EntityPropertyRegister.FIRE_BALL.get().setAbilityId(), projCount == 1 ? offsetShoot(player) : 0,
                abilityId.getPath().intern()
            )
        );
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
