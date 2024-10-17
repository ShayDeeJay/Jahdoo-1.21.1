package org.jahdoo.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.jahdoo.JahdooMod;
import org.jahdoo.client.block_renderer.*;
import org.jahdoo.client.entity_renderer.CustomAoeRenderer;
import org.jahdoo.client.entity_renderer.ElementProjectileRenderer;
import org.jahdoo.client.entity_renderer.GenericProjectileRenderer;
import org.jahdoo.client.entity_renderer.decoy.DecoyRenderer;
import org.jahdoo.client.entity_renderer.etneral_wizzard.EternalWizardRenderer;
import org.jahdoo.particle.GenericParticle;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.registers.ParticlesRegister;
import org.jahdoo.utils.KeyBinding;


@EventBusSubscriber(modid = JahdooMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntitiesRegister.CREATOR_BE.get(), CreatorRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.WAND_MANAGER_TABLE_BE.get(), WandManagerTableRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.INFUSER_BE.get(), InfuserRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.WAND_BE.get(), WandBlockRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitiesRegister.TANK_BE.get(), TankRenderer::new);
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
    public static void entityRegister(EntityRenderersEvent.RegisterRenderers event) {

        event.registerEntityRenderer(EntitiesRegister.FROST_ELEMENT_PROJECTILE.get(), context -> new ElementProjectileRenderer(context, ElementRegistry.FROST.get().getAbilityProjectileTexture()));
        event.registerEntityRenderer(EntitiesRegister.INFERNO_ELEMENT_PROJECTILE.get(), context -> new ElementProjectileRenderer(context, ElementRegistry.INFERNO.get().getAbilityProjectileTexture()));
        event.registerEntityRenderer(EntitiesRegister.MYSTIC_ELEMENT_PROJECTILE.get(), context -> new ElementProjectileRenderer(context, ElementRegistry.MYSTIC.get().getAbilityProjectileTexture()));
        event.registerEntityRenderer(EntitiesRegister.VITALITY_ELEMENT_PROJECTILE.get(), context -> new ElementProjectileRenderer(context, ElementRegistry.VITALITY.get().getAbilityProjectileTexture()));
        event.registerEntityRenderer(EntitiesRegister.LIGHTNING_ELEMENT_PROJECTILE.get(), context -> new ElementProjectileRenderer(context, ElementRegistry.LIGHTNING.get().getAbilityProjectileTexture()));
        event.registerEntityRenderer(EntitiesRegister.GENERIC_PROJECTILE.get(), GenericProjectileRenderer::new);
        event.registerEntityRenderer(EntitiesRegister.CUSTOM_AOE_CLOUD.get(), CustomAoeRenderer::new);
        event.registerEntityRenderer(EntitiesRegister.ETERNAL_WIZARD.get(), EternalWizardRenderer::new);
        event.registerEntityRenderer(EntitiesRegister.DECOY.get(), DecoyRenderer::new);
    }

    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(KeyBinding.QUICK_SELECT);
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
}
