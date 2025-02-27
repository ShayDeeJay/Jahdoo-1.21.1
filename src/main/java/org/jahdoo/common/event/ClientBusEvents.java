package org.jahdoo.common.event;

import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.block.challange_altar.ChallengeAltarRenderer;
import org.jahdoo.common.block.enchanted_block.EnchantedBlockRenderer;
import org.jahdoo.common.block.infuser.InfuserRenderer;
import org.jahdoo.common.block.loot_chest.LootChestRenderer;
import org.jahdoo.common.block.modular_chaos_cube.ModularChaosCubeRenderer;
import org.jahdoo.common.block.rune_table.RuneTableRenderer;
import org.jahdoo.common.block.shopping_table.ShoppingTableRenderer;
import org.jahdoo.common.block.tank.NexiteTankRenderer;
import org.jahdoo.common.block.wand.WandBlockRenderer;
import org.jahdoo.common.block.wand_manager.WandManagerTableRenderer;
import org.jahdoo.common.client.KeyBinding;
import org.jahdoo.common.client.RuneTooltipRenderer;
import org.jahdoo.common.entities.aoe_cloud.AoeCloudRenderer;
import org.jahdoo.common.entities.burning_skull.BurningSkullRenderer;
import org.jahdoo.common.entities.element_projectile.ElementProjectileRenderer;
import org.jahdoo.common.entities.generic_projectile.GenericProjectileRenderer;
import org.jahdoo.common.entities.ancient_golem.AncientGolemRenderer;
import org.jahdoo.common.entities.decoy.DecoyRenderer;
import org.jahdoo.common.entities.eternal_wizard.EternalWizardRenderer;
import org.jahdoo.common.entities.void_spider.VoidSpiderRenderer;
import org.jahdoo.common.block.augment_modification_station.AugmentModificationScreen;

import org.jahdoo.common.block.modular_chaos_cube.ModularChaosCubeScreen;
import org.jahdoo.common.block.rune_table.RuneTableScreen;
import org.jahdoo.common.block.wand.WandBlockScreen;
import org.jahdoo.common.block.wand_manager.WandManagerScreen;
import org.jahdoo.common.particle.GenericParticle;
import org.jahdoo.common.registers.*;


@EventBusSubscriber(modid = JahdooMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientBusEvents {

    @SubscribeEvent
    public static void registerItemColour(final RegisterColorHandlersEvent.Item event){
        event.register((stack, color) -> getColour(stack), ItemsRegister.RUNE.get());
    }

    @SubscribeEvent
    public static void tooltipEvent(RegisterClientTooltipComponentFactoriesEvent event){
        event.register(RuneTooltipRenderer.RuneComponent.class, RuneTooltipRenderer::new);
    }

    public static int getColour(ItemStack stack){
        var colour = stack.get(DataComponentRegistry.RUNE_DATA.get());
        if(colour != null) return colour.colour();
        return -1;
    }

    @SubscribeEvent
    public static void onClientSetup(RegisterMenuScreensEvent event) {
        event.register(MenusRegister.WAND_BLOCK_MENU.get(), WandBlockScreen::new);
        event.register(MenusRegister.MODULAR_CHAOS_CUBE_MENU.get(), ModularChaosCubeScreen::new);
        event.register(MenusRegister.AUGMENT_MODIFICATION_MENU.get(), AugmentModificationScreen::new);
        event.register(MenusRegister.WAND_MANAGER_MENU.get(), WandManagerScreen::new);
        event.register(MenusRegister.RUNE_TABLE_MENU.get(), RuneTableScreen::new);
    }

    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(KeyBinding.QUICK_SELECT);
        event.register(KeyBinding.MAGNET);
        event.register(KeyBinding.WAND_SLOT_1A);
        event.register(KeyBinding.WAND_SLOT_2A);
        event.register(KeyBinding.WAND_SLOT_3A);
        event.register(KeyBinding.WAND_SLOT_4A);
        event.register(KeyBinding.WAND_SLOT_5A);
        event.register(KeyBinding.WAND_SLOT_6A);
        event.register(KeyBinding.WAND_SLOT_7A);
        event.register(KeyBinding.WAND_SLOT_8A);
        event.register(KeyBinding.WAND_SLOT_9A);
        event.register(KeyBinding.WAND_SLOT_10A);
    }

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticlesRegister.GENERIC.get(), GenericParticle.GenericProvider::new);
        event.registerSpriteSet(ParticlesRegister.ELECTRIC.get(), GenericParticle.ElectricalParticle::new);
        event.registerSpriteSet(ParticlesRegister.MAGIC.get(), GenericParticle.GenericProvider::new);
        event.registerSpriteSet(ParticlesRegister.SOFT.get(), GenericParticle.GenericProvider::new);
        event.registerSpriteSet(ParticlesRegister.BAKED_INFERNO.get(), GenericParticle.BakedProvider::new);
        event.registerSpriteSet(ParticlesRegister.BAKED_FROST.get(), GenericParticle.BakedProvider::new);
        event.registerSpriteSet(ParticlesRegister.BAKED_MYSTIC.get(), GenericParticle.BakedProvider::new);
        event.registerSpriteSet(ParticlesRegister.BAKED_VITALITY.get(), GenericParticle.BakedProvider::new);
        event.registerSpriteSet(ParticlesRegister.BAKED_UTILITY.get(), GenericParticle.BakedProvider::new);
        event.registerSpriteSet(ParticlesRegister.HEAL.get(), GenericParticle.BakedProvider::new);
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        //Block entities
        event.registerBlockEntityRenderer(BlockEntitiesRegister.WAND_MANAGER_TABLE_BE.get(), WandManagerTableRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.INFUSER_BE.get(), InfuserRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.CHALLENGE_ALTAR_BE.get(), ChallengeAltarRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.MODULAR_CHAOS_CUBE_BE.get(), ModularChaosCubeRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.LOOT_CHEST_BE.get(), LootChestRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.WAND_BE.get(), WandBlockRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.TANK_BE.get(), NexiteTankRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.SHOPPING_TABLE_BE.get(), ShoppingTableRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.RUNE_TABLE_BE.get(), RuneTableRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.ENCHANTED_BE.get(), EnchantedBlockRenderer::new);

        //Entities
        event.registerEntityRenderer(EntitiesRegister.FROST_ELEMENT_PROJECTILE.get(), context -> new ElementProjectileRenderer(context, ElementRegistry.frost().projectileTexture()));
        event.registerEntityRenderer(EntitiesRegister.INFERNO_ELEMENT_PROJECTILE.get(), context -> new ElementProjectileRenderer(context, ElementRegistry.inferno().projectileTexture()));
        event.registerEntityRenderer(EntitiesRegister.MYSTIC_ELEMENT_PROJECTILE.get(), context -> new ElementProjectileRenderer(context, ElementRegistry.mystic().projectileTexture()));
        event.registerEntityRenderer(EntitiesRegister.VITALITY_ELEMENT_PROJECTILE.get(), context -> new ElementProjectileRenderer(context, ElementRegistry.vitality().projectileTexture()));
        event.registerEntityRenderer(EntitiesRegister.GENERIC_PROJECTILE.get(), GenericProjectileRenderer::new);
        event.registerEntityRenderer(EntitiesRegister.CUSTOM_AOE_CLOUD.get(), AoeCloudRenderer::new);
        event.registerEntityRenderer(EntitiesRegister.ETERNAL_WIZARD.get(), EternalWizardRenderer::new);
        event.registerEntityRenderer(EntitiesRegister.DECOY.get(), DecoyRenderer::new);
        event.registerEntityRenderer(EntitiesRegister.CUSTOM_ZOMBIE.get(), ZombieRenderer::new);
        event.registerEntityRenderer(EntitiesRegister.CUSTOM_SKELETON.get(), SkeletonRenderer::new);
        event.registerEntityRenderer(EntitiesRegister.ANCIENT_GOLEM.get(), AncientGolemRenderer::new);
        event.registerEntityRenderer(EntitiesRegister.FLAMING_SKULL.get(), BurningSkullRenderer::new);
        event.registerEntityRenderer(EntitiesRegister.VOID_SPIDER.get(), VoidSpiderRenderer::new);
        event.registerEntityRenderer(EntitiesRegister.VOID_SPIDER_SPAWN.get(), VoidSpiderRenderer::new);

    }
}
