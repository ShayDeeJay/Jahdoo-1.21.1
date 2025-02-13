package org.jahdoo.event.event_helpers;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.client.KeyBinding;
import org.jahdoo.client.gui.ability_and_utility_menus.AbilityWheelMenu;
import org.jahdoo.client.gui.augment_menu.AugmentScreen;
import org.jahdoo.items.magnet.MagnetData;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.networking.packet.client2server.MageFlightPacketS2CPacket;
import org.jahdoo.networking.packet.client2server.MagnetActiveC2SPacket;
import org.jahdoo.networking.packet.client2server.SyncComponentC2S;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.Configuration;
import org.jahdoo.utils.ModHelpers;
import top.theillusivec4.curios.Curios;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;

import static net.minecraft.world.InteractionHand.OFF_HAND;
import static org.jahdoo.registers.ElementRegistry.getElementFromWand;

public class KeyBindHelper {
    public static void toggleLockAbility(Player player){
        if(KeyBinding.MAGNET.isDown()) {
            PacketDistributor.sendToServer(new MagnetActiveC2SPacket());
            KeyBinding.MAGNET.setDown(false);
        }
    }

    public static void quickSelectBehaviour(Player player, Minecraft instance) {
        if(player != null && ModHelpers.getUsedItem(player).getItem() instanceof WandItem){
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
