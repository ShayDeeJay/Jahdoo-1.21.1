package org.jahdoo.ascension.ability.abilities.nova_smash;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.ascension.element.AbstractElement;
//import org.assets.jahdoo.attachments.player_abilities.NovaSmash;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.items.wand.CastHelper;
import org.jahdoo.common.registers.ElementRegistry;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.ability.AbilityBuilder;

import static org.jahdoo.common.registers.AttachmentRegister.NOVA_SMASH;

public class NovaSmashAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = Helpers.res("nova_smash");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.RARE;
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
        return ElementRegistry.mystic();
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
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setStaticMana(40)
            .setStaticCooldown(400)
            .setDamage(12, 4, 2)
            .build();
    }
}
