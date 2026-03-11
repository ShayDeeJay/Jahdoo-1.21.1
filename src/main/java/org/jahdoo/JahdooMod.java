package org.jahdoo;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jahdoo.common.event.ClientBusEvents;
import org.jahdoo.common.event.ServerBusEvents;

import static org.jahdoo.CommonSetup.*;

@Mod(JahdooMod.MOD_ID)
public class JahdooMod {

    public static final String MOD_ID = "jahdoo";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID+"_mod");

    public JahdooMod(IEventBus modEventBus, ModContainer container) {
        modEventBus.addListener(this::commonSetup);

        modEventBus.register(new ServerBusEvents());
        modEventBus.register(new ClientBusEvents());

        listeners(modEventBus);
        configs(container);
        registers(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        common(event);
    }

}
