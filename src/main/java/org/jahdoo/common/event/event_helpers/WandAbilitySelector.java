package org.jahdoo.common.event.event_helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.common.networking.client2server.SelectAbilityC2SP;
import org.jahdoo.common.networking.client2server.UseAbilityC2SP;
import org.jahdoo.common.registers.AttachmentReg;

import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponentTrans;

public class WandAbilitySelector {

    private static void displayUnassignedKeyMessage(Player player, int keyNum){
        var b = withStyleComponent(String.valueOf(keyNum), ColourStore.PERK_GREEN);
        var a = withStyleComponentTrans("abilitySelector.jahdoo.non_assigned", ColourStore.SUB_HEADER_COLOUR, b);
        player.displayClientMessage(a, true);
    }

    public static void selectWandSlot(int keyNum){
        var player = Minecraft.getInstance().player;
        if(player == null) return;

        var casterData = player.getData(AttachmentReg.CASTER_DATA.get());
        var getAbility = casterData.abilitySlots.get(keyNum - 1);

        if (!getAbility.isEmpty()) {
            casterData.setSelectedAbility(getAbility);
            PacketDistributor.sendToServer(new SelectAbilityC2SP(getAbility));
            PacketDistributor.sendToServer(new UseAbilityC2SP());
        } else displayUnassignedKeyMessage(player, keyNum);
    }

}
