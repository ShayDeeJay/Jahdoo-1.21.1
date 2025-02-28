package org.jahdoo.common.block.challange_altar;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.ascension.DimHandler;
import org.jahdoo.ascension.MobManager;
import org.jahdoo.ascension.attachments.player_abilities.ChallengeLevelData;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.SyncedBlockEntity;
import org.jahdoo.common.networking.packet.server2client.AltarBlockS2C;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.SoundReg;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

import static net.minecraft.world.level.block.Blocks.*;
import static org.jahdoo.ascension.RewardLootTables.getCoinItems;
import static org.jahdoo.ascension.attachments.player_abilities.ChallengeLevelData.*;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.common.block.TrialPortalBlock.DIMENSION_KEY;
import static org.jahdoo.common.block.TrialPortalBlock.KEY_TRADING_POST;
import static org.jahdoo.common.block.challange_altar.ChallengeAltarAnim.*;
import static org.jahdoo.common.block.challange_altar.ChallengeAltarBlock.readyNextSubRound;
import static org.jahdoo.common.block.loot_chest.LootChestBlock.lootsplosian;
import static org.jahdoo.common.entities.EntityAnimations.ALTAR_IDLE;
import static org.jahdoo.common.entities.EntityAnimations.ALTAR_SPAWNING;
import static org.jahdoo.common.registers.AttachmentReg.CHALLENGE_ALTAR;
import static org.jahdoo.common.registers.BlockReg.TRAIL_PORTAL;


public class ChallengeAltarBlockEntity extends SyncedBlockEntity implements GeoBlockEntity {

    public ServerBossEvent bossEvent;
    public final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int privateTicks;
    public double animateTick;
    private int initiateSpawning;
    private boolean beginSpawning;
    private int buildTick;
    private int placeCounter;

    public ChallengeAltarBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.CHALLENGE_ALTAR_BE.get(), pos, state);
        this.setData(CHALLENGE_ALTAR, DEFAULT);
        this.bossEvent = new ServerBossEvent(Component.literal(""), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.NOTCHED_20);
    }

    public ChallengeLevelData altarData(){
        return getProperties(this);
    }

    private boolean isSubRoundActive() {
        return altarData().isSubRoundActive(this);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private boolean completeRound() {
        return altarData().round() > 0 && altarData().round() == altarData().maxRound();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, this::eAnimation));
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        this.bossEvent.removeAllPlayers();
    }

    private void summonMobs(int maxSpawn) {
        if(altarData().killedMobs < altarData().maxMobs()) {
            MobManager.summonEntities(this, maxSpawn);
            this.beginSpawning = false;
            this.initiateSpawning = 0;
        }
    }

    private void updatePacket(){
        if(getLevel() instanceof ServerLevel getLevel){
            var pos = this.getBlockPos();
            var payloads = new AltarBlockS2C(pos, altarData(), privateTicks);
            Helpers.sendPacketsToPlayerDistance(pos.getCenter(), 64, getLevel, payloads);
        }
    }

    private void tickBossEvent() {
        var data = this.altarData();
        var progress = data.maxMobs > 0 ? (float) data.activeMobs().size() / data.maxSpawnableMobs() : 0.0f;

        bossEvent.setVisible(isSubRoundActive() && !data.activeMobs().isEmpty());
        bossEvent.setProgress(progress);
        bossEvent.setName(Component.nullToEmpty(data.activeMobs().size() + " / " + data.maxSpawnableMobs()));
    }

    private void completeRound(ServerLevel level) {
        if(completeRound()){
            this.privateTicks = 0;
            resetAltar(this);
            bossEvent.removeAllPlayers();
            level.setData(CHALLENGE_ALTAR, newRound(altarData().maxRound, DimHandler.TRADING_POST));
            sendLevelPlayersNotification(level, "Trial Successful", SoundReg.END_TRIAL.get(), 40);
        }
    }

    private PlayState eAnimation(AnimationState<ChallengeAltarBlockEntity> state) {
        if(isSubRoundActive() && !isCompleted(this)) {
            var animation = privateTicks <= 100 ? ALTAR_SPAWNING : ALTAR_IDLE;
            return state.setAndContinue(animation);
        }
        return PlayState.STOP;
    }

    private void sendLevelPlayersNotification(ServerLevel level, String message, SoundEvent sound, int fadeCalc) {
        Helpers.sendPacketsToPlayerDistance(this.getBlockPos().getCenter(), 200, level,
            serverPlayer -> {
                serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(fadeCalc, fadeCalc + 10, fadeCalc - 10));
                serverPlayer.connection.send(new ClientboundSetTitleTextPacket(Helpers.withStyleComponent(message, ColourStore.PERK_GREEN)));
                serverPlayer.playNotifySound(sound, SoundSource.NEUTRAL, 1,1);
            }
        );
    }

    private void manageActivePlayers(BlockPos pos) {
        if(!(this.getLevel() instanceof ServerLevel serverLevel)) return;

        for (var player : serverLevel.players()) {
            if(!(player instanceof ServerPlayer serverPlayer)) return;
            var inEven = bossEvent.getPlayers().contains(serverPlayer);

            if (serverPlayer.distanceToSqr(pos.getCenter()) < 10000) {
                if (!inEven) bossEvent.addPlayer(serverPlayer);
            } else {
                if (inEven) bossEvent.removePlayer(serverPlayer);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("challenge_altar.private", privateTicks);
        tag.putInt("challenge_altar.initiateSpawn", initiateSpawning);
        tag.putInt("challenge_altar.buildTick", buildTick);
        tag.putInt("challenge_altar.placeCounter", placeCounter);
        tag.putBoolean("challenge_altar.beginSpawn", beginSpawning);
        tag.putDouble("animate", this.animateTick);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        privateTicks = tag.getInt("challenge_altar.private");
        initiateSpawning = tag.getInt("challenge_altar.initiateSpawn");
        buildTick = tag.getInt("challenge_altar.buildTick");
        placeCounter = tag.getInt("challenge_altar.placeCounter");
        beginSpawning = tag.getBoolean("challenge_altar.beginSpawning");
        animateTick = tag.getDouble("animate");
    }

    private boolean removeKilledMobs() {
        for (var activeMob : altarData().activeMobs()) {
            if(this.level instanceof ServerLevel serverLevel){
                var entity = serverLevel.getEntity(activeMob);
                if(entity != null && !entity.isAlive()){
                    altarData().removeMob(activeMob);
                    incrementKilledMobs(this);
                    return true;
                }
            }
        }
        return false;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if(level instanceof ServerLevel serverLevel){
            removeKilledMobs();
            if(isCompleted(this)) {
                buildTick++;
                portalBuilder(level, serverLevel, pos);
            }

            manageActivePlayers(pos);
            tickBossEvent();
            activeSubRoundEvent(level, pos);
            this.completeRound(serverLevel);
            if (privateTicks == 1) onActivationAnim(level, pos, privateTicks);
            resetSubRound();
            this.updatePacket();
        }
    }

    private void resetSubRound() {
        if(!completeRound()){
            if (altarData().killedMobs > 0 && altarData().killedMobs == altarData().maxMobs()) {
                this.privateTicks = 0;
                resetSubRoundAltar(this);
                if(getLevel() instanceof ServerLevel serverLevel){
                    var round = getRound(this);
                    var maxRound = getMaxRounds(this);
                    if(round < maxRound){
                        sendLevelPlayersNotification(serverLevel, "Round " + round, SoundReg.END_TRIAL.get(), 20);
                    }
                }
                bossEvent.removeAllPlayers();
                readyNextSubRound(this, altarData(), Math.max(1, altarData().round));
            }
        }
    }

    private void activeSubRoundEvent(Level level, BlockPos pos) {
        if(isSubRoundActive()){
            this.privateTicks++;
            idleParticleAnim(pos, privateTicks, this.getLevel());
            if(!removeKilledMobs() && altarData().activeMobs().isEmpty()){
                if(privateTicks == 93) onActivationAnim(level, pos, privateTicks);
                if(privateTicks == 96) summonMobs(altarData().maxSpawnableMobs());
            }

            var inPlay = altarData().activeMobs().size();
            var allowed = altarData().maxMobsOnMap();
            var maxMobs = altarData().maxMobs();
            var killedMobs = altarData().killedMobs();
            if(privateTicks < 100 || inPlay > allowed) return;

            if((killedMobs + inPlay) < maxMobs && inPlay < allowed) {
                if(Random.nextInt(10) == 0) summonMobs(1);
            }
        }
    }

    private void portalBuilder(Level level, ServerLevel serverLevel, BlockPos pos) {
        var south = pos.south(4);
        var getRandomBlock = List.of(MOSSY_STONE_BRICKS, STONE_BRICKS, COBBLESTONE, STONE, MOSS_BLOCK).get(Random.nextInt(5));
        var mossyStoneBricks = getRandomBlock.defaultBlockState();
        var placeBlockIfAbsent = getBlockPosServerLevelBiConsumer(level, mossyStoneBricks);

        if (placeCounter < 5) {
            placeBlockIfAbsent.accept(south.east(2).above(placeCounter), serverLevel);
            placeBlockIfAbsent.accept(south.west(2).above(placeCounter), serverLevel);
        } else if (placeCounter < 8) {
            var horizontalStep = placeCounter - 5;
            placeBlockIfAbsent.accept(south.east(horizontalStep).above(5), serverLevel);
            placeBlockIfAbsent.accept(south.west(horizontalStep).above(5), serverLevel);
            placeBlockIfAbsent.accept(south.east(horizontalStep).below(), serverLevel);
            placeBlockIfAbsent.accept(south.west(horizontalStep).below(), serverLevel);
        }

        if (buildTick == 15) {
            level.destroyBlock(pos, false);
            var lootLevel = this.altarData().maxRound;
            var setLootValue = (lootLevel + 3) * Random.nextInt(1, 5);
            for(int i = 0; i < 4; i++){
                var rewards = getCoinItems(serverLevel, pos.getCenter(), lootLevel);
                lootsplosian(pos, serverLevel, setLootValue, ColourStore.PERK_GREEN, rewards, false);
            }
            Helpers.getSoundWithPosition(level, south, SoundEvents.END_PORTAL_SPAWN, 0.8F, 1.5F);
            for (var i = 0; i < 5; i++) {
                BlockPos portalBase = south.above(i);
                var portalState = TRAIL_PORTAL.get().defaultBlockState().setValue(DIMENSION_KEY, KEY_TRADING_POST);
                serverLevel.setBlockAndUpdate(portalBase, portalState);
                serverLevel.setBlockAndUpdate(portalBase.west(), portalState);
                serverLevel.setBlockAndUpdate(portalBase.east(), portalState);
            }
        }

        placeCounter = (placeCounter + 1) % 8;
    }

}

