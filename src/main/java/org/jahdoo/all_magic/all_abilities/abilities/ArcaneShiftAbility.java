package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.all_abilities.abilities.raw_abilities.ArcaneShift;
import org.jahdoo.all_magic.JahdooRarity;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.all_magic.AbilityBuilder;

import static org.jahdoo.all_magic.AbilityBuilder.*;

public class ArcaneShiftAbility extends AbstractAbility {

    public static final ResourceLocation abilityId = ModHelpers.modResourceLocation("arcane_shift");
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
        return ElementRegistry.MYSTIC.get();
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setMana(50, 20, 1)
            .setCooldown(1200, 400, 100)
            .setDamage(20, 5, 1)
            .setAbilityTagModifiersRandom(CASTING_DISTANCE, 50, 25, true, 5)
            .setAbilityTagModifiersRandom(maxEntities, 30, 5, true, 5)
            .setAbilityTagModifiersRandom(lifeTime, 10, 5, true, 1)
            .build();
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
        return JahdooRarity.UNCOMMON;
    }

}
