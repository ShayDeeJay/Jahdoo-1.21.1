package org.jahdoo.common.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import org.jahdoo.common.components.TicketData;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.mod.LevelBoonReg;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.Helpers;

import java.util.List;

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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var item = player.getItemInHand(usedHand);

        if(!level.isClientSide) addBoon(item, true);

        return super.use(level, player, usedHand);
    }

    @Override
    public Component getName(ItemStack stack) {
        var type = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        var name = super.getName(stack);
        if(type == null) return name;

        var newId = stack.get(ComponentReg.ID);
        var withName = Helpers.stringIdToName(newId)+" "+ name.getString();
        var getBoon = LevelBoonReg.fromId(newId).orElseThrow();
        var colour = getBoon.getHeaderColour();
        return Helpers.withStyleComponent(withName, colour);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var getTicketMods = stack.get(ComponentReg.TICKET_DATA);
        TrialNexusTicket.modifierTooltips(tooltipComponents, getTicketMods);
        var capacity = withStyleComponent("Ticket Capacity: ", ColourStore.SUB_HEADER_COLOUR);
        var capacity1 = withStyleComponent("+" + stack.get(ComponentReg.STORE_INTEGER), ColourStore.UNIQUE_A);
        tooltipComponents.add(capacity.copy().append(capacity1));
    }

    public static void addBoon(ItemStack itemStack, boolean addNegativeModifier){
        var posRarity = JahdooRarity.getRarity();
        var posBoon = LevelBoonReg.getStampBoons(posRarity);
        TicketData.addNewEntry(itemStack, posBoon.id(), posBoon.value(posRarity));
        itemStack.set(ComponentReg.ID, posBoon.id());
        itemStack.set(ComponentReg.STORE_INTEGER, Random.nextInt(10, 50));
        if(addNegativeModifier){
            var negBoon = LevelBoonReg.randomNegative();
            var negRarity = JahdooRarity.getRarity();
            TicketData.addNewEntry(itemStack, negBoon.id(), negBoon.value(negRarity));
        }

        itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(posBoon.getStampIndex()+1));
    }

}
