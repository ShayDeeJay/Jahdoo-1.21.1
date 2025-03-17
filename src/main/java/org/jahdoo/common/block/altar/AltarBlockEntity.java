package org.jahdoo.common.block.altar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.ascension.MobManager;
import org.jahdoo.ascension.RewardLootTables;
import org.jahdoo.ascension.attachments.InstanceData;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.SyncedBlockEntity;
import org.jahdoo.common.networking.server2client.InstanceSyncS2CP;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.SoundReg;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.jahdoo.ascension.BlockSetupManager.setPerkTable;
import static org.jahdoo.ascension.StructureManager.placeLocksWithData;
import static org.jahdoo.ascension.utils.Helpers.getSoundWithPosition;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
import static org.jahdoo.common.block.altar.AltarAnim.idleParticleAnim;
import static org.jahdoo.common.block.altar.AltarAnim.onActivationAnim;
import static org.jahdoo.common.block.loot_chest.LootChestBlock.lootsplosian;
import static org.jahdoo.common.entities.EntityAnimations.ALTAR_IDLE;


public class AltarBlockEntity extends SyncedBlockEntity implements GeoBlockEntity {

    public ServerBossEvent bossEvent;
    public final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int privateTicks;
    public int mobsSpawned;
    public double animateTick;
    public boolean started;
    public boolean beginSpawning;
    public String roomId;
    public List<UUID> spawnedMobs = new ArrayList<>();

    public AltarBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.CHALLENGE_ALTAR_BE.get(), pos, state);
        this.bossEvent = new ServerBossEvent(Component.literal(""), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.NOTCHED_20);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, this::eAnimation));
    }

    private PlayState eAnimation(AnimationState<AltarBlockEntity> state) {
        if(started) return state.setAndContinue(ALTAR_IDLE);
        return PlayState.STOP;
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        this.bossEvent.removeAllPlayers();
    }

    public void summonMobs() {
        this.started = true;
        this.updateBlock();
    }

    private void tickBossEvent() {
//        var data = this.altarData();
//        var progress = data.maxMobs > 0 ? (float) data.activeMobs().size() / data.maxSpawnableMobs() : 0.0f;

//        bossEvent.setVisible(!spawnedMobs.isEmpty());
//        bossEvent.setProgress(progress);
//        bossEvent.setName(Component.nullToEmpty(spawnedMobs.size() + " / " + data.maxSpawnableMobs()));
    }

    private void removeKilledMobs(ServerLevel serverLevel) {
        for (var activeMob : this.spawnedMobs) {
            var entity = serverLevel.getEntity(activeMob);
            if(entity == null || !entity.isAlive()){
                this.spawnedMobs.remove(activeMob);
                this.mobsSpawned++;
                return;
            }
        }
    }

    private static void onCompleteAltar(BlockPos pos, ServerLevel serverLevel) {
        var data = serverLevel.getData(AttachmentReg.INSTANCE_DATA);
        var clearedRooms = data.getClearedRooms();

        lootsplosian(pos, serverLevel, clearedRooms, ColourStore.PERK_GREEN, RewardLootTables.getCoinItems(serverLevel, pos.getCenter(), clearedRooms), false);
        placeLocksWithData(serverLevel, pos.below(2));
        serverLevel.destroyBlock(pos, false);
        getSoundWithPosition(serverLevel, pos, SoundReg.END_TRIAL.get(), 2, 1.5F);
        data.incrementClearedRooms();

        Helpers.sendPacketsToPlayerDistance(pos.getCenter(), 400, serverLevel, serverPlayer -> serverPacket(serverPlayer, data));

        if(clearedRooms % 2 == 0) setPerkTable(serverLevel, pos, 2);
    }

    private static void serverPacket(ServerPlayer serverPlayer, InstanceData data) {
        var cleared = data.getClearedRooms();
        PacketDistributor.sendToPlayer(serverPlayer, new InstanceSyncS2CP(data));

        if(cleared % 5 == 0){
            serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(40, 50, 30));
            serverPlayer.connection.send(new ClientboundSetTitleTextPacket(withStyleComponent("Current Run", ColourStore.PERK_GREEN)));
            serverPlayer.connection.send(new ClientboundSetSubtitleTextPacket(withStyleComponent("Rooms Completed: " + cleared, ColourStore.PERK_GREEN)));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("challenge_altar.private", privateTicks);
        tag.putBoolean("challenge_altar.beginSpawn", beginSpawning);
        tag.putDouble("animate", this.animateTick);
        tag.putBoolean("started", this.started);
        tag.putInt("spawned", this.mobsSpawned);
        tag.putString("roomId", this.roomId);

        var uuids = new CompoundTag();
        for (var spawnedMob : this.spawnedMobs) {
            uuids.putUUID(String.valueOf(spawnedMob), spawnedMob);
        }
        tag.put("uuid", uuids);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        privateTicks = tag.getInt("challenge_altar.private");
        beginSpawning = tag.getBoolean("challenge_altar.beginSpawning");
        animateTick = tag.getDouble("animate");
        started = tag.getBoolean("started");
        mobsSpawned = tag.getInt("spawned");
        roomId = tag.getString("roomId");

        var uuids = tag.getCompound("uuid");
        for (var uuid : uuids.getAllKeys()) {
            this.spawnedMobs.add(uuids.getUUID(uuid));
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if(level instanceof ServerLevel serverLevel){
            if(this.started) {
                this.privateTicks++;
                this.updateBlock();
                idleParticleAnim(pos, privateTicks, level);
            }

            removeKilledMobs(serverLevel);
            tickBossEvent();

            if(privateTicks == 30){
                MobManager.summonEntities(this, roomId);
                this.beginSpawning = false;
            }

            if (privateTicks == 1) onActivationAnim(level, pos, privateTicks);
            if (privateTicks > 30 && started && this.spawnedMobs.isEmpty()) {
                onCompleteAltar(pos, serverLevel);
            }
        }
    }

}

