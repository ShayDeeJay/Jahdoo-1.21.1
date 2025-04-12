package org.jahdoo.ascension.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.ascension.level_manager.InstanceDifficulty;
import org.jahdoo.common.networking.server2client.CastingDataSyncS2CP;
import org.jahdoo.common.networking.server2client.RunDataS2CP;
import org.jahdoo.common.registers.AttachmentReg;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.common.registers.AttachmentReg.RUN_DATA;

public class RunData implements IAttachment {

    private int experienceGained;
    private int mobsKilled;
    private int roomsCleared;
    private int chestsOpened;
    private String dateAndTime;

    public RunData() {}

    public RunData(
        int experienceGained,
        int mobsKilled,
        int roomsCleared,
        int chestsOpened,
        String dateAndTime
    ) {
        this.experienceGained = experienceGained;
        this.mobsKilled = mobsKilled;
        this.roomsCleared = roomsCleared;
        this.chestsOpened = chestsOpened;
        this.dateAndTime = dateAndTime;
    }

    // Getters
    public int getExperienceGained() {
        return experienceGained;
    }

    public int getMobsKilled() {
        return mobsKilled;
    }

    public int getRoomsCleared() {
        return roomsCleared;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public int getChestsOpened() {
        return chestsOpened;
    }

    // Setters
    public void setExperienceGained(String difficulty, int experienceGained) {
        var multiplier = InstanceDifficulty.getFromName(difficulty);
        this.experienceGained += (experienceGained * multiplier.expMultiplier());
    }

    public void setMobsKilled(int mobsKilled) {
        this.mobsKilled += mobsKilled;
    }

    public void incrementRoomsCleared() {
        this.roomsCleared++;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public void incrementChestsOpened() {
        this.chestsOpened++;
    }

    public void onEndRun(Player player) {
        var pastRun = new RunData(getExperienceGained(), getMobsKilled(), getRoomsCleared(), getChestsOpened(), getDateAndTime());
        CastingData.addNewRun(player, pastRun);
        CastingData.addExperience(player, getExperienceGained());
        this.experienceGained = 0;
        this.mobsKilled = 0;
        this.roomsCleared = 0;
        this.chestsOpened = 0;
        this.setDateAndTime("");
    }

    public static void setDateAndTime(ServerPlayer player) {
        var runData = player.getData(AttachmentReg.RUN_DATA.get());
        var dateFormatter = DateTimeFormatter.ofPattern("dd/MM/ yy");
        var formattedDate = LocalDate.now().format(dateFormatter);
        var timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        var formattedTime = LocalTime.now().format(timeFormatter);

        runData.setDateAndTime(formattedDate + " " + formattedTime);
        PacketDistributor.sendToPlayer(player, new RunDataS2CP(runData));
    }

    public static void endRun(ServerPlayer player) {
        var runData = player.getData(AttachmentReg.RUN_DATA.get());
        var castData = player.getData(AttachmentReg.CASTER_DATA.get());
        runData.onEndRun(player);
        PacketDistributor.sendToPlayer(player, new CastingDataSyncS2CP(castData));
    }

    public static void incrementClearedRoomExp(ServerPlayer player, String difficulty) {
        var runData = player.getData(AttachmentReg.RUN_DATA.get());
        runData.incrementRoomsCleared();
        runData.setExperienceGained(difficulty, 5);
        PacketDistributor.sendToPlayer(player, new RunDataS2CP(runData));
    }

    public static void incrementKilledMobsExp(ServerLevel level, LivingEntity player) {
        var instanceData = level.getData(INSTANCE_DATA.get());
        var runData = player.getData(RUN_DATA.get());
        runData.setMobsKilled(1);
        runData.setExperienceGained(instanceData.getDifficulty(), 1);
        if(player instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new RunDataS2CP(runData));
        }
    }

    public static void incrementChestOpenedExp(ServerLevel level, LivingEntity player, int chestValue) {
        var instanceData = level.getData(INSTANCE_DATA.get());
        var runData = player.getData(RUN_DATA.get());
        runData.incrementChestsOpened();
        runData.setExperienceGained(instanceData.getDifficulty(), chestValue);
        if(player instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new RunDataS2CP(runData));
        }
    }

    // Codec
    public static final Codec<RunData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.INT.fieldOf("ExperienceGained").forGetter(RunData::getExperienceGained),
            Codec.INT.fieldOf("MobsKilled").forGetter(RunData::getMobsKilled),
            Codec.INT.fieldOf("RoomsCleared").forGetter(RunData::getRoomsCleared),
            Codec.INT.fieldOf("ChestsOpened").forGetter(RunData::getChestsOpened),
            Codec.STRING.fieldOf("DateAndTime").forGetter(RunData::getDateAndTime)
        ).apply(instance, RunData::new)
    );

    // Save NBT
    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putInt("ExperienceGained", experienceGained);
        nbt.putInt("MobsKilled", mobsKilled);
        nbt.putInt("RoomsCleared", roomsCleared);
        if (dateAndTime != null) {
            nbt.putString("DateAndTime", dateAndTime);
        }
    }

    // Load NBT
    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        experienceGained = nbt.getInt("ExperienceGained");
        mobsKilled = nbt.getInt("MobsKilled");
        roomsCleared = nbt.getInt("RoomsCleared");
        dateAndTime = nbt.contains("DateAndTime") ? nbt.getString("DateAndTime") : null;
    }

    public String toString() {
        return "RunData{" +
            "experienceGained=" + experienceGained +
            ", mobsKilled=" + mobsKilled +
            ", roomsCleared=" + roomsCleared +
            ", chestsOpened=" + chestsOpened +
            ", dateAndTime='" + dateAndTime + '\'' +
            '}';
    }
}
