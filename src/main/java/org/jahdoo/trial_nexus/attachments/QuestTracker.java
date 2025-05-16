package org.jahdoo.trial_nexus.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.common.networking.server2client.QuestTrackerS2CP;
import org.jahdoo.common.registers.AttachmentReg;

import java.util.ArrayList;
import java.util.List;

import static net.neoforged.neoforge.network.PacketDistributor.sendToPlayer;

public class QuestTracker implements IAttachment {

    public static final String CLAIMED_QUESTS_TAG = "claimed_quests";
    private List<String> claimedQuests = new ArrayList<>();

    public QuestTracker(){}

    public QuestTracker (List<String> questTracker){
        this.claimedQuests = questTracker;
    }

    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        var localTag = new CompoundTag();

        for (var claimedQuest : claimedQuests) {
            localTag.putString(claimedQuest, claimedQuest);
        }

        nbt.put(CLAIMED_QUESTS_TAG, localTag);
    }

    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        var allKeys = nbt.getCompound(CLAIMED_QUESTS_TAG);
        claimedQuests.addAll(allKeys.getAllKeys());
    }

    public List<String> getClaimedQuests(){
        return claimedQuests;
    }

    public void addClaimedQuest(String id){
        if(!claimedQuests.contains(id)) claimedQuests.add(id);
    }

    public boolean claimedQuest(String questId){
        return claimedQuests.contains(questId);
    }

    public void removeClaimedQuest(String string){
        claimedQuests.removeIf(s -> s.equals(string));
    }

    public void clearClaimedQuests(){
        claimedQuests.clear();
    }

    public static void addClaimedQuest(Player player, String questId){
        getQuestTracker(player).addClaimedQuest(questId);
    }

    public static QuestTracker getQuestTracker(Player player){
        return player.getData(AttachmentReg.QUEST_TRACKER_DATA);
    }

    public static boolean claimedQuest(Player player, String questId){
        return getQuestTracker(player).claimedQuest(questId);
    }

    public static void clearClaimedQuest(ServerPlayer player){
        getQuestTracker(player).clearClaimedQuests();
        sendToPlayer(player, new QuestTrackerS2CP(QuestTracker.getQuestTracker(player)));

    }

    public static final StreamCodec<FriendlyByteBuf, QuestTracker> STREAM_CODEC = StreamCodec.ofMember(
        QuestTracker::serialise,
        QuestTracker::deserialise
    );

    private void serialise(FriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeCollection(claimedQuests, FriendlyByteBuf::writeUtf);
    }

    private static QuestTracker deserialise(FriendlyByteBuf friendlyByteBuf){
        return new QuestTracker(
            friendlyByteBuf.readList(FriendlyByteBuf::readUtf)
        );
    }

    public static final Codec<QuestTracker> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.list(Codec.STRING).fieldOf("QuestTracker").forGetter(QuestTracker::getClaimedQuests)
        ).apply(instance, QuestTracker::new)
    );

}
