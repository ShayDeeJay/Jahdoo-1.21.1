package org.jahdoo;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jahdoo.common.items.gauntlet.GloveRenderer;
import org.jahdoo.common.items.pendent.PendentRenderer;
import org.jahdoo.common.items.tome.TomeRenderer;
import org.jahdoo.common.datagen.loot.ModLootModifiers;
import org.jahdoo.common.registers.*;
import org.jahdoo.ascension.utils.Configuration;
import org.jahdoo.ascension.utils.CreativeTab;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod(JahdooMod.MOD_ID)
public class JahdooMod {

    public static final String MOD_ID = "jahdoo";
    public static final Logger LOGGER = LogManager.getLogger("jahdoo_mod");

    public JahdooMod(IEventBus modEventBus, ModContainer container) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(AbilityReg::registerRegistry);
        modEventBus.addListener(ElementReg::registerRegistry);
        modEventBus.addListener(EntityDataReg::registerRegistry);

        // Register the config
        // This will use NeoForge's ConfigurationScreen to display this mod's configs
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        container.registerConfig(ModConfig.Type.CLIENT, Configuration.CLIENT_CONFIG);
        ArmorMaterialReg.register(modEventBus);
        AttributeReg.register(modEventBus);
        AttachmentReg.register(modEventBus);
        CreativeTab.register(modEventBus);
        BlockReg.register(modEventBus);
        BlockEntityReg.register(modEventBus);
        MenuReg.register(modEventBus);
        EntityReg.register(modEventBus);
        EffectReg.register(modEventBus);
        ParticleReg.register(modEventBus);
        SoundReg.register(modEventBus);
        ItemReg.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        ComponentReg.register(modEventBus);
        EntityDataReg.register(modEventBus);
        AbilityReg.register(modEventBus);
        ElementReg.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        CuriosRendererRegistry.register(ItemReg.PENDENT.get(), PendentRenderer::new);
        CuriosRendererRegistry.register(ItemReg.TOME_OF_UNITY.get(), TomeRenderer::new);
        CuriosRendererRegistry.register(ItemReg.BATTLEMAGE_GAUNTLET.get(), GloveRenderer::new);
    }

}
