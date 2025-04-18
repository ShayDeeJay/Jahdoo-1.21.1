package org.jahdoo.ascension.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.ascension.level_manager.InstanceDifficulty;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.networking.server2client.PlayerTrialDataS2CP;
import org.jahdoo.common.registers.AttachmentReg;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.jahdoo.ascension.attachments.RunData.*;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.common.registers.AttachmentReg.PLAYER_TRIAL_DATA;

public class PlayerTrialData implements IAttachment{

    private List<RunData> pastRuns = new ArrayList<>();
    private List<InstanceData> pastInstances = new ArrayList<>();

    public PlayerTrialData(){}

    public PlayerTrialData(
        List<RunData> pastRuns,
        List<InstanceData> pastInstances
    ){
        this.pastRuns = pastRuns;
        this.pastInstances = pastInstances;
    }

    public List<InstanceData> getInstanceData() {
        return pastInstances;
    }

    public void addInstance(InstanceData instanceData) {
        this.pastInstances.add(instanceData);
    }

    public List<RunData> getPastRuns() {
        return pastRuns;
    }

    public void addNewRun(RunData runData){
        this.pastRuns.add(runData);
    }

    public static final Codec<PlayerTrialData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.list(RunData.CODEC).optionalFieldOf("PastRuns",List.of()).forGetter(PlayerTrialData::getPastRuns),
            Codec.list(InstanceData.CODEC).optionalFieldOf("PastInstances",List.of()).forGetter(PlayerTrialData::getInstanceData)
        ).apply(instance, PlayerTrialData::new)
    );

    public static PlayerTrialData getData(Player player){
        return player.getData(AttachmentReg.PLAYER_TRIAL_DATA.get());
    }

    public static Optional<InstanceData> getLastInstance(Player player){
        var runs = getData(player).getInstanceData();
        return runs.isEmpty() ? Optional.empty() : Optional.of(runs.getLast());
    }

    public static Optional<RunData> getLastRun(Player player){
        var runs = getData(player).getPastRuns();
        return runs.isEmpty() ? Optional.empty() : Optional.of(runs.getLast());
    }

    public static void addNewRun(Player player, RunData runData){
        var data = player.getData(PLAYER_TRIAL_DATA);
        data.addNewRun(runData);
    }

    public static void addNewInstance(Player player, InstanceData instanceData){
        var data = player.getData(PLAYER_TRIAL_DATA);
        data.addInstance(instanceData);
    }

    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {

        var instanceList = new ListTag();
        for (InstanceData instanceData : pastInstances) {
            var runTag = new CompoundTag();
            instanceData.saveNBTData(runTag, provider);
            instanceList.add(runTag);
        }
        nbt.put("PastInstances", instanceList);

        System.out.println(instanceList);

        var runList = new ListTag();
        for (RunData pastRun : pastRuns) {
            var runTag = new CompoundTag();
            pastRun.saveNBTData(runTag, provider);
            runList.add(runTag);
        }
        nbt.put("PastRuns", runList);

    }

    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {

        pastInstances.clear();
        if (nbt.contains("PastInstances", Tag.TAG_LIST)) {
            var instanceList = nbt.getList("PastInstances", Tag.TAG_COMPOUND);
            for (Tag tag : instanceList) {
                var runTag = (CompoundTag) tag;
                var instanceRun = new InstanceData("", new HashMap<>()); // placeholder init
                instanceRun.loadNBTData(runTag, provider);
                System.out.println(instanceRun);
                pastInstances.add(instanceRun);
            }
        }

        pastRuns.clear();
        if (nbt.contains("PastRuns", Tag.TAG_LIST)) {
            var runList = nbt.getList("PastRuns", Tag.TAG_COMPOUND);
            for (Tag tag : runList) {
                var runTag = (CompoundTag) tag;
                var pastRun = new RunData(new HashMap<>(), ""); // placeholder init
                pastRun.loadNBTData(runTag, provider);
                pastRuns.add(pastRun);
            }
        }

    }

    public static void addDummyData(Level level, Player player, int multiplier, boolean eraseData) {
        if(level instanceof ServerLevel){
            if(player instanceof ServerPlayer serverPlayer){
                var x = serverPlayer.getData(AttachmentReg.PLAYER_TRIAL_DATA);

                if(eraseData){
                    x.getPastRuns().clear();
                    x.getInstanceData().clear();
                }

                for(int i = 0; i < multiplier; i++){
                    var newInstance = new InstanceData();
                    var newRunData = new RunData();
                    var name = Helpers.listRandom(Arrays.stream(InstanceDifficulty.values()).toList()).getSerializedName();

                    newInstance.setExperience(Random.nextInt(200, 500));
                    newInstance.setPlatinumCoin(Random.nextInt(5, 30));
                    newInstance.setGoldCoin(Random.nextInt(10, 50));
                    newInstance.setSilverCoin(Random.nextInt(50, 150));
                    newInstance.setBronzeCoin(Random.nextInt(3050, 9050));
                    newInstance.setDifficulty(name);
                    newInstance.incrementVoidSpider(Random.nextInt(10, 30));
                    newInstance.incrementHorde(Random.nextInt(400, 900));
                    newInstance.incrementSkeleton(Random.nextInt(10, 90));
                    newInstance.incrementArmor(Random.nextInt(50, 80));
                    newInstance.incrementHealth(Random.nextInt(50, 80));
                    newInstance.incrementSpeed(Random.nextInt(50, 80));
                    newInstance.incrementAttackDamage(Random.nextInt(50, 80));
                    newInstance.incrementEternalWizard(Random.nextInt(10, 40));
                    newInstance.setMaxTime(Random.nextInt(50400, 100400));

                    var dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
                    var timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

                    var date = LocalDate.now().format(dateFormatter);
                    var time = LocalTime.now().format(timeFormatter);

                    newRunData.setDateAndTime(date + " " + time);
                    newRunData.addStat(ROOMS_CLEARED, Random.nextInt(40, 80));
                    newRunData.addStat(CHESTS_OPENED, Random.nextInt(5, 40));
                    newRunData.addStat(MOBS_KILLED, Random.nextInt(300, 700));
                    newRunData.addStat(TIME_IN_TRIAL, Random.nextInt(38400, 43000));
                    newRunData.addStat(EXPERIENCE, Random.nextInt(400, 700));
                    newRunData.addStat(BRONZE_COIN, Random.nextInt(3050, 9050));
                    newRunData.addStat(SILVER_COIN, Random.nextInt(50, 150));
                    newRunData.addStat(GOLD_COIN, Random.nextInt(10, 50));
                    newRunData.addStat(PLATINUM_COIN, Random.nextInt(5, 30));

                    x.addInstance(newInstance);
                    x.addNewRun(newRunData);
                }

                PacketDistributor.sendToPlayer(serverPlayer, new PlayerTrialDataS2CP(x));
            }
        }
    }
}
