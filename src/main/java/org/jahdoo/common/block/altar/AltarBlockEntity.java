package org.jahdoo.common.block.altar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.ascension.MobManager;
import org.jahdoo.ascension.attachments.player_abilities.ChallengeLevelData;
import org.jahdoo.ascension.attachments.player_abilities.InstanceData;
import org.jahdoo.common.block.SyncedBlockEntity;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.BlockEntityReg;
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

import static org.jahdoo.ascension.StructureManager.placeLocksWithData;
import static org.jahdoo.ascension.attachments.player_abilities.ChallengeLevelData.getProperties;
import static org.jahdoo.common.block.altar.AltarAnim.idleParticleAnim;
import static org.jahdoo.common.block.altar.AltarAnim.onActivationAnim;
import static org.jahdoo.common.entities.EntityAnimations.ALTAR_IDLE;


public class AltarBlockEntity extends SyncedBlockEntity implements GeoBlockEntity {

    public ServerBossEvent bossEvent;
    public final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int privateTicks;
    public int mobsSpawned;
    public double animateTick;
    public boolean started;
    public boolean beginSpawning;
    public List<UUID> spawnedMobs = new ArrayList<>();

    public AltarBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.CHALLENGE_ALTAR_BE.get(), pos, state);
        this.bossEvent = new ServerBossEvent(Component.literal(""), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.NOTCHED_20);
    }

    public ChallengeLevelData altarData(){
        return getProperties(this);
    }

    public InstanceData getInstanceData(){
       return this.getLevel().getData(AttachmentReg.INSTANCE_DATA);
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
        MobManager.summonEntities(this);
        this.beginSpawning = false;
        this.started = true;
        this.updateBlock();
    }

    private void tickBossEvent() {
        var data = this.altarData();
        var progress = data.maxMobs > 0 ? (float) data.activeMobs().size() / data.maxSpawnableMobs() : 0.0f;

        bossEvent.setVisible(!spawnedMobs.isEmpty());
        bossEvent.setProgress(progress);
        bossEvent.setName(Component.nullToEmpty(spawnedMobs.size() + " / " + data.maxSpawnableMobs()));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("challenge_altar.private", privateTicks);
        tag.putBoolean("challenge_altar.beginSpawn", beginSpawning);
        tag.putDouble("animate", this.animateTick);
        tag.putBoolean("started", this.started);
        tag.putInt("spawned", this.mobsSpawned);

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

        var uuids = tag.getCompound("uuid");
        for (var uuid : uuids.getAllKeys()) {
            this.spawnedMobs.add(uuids.getUUID(uuid));
        }
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

    public void tick(Level level, BlockPos pos, BlockState state) {
        if(level instanceof ServerLevel serverLevel){
            if(this.started) {
                this.privateTicks++;
                idleParticleAnim(pos, privateTicks, level);
            }

            System.out.println(this.spawnedMobs);
            removeKilledMobs(serverLevel);
            tickBossEvent();

            if (privateTicks == 1) onActivationAnim(level, pos, privateTicks);
            if (started && this.spawnedMobs.isEmpty()) {
                placeLocksWithData(serverLevel, pos);
                level.destroyBlock(pos, false);
            }
        }
    }

}

