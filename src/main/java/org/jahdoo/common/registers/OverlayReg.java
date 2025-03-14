package org.jahdoo.common.registers;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.client.overlay.ManaBarOverlay;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.client.overlay.WalletOverlay;

@EventBusSubscriber(modid = JahdooMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class OverlayReg {

    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiLayersEvent event) {
        event.registerBelow(VanillaGuiLayers.AIR_LEVEL, Helpers.res("mana_bar"), new ManaBarOverlay());
        event.registerAboveAll(Helpers.res("wallet"), new WalletOverlay());
    }

}
