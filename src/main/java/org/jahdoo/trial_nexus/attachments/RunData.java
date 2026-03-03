package org.jahdoo.trial_nexus.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jahdoo.common.networking.server2client.RunDataS2CP;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.mod.StatEntryReg;
import org.jahdoo.trial_nexus.level_manager.InstanceDifficulty;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.jahdoo.trial_nexus.trackable.stat_entry.AbstractStatEntry;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static net.neoforged.neoforge.network.PacketDistributor.sendToPlayer;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.common.registers.AttachmentReg.RUN_DATA;
import static org.jahdoo.trial_nexus.attachments.CasterData.addExperience;

public class RunData implements IAttachment {

    private Map<String, Integer> stats = new HashMap<>();
    private String currentQuestId = "";
    private String dateAndTime;
    private int playerLevel;
    private int timeInTrial;
    private boolean completedQuest;
    private boolean died;

    public static RunData EMPTY = new RunData(new HashMap<>(), "", "", false, false);

    public RunData() {}

    public RunData(
        Map<String, Integer> stats,
        String dateAndTime,
        String currentQuestId,
        boolean completedQuest,
        boolean died
    ) {
        this.stats = stats;
        this.dateAndTime = dateAndTime;
        this.currentQuestId = currentQuestId;
        this.completedQuest = completedQuest;
        this.died = died;
    }

    public int getBronzeCoin(){
        return getStat(StatEntryReg.BRONZE_COIN_STAT.get().id());
    }

    public int getSilverCoin(){
        return getStat(StatEntryReg.SILVER_COIN_STAT.get().id());
    }

    public int getGoldCoin(){
        return getStat(StatEntryReg.GOLD_COIN_STAT.get().id());
    }

    public int getPlatinumCoin(){
        return getStat(StatEntryReg.PLATINUM_COIN_STAT.get().id());
    }

    public int getRoomsCleared(){
        return getStat(StatEntryReg.ROOMS_CLEARED.get().id());
    }

    public int getMobsKilled(){
        return getStat(StatEntryReg.MOBS_KILLED.get().id());
    }

    public int getCommonChests(){
        return getStat(StatEntryReg.COMMON_CHEST.get().id());
    }

    public int getRareChests(){
        return getStat(StatEntryReg.RARE_CHEST.get().id());
    }

    public int getLegendaryChests(){
        return getStat(StatEntryReg.LEGENDARY_CHEST.get().id());
    }

    public int getMythicChests(){
        return getStat(StatEntryReg.MYTHIC_CHEST.get().id());
    }

    public int getChampionsKilled(){
        return getStat(StatEntryReg.CHAMPIONS_KILLED.get().id());
    }

    public int getChallengersKilled(){
        return getStat(StatEntryReg.CHALLENGER.get().id());
    }

    public int getPotsBroken(){
        return getStat(StatEntryReg.LOOT_POT.get().id());
    }

    public int getOresBroken(){
        return getStat(StatEntryReg.ORES.get().id());
    }

    public int getStat(String key) {
        return stats.getOrDefault(key, 0);
    }

    public void addStat(String key, int amount) {
        stats.put(key, getStat(key) + amount);
    }

    public boolean died() {
        return died;
    }

    public int getTimeInTrial() {
        return timeInTrial;
    }

    public int getPlayerLevel() {
        return playerLevel;
    }

    public boolean isCompletedQuest() {
        return completedQuest;
    }

    public void incrementStat(String key) {
        addStat(key, 1);
    }

    public String getCurrentQuestId() {
        return currentQuestId;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDied() {
        this.died = true;
    }

    public void setTimeInTrial(int timeInTrial) {
        this.timeInTrial = timeInTrial;
    }

    public void setPlayerLevel(int playerLevel) {
        this.playerLevel = playerLevel;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public void setCompletedQuest(boolean completedQuest) {
        this.completedQuest = completedQuest;
    }

    public void setCurrentQuestId(String currentQuestId) {
        this.currentQuestId = currentQuestId;
    }

    public static RunData getRunData(Player player){
        return player.getData(RUN_DATA.get());
    }

    public void setExperienceGained(String difficulty, int baseExp) {
        var multiplier = InstanceDifficulty.getFromName(difficulty);
        addStat(StatEntryReg.EXPERIENCE.get().id(), baseExp * multiplier.expMultiplier());
    }

    public void onEndRun(Player player, boolean died) {

        if(!this.dateAndTime.isEmpty()){
            var data = player.level().getData(INSTANCE_DATA);

            setTimeInTrial(data.getTicks());

            var thisInstance = new InstanceData(data.getDifficulty(), data.getInstance());
            var thisRun = new RunData(new HashMap<>(stats), dateAndTime, currentQuestId, completedQuest, died);

            PlayerTrialData.addNewTrialData(player, thisRun, thisInstance);
            if (!died) addExperience(player, getStat(StatEntryReg.EXPERIENCE.get().id()));
        }

        stats.clear();
        setCurrentQuestId("");
        setCompletedQuest(false);
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

    public static void addNewQuest(ServerPlayer player, String currentQuestId) {
        var runData = player.getData(RUN_DATA.get());
        runData.setCurrentQuestId(currentQuestId);
        sendToPlayer(player, new RunDataS2CP(runData));
    }

    public static int getStat(Player player, String getStat) {
        var runData = player.getData(RUN_DATA.get());
        return runData.getStat(getStat);
    }

    public static RunData emptyRun(){
        return new RunData(new HashMap<>(), "", "", false, false);
    }

    public static void endRun(ServerPlayer player, boolean died) {
        var runData = player.getData(AttachmentReg.RUN_DATA.get());
        setPlayerLevel(CasterData.getLevel(player), player);

        if (runData.dateAndTime != null) {
            runData.onEndRun(player, died);
            if(player.level() instanceof CustomLevel cLevel && LevelGenerator.isNexus(cLevel)) {
                if(cLevel.players().isEmpty()){
                    LevelGenerator.removeLevel(cLevel);
                }
            }
        }
    }

    public static void setPlayerLevel(int value, ServerPlayer player) {
        var data = player.getData(RUN_DATA.get());
        data.setPlayerLevel(value);
    }

    public static void addExperienceToTotal(int value, ServerPlayer player) {
        var runData = player.getData(RUN_DATA.get());
        runData.addStat(StatEntryReg.EXPERIENCE.get().id(), value);
        sendToPlayer(player, new RunDataS2CP(runData));
    }

    public static void incrementCoin(LivingEntity player, String difficulty, int coinType, int coinValue) {
        var type = PlayerWallet.CoinProperties.getType(coinType);
        addAndSync(player, difficulty, coinValue, type.getSerializedName(), coinValue);
    }

    public static void incrementRoomExp(LivingEntity player, String difficulty, int amount) {
        incrementAndSync(player, difficulty, amount, StatEntryReg.ROOMS_CLEARED);
    }

    public static void incrementOres(LivingEntity player, String difficulty, int amount) {
        incrementAndSync(player, difficulty, amount, StatEntryReg.ORES);
    }

    public static void incrementPotsBroken(LivingEntity player, String difficulty, int amount) {
        incrementAndSync(player, difficulty, amount, StatEntryReg.LOOT_POT);
    }

    public static void incrementSafeOpened(LivingEntity player, String difficulty, int amount) {
        incrementAndSync(player, difficulty, amount, StatEntryReg.SAFE);
    }

    public static void incrementChampionsKilled(LivingEntity player, String difficulty, int amount) {
        incrementAndSync(player, difficulty, amount, StatEntryReg.CHAMPIONS_KILLED);
    }

    public static void incrementKilledMobsExp(LivingEntity player, String difficulty, int amount) {
        incrementAndSync(player, difficulty, amount, StatEntryReg.MOBS_KILLED);
    }

    public static void incrementChallengerKilled(LivingEntity player, String difficulty, int amount) {
        incrementAndSync(player, difficulty, amount, StatEntryReg.CHALLENGER);
    }

    public static void incrementAndSync(LivingEntity player, String difficulty, int amount, DeferredHolder<AbstractStatEntry, AbstractStatEntry> stat) {
        if (player instanceof ServerPlayer serverPlayer) {
            var runData = player.getData(RUN_DATA.get());
            runData.incrementStat(stat.get().id());
            runData.setExperienceGained(difficulty, amount);
            sendToPlayer(serverPlayer, new RunDataS2CP(runData));
        }
    }

    public static void addAndSync(LivingEntity player, String difficulty, int amount, String stat, int increment) {
        if (player instanceof ServerPlayer serverPlayer) {
            var runData = player.getData(RUN_DATA.get());
            runData.addStat(stat, increment);
            runData.setExperienceGained(difficulty, amount);
            sendToPlayer(serverPlayer, new RunDataS2CP(runData));
        }
    }

    public static void incrementChestOpenedExp(ServerLevel level, LivingEntity player, int chestValue) {
        var instanceData = level.getData(INSTANCE_DATA.get());
        var runData = player.getData(RUN_DATA.get());
        var key = chestValue == 0 ?
            StatEntryReg.COMMON_CHEST.get().id() : chestValue == 1 ?
            StatEntryReg.RARE_CHEST.get().id() : chestValue == 2 ?
            StatEntryReg.LEGENDARY_CHEST.get().id() :
            StatEntryReg.MYTHIC_CHEST.get().id();

        runData.incrementStat(key);
        runData.setExperienceGained(instanceData.getDifficulty(), chestValue);

        if (player instanceof ServerPlayer serverPlayer)
            sendToPlayer(serverPlayer, new RunDataS2CP(runData));

    }

    public static final StreamCodec<FriendlyByteBuf, RunData> STREAM_CODEC = StreamCodec.ofMember(
        RunData::serialise,
        RunData::deserialise
    );

    private void serialise(FriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeMap(stats, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeInt);
        friendlyByteBuf.writeUtf(dateAndTime);
        friendlyByteBuf.writeUtf(currentQuestId);
        friendlyByteBuf.writeBoolean(completedQuest);
        friendlyByteBuf.writeBoolean(died);
    }

    private static RunData deserialise(FriendlyByteBuf friendlyByteBuf){
        return new RunData(
            friendlyByteBuf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readInt),
            friendlyByteBuf.readUtf(),
            friendlyByteBuf.readUtf(),
            friendlyByteBuf.readBoolean(),
            friendlyByteBuf.readBoolean()
        );
    }

    public static final Codec<RunData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("Stats").forGetter(run -> run.stats),
            Codec.STRING.fieldOf("DateAndTime").forGetter(RunData::getDateAndTime),
            Codec.STRING.fieldOf("QuestId").forGetter(RunData::getCurrentQuestId),
            Codec.BOOL.fieldOf("CompletedQuest").forGetter(RunData::isCompletedQuest),
            Codec.BOOL.fieldOf("Died").forGetter(RunData::died)
        ).apply(instance, RunData::new)
    );

    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        var statsTag = new CompoundTag();

        for (var entry : stats.entrySet()) {
            statsTag.putInt(entry.getKey(), entry.getValue());
        }

        nbt.put("Stats", statsTag);
        nbt.putString("QuestID", currentQuestId);
        nbt.putBoolean("CompletedQuest", completedQuest);
        nbt.putBoolean("Died", died);
        if (dateAndTime != null) nbt.putString("DateAndTime", dateAndTime);
    }

    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        stats.clear();

        if (nbt.contains("Stats")) {
            var statsTag = nbt.getCompound("Stats");
            for (var key : statsTag.getAllKeys()) {
                stats.put(key, statsTag.getInt(key));
            }
        }

        this.currentQuestId = nbt.getString("QuestID");
        this.completedQuest = nbt.getBoolean("CompletedQuest");
        this.died = nbt.getBoolean("Died");
        dateAndTime = nbt.contains("DateAndTime") ? nbt.getString("DateAndTime") : null;
    }

    @Override
    public String toString() {
        return "RunData{" +
            "stats=" + stats +
            ", dateAndTime='" + dateAndTime + '\'' +
            ", quest='" + currentQuestId + '\'' +
            '}';
    }
}