package org.jahdoo.ability.abilities.ability_data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.ability.AbilityBuilder;

public class HellfireAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = ModHelpers.res("hellfire");

    @Override
    public void invokeAbility(Player player) {
        AoeCloud aoeCloud = new AoeCloud(player.level(), player, 0.3f, EntityPropertyRegister.HELLFIRE.get().setAbilityId(), abilityId.getPath().intern());
        aoeCloud.setPos(player.getX(), player.getY(), player.getZ());
        player.level().addFreshEntity(aoeCloud);
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
            .setStaticMana(80)
            .setStaticCooldown(600)
            .setDamage(30, 15, 5)
            .setEffectDuration(300, 20, 20)
            .setEffectStrength(10, 0,1)
            .setRange(20,10,2)
            .build();
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_MINER_DESCRIPTION;
    }

    @Override
    public int getCastType() {
        return AREA_CAST;
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
