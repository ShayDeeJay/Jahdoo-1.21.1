package org.jahdoo;


import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jahdoo.client.curio_renderer.TomeRenderer;
import org.jahdoo.client.gui.infusion_table.InfusionTableScreen;
import org.jahdoo.client.gui.wand_block.WandBlockScreen;
import org.jahdoo.loot.ModLootModifiers;
import org.jahdoo.recipe.RecipeRegistry;
import org.jahdoo.registers.*;
import org.jahdoo.utils.ModCreativeModTabs;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod(JahdooMod.MOD_ID)
public class JahdooMod {

    public static final String MOD_ID = "jahdoo";
    public static final Logger logger = LogManager.getLogger("jahdoo_mod");


    public JahdooMod(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(AbilityRegister::registerRegistry);
        modEventBus.addListener(ElementRegistry::registerRegistry);
        modEventBus.addListener(EntityPropertyRegister::registerRegistry);

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
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(RegisterMenuScreensEvent event) {
            event.register(MenusRegister.CRYSTAL_INFUSION_MENU.get(), InfusionTableScreen::new);
            event.register(MenusRegister.WAND_BLOCK_MENU.get(), WandBlockScreen::new);

        }
    }

}
