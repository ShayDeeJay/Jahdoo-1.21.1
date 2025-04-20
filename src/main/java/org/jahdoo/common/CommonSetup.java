package org.jahdoo.common;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jahdoo.ascension.utils.Configuration;
import org.jahdoo.common.datagen.loot.ModLootModifiers;
import org.jahdoo.common.items.gauntlet.GloveRenderer;
import org.jahdoo.common.items.pendent.PendentRenderer;
import org.jahdoo.common.items.tome.TomeRenderer;
import org.jahdoo.common.registers.*;
import org.jahdoo.common.registers.mod.*;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CommonSetup {

    public static void configs(ModContainer container){
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        container.registerConfig(ModConfig.Type.CLIENT, Configuration.CLIENT_CONFIG);
    }

    public static void listeners(IEventBus modEventBus){
        modEventBus.addListener(AbilityReg::registerRegistry);
        modEventBus.addListener(ElementReg::registerRegistry);
        modEventBus.addListener(EntityDataReg::registerRegistry);
        modEventBus.addListener(LevelBoonReg::registerRegistry);
        modEventBus.addListener(SkillReg::registerRegistry);
        modEventBus.addListener(QuestReg::registerRegistry);
    }

    public static void common(final FMLCommonSetupEvent event){
        CuriosRendererRegistry.register(ItemReg.PENDENT.get(), PendentRenderer::new);
        CuriosRendererRegistry.register(ItemReg.TOME_OF_UNITY.get(), TomeRenderer::new);
        CuriosRendererRegistry.register(ItemReg.BATTLEMAGE_GAUNTLET.get(), GloveRenderer::new);
    }

    public static void registers(IEventBus modEventBus){
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
        LevelBoonReg.register(modEventBus);
        SkillReg.register(modEventBus);
        QuestReg.register(modEventBus);
    }

}
