package org.jahdoo.ascension.ability.abilities.arcane_shift;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.ability.AbilityBuilder;

public class ArcaneShiftAbility extends AbilityRegistrar {

    public static final ResourceLocation abilityId = Helpers.res("arcane_shift");
    public static final String distance = "Teleport Distance";
    public static final String maxEntities = "Mystic Missile Shots";
    public static final String lifeTime = "Shot Range";

    @Override
    public int getCastType() {
        return DISTANCE_CAST;
    }

    @Override
    public int getCastDuration(Player player) {
        return 0;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementReg.mystic();
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
    public void invokeAbility(Player player) {
        new ArcaneShift(player).teleportToHome();
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.RARE;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setStaticMana(80)
            .setStaticCooldown(800)
            .setDamage(20, 5, 3)
            .setCastingDistance(50, 25, 5)
            .build();
    }

}
