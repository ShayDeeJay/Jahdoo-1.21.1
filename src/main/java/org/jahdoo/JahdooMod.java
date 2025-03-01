package org.jahdoo;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.jahdoo.common.CommonSetup.*;

@Mod(JahdooMod.MOD_ID)
public class JahdooMod {

    public static final String MOD_ID = "jahdoo";
    public static final Logger LOGGER = LogManager.getLogger("jahdoo_mod");

    public JahdooMod(IEventBus modEventBus, ModContainer container) {
        modEventBus.addListener(this::commonSetup);
        listeners(modEventBus);
        configs(container);
        registers(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        common(event);
    }

}
