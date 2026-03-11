package org.jahdoo;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jahdoo.common.datagen.loot.ModLootModifiers;
import org.jahdoo.common.items.ability_augment.AugmentCrystalRenderer;
import org.jahdoo.common.items.gauntlet.GloveRenderer;
import org.jahdoo.common.items.shields.ShieldRenderer;
import org.jahdoo.common.items.tome.TomeRenderer;
import org.jahdoo.common.registers.*;
import org.jahdoo.common.registers.mod.*;
import org.jahdoo.trial_nexus.utils.Configuration;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CommonSetup {

    public static void configs(ModContainer container){
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        container.registerConfig(ModConfig.Type.CLIENT, Configuration.CLIENT_CONFIG);
    }

    public static void listeners(IEventBus modEventBus){
        modEventBus.addListener(AbilityReg::registerRegistry);
        modEventBus.addListener(StatEntryReg::registerRegistry);
        modEventBus.addListener(ElementReg::registerRegistry);
        modEventBus.addListener(EntityEffectReg::registerRegistry);
        modEventBus.addListener(EntityDataReg::registerRegistry);
        modEventBus.addListener(CreatorRecipeReg::registerRegistry);
        modEventBus.addListener(LevelBoonReg::registerRegistry);
        modEventBus.addListener(PlayerBoonReg::registerRegistry);
        modEventBus.addListener(SkillReg::registerRegistry);
        modEventBus.addListener(QuestReg::registerRegistry);
        modEventBus.addListener(RuneReg::registerRegistry);
        modEventBus.addListener(TaskReg::registerRegistry);
    }

    public static void common(final FMLCommonSetupEvent event){
        CuriosRendererRegistry.register(ItemReg.TOME_OF_UNITY.get(), TomeRenderer::new);
        CuriosRendererRegistry.register(ItemReg.BATTLEMAGE_GAUNTLET.get(), GloveRenderer::new);
        CuriosRendererRegistry.register(ItemReg.AUGMENT_CRYSTAL.get(), AugmentCrystalRenderer::new);
        CuriosRendererRegistry.register(ItemReg.BASIC_SHIELD.get(), ShieldRenderer::new);
        CuriosRendererRegistry.register(ItemReg.UNDEAD_PROTECTOR_SHIELD.get(), ShieldRenderer::new);
    }

    public static void registers(IEventBus modEventBus){
        ArmorMaterialReg.register(modEventBus);
        AttributeReg.register(modEventBus);
        StatEntryReg.register(modEventBus);
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
        EntityEffectReg.register(modEventBus);
        ElementReg.register(modEventBus);
        LevelBoonReg.register(modEventBus);
        PlayerBoonReg.register(modEventBus);
        SkillReg.register(modEventBus);
        TaskReg.register(modEventBus);
        QuestReg.register(modEventBus);
        RuneReg.register(modEventBus);
        CreatorRecipeReg.register(modEventBus);
    }

}
