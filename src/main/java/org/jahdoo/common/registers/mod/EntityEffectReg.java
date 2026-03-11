package org.jahdoo.common.registers.mod;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jahdoo.JahdooMod;
import org.jahdoo.trial_nexus.attachments.effects.*;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import java.util.List;
import java.util.function.Supplier;

public class EntityEffectReg {

    public static final ResourceKey<Registry<AbstractEntityEffect>> ENTITY_EFFECT_KEY = ResourceKey.createRegistryKey(JahdooHelpers.res("entity_effect"));
    private static final DeferredRegister<AbstractEntityEffect> ELEMENT = DeferredRegister.create(ENTITY_EFFECT_KEY, JahdooMod.MOD_ID);
    public static final Registry<AbstractEntityEffect> REGISTRY =  new RegistryBuilder<>(ENTITY_EFFECT_KEY).create();

    public static void registerRegistry(NewRegistryEvent event) {
        JahdooMod.LOGGER.debug("Entity.Effect.RegisterRegistry");
        event.register(REGISTRY);
    }

    public static List<AbstractEntityEffect> getAll() {
        return REGISTRY.stream().toList();
    }

    public static List<AbstractEntityEffect> getHasEffects(LivingEntity entity) {
        return getAll().stream().filter(s -> entity.hasData(s.getAttachment())).toList();
    }

    public static final DeferredHolder<AbstractEntityEffect, AbstractEntityEffect> VITALITY_EFFECT =
        registerEffect(VitalityEffect::new);

    public static final DeferredHolder<AbstractEntityEffect, AbstractEntityEffect> MYSTIC_EFFECT =
        registerEffect(MysticEffect::new);

    public static final DeferredHolder<AbstractEntityEffect, AbstractEntityEffect> INFERNO_EFFECT =
        registerEffect(InfernoEffect::new);

    public static final DeferredHolder<AbstractEntityEffect, AbstractEntityEffect> FROST_EFFECT =
        registerEffect(FrostEffect::new);

    private static DeferredHolder<AbstractEntityEffect, AbstractEntityEffect> registerEffect(Supplier<AbstractEntityEffect> effect) {
        return ELEMENT.register(effect.get().id(), effect);
    }

    public static void register(IEventBus eventBus) {
        ELEMENT.register(eventBus);
    }

}
