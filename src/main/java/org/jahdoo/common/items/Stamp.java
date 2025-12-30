package org.jahdoo.common.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.mod.LevelBoonReg;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.ColourStore;

import java.util.List;

import static org.jahdoo.common.components.TicketData.addNewEntry;
import static org.jahdoo.trial_nexus.utils.Helpers.Random;
import static org.jahdoo.trial_nexus.utils.Helpers.withStyleComponent;

public class Stamp extends Item implements JahdooItem{

    public Stamp() {
        super(new Properties());
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return stack.get(ComponentReg.STORE_INTEGER) != null ? 1 : super.getMaxStackSize(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.empty();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var getTicketMods = stack.get(ComponentReg.TICKET_DATA);
        var i = stack.get(ComponentReg.STORE_INTEGER);

        TrialNexusTicket.modifierTooltips(tooltipComponents, getTicketMods, stack);
        if(i != null) appendCapacity(tooltipComponents, i);
    }

    public static void appendCapacity(List<Component> tooltipComponents, int i) {
        var capacity = withStyleComponent("Capacity: ", ColourStore.SUB_HEADER_COLOUR);
        var capacity1 = withStyleComponent("+" + i, ColourStore.UNIQUE_A);
        var append = capacity.copy().append(capacity1);

        tooltipComponents.add(append);
    }

    public static void addBoon(ItemStack itemStack, boolean addNegativeModifier){
        var posBoon = LevelBoonReg.getStampBoons();

        addNewEntry(itemStack, posBoon.id(), posBoon.value(JahdooRarity.getRarity()));
        itemStack.set(ComponentReg.ID, posBoon.id());
        itemStack.set(ComponentReg.STORE_INTEGER, Random.nextInt(10, 50));

        if(addNegativeModifier){
            var negBoon = LevelBoonReg.randomNegative();
            var negRarity = JahdooRarity.getRarity();
            addNewEntry(itemStack, negBoon.id(), negBoon.value(negRarity));
        }

        itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(posBoon.getStampIndex()+1));
    }

}
