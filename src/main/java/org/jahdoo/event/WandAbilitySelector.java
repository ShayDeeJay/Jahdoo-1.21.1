package org.jahdoo.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.client.SharedUI;
import org.jahdoo.client.gui.ability_and_utility_menus.AbilityWheelMenu;
import org.jahdoo.networking.packet.SelectedAbilityC2SPacket;
import org.jahdoo.networking.packet.StopUsingC2SPacket;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.utils.ModTags;

import java.util.List;

public class WandAbilitySelector {

    public static void selectWandSlot(int keyNum){
        Player player = Minecraft.getInstance().player;
        if(player == null) return;
        ItemStack playerHandItem = player.getMainHandItem();
        if(!playerHandItem.is(ModTags.Items.WAND_TAGS)) return;

        List<String> arrangedAbilities = AbilityWheelMenu.getAllAbilities(playerHandItem);

        boolean condition1 = keyNum < arrangedAbilities.size();
        boolean condition2 = !arrangedAbilities.isEmpty() && arrangedAbilities.size() > keyNum - 1;

        if(condition1 || condition2){
            List<AbstractAbility> getAbility = AbilityRegister.getSpellsByTypeId(arrangedAbilities.get(keyNum - 1));
            if(!getAbility.isEmpty()){
                var a1 = getAbility.getFirst();
                var a = Component.literal(a1.getAbilityName()).withStyle(style -> style.withColor(SharedUI.getElementColour(a1, playerHandItem)));
                player.displayClientMessage(a, true);
                PacketDistributor.sendToServer(new StopUsingC2SPacket());
                PacketDistributor.sendToServer(new SelectedAbilityC2SPacket(arrangedAbilities.get(keyNum - 1)));

            } else displayUnassignedKeyMessage(player, keyNum);
        }  else displayUnassignedKeyMessage(player, keyNum);
    }


    private static void displayUnassignedKeyMessage(Player player, int keyNum){
        var a = Component.literal("No ability assigned to slot ").withStyle(style -> style.withColor(-1772304));
        var b = Component.literal(String.valueOf(keyNum)).withStyle(style -> style.withColor(-13457271));
        var c = a.append(b);
        player.displayClientMessage(c, true);
    }
}
