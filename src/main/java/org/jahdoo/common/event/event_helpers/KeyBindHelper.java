package org.jahdoo.common.event.event_helpers;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.common.client.KeyBinding;
import org.jahdoo.common.client.gui.ability_and_utility_menus.AbilityWheelMenu;
import org.jahdoo.common.client.gui.augment_menu.AugmentScreen;
import org.jahdoo.common.items.wand.WandItem;
import org.jahdoo.common.networking.packet.client2server.MagnetActiveC2SPacket;
import org.jahdoo.ascension.utils.Configuration;
import org.jahdoo.ascension.utils.Helpers;

public class KeyBindHelper {
    public static void toggleLockAbility(Player player){
        if(KeyBinding.MAGNET.isDown()) {
            PacketDistributor.sendToServer(new MagnetActiveC2SPacket());
            KeyBinding.MAGNET.setDown(false);
        }
    }

    public static void quickSelectBehaviour(Player player, Minecraft instance) {
        if(player != null && Helpers.getUsedItem(player).getItem() instanceof WandItem){
            if(Configuration.QUICK_SELECT.get()){
                if(KeyBinding.QUICK_SELECT.isDown()){
                    if(!(instance.screen instanceof AbilityWheelMenu) && !(instance.screen instanceof AugmentScreen)){
                        instance.setScreen(new AbilityWheelMenu());
                    }
                }
            } else {
                if (InputConstants.isKeyDown(instance.getWindow().getWindow(), KeyBinding.QUICK_SELECT.getKey().getValue())) {
                    if(!(instance.screen instanceof AbilityWheelMenu) && !(instance.screen instanceof AugmentScreen)){
                        instance.setScreen(new AbilityWheelMenu());
                    }
                } else {
                    if(instance.screen instanceof AbilityWheelMenu){
                        instance.popGuiLayer();
                    }
                }
            }
        }
    }
}
