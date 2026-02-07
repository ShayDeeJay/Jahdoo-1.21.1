package org.jahdoo.common.items.gauntlet;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.items.BaseItem;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

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
        return JahdooHelpers.withStyleComponent(super.getName(stack).getString(), ColourStore.GOLD_COIN);
    }

    @Override
    public void implicitModifiers(ItemStack stack, List<Component> tooltipComponents) {
        super.implicitModifiers(stack, tooltipComponents);
        tooltipComponents.add(JahdooHelpers.withStyleComponent("Offhand Wands", ColourStore.SYMPATHISER_ORANGE));
    }

}
