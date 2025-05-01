package org.jahdoo.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.CoreData;

import java.util.List;

public class CoreItem extends Item {

    public CoreItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var current = CoreData.getFilled(stack);
        var max = CoreData.getRequired(stack);
        tooltipComponents.add(Helpers.withStyleComponent(current +" / "+ max, ColourStore.PERK_GREEN));
    }

}
