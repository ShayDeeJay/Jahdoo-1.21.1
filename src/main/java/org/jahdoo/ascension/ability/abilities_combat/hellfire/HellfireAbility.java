package org.jahdoo.ascension.ability.abilities_combat.hellfire;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.ascension.ability.abilities_combat.BurningSkullsAbility;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.registers.ElementReg;

import static org.jahdoo.common.registers.EntityDataReg.HELLFIRE;

public class HellfireAbility extends AbilityRegistrar {

    public static final ResourceLocation abilityId = Helpers.res("hellfire");

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.EPIC;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
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
        return ElementReg.inferno();
    }

    @Override
    public void invokeAbility(Player player) {
        AoeCloud aoeCloud = new AoeCloud(player.level(), player, 0.3f, HELLFIRE.get().setAbilityId(), abilityId.getPath().intern());
        aoeCloud.setPos(player.getX(), player.getY(), player.getZ());
        BurningSkullsAbility.infernoSoundEffect(player);
        player.level().addFreshEntity(aoeCloud);
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

}
