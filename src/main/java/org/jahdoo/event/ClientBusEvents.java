package org.jahdoo.event;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import org.jahdoo.JahdooMod;
import org.jahdoo.client.KeyBinding;
import org.jahdoo.client.RuneTooltipRenderer;
import org.jahdoo.client.block_renderer.*;
import org.jahdoo.client.entity_renderer.CustomAoeRenderer;
import org.jahdoo.client.entity_renderer.ElementProjectileRenderer;
import org.jahdoo.client.entity_renderer.GenericProjectileRenderer;
import org.jahdoo.client.entity_renderer.ancient_golem.AncientGolemRenderer;
import org.jahdoo.client.entity_renderer.decoy.DecoyRenderer;
import org.jahdoo.client.entity_renderer.etneral_wizzard.EternalWizardRenderer;
import org.jahdoo.client.gui.block.augment_modification_station.AugmentModificationScreen;
import org.jahdoo.client.gui.block.infusion_table.InfusionTableScreen;

import org.jahdoo.client.gui.block.modular_chaos_cube.ModularChaosCubeScreen;
import org.jahdoo.client.gui.block.rune_table.RuneTableScreen;
import org.jahdoo.client.gui.block.wand_block.WandBlockScreen;
import org.jahdoo.client.gui.block.wand_manager_table.WandManagerScreen;
import org.jahdoo.particle.GenericParticle;
import org.jahdoo.registers.*;


@EventBusSubscriber(modid = JahdooMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientBusEvents {

    @SubscribeEvent
    public static void registerItemColour(final RegisterColorHandlersEvent.Item event){
        event.register((stack, color) -> getColour(stack), ItemsRegister.RUNE.get());
    }

    public static int getColour(ItemStack stack){
        var colour = stack.get(DataComponentRegistry.RUNE_DATA.get());
        if(colour != null) return colour.colour();
        return -1;
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        //Block entities
        event.registerBlockEntityRenderer(BlockEntitiesRegister.CREATOR_BE.get(), CreatorRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.WAND_MANAGER_TABLE_BE.get(), WandManagerTableRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.INFUSER_BE.get(), InfuserRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.CHALLENGE_ALTAR_BE.get(), ChallengeAltarRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.MODULAR_CHAOS_CUBE_BE.get(), ModularChaosCubeRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.LOOT_CHEST_BE.get(), LootChestRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.WAND_BE.get(), WandBlockRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.TANK_BE.get(), NexiteTankRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.SHOPPING_TABLE_BE.get(), ShoppingTableRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.RUNE_TABLE_BE.get(), RuneTableRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.AUGMENT_MODIFICATION_STATION_BE.get(), AugmentModificationStationRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.ENCHANTED_BE.get(), EnchantedBlockRenderer::new);

        //Entities
        event.registerEntityRenderer(EntitiesRegister.FROST_ELEMENT_PROJECTILE.get(), context -> new ElementProjectileRenderer(context, ElementRegistry.FROST.get().getAbilityProjectileTexture()));
        event.registerEntityRenderer(EntitiesRegister.INFERNO_ELEMENT_PROJECTILE.get(), context -> new ElementProjectileRenderer(context, ElementRegistry.INFERNO.get().getAbilityProjectileTexture()));
        event.registerEntityRenderer(EntitiesRegister.MYSTIC_ELEMENT_PROJECTILE.get(), context -> new ElementProjectileRenderer(context, ElementRegistry.MYSTIC.get().getAbilityProjectileTexture()));
        event.registerEntityRenderer(EntitiesRegister.VITALITY_ELEMENT_PROJECTILE.get(), context -> new ElementProjectileRenderer(context, ElementRegistry.VITALITY.get().getAbilityProjectileTexture()));
        event.registerEntityRenderer(EntitiesRegister.LIGHTNING_ELEMENT_PROJECTILE.get(), context -> new ElementProjectileRenderer(context, ElementRegistry.LIGHTNING.get().getAbilityProjectileTexture()));
        event.registerEntityRenderer(EntitiesRegister.GENERIC_PROJECTILE.get(), GenericProjectileRenderer::new);
        event.registerEntityRenderer(EntitiesRegister.CUSTOM_AOE_CLOUD.get(), CustomAoeRenderer::new);
        event.registerEntityRenderer(EntitiesRegister.ETERNAL_WIZARD.get(), EternalWizardRenderer::new);
        event.registerEntityRenderer(EntitiesRegister.DECOY.get(), DecoyRenderer::new);
        event.registerEntityRenderer(EntitiesRegister.CUSTOM_ZOMBIE.get(), ZombieRenderer::new);
        event.registerEntityRenderer(EntitiesRegister.CUSTOM_SKELETON.get(), SkeletonRenderer::new);
        event.registerEntityRenderer(EntitiesRegister.ANCIENT_GOLEM.get(), AncientGolemRenderer::new);

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
        event.registerSpriteSet(ParticlesRegister.BAKED_LIGHTNING.get(), GenericParticle.BakedProvider::new);
        event.registerSpriteSet(ParticlesRegister.BAKED_UTILITY.get(), GenericParticle.BakedProvider::new);
        event.registerSpriteSet(ParticlesRegister.HEAL.get(), GenericParticle.BakedProvider::new);
    }

    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(KeyBinding.QUICK_SELECT);
        event.register(KeyBinding.TARGET_LOCK_A);
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
    public static void tooltipEvent(RegisterClientTooltipComponentFactoriesEvent event){
        event.register(RuneTooltipRenderer.RuneComponent.class, RuneTooltipRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(RegisterMenuScreensEvent event) {
        event.register(MenusRegister.CRYSTAL_INFUSION_MENU.get(), InfusionTableScreen::new);
        event.register(MenusRegister.WAND_BLOCK_MENU.get(), WandBlockScreen::new);
        event.register(MenusRegister.MODULAR_CHAOS_CUBE_MENU.get(), ModularChaosCubeScreen::new);
        event.register(MenusRegister.AUGMENT_MODIFICATION_MENU.get(), AugmentModificationScreen::new);
        event.register(MenusRegister.WAND_MANAGER_MENU.get(), WandManagerScreen::new);
        event.register(MenusRegister.RUNE_TABLE_MENU.get(), RuneTableScreen::new);
    }
}
