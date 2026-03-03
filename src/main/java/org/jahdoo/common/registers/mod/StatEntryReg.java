package org.jahdoo.common.registers.mod;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jahdoo.JahdooMod;
import org.jahdoo.trial_nexus.trackable.stat_entry.AbstractStatEntry;
import org.jahdoo.trial_nexus.trackable.stat_entry.StatCategory;
import org.jahdoo.trial_nexus.trackable.stat_entry.stats.*;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import java.util.List;
import java.util.function.Supplier;

public class StatEntryReg {

    public static final ResourceKey<Registry<AbstractStatEntry>> STAT_ENTRY_REGISTRY_KEY = ResourceKey.createRegistryKey(JahdooHelpers.res("stat_entry"));
    private static final DeferredRegister<AbstractStatEntry> STAT_ENTRY = DeferredRegister.create(STAT_ENTRY_REGISTRY_KEY, JahdooMod.MOD_ID);
    public static final Registry<AbstractStatEntry> REGISTRY =  new RegistryBuilder<>(STAT_ENTRY_REGISTRY_KEY).create();

    public static void registerRegistry(NewRegistryEvent event) {
        JahdooMod.LOGGER.debug("StatEntry.RegisterRegistry");
        event.register(REGISTRY);
    }

    private static DeferredHolder<AbstractStatEntry, AbstractStatEntry> registerElement(Supplier<AbstractStatEntry> levelBoon) {
        return STAT_ENTRY.register(levelBoon.get().id(), levelBoon);
    }

    public static List<AbstractStatEntry> getAll() {
        return REGISTRY.stream().toList();
    }

    public static List<AbstractStatEntry> getCoins() {
        return REGISTRY
            .stream()
            .filter(stat -> stat.category().equals(StatCategory.COIN))
            .toList();
    }

    public static List<AbstractStatEntry> getLoot() {
        return REGISTRY
            .stream()
            .filter(stat -> stat.category().equals(StatCategory.LOOT))
            .toList();
    }

    public static List<AbstractStatEntry> getOres() {
        return REGISTRY
            .stream()
            .filter(stat -> stat.category().equals(StatCategory.ORE))
            .toList();
    }

    public static List<AbstractStatEntry> getGeneral() {
        return REGISTRY
            .stream()
            .filter(stat -> stat.category().equals(StatCategory.GENERAL))
            .toList();
    }

    public static List<AbstractStatEntry> getMob() {
        return REGISTRY
            .stream()
            .filter(stat -> stat.category().equals(StatCategory.MOB))
            .toList();
    }

    //Coin
    public static final DeferredHolder<AbstractStatEntry, AbstractStatEntry> BRONZE_COIN_STAT =
        registerElement(BronzeCoin::new);
    public static final DeferredHolder<AbstractStatEntry, AbstractStatEntry> SILVER_COIN_STAT =
        registerElement(SilverCoin::new);
    public static final DeferredHolder<AbstractStatEntry, AbstractStatEntry> GOLD_COIN_STAT =
        registerElement(GoldCoin::new);
    public static final DeferredHolder<AbstractStatEntry, AbstractStatEntry> PLATINUM_COIN_STAT =
        registerElement(PlatinumCoin::new);

    //Loot
    public static final DeferredHolder<AbstractStatEntry, AbstractStatEntry> COMMON_CHEST =
        registerElement(CommonChest::new);
    public static final DeferredHolder<AbstractStatEntry, AbstractStatEntry> RARE_CHEST =
        registerElement(RareChest::new);
    public static final DeferredHolder<AbstractStatEntry, AbstractStatEntry> LEGENDARY_CHEST =
        registerElement(LegendaryChest::new);
    public static final DeferredHolder<AbstractStatEntry, AbstractStatEntry> MYTHIC_CHEST =
        registerElement(MythicChest::new);
    public static final DeferredHolder<AbstractStatEntry, AbstractStatEntry> LOOT_POT =
        registerElement(Pots::new);
    public static final DeferredHolder<AbstractStatEntry, AbstractStatEntry> SAFE =
        registerElement(Safe::new);

    //Ore
    public static final DeferredHolder<AbstractStatEntry, AbstractStatEntry> ORES =
        registerElement(Ores::new);

    //General
    public static final DeferredHolder<AbstractStatEntry, AbstractStatEntry> EXPERIENCE =
        registerElement(ExperienceStat::new);
    public static final DeferredHolder<AbstractStatEntry, AbstractStatEntry> ROOMS_CLEARED =
        registerElement(RoomsCleared::new);

    //Mobs
    public static final DeferredHolder<AbstractStatEntry, AbstractStatEntry> MOBS_KILLED =
        registerElement(MobsKilled::new);
    public static final DeferredHolder<AbstractStatEntry, AbstractStatEntry> CHAMPIONS_KILLED =
        registerElement(ChampionsKilled::new);
    public static final DeferredHolder<AbstractStatEntry, AbstractStatEntry> CHALLENGER =
        registerElement(ChallengersKilled::new);

    public static void register(IEventBus eventBus) {
        STAT_ENTRY.register(eventBus);
    }

}
