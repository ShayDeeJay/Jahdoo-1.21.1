package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.JahdooRarity;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ProjectilePropertyRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.all_magic.AbilityBuilder;

public class HellfireAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("hellfire");

    @Override
    public void invokeAbility(Player player) {
        AoeCloud aoeCloud = new AoeCloud(player.level(), player, 0.3f, ProjectilePropertyRegister.HELLFIRE.get().setAbilityId(), abilityId.getPath().intern());
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
            .setMana(20, 10,  1)
            .setCooldown(400, 100, 50)
            .setDamage(20, 5, 1)
            .setEffectDuration(300, 20, 20)
            .setEffectStrength(10, 0,1)
            .setRange(20,10,1)
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
