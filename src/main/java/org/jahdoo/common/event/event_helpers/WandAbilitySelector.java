package org.jahdoo.common.event.event_helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.client.gui.AbilityWheelMenu;
import org.jahdoo.common.networking.client2server.SelectAbilityC2SP;
import org.jahdoo.common.networking.client2server.StopUsingC2SP;
import org.jahdoo.common.registers.AbilityReg;
import org.jahdoo.ascension.utils.ModTags;

import java.util.List;

import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponentTrans;

public class WandAbilitySelector {

    private static void displayUnassignedKeyMessage(Player player, int keyNum){
        var b = withStyleComponent(String.valueOf(keyNum), -13457271);
        var a = withStyleComponentTrans("abilitySelector.jahdoo.non_assigned", -1772304, b);
        player.displayClientMessage(a, true);
    }

    public static void selectWandSlot(int keyNum){
        Player player = Minecraft.getInstance().player;
        if(player == null) return;
        ItemStack playerHandItem = player.getItemInHand(player.getUsedItemHand());
        if(!playerHandItem.is(ModTags.Items.WAND_TAGS)) return;

        List<String> arrangedAbilities = AbilityWheelMenu.getAllAbilities(playerHandItem);

        boolean condition1 = keyNum < arrangedAbilities.size();
        boolean condition2 = !arrangedAbilities.isEmpty() && arrangedAbilities.size() > keyNum - 1;

        if(condition1 || condition2){
            List<AbilityRegistrar> getAbility = AbilityReg.getSpellsByTypeId(arrangedAbilities.get(keyNum - 1));
            if(!getAbility.isEmpty()){
                var a1 = getAbility.getFirst();
                var a = withStyleComponent(a1.getAbilityName(),SharedUI.getElementColour(a1, playerHandItem));
                player.displayClientMessage(a, true);
                PacketDistributor.sendToServer(new StopUsingC2SP());
                PacketDistributor.sendToServer(new SelectAbilityC2SP(arrangedAbilities.get(keyNum - 1)));

            } else displayUnassignedKeyMessage(player, keyNum);
        }  else displayUnassignedKeyMessage(player, keyNum);
    }

}
