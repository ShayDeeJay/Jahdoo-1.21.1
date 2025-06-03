package org.jahdoo.common.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
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
import org.jahdoo.trial_nexus.utils.Helpers;

import java.util.List;

public class Stamp extends Item implements JahdooItem{

    public Stamp() {
        super(new Properties());
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

        var newId = stack.get(ComponentReg.TICKET_DATA).values().entrySet().stream().toList().getLast();
        var withName = Helpers.stringIdToName(newId.getKey())+" "+ name.getString();
        var color = FastColor.ARGB32.color(192, 160, 124);
        return Helpers.withStyleComponent(withName, color);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var getTicketMods = stack.get(ComponentReg.TICKET_DATA);
        TrialNexusTicket.modifierTooltips(tooltipComponents, getTicketMods);
    }

    public static void addBoon(ItemStack itemStack, boolean addNegativeModifier){
        var posRarity = JahdooRarity.getRarity();
        var posBoon = LevelBoonReg.getStampBoons(posRarity);
        TicketData.addNewEntry(itemStack, posBoon.id(), posBoon.value(posRarity));

        if(addNegativeModifier){
            var negBoon = LevelBoonReg.randomNegative();
            var negRarity = JahdooRarity.getRarity();
            TicketData.addNewEntry(itemStack, negBoon.id(), negBoon.value(negRarity));
        }

        itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(posBoon.getStampIndex()+1));
    }

}
