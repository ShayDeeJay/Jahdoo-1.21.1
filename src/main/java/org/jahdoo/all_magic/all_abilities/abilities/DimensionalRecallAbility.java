package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.all_magic.AbilityBuilder;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.JahdooRarity;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;

import static org.jahdoo.registers.AttachmentRegister.DIMENSIONAL_RECALL;

public class DimensionalRecallAbility extends AbstractAbility {

    public static final ResourceLocation abilityId = ModHelpers.modResourceLocation("dimensional_recall");

    @Override
    public int getCastType() {
        return HOLD_CAST;
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
            .setMana(120, 80, 10)
            .setCooldown(6000, 3600, 400)
            .build();
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_MINER_DESCRIPTION;
    }

    @Override
    public void invokeAbility(Player player) {
        player.getData(DIMENSIONAL_RECALL).setStartedUsing(true);
    }

    @Override
    public boolean internallyChargeManaAndCooldown() {
        return true;
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.EPIC;
    }

}
