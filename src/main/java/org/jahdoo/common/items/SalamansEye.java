package org.jahdoo.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class SalamansEye extends BaseItem implements ICurioItem{

    public static final String RELIC = "relic";
    public static final ResourceLocation RES = JahdooHelpers.res("relics/new");

    public SalamansEye() {
        super(new Properties());
    }

    @Override
    public Component getName(ItemStack stack) {
        return TextHelpers.withStyleComponent(super.getName(stack).getString(), JahdooRarity.EPIC.getColour());
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        var player = slotContext.entity();

        CuriosApi.getCuriosInventory(player)
            .ifPresent(i -> i.addTransientSlotModifier(RELIC, RES, 2, AttributeModifier.Operation.ADD_VALUE));
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        var player = slotContext.entity();

        CuriosApi.getCuriosInventory(player)
            .ifPresent(i -> i.removeSlotModifier(RELIC, RES));
    }

}

