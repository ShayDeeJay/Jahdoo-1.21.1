package org.jahdoo.common.registers.mod;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jahdoo.JahdooMod;
import org.jahdoo.trial_nexus.trackable.player_boons.AbstractPlayerBoon;
import org.jahdoo.trial_nexus.trackable.player_boons.boons.*;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.shaydee.shaydeeapi.Helpers;

import java.util.List;
import java.util.function.Supplier;

public class PlayerBoonReg {

    public static final ResourceKey<Registry<AbstractPlayerBoon>> PLAYER_BOON_REGISTRY_KEY = ResourceKey.createRegistryKey(JahdooHelpers.res("player_boon"));
    private static final DeferredRegister<AbstractPlayerBoon> PLAYER_BOON = DeferredRegister.create(PLAYER_BOON_REGISTRY_KEY, JahdooMod.MOD_ID);
    public static final Registry<AbstractPlayerBoon> REGISTRY =  new RegistryBuilder<>(PLAYER_BOON_REGISTRY_KEY).create();

    public static void registerRegistry(NewRegistryEvent event) {
        JahdooMod.LOGGER.debug("PlayerBoon.RegisterRegistry");
        event.register(REGISTRY);
    }

    private static DeferredHolder<AbstractPlayerBoon, AbstractPlayerBoon> registerElement(Supplier<AbstractPlayerBoon> levelBoon) {
        return PLAYER_BOON.register(levelBoon.get().id(), levelBoon);
    }

    public static List<AbstractPlayerBoon> getAll() {
        return REGISTRY.stream().toList();
    }

    public static AbstractPlayerBoon randomBoon() {
        var element = REGISTRY
            .stream()
            .toList();
        return Helpers.listRandom(element);
    }

    public static AbstractPlayerBoon getFromId(String id) {
        var element = REGISTRY
            .stream()
            .filter(a -> a.id().equals(id))
            .toList();
        return Helpers.listRandom(element);
    }

    //Negative
    public static final DeferredHolder<AbstractPlayerBoon, AbstractPlayerBoon> MANA_POOL =
        registerElement(ManaPoolBoon::new);

    public static final DeferredHolder<AbstractPlayerBoon, AbstractPlayerBoon> MANA_REGEN =
        registerElement(ManaRegenBoon::new);

    public static final DeferredHolder<AbstractPlayerBoon, AbstractPlayerBoon> COOLDOWN =
        registerElement(CooldownBoon::new);

    public static final DeferredHolder<AbstractPlayerBoon, AbstractPlayerBoon> DAMAGE =
        registerElement(DamageAmplifierBoon::new);

    public static final DeferredHolder<AbstractPlayerBoon, AbstractPlayerBoon> MANA_REDUCTION =
        registerElement(ManaReductionBoon::new);

    public static final DeferredHolder<AbstractPlayerBoon, AbstractPlayerBoon> MAX_HEALTH =
        registerElement(MaxHealthBoon::new);

    public static final DeferredHolder<AbstractPlayerBoon, AbstractPlayerBoon> MAX_ABSORPTION =
        registerElement(MaxAbsorptionBoon::new);

    public static final DeferredHolder<AbstractPlayerBoon, AbstractPlayerBoon> ATTACK_DAMAGE =
        registerElement(AttackDamageBoon::new);

    public static final DeferredHolder<AbstractPlayerBoon, AbstractPlayerBoon> ATTACK_SPEED =
        registerElement(AttackSpeedBoon::new);

    public static void register(IEventBus eventBus) {
        PLAYER_BOON.register(eventBus);
    }

}
