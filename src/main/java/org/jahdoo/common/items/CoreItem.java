package org.jahdoo.common.items;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.CoreData;

import java.util.List;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ascension.utils.Helpers.getColorTransition;

public class CoreItem extends Item implements JahdooItem{

    public CoreItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        var tick = Minecraft.getInstance().level.getGameTime();
        var isFilled = CoreData.isFull(stack);
        var color = color(233, 132, 148);
        var color1 = color(234, 144, 248);
        var colour = getColorTransition(color, color1, (int) tick, 50);
        var string = super.getName(stack).getString();
        var filledColour = isFilled ? colour : color(182, 156, 180);
        return Helpers.withStyleComponent((!isFilled ? "Hollow" : "Charged") + " " + string, filledColour);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(!CoreData.isFull(stack)){
            var current = CoreData.getFilled(stack);
            var max = CoreData.getRequired(stack);
            tooltipComponents.add(Helpers.withStyleComponent(current + "/" + max, ColourStore.PERK_GREEN));
        }
    }

}
