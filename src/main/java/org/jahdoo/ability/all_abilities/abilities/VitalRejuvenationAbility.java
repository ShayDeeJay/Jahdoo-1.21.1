package org.jahdoo.ability.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.AbilityBuilder;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.JahdooRarity;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.registers.AttachmentRegister.VITAL_REJUVENATION;

public class VitalRejuvenationAbility extends AbilityRegistrar {

    public static final ResourceLocation abilityId = ModHelpers.res("vital_rejuvenation");
    public static final String MAX_ABSORPTION = "Max Absorption hearts";
    public static final String CAST_DELAY = "Cast Charge Delay";


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
        return ElementRegistry.VITALITY.get();
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setMana(80, 40, 5)
            .setAbilityTagModifiersRandom(MAX_ABSORPTION, 10, 2, true, 1)
            .setAbilityTagModifiersRandom(CAST_DELAY, 20, 5, false, 5)
            .build();
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_MINER_DESCRIPTION;
    }

    @Override
    public void invokeAbility(Player player) {
        player.getData(VITAL_REJUVENATION).setStartedUsing(true);
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
