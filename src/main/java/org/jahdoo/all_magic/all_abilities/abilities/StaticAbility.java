package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.JahdooRarity;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.all_magic.AbilityBuilder;

import static org.jahdoo.registers.AttachmentRegister.STATIC;

public class StaticAbility extends AbstractAbility {

    public static final ResourceLocation abilityId = ModHelpers.res("static");
    public static final String mana_per_damage = "Mana Per Hit";

    @Override
    public void invokeAbility(Player player) {
        var staticAbility = player.getData(STATIC);
        if(staticAbility.getIsActive()) staticAbility.deactivate(player); else staticAbility.activate(player);
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.UNCOMMON;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setMana(50, 20,  5)
            .setCooldown(1200, 600, 50)
            .setDamage(30, 10, 5)
            .setEffectDuration(300, 50, 50)
            .setEffectStrength(10, 1,1)
            .setEffectChance(10,1,1)
            .setRange(15,1,1)
            .setAbilityTagModifiersRandom(mana_per_damage, 20,5, false, 5)
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
    public boolean internallyChargeManaAndCooldown() {
        return true;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementRegistry.LIGHTNING.get();
    }
}
