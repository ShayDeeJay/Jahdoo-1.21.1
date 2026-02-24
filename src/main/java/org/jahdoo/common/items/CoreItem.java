package org.jahdoo.common.items;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.registers.ComponentReg;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.List;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.registers.ComponentReg.CORE_DATA;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.getColorTransition;

public class CoreItem extends Item implements JahdooItem{

    public CoreItem(int value) {
        super(addCompData(value));
    }

    public static Item.Properties addCompData(int value){
        var property = new Item.Properties();
        if(value > 0) property.component(CORE_DATA, new CoreData(value, 0));
        return property;
    }

    @Override
    public Component getName(ItemStack stack) {
        var level = Minecraft.getInstance().level;
        if(level == null) return Component.empty();

        var isFilled = !stack.has(ComponentReg.CORE_DATA);
        var coreColour = getCoreColour(level.getGameTime());
        var filledColour = isFilled ? coreColour : ColourHelpers.getColourDarker(coreColour, 2F);

        return TextHelpers.withStyleComponent(super.getName(stack).getString(), filledColour);
    }

    public static int getCoreColour(long tick){
        var color = color(233, 132, 148);
        var color1 = color(234, 144, 248);
        return getColorTransition(color, color1, (int) tick, 50);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(!CoreData.isFull(stack) && stack.has(ComponentReg.CORE_DATA)){
            var current = CoreData.getFilled(stack);
            var max = CoreData.getRequired(stack);
            tooltipComponents.add(TextHelpers.withStyleComponent(current + "/" + max, ColourHelpers.getPerkGreen()));
        }
    }

    @Override
    public String descriptionId() {
        return "description.item.jahdoo.cores";
    }

}
