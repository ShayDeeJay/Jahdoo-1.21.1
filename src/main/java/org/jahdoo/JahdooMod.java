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
import org.jahdoo.client.curio_renderer.GloveRenderer;
import org.jahdoo.client.curio_renderer.TomeRenderer;
import org.jahdoo.loot.ModLootModifiers;
import org.jahdoo.recipe.RecipeRegistry;
import org.jahdoo.registers.*;
import org.jahdoo.utils.Configuration;
import org.jahdoo.utils.ModCreativeModTabs;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod(JahdooMod.MOD_ID)
public class JahdooMod {

    public static final String MOD_ID = "jahdoo";
    public static final Logger LOGGER = LogManager.getLogger("jahdoo_mod");


    public JahdooMod(IEventBus modEventBus, ModContainer container) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(AbilityRegister::registerRegistry);
        modEventBus.addListener(ElementRegistry::registerRegistry);
        modEventBus.addListener(EntityPropertyRegister::registerRegistry);

        // Register the config
        // This will use NeoForge's ConfigurationScreen to display this mod's configs
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        container.registerConfig(ModConfig.Type.CLIENT, Configuration.CLIENT_CONFIG);
        ArmorMaterialRegistry.register(modEventBus);
        AttributesRegister.register(modEventBus);
        RecipeRegistry.register(modEventBus);
        AttachmentRegister.register(modEventBus);
        ModCreativeModTabs.register(modEventBus);
        BlocksRegister.register(modEventBus);
        BlockEntitiesRegister.register(modEventBus);
        MenusRegister.register(modEventBus);
        EntitiesRegister.register(modEventBus);
        EffectsRegister.register(modEventBus);
        ParticlesRegister.register(modEventBus);
        SoundRegister.register(modEventBus);
        ItemsRegister.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        DataComponentRegistry.register(modEventBus);
        EntityPropertyRegister.register(modEventBus);
        AbilityRegister.register(modEventBus);
        ElementRegistry.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        CuriosRendererRegistry.register(ItemsRegister.TOME_OF_UNITY.get(), TomeRenderer::new);
        CuriosRendererRegistry.register(ItemsRegister.BATTLEMAGE_GAUNTLET.get(), GloveRenderer::new);
    }


}
