package org.jahdoo.common.registers.mod;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jahdoo.JahdooMod;
import org.jahdoo.trial_nexus.quests.*;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.shaydee.shaydeeapi.Helpers;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class QuestReg {

    public static final ResourceKey<Registry<AbstractQuest>> QUEST_REGISTRY_KEY = ResourceKey.createRegistryKey(JahdooHelpers.res("quest"));
    private static final DeferredRegister<AbstractQuest> QUEST = DeferredRegister.create(QUEST_REGISTRY_KEY, JahdooMod.MOD_ID);
    public static final Registry<AbstractQuest> REGISTRY =  new RegistryBuilder<>(QUEST_REGISTRY_KEY).create();

    public static void registerRegistry(NewRegistryEvent event) {
        JahdooMod.LOGGER.debug("Quest.RegisterRegistry");
        event.register(REGISTRY);
    }

    private static DeferredHolder<AbstractQuest, AbstractQuest> registerQuest(Supplier<AbstractQuest> skill) {
        return QUEST.register(skill.get().questName(), skill);
    }

    public static AbstractQuest getRandomQuest() {
        return Helpers.listRandom(REGISTRY.stream().toList());
    }

    public static List<AbstractQuest> getAllQuests() {
        return REGISTRY.stream().toList();
    }

    public static Optional<AbstractQuest> getQuestByName(String questName) {
        var list = REGISTRY.stream()
            .filter(e -> Objects.equals(e.questName(), questName))
            .toList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    public static final DeferredHolder<AbstractQuest, AbstractQuest> MASSACRE =
        registerQuest(KillHordeQuest::new);

    public static final DeferredHolder<AbstractQuest, AbstractQuest> COIN_CATCHER_BRONZE =
        registerQuest(BronzeCoins::new);

    public static final DeferredHolder<AbstractQuest, AbstractQuest> COIN_CATCHER_SILVER =
        registerQuest(SilverCoins::new);

    public static final DeferredHolder<AbstractQuest, AbstractQuest> COIN_CATCHER_GOLD =
        registerQuest(GoldCoins::new);

    public static final DeferredHolder<AbstractQuest, AbstractQuest> RUSH =
        registerQuest(ClearRoomsQuest::new);

    public static final DeferredHolder<AbstractQuest, AbstractQuest> EXPERIENCED =
        registerQuest(GetXPQuest::new);


    public static void register(IEventBus eventBus) {
        QUEST.register(eventBus);
    }

}
