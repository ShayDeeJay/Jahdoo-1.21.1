package org.jahdoo.block.challange_altar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.attachments.player_abilities.ChallengeAltarData;
import org.jahdoo.block.SyncedBlockEntity;
import org.jahdoo.challenge.MobManager;
import org.jahdoo.networking.packet.server2client.AltarBlockS2C;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import static org.jahdoo.block.challange_altar.ChallengeAltarAnim.*;
import static org.jahdoo.block.challange_altar.ChallengeAltarBlock.readyNextSubRound;
import static org.jahdoo.block.loot_chest.LootChestBlock.FACING;
import static org.jahdoo.entities.EntityAnimations.*;
import static org.jahdoo.registers.AttachmentRegister.CHALLENGE_ALTAR;
import static org.jahdoo.registers.BlocksRegister.LOOT_CHEST;
import static org.jahdoo.registers.BlocksRegister.TRAIL_PORTAL;


public class ChallengeAltarBlockEntity extends SyncedBlockEntity implements GeoBlockEntity {
    public ServerBossEvent bossEvent;
    public final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int privateTicks;
    public int initiateSpawning;
    public boolean beginSpawning;
    public double animateTick;
    public int buildTick;
    public int placeCounter;


    public ChallengeAltarBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.CHALLENGE_ALTAR_BE.get(), pPos, pBlockState);
        this.setData(CHALLENGE_ALTAR, ChallengeAltarData.DEFAULT);
        this.bossEvent = new ServerBossEvent(Component.literal(""), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.NOTCHED_20);
    }

    private void updatePacket(){
        if(getLevel() instanceof ServerLevel sLevel){
            var pos = this.getBlockPos();
            var payloads = new AltarBlockS2C(pos, altarData(), privateTicks);
            ModHelpers.sendPacketsToPlayerDistance(pos.getCenter(), 64, sLevel, payloads);
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        this.bossEvent.removeAllPlayers();
    }

    @Override
    public boolean isRemoved() {
        return super.isRemoved();
    }

    public ChallengeAltarData altarData(){
        return ChallengeAltarData.getProperties(this);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState blockState) {
        if(pLevel instanceof ServerLevel serverLevel){
            if(ChallengeAltarData.isCompleted(this)) {
                buildTick++;
                portalBuilder(pLevel, serverLevel, pPos);
            } else {
                manageActivePlayers(pPos);
                tickBossEvent();
                activeSubRoundEvent(pLevel, pPos);
                this.completeRound(serverLevel);
                if (privateTicks == 1) onActivationAnim(pLevel, pPos, privateTicks);
                this.updatePacket();
                resetSubRound();
            }
        }
    }

    private void portalBuilder(Level pLevel, ServerLevel serverLevel, BlockPos pos) {
        if (buildTick % 3 == 0) {
            var mossyStoneBricks = Blocks.MOSSY_STONE_BRICKS;
            var south = pos.south(4);
            var breakSound = mossyStoneBricks.defaultBlockState().getSoundType().getBreakSound();

            if (placeCounter < 5) {
                var above = south.east(2).above(placeCounter);
                var above1 = south.west(2).above(placeCounter);
                if(serverLevel.getBlockState(above1).isAir()){
                    ModHelpers.getSoundWithPosition(pLevel, above1, breakSound);
                    serverLevel.setBlockAndUpdate(above1, mossyStoneBricks.defaultBlockState());
                }
                if(serverLevel.getBlockState(above).canBeReplaced()){
                    ModHelpers.getSoundWithPosition(pLevel, above, breakSound);
                    serverLevel.setBlockAndUpdate(above, mossyStoneBricks.defaultBlockState());
                }
            } else if (placeCounter < 8) {
                int horizontalStep = placeCounter - 5; // Normalize to 0-2 range
                var above = south.east(horizontalStep).above(5);
                var above1 = south.west(horizontalStep).above(5);
                if(serverLevel.getBlockState(above1).isAir()){
                    ModHelpers.getSoundWithPosition(pLevel, above1, breakSound);
                    serverLevel.setBlockAndUpdate(above1, mossyStoneBricks.defaultBlockState());
                }
                if(serverLevel.getBlockState(above).isAir()){
                    ModHelpers.getSoundWithPosition(pLevel, above, breakSound);
                    serverLevel.setBlockAndUpdate(above, mossyStoneBricks.defaultBlockState());
                }
            }

            if(buildTick == 30){
                serverLevel.setBlockAndUpdate(pos, LOOT_CHEST.get().defaultBlockState().setValue(FACING, Direction.SOUTH));
                ModHelpers.getSoundWithPosition(pLevel, south, SoundEvents.END_PORTAL_SPAWN, 0.8F, 1.5F);
                for (int i = 0; i < 5; i ++){
                    serverLevel.setBlockAndUpdate(south.above(i), TRAIL_PORTAL.get().defaultBlockState());
                    serverLevel.setBlockAndUpdate(south.west().above(i), TRAIL_PORTAL.get().defaultBlockState());
                    serverLevel.setBlockAndUpdate(south.east().above(i), TRAIL_PORTAL.get().defaultBlockState());
                }
            }

            placeCounter++;
            if (placeCounter >= 8) placeCounter = 0;
        }
    }

    private void activeSubRoundEvent(Level pLevel, BlockPos pPos) {
        if(isSubRoundActive()){
            this.privateTicks++;
            idleParticleAnim(pPos, privateTicks, this.getLevel());
            if(!removeKilledMobs() && altarData().activeMobs().isEmpty()){
                if(privateTicks == 93) onActivationAnim(pLevel, pPos, privateTicks);
                if(privateTicks == 96) summonMobs();
            }
        }
    }

    private void tickBossEvent() {
        float progress = this.altarData().maxMobs > 0 ? (float) this.altarData().activeMobs().size() / this.altarData().maxSpawnableMobs() : 0.0f;
        bossEvent.setVisible(isSubRoundActive() && !altarData().activeMobs().isEmpty());
        bossEvent.setProgress(progress);
        bossEvent.setName(Component.nullToEmpty(this.altarData().activeMobs().size() + " / " + this.altarData().maxSpawnableMobs()));
    }

    private void manageActivePlayers(BlockPos pPos) {
        if(!(this.getLevel() instanceof ServerLevel serverLevel)) return;
        for (var player : serverLevel.players()) {
            if(!(player instanceof ServerPlayer serverPlayer)) return;
            var inEven = bossEvent.getPlayers().contains(serverPlayer);

            if (serverPlayer.distanceToSqr(pPos.getCenter()) < 10000) {
                if (!inEven) bossEvent.addPlayer(serverPlayer);
            } else {
                if (inEven) bossEvent.removePlayer(serverPlayer);
            }
        }
    }

    private boolean isSubRoundActive() {
        return altarData().isSubRoundActive(this);
    }

    private void summonMobs() {
        if(altarData().killedMobs < altarData().maxMobs()) {
            MobManager.summonEntities(this);
            this.beginSpawning = false;
            this.initiateSpawning = 0;
        }
    }

    private void resetSubRound() {
        if(!ChallengeAltarData.isCompleted(this)){
            if (altarData().killedMobs > 0 && altarData().killedMobs == altarData().maxMobs()) {
                ChallengeAltarData.resetSubRoundAltar(this);
                bossEvent.removeAllPlayers();
                this.privateTicks = 0;
                readyNextSubRound(this, altarData(), Math.max(1, altarData().round));
            }
        }
    }

    private void completeRound(ServerLevel serverLevel) {
        if(completeRound()){
            ChallengeAltarData.resetAltar(this);
            bossEvent.removeAllPlayers();
            this.privateTicks = 0;
            serverLevel.setData(CHALLENGE_ALTAR, ChallengeAltarData.newRound(altarData().maxRound));
            ModHelpers.sendPacketsToPlayerDistance(this.getBlockPos().getCenter(), 200, serverLevel,
                serverPlayer -> {
                    serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(40, 50, 30));
                    serverPlayer.connection.send(new ClientboundSetTitleTextPacket(ModHelpers.withStyleComponent("Trial Successful", ColourStore.PERK_GREEN)));
                    serverPlayer.playNotifySound(SoundRegister.END_TRIAL.get(), SoundSource.NEUTRAL, 1,1);
                }
            );
        }
    }

    private boolean completeRound() {
        return altarData().round() > 0 && altarData().round() == altarData().maxRound();
    }

    private boolean removeKilledMobs() {
        for (var activeMob : altarData().activeMobs()) {
            if(this.level instanceof ServerLevel serverLevel){
                var entity = serverLevel.getEntity(activeMob);
                if(entity == null || !entity.isAlive()){
                    altarData().removeMob(activeMob);
                    ChallengeAltarData.incrementKilledMobs(this);
                    if(altarData().activeMobs().isEmpty()) this.privateTicks = 0;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, this::eAnimation));
    }

    private PlayState eAnimation(AnimationState<ChallengeAltarBlockEntity> state) {
        if(isSubRoundActive() && !ChallengeAltarData.isCompleted(this)) {
            var animation = privateTicks <= 100 ? ALTAR_SPAWNING : ALTAR_IDLE;
            return state.setAndContinue(animation);
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.putInt("challenge_altar.private", privateTicks);
        pTag.putInt("challenge_altar.initiateSpawn", initiateSpawning);
        pTag.putInt("challenge_altar.buildTick", buildTick);
        pTag.putInt("challenge_altar.placeCounter", placeCounter);
        pTag.putBoolean("challenge_altar.beginSpawn", beginSpawning);
        pTag.putDouble("animate", this.animateTick);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        privateTicks = pTag.getInt("challenge_altar.private");
        initiateSpawning = pTag.getInt("challenge_altar.initiateSpawn");
        buildTick = pTag.getInt("challenge_altar.buildTick");
        placeCounter = pTag.getInt("challenge_altar.placeCounter");
        beginSpawning = pTag.getBoolean("challenge_altar.beginSpawning");
        animateTick = pTag.getDouble("animate");
    }



}

