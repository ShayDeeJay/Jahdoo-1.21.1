package org.jahdoo.common.registers.mod;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jahdoo.JahdooMod;
import org.jahdoo.trial_nexus.tasks.*;
import org.jahdoo.trial_nexus.utils.Helpers;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public class TaskReg {

    public static final ResourceKey<Registry<AbstractTask>> TASK_REGISTRY_KEY = ResourceKey.createRegistryKey(Helpers.res("task"));
    private static final DeferredRegister<AbstractTask> TASK = DeferredRegister.create(TASK_REGISTRY_KEY, JahdooMod.MOD_ID);
    public static final Registry<AbstractTask> REGISTRY =  new RegistryBuilder<>(TASK_REGISTRY_KEY).create();

    public static void registerRegistry(NewRegistryEvent event) {
        JahdooMod.LOGGER.debug("Task.RegisterRegistry");
        event.register(REGISTRY);
    }

    private static DeferredHolder<AbstractTask, AbstractTask> registerTask(Supplier<AbstractTask> skill) {
        return TASK.register(skill.get().taskId(), skill);
    }

    public static List<AbstractTask> getAllTasks() {
        return REGISTRY.stream().toList();
    }

    public static List<AbstractTask> getAllTasksSorted(Player player) {
        return REGISTRY.stream()
            .sorted(Comparator.comparing(s -> !player.getTags().contains(s.taskId())))
            .toList();
    }

    public static List<AbstractTask> getTasksByTriggerType(AbstractTask.TriggerType type){
        return getAllTasks().stream().filter(s -> s.type().equals(type)).toList();
    }

    public static final DeferredHolder<AbstractTask, AbstractTask> CLIMBER =
        registerTask(RookieAssassin::new);

    public static final DeferredHolder<AbstractTask, AbstractTask> FIRST_TIMER =
        registerTask(FirstTimer::new);

    public static final DeferredHolder<AbstractTask, AbstractTask> DEEPER_DUNGEON =
        registerTask(DeeperDungeon::new);

    public static final DeferredHolder<AbstractTask, AbstractTask> CHAMPION_OF_CHAMPIONS =
        registerTask(ChampionOfChampions::new);

    public static final DeferredHolder<AbstractTask, AbstractTask> CERTIFIED_ASSASSIN =
        registerTask(CertifiedAssassin::new);

    public static final DeferredHolder<AbstractTask, AbstractTask> COMMON_CHEST =
        registerTask(CommonLooter::new);

    public static final DeferredHolder<AbstractTask, AbstractTask> RARE_CHEST =

        registerTask(RareLooter::new);

    public static final DeferredHolder<AbstractTask, AbstractTask> LEGENDARY_CHEST =
        registerTask(LegendaryLooter::new);

    public static final DeferredHolder<AbstractTask, AbstractTask> MYTHIC_ASSASSIN =
        registerTask(MythicLooter::new);

    public static void register(IEventBus eventBus) {
        TASK.register(eventBus);
    }

}
