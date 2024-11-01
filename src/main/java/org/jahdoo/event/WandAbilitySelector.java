package org.jahdoo.event;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.client.SharedUI;
import org.jahdoo.client.gui.ability_and_utility_menus.AbilityWheelMenu;
import org.jahdoo.networking.packet.client2server.SelectedAbilityC2SPacket;
import org.jahdoo.networking.packet.client2server.StopUsingC2SPacket;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.utils.ModTags;

import java.util.List;

import static org.jahdoo.utils.ModHelpers.withStyleComponent;
import static org.jahdoo.utils.ModHelpers.withStyleComponentTrans;

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
                var a = withStyleComponent(a1.getAbilityName(),SharedUI.getElementColour(a1, playerHandItem));
                player.displayClientMessage(a, true);
                PacketDistributor.sendToServer(new StopUsingC2SPacket());
                PacketDistributor.sendToServer(new SelectedAbilityC2SPacket(arrangedAbilities.get(keyNum - 1)));

            } else displayUnassignedKeyMessage(player, keyNum);
        }  else displayUnassignedKeyMessage(player, keyNum);
    }


    private static void displayUnassignedKeyMessage(Player player, int keyNum){
        var b = withStyleComponent(String.valueOf(keyNum), -13457271);
        var a = withStyleComponentTrans("abilitySelector.jahdoo.non_assigned", -1772304, b);
        player.displayClientMessage(a, true);
    }
}
