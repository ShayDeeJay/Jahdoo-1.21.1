package org.jahdoo.common.registers.mod;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jahdoo.JahdooMod;
import org.jahdoo.trial_nexus.boon.player_boons.AbstractPlayerBoons;
import org.jahdoo.trial_nexus.boon.player_boons.boons.*;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.shaydee.shaydeeapi.Helpers;

import java.util.function.Supplier;

public class PlayerBoonReg {

    public static final ResourceKey<Registry<AbstractPlayerBoons>> PLAYER_BOON_REGISTRY_KEY = ResourceKey.createRegistryKey(JahdooHelpers.res("player_boon"));
    private static final DeferredRegister<AbstractPlayerBoons> PLAYER_BOON = DeferredRegister.create(PLAYER_BOON_REGISTRY_KEY, JahdooMod.MOD_ID);
    public static final Registry<AbstractPlayerBoons> REGISTRY =  new RegistryBuilder<>(PLAYER_BOON_REGISTRY_KEY).create();

    public static void registerRegistry(NewRegistryEvent event) {
        JahdooMod.LOGGER.debug("PlayerBoon.RegisterRegistry");
        event.register(REGISTRY);
    }

    private static DeferredHolder<AbstractPlayerBoons, AbstractPlayerBoons> registerElement(Supplier<AbstractPlayerBoons> levelBoon) {
        return PLAYER_BOON.register(levelBoon.get().id(), levelBoon);
    }

    public static AbstractPlayerBoons randomBoon() {
        var element = REGISTRY
            .stream()
            .toList();
        return Helpers.listRandom(element);
    }

    public static AbstractPlayerBoons getFromId(String id) {
        var element = REGISTRY
            .stream()
            .filter(a -> a.id().equals(id))
            .toList();
        return Helpers.listRandom(element);
    }

    //Negative
    public static final DeferredHolder<AbstractPlayerBoons, AbstractPlayerBoons> MANA_POOL =
        registerElement(ManaPoolBoon::new);

    public static final DeferredHolder<AbstractPlayerBoons, AbstractPlayerBoons> MANA_REGEN =
        registerElement(ManaRegenBoon::new);

    public static final DeferredHolder<AbstractPlayerBoons, AbstractPlayerBoons> COOLDOWN =
        registerElement(CooldownBoon::new);

    public static final DeferredHolder<AbstractPlayerBoons, AbstractPlayerBoons> DAMAGE =
        registerElement(DamageAmplifierBoon::new);

    public static final DeferredHolder<AbstractPlayerBoons, AbstractPlayerBoons> MANA_REDUCTION =
        registerElement(ManaReductionBoon::new);

    public static final DeferredHolder<AbstractPlayerBoons, AbstractPlayerBoons> MAX_HEALTH =
        registerElement(MaxHealthBoon::new);

    public static final DeferredHolder<AbstractPlayerBoons, AbstractPlayerBoons> MAX_ABSORPTION =
        registerElement(MaxAbsorptionBoon::new);

    public static final DeferredHolder<AbstractPlayerBoons, AbstractPlayerBoons> ATTACK_DAMAGE =
        registerElement(AttackDamageBoon::new);

    public static final DeferredHolder<AbstractPlayerBoons, AbstractPlayerBoons> ATTACK_SPEED =
        registerElement(AttackSpeedBoon::new);

    public static void register(IEventBus eventBus) {
        PLAYER_BOON.register(eventBus);
    }

}
