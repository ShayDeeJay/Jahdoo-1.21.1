package org.jahdoo.ability.abilities.ability_data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.units.qual.K;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.ability.AbstractElement;
//import org.assets.jahdoo.attachments.player_abilities.NovaSmash;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.items.wand.CastHelper;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.ability.AbilityBuilder;

import static org.jahdoo.registers.AttachmentRegister.NOVA_SMASH;

public class NovaSmashAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = ModHelpers.res("nova_smash");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void invokeAbility(Player player) {
        if(!player.onGround()) {
            var novaSmash = player.getData(NOVA_SMASH);
            novaSmash.setCanSmash(true);
            CastHelper.chargeManaAndCooldown(abilityId.getPath().intern(), player);
        }
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.RARE;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setStaticMana(40)
            .setStaticCooldown(400)
            .setDamage(12, 4, 2)
            .build();
    }

    @Override
    public boolean internallyChargeManaAndCooldown() {
        return true;
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_PLACER;
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
        return ElementRegistry.MYSTIC.get();
    }
}
