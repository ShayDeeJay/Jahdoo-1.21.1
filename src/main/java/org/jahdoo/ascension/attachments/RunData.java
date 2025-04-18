package org.jahdoo.ascension.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.level_manager.InstanceDifficulty;
import org.jahdoo.common.networking.server2client.PlayerTrialDataS2CP;
import org.jahdoo.common.networking.server2client.RunDataS2CP;
import org.jahdoo.common.registers.AttachmentReg;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static net.neoforged.neoforge.network.PacketDistributor.sendToPlayer;
import static org.jahdoo.common.registers.AttachmentReg.*;

public class RunData implements IAttachment {

    public static final String EXPERIENCE = "experience";
    public static final String MOBS_KILLED = "mobs_killed";
    public static final String ROOMS_CLEARED = "rooms_cleared";
    public static final String CHESTS_OPENED = "chests_cleared";
    public static final String TIME_IN_TRIAL = "time_in_trial";
    public static final String BRONZE_COIN = "bronze_coin";
    public static final String SILVER_COIN = "silver_coin";
    public static final String GOLD_COIN = "gold_coin";
    public static final String PLATINUM_COIN = "platinum_coin";

    private Map<String, Integer> stats = new HashMap<>();
    private String dateAndTime;

    public RunData() {}

    public RunData(Map<String, Integer> stats, String dateAndTime) {
        this.stats = stats;
        this.dateAndTime = dateAndTime;
    }

    public int getStat(String key) {
        return stats.getOrDefault(key, 0);
    }

    public void addStat(String key, int amount) {
        stats.put(key, getStat(key) + amount);
    }

    public void incrementStat(String key) {
        addStat(key, 1);
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public void setExperienceGained(String difficulty, int baseExp) {
        var multiplier = InstanceDifficulty.getFromName(difficulty);
        addStat(EXPERIENCE, baseExp * multiplier.expMultiplier());
    }

    public void onEndRun(Player player, boolean died) {
        var data = player.level().getData(INSTANCE_DATA);
        var runData = player.level().getData(PLAYER_TRIAL_DATA);
        addStat(TIME_IN_TRIAL, data.getTicks());

        PlayerTrialData.addNewInstance(player, new InstanceData(data.getDifficulty(), data.getInstance()));
        runData.addInstance(data);

        if (!died) {
            var pastRun = new RunData(new HashMap<>(stats), dateAndTime);
            PlayerTrialData.addNewRun(player, pastRun);
            CasterData.addExperience(player, getStat(EXPERIENCE) + data.getExperience());
        }

        stats.clear();
        setDateAndTime("");
    }

    public static void setDateAndTime(ServerPlayer player) {
        var runData = player.getData(AttachmentReg.RUN_DATA.get());
        var dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        var timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        var date = LocalDate.now().format(dateFormatter);
        var time = LocalTime.now().format(timeFormatter);

        runData.setDateAndTime(date + " " + time);
        sendToPlayer(player, new RunDataS2CP(runData));
    }

    public static void endRun(ServerPlayer player, boolean died) {
        var runData = player.getData(AttachmentReg.RUN_DATA.get());
        var castData = PlayerTrialData.getData(player);
        if (runData.dateAndTime != null) {
            runData.onEndRun(player, died);
            sendToPlayer(player, new PlayerTrialDataS2CP(castData));
        }
    }

    public static void incrementClearedRoomExp(ServerPlayer player, String difficulty) {
        var runData = player.getData(AttachmentReg.RUN_DATA.get());
        runData.incrementStat(ROOMS_CLEARED);
        runData.setExperienceGained(difficulty, 5);
        sendToPlayer(player, new RunDataS2CP(runData));
    }

    public static void incrementKilledMobsExp(ServerLevel level, LivingEntity player) {
        var instanceData = level.getData(INSTANCE_DATA.get());
        var runData = player.getData(RUN_DATA.get());

        runData.incrementStat(MOBS_KILLED);
        runData.setExperienceGained(instanceData.getDifficulty(), 1);

        if (player instanceof ServerPlayer serverPlayer) {
            sendToPlayer(serverPlayer, new RunDataS2CP(runData));
        }
    }

    public static void incrementCoin(ServerLevel level, LivingEntity player, int coinType, int coinValue) {
        var instanceData = level.getData(INSTANCE_DATA.get());
        var runData = player.getData(RUN_DATA.get());
        var type = coinType == 1 ? SILVER_COIN : coinType == 2 ? GOLD_COIN : coinType == 3 ? PLATINUM_COIN : BRONZE_COIN;

        runData.addStat(type, coinValue);
        runData.setExperienceGained(instanceData.getDifficulty(), coinValue);
        if (player instanceof ServerPlayer serverPlayer) {
            sendToPlayer(serverPlayer, new RunDataS2CP(runData));
        }
    }

    public static void incrementChestOpenedExp(ServerLevel level, LivingEntity player, int chestValue) {
        var instanceData = level.getData(INSTANCE_DATA.get());
        var runData = player.getData(RUN_DATA.get());
        runData.incrementStat(CHESTS_OPENED);
        runData.setExperienceGained(instanceData.getDifficulty(), chestValue);
        if (player instanceof ServerPlayer serverPlayer) {
            sendToPlayer(serverPlayer, new RunDataS2CP(runData));
        }
    }

    public static final Codec<RunData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("Stats").forGetter(run -> run.stats),
            Codec.STRING.fieldOf("DateAndTime").forGetter(RunData::getDateAndTime)
        ).apply(instance, RunData::new)
    );

    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        var statsTag = new CompoundTag();

        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            statsTag.putInt(entry.getKey(), entry.getValue());
        }

        nbt.put("Stats", statsTag);
        if (dateAndTime != null) nbt.putString("DateAndTime", dateAndTime);
    }

    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        stats.clear();

        if (nbt.contains("Stats")) {
            CompoundTag statsTag = nbt.getCompound("Stats");
            for (String key : statsTag.getAllKeys()) {
                stats.put(key, statsTag.getInt(key));
            }
        }

        dateAndTime = nbt.contains("DateAndTime") ? nbt.getString("DateAndTime") : null;
    }

    @Override
    public String toString() {
        return "RunData{" +
            "stats=" + stats +
            ", dateAndTime='" + dateAndTime + '\'' +
            '}';
    }
}