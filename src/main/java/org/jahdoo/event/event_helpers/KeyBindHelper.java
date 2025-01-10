package org.jahdoo.event.event_helpers;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.client.KeyBinding;
import org.jahdoo.client.gui.ability_and_utility_menus.AbilityWheelMenu;
import org.jahdoo.client.gui.augment_menu.AugmentScreen;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.utils.Configuration;

public class KeyBindHelper {
    public static void toggleLockAbility(Player player){
        if(KeyBinding.TARGET_LOCK_A.isDown()){
            var lockOnTarget = Configuration.LOCK_ON_TARGET.get();
            Configuration.LOCK_ON_TARGET.set(!lockOnTarget);
            player.displayClientMessage(Component.literal("Lock on target " + !lockOnTarget), true);
            KeyBinding.TARGET_LOCK_A.setDown(false);
        }
    }

    public static void QuickSelectBehaviour(Player player, Minecraft instance) {
        if(player != null && player.getMainHandItem().getItem() instanceof WandItem){

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
