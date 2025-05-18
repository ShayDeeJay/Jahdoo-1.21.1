package org.jahdoo.common.event;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.common.networking.server2client.JahdooToastS2CP;
import org.jahdoo.common.registers.mod.TaskReg;
import org.jahdoo.trial_nexus.tasks.AbstractTask;

public class TriggerEvents {

    static void triggerKillEvent(Entity entity, Level level) {
        sharedMethod(AbstractTask.TriggerType.KIll, entity, level);
    }

    static void triggerUseEvent(Entity entity, Level level) {
        sharedMethod(AbstractTask.TriggerType.USE, entity, level);
    }

    public static void triggerQuestCompleteEvent(Entity entity, Level level) {
        sharedMethod(AbstractTask.TriggerType.QUEST_COMPLETION, entity, level);
    }

    public static void triggerRoomClearEvent(Entity entity, Level level) {
        sharedMethod(AbstractTask.TriggerType.ROOM_CLEAR, entity, level);
    }

    private static void sharedMethod(AbstractTask.TriggerType use, Entity entity, Level level) {
        if(level instanceof CustomLevel && entity instanceof ServerPlayer serverPlayer){
            for (var task : TaskReg.getTasksByTriggerType(use)) {
                setMethod(serverPlayer, task);
            }
        }
    }

    public static void onPlayerJoined(Entity entity){
        if(entity instanceof ServerPlayer serverPlayer){
            for (var task : TaskReg.getAllTasks()) setMethod(serverPlayer, task);
        }
    }

    private static void setMethod(ServerPlayer serverPlayer, AbstractTask task) {
        var taskComplete = task.completionPredicate(serverPlayer);
        var key = task.taskId();
        var data = serverPlayer.getTags();
        var registeredComplete = data.contains(key);

        if (!registeredComplete && taskComplete) {
            data.add(key);
            PacketDistributor.sendToPlayer(serverPlayer, new JahdooToastS2CP(task.taskName(), task.taskDescription(), task.taskIcon()));
        }
    }

}
