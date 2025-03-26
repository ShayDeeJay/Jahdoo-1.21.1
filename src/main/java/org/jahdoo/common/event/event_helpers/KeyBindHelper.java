package org.jahdoo.common.event.event_helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.common.client.screens.AbilityWheelScreen;
import org.jahdoo.common.client.screens.AugmentScreen;
import org.jahdoo.common.items.wand.WandItem;
import org.jahdoo.common.networking.client2server.MagnetActiveC2SP;
import org.jahdoo.ascension.utils.Configuration;
import org.jahdoo.ascension.utils.Helpers;

import static com.mojang.blaze3d.platform.InputConstants.*;
import static org.jahdoo.common.client.KeyBinding.*;

public class KeyBindHelper {

    public static void toggleLockAbility(Player player){
        if(MAGNET.isDown()) {
            PacketDistributor.sendToServer(new MagnetActiveC2SP());
            MAGNET.setDown(false);
        }
    }

    public static void setAbilityWheel(Minecraft instance){
        var notAbilityWheel = !(instance.screen instanceof AbilityWheelScreen);
        var notAugmentScreen = !(instance.screen instanceof AugmentScreen);
        if(notAbilityWheel && notAugmentScreen) instance.setScreen(new AbilityWheelScreen());
    }

    public static void quickSelectBehaviour(Player player, Minecraft instance) {
        if(player == null || !(Helpers.getUsedItem(player).getItem() instanceof WandItem)) return;

        if(Configuration.QUICK_SELECT.get()){
            if(QUICK_SELECT.isDown()) setAbilityWheel(instance);
        } else {
            var window = instance.getWindow().getWindow();
            var quickSelect = QUICK_SELECT.getKey().getValue();
            var keyDown = isKeyDown(window, quickSelect);
            var isAbilityWheel = instance.screen instanceof AbilityWheelScreen;

            if(keyDown) setAbilityWheel(instance); else if(isAbilityWheel) instance.popGuiLayer();
        }
    }

}
