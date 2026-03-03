package org.jahdoo.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.mod.LevelBoonReg;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jetbrains.annotations.Nullable;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.MathHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.List;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;
import static org.jahdoo.common.components.TicketData.addNewEntry;
import static org.jahdoo.trial_nexus.rarity.JahdooRarity.getRarity;
import static org.jahdoo.trial_nexus.rarity.JahdooRarity.getReverseRarity;

public class Stamp extends BaseJahdooItem implements JahdooItem{

    public Stamp() {
        super(new Properties());
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return stack.get(ComponentReg.STORE_INTEGER) != null ? 1 : super.getMaxStackSize(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        return TextHelpers.withStyleComponentTrans("item.jahdoo.stamp", FastColor.ARGB32.color(192, 160, 124));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var getTicketMods = stack.get(ComponentReg.TICKET_DATA);
        var i = stack.get(ComponentReg.STORE_INTEGER);

        TrialNexusTicket.modifierTooltips(tooltipComponents, getTicketMods, stack);
        if(i != null) appendCapacity(tooltipComponents, i);
    }

    public static void appendCapacity(List<Component> tooltipComponents, int i) {
        var capacity = TextHelpers.withStyleComponentTrans("info.jahdoo.capacity", ColourHelpers.getSubHeaderColour());
        var capacity1 = TextHelpers.withStyleComponent("+" + i, ColourHelpers.getUniqueA());
        var append = capacity.copy().append(capacity1);

        tooltipComponents.add(append);
    }

    public static void addBoon(ItemStack itemStack, @Nullable JahdooRarity rarity){
        var newRarity = rarity == null ? getRarity() : rarity;
        var posBoon = LevelBoonReg.withRarityPositive(newRarity);
        itemStack.set(CUSTOM_MODEL_DATA, new CustomModelData(posBoon.getStampIndex()+1));
        itemStack.set(ComponentReg.STORE_INTEGER, (int) getReverseRarity().getAttributes().getRandomManaPool());
        itemStack.set(ComponentReg.ID, posBoon.id());
        addNewEntry(itemStack, posBoon.id(), posBoon.value(getRarity()));


        var rarityPercent = (newRarity.getId()) * 10;
        if(MathHelpers.percentageChance(100 - rarityPercent)){
            var negBoon = LevelBoonReg.randomNegative();
            addNewEntry(itemStack, negBoon.id(), negBoon.value(newRarity));
        }
    }

}
