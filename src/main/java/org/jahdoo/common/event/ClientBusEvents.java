package org.jahdoo.common.event;

import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.jahdoo.JahdooMod;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.altar.AltarRenderer;
import org.jahdoo.common.block.chaos_cube.ChaosCubeRenderer;
import org.jahdoo.common.block.chaos_cube.ChaosCubeScreen;
import org.jahdoo.common.block.creator.CreatorRenderer;
import org.jahdoo.common.block.dissembler.DisassemblerRenderer;
import org.jahdoo.common.block.enchanted_block.EnchantedBlockRenderer;
import org.jahdoo.common.block.lock.LockRenderer;
import org.jahdoo.common.block.loot_chest.LootChestRenderer;
import org.jahdoo.common.block.perk_table.PerkTableRenderer;
import org.jahdoo.common.block.power_up_station.PowerUpStationRenderer;
import org.jahdoo.common.block.rune_table.RuneTableRenderer;
import org.jahdoo.common.block.rune_table.enchanted_forge.RuneTableScreen;
import org.jahdoo.common.block.shopping_table.ShoppingTableRenderer;
import org.jahdoo.common.block.tank.TankRenderer;
import org.jahdoo.common.block.wand_manager.WandManagerRenderer;
import org.jahdoo.common.block.wand_manager.WandManagerScreen;
import org.jahdoo.common.client.RuneTooltipRenderer;
import org.jahdoo.common.client.overlay.CustomHudOverlay;
import org.jahdoo.common.client.overlay.InstanceDataOverlay;
import org.jahdoo.common.client.overlay.WalletOverlay;
import org.jahdoo.common.entities.ancient_golem.AncientGolemRenderer;
import org.jahdoo.common.entities.aoe_cloud.AoeCloudRenderer;
import org.jahdoo.common.entities.burning_skull.BurningSkullRenderer;
import org.jahdoo.common.entities.decoy.DecoyRenderer;
import org.jahdoo.common.entities.element_projectile.ElementProjectileRenderer;
import org.jahdoo.common.entities.eternal_wizard.EternalWizardRenderer;
import org.jahdoo.common.entities.generic_projectile.GenericProjectileRenderer;
import org.jahdoo.common.entities.ice_spear.IceSpearRenderer;
import org.jahdoo.common.entities.inferno_creeper.InfernoCreeperRenderer;
import org.jahdoo.common.entities.void_spider.VoidSpiderRenderer;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.mod.ElementReg;

import static org.jahdoo.common.client.KeyBinding.*;
import static org.jahdoo.common.event.event_helpers.EventHelpers.getColour;
import static org.jahdoo.common.particle.GenericParticle.*;
import static org.jahdoo.common.registers.BlockEntityReg.*;
import static org.jahdoo.common.registers.EntityReg.*;
import static org.jahdoo.common.registers.MenuReg.*;
import static org.jahdoo.common.registers.ParticleReg.*;


@EventBusSubscriber(modid = JahdooMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientBusEvents {

    @SubscribeEvent
    public static void registerItemColour(final RegisterColorHandlersEvent.Item event){
        event.register((stack, color) -> getColour(stack), ItemReg.RUNE.get());
    }

    @SubscribeEvent
    public static void tooltipEvent(RegisterClientTooltipComponentFactoriesEvent event){
        event.register(RuneTooltipRenderer.RuneComponent.class, RuneTooltipRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiLayersEvent event) {
        event.registerBelow(VanillaGuiLayers.AIR_LEVEL, Helpers.res("mana_bar"), new CustomHudOverlay());
        event.registerBelow(VanillaGuiLayers.AIR_LEVEL, Helpers.res("level_data"), new InstanceDataOverlay());
        event.registerAboveAll(Helpers.res("wallet"), new WalletOverlay());
    }

    @SubscribeEvent
    public static void onClientSetup(RegisterMenuScreensEvent event) {
        event.register(MODULAR_CHAOS_CUBE_MENU.get(), ChaosCubeScreen::new);
//        event.register(AUGMENT_MODIFICATION_MENU.get(), AugmentModificationScreen::new);
        event.register(WAND_MANAGER_MENU.get(), WandManagerScreen::new);
        event.register(RUNE_TABLE_MENU.get(), RuneTableScreen::new);
    }

    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(WAND_SLOT_1A);
        event.register(WAND_SLOT_2A);
        event.register(WAND_SLOT_3A);
        event.register(WAND_SLOT_4A);
        event.register(WAND_SLOT_5A);
        event.register(WAND_SLOT_6A);
        event.register(WAND_SLOT_7A);
        event.register(WAND_SLOT_8A);
        event.register(WAND_SLOT_9A);
        event.register(WAND_SLOT_10A);
        event.register(QUICK_SELECT);
        event.register(STAT_SCREEN);
        event.register(MAGNET);
    }

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(SOFT.get(), GenericProvider::new);
        event.registerSpriteSet(PLUS.get(), PlusParticle::new);
        event.registerSpriteSet(HEAL.get(), BakedProvider::new);
        event.registerSpriteSet(MAGIC.get(), GenericProvider::new);
        event.registerSpriteSet(GENERIC.get(), GenericProvider::new);
        event.registerSpriteSet(ELECTRIC.get(), ElectricalParticle::new);
        event.registerSpriteSet(BAKED_INFERNO.get(), BakedProvider::new);
        event.registerSpriteSet(BAKED_FROST.get(), BakedProvider::new);
        event.registerSpriteSet(BAKED_MYSTIC.get(), BakedProvider::new);
        event.registerSpriteSet(BAKED_VITALITY.get(), BakedProvider::new);
        event.registerSpriteSet(BAKED_UTILITY.get(), BakedProvider::new);
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        //Block entities
        event.registerBlockEntityRenderer(POWER_UP_BE.get(), PowerUpStationRenderer::new);
        event.registerBlockEntityRenderer(TANK_BE.get(), TankRenderer::new);
        event.registerBlockEntityRenderer(LOCK_BE.get(), LockRenderer::new);
        event.registerBlockEntityRenderer(INFUSER_BE.get(), DisassemblerRenderer::new);
        event.registerBlockEntityRenderer(LOOT_CHEST_BE.get(), LootChestRenderer::new);
        event.registerBlockEntityRenderer(RUNE_TABLE_BE.get(), RuneTableRenderer::new);
        event.registerBlockEntityRenderer(PERK_TABLE_BE.get(), PerkTableRenderer::new);
        event.registerBlockEntityRenderer(CHALLENGE_ALTAR_BE.get(), AltarRenderer::new);
        event.registerBlockEntityRenderer(ENCHANTED_BE.get(), EnchantedBlockRenderer::new);
        event.registerBlockEntityRenderer(SHOPPING_TABLE_BE.get(), ShoppingTableRenderer::new);
        event.registerBlockEntityRenderer(WAND_MANAGER_TABLE_BE.get(), WandManagerRenderer::new);
        event.registerBlockEntityRenderer(CREATOR_BE.get(), CreatorRenderer::new);
        event.registerBlockEntityRenderer(MODULAR_CHAOS_CUBE_BE.get(), ChaosCubeRenderer::new);

        //Entities
        event.registerEntityRenderer(FROST_ELEMENT_PROJECTILE.get(), context -> new ElementProjectileRenderer(context, ElementReg.frost().projectileTexture()));
        event.registerEntityRenderer(INFERNO_ELEMENT_PROJECTILE.get(), context -> new ElementProjectileRenderer(context, ElementReg.inferno().projectileTexture()));
        event.registerEntityRenderer(MYSTIC_ELEMENT_PROJECTILE.get(), context -> new ElementProjectileRenderer(context, ElementReg.mystic().projectileTexture()));
        event.registerEntityRenderer(VITALITY_ELEMENT_PROJECTILE.get(), context -> new ElementProjectileRenderer(context, ElementReg.vitality().projectileTexture()));
        event.registerEntityRenderer(GENERIC_PROJECTILE.get(), GenericProjectileRenderer::new);
        event.registerEntityRenderer(CUSTOM_AOE_CLOUD.get(), AoeCloudRenderer::new);
        event.registerEntityRenderer(ETERNAL_WIZARD.get(), EternalWizardRenderer::new);
        event.registerEntityRenderer(DECOY.get(), DecoyRenderer::new);
        event.registerEntityRenderer(CUSTOM_ZOMBIE.get(), ZombieRenderer::new);
        event.registerEntityRenderer(CUSTOM_SKELETON.get(), SkeletonRenderer::new);
        event.registerEntityRenderer(ANCIENT_GOLEM.get(), AncientGolemRenderer::new);
        event.registerEntityRenderer(FLAMING_SKULL.get(), BurningSkullRenderer::new);
        event.registerEntityRenderer(ICE_SPEAR.get(), IceSpearRenderer::new);
        event.registerEntityRenderer(INFERNO_CREEPER.get(), InfernoCreeperRenderer::new);
        event.registerEntityRenderer(VOID_SPIDER.get(), VoidSpiderRenderer::new);
        event.registerEntityRenderer(VOID_SPIDER_SPAWN.get(), VoidSpiderRenderer::new);
    }
}
