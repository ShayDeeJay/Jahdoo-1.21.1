package org.jahdoo.common.items.gauntlet;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.items.BaseItem;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class BattlemageGauntlet extends BaseItem implements ICurioItem {

    public BattlemageGauntlet() {
        super(
            new Properties()
                .durability(300)
                .stacksTo(1)
        );
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public Component getName(ItemStack stack) {
        return TextHelpers.withStyleComponent(super.getName(stack).getString(), ColourHelpers.getGoldCoin());
    }
}
