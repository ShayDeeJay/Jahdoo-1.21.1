package org.jahdoo.block.challange_altar;

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
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.attachments.player_abilities.ChallengeAltarData;
import org.jahdoo.block.SyncedBlockEntity;
import org.jahdoo.challenge.MobManager;
import org.jahdoo.challenge.RewardLootTables;
import org.jahdoo.items.KeyItem;
import org.jahdoo.networking.packet.server2client.AltarBlockS2C;
import org.jahdoo.registers.AttachmentRegister;
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

import java.util.List;

import static org.jahdoo.block.TrialPortalBlock.*;
import static org.jahdoo.block.challange_altar.ChallengeAltarAnim.*;
import static org.jahdoo.block.challange_altar.ChallengeAltarBlock.readyNextSubRound;
import static org.jahdoo.block.loot_chest.LootChestBlock.lootsplosian;
import static org.jahdoo.challenge.RewardLootTables.*;
import static org.jahdoo.entities.EntityAnimations.*;
import static org.jahdoo.registers.AttachmentRegister.CHALLENGE_ALTAR;
import static org.jahdoo.registers.BlocksRegister.TRAIL_PORTAL;
import static org.jahdoo.utils.ModHelpers.Random;


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
            removeKilledMobs();
            if(ChallengeAltarData.isCompleted(this)) {
                buildTick++;
                portalBuilder(pLevel, serverLevel, pPos);
            }

            manageActivePlayers(pPos);
            tickBossEvent();
            activeSubRoundEvent(pLevel, pPos);
            this.completeRound(serverLevel);
            if (privateTicks == 1) onActivationAnim(pLevel, pPos, privateTicks);
            resetSubRound();
            this.updatePacket();
        }
    }

    private void portalBuilder(Level pLevel, ServerLevel serverLevel, BlockPos pos) {
        var south = pos.south(4);
        var getRandomBlock = List.of(Blocks.MOSSY_STONE_BRICKS, Blocks.STONE_BRICKS, Blocks.COBBLESTONE, Blocks.STONE, Blocks.MOSS_BLOCK).get(Random.nextInt(5));
        var mossyStoneBricks = getRandomBlock.defaultBlockState();
        var placeBlockIfAbsent = getBlockPosServerLevelBiConsumer(pLevel, mossyStoneBricks);

        if (placeCounter < 5) {
            // Place blocks vertically for initial steps
            placeBlockIfAbsent.accept(south.east(2).above(placeCounter), serverLevel);
            placeBlockIfAbsent.accept(south.west(2).above(placeCounter), serverLevel);
        } else if (placeCounter < 8) {
            // Normalize placeCounter and place blocks above and below
            int horizontalStep = placeCounter - 5;
            placeBlockIfAbsent.accept(south.east(horizontalStep).above(5), serverLevel);
            placeBlockIfAbsent.accept(south.west(horizontalStep).above(5), serverLevel);
            placeBlockIfAbsent.accept(south.east(horizontalStep).below(), serverLevel);
            placeBlockIfAbsent.accept(south.west(horizontalStep).below(), serverLevel);
        }

        if (buildTick == 15) {
            // Place loot chest and portal blocks
            pLevel.destroyBlock(pos, false);
            var lootLevel = this.altarData().maxRound;
            var setLootValue = (lootLevel + 3) * Random.nextInt(1, 5);
            for(int i = 0; i < 4; i++){
                var rewards = getCoinItems(serverLevel, pos.getCenter(), lootLevel);
                lootsplosian(pos, serverLevel, setLootValue, ColourStore.PERK_GREEN, rewards, false);
            }
            ModHelpers.getSoundWithPosition(pLevel, south, SoundEvents.END_PORTAL_SPAWN, 0.8F, 1.5F);
            for (int i = 0; i < 5; i++) {
                BlockPos portalBase = south.above(i);
                var portalState = TRAIL_PORTAL.get().defaultBlockState().setValue(DIMENSION_KEY, KEY_TRADING_POST);
                serverLevel.setBlockAndUpdate(portalBase, portalState);
                serverLevel.setBlockAndUpdate(portalBase.west(), portalState);
                serverLevel.setBlockAndUpdate(portalBase.east(), portalState);
            }
        }
        placeCounter = (placeCounter + 1) % 8;
    }

    private void activeSubRoundEvent(Level pLevel, BlockPos pPos) {
        if(isSubRoundActive()){
            this.privateTicks++;
            idleParticleAnim(pPos, privateTicks, this.getLevel());
            if(!removeKilledMobs() && altarData().activeMobs().isEmpty()){
                if(privateTicks == 93) onActivationAnim(pLevel, pPos, privateTicks);
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

    private void tickBossEvent() {
        var data = this.altarData();
        var progress = data.maxMobs > 0 ? (float) data.activeMobs().size() / data.maxSpawnableMobs() : 0.0f;
        bossEvent.setVisible(isSubRoundActive() && !data.activeMobs().isEmpty());
        bossEvent.setProgress(progress);
        bossEvent.setName(Component.nullToEmpty(data.activeMobs().size() + " / " + data.maxSpawnableMobs()));
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

    private void summonMobs(int maxSpawn) {
        if(altarData().killedMobs < altarData().maxMobs()) {
            MobManager.summonEntities(this, maxSpawn);
            this.beginSpawning = false;
            this.initiateSpawning = 0;
        }
    }

    private void resetSubRound() {
        if(!completeRound()){
            if (altarData().killedMobs > 0 && altarData().killedMobs == altarData().maxMobs()) {
                this.privateTicks = 0;
                ChallengeAltarData.resetSubRoundAltar(this);
                if(getLevel() instanceof ServerLevel serverLevel){
                    var round = ChallengeAltarData.getRound(this);
                    var maxRound = ChallengeAltarData.getMaxRounds(this);
                    if(round < maxRound){
                        sendLevelPlayersNotification(serverLevel, "Round " + round, SoundEvents.NOTE_BLOCK_BELL.value(), 20);
                    }
                }
                bossEvent.removeAllPlayers();
                readyNextSubRound(this, altarData(), Math.max(1, altarData().round));
            }
        }
    }

    private void completeRound(ServerLevel serverLevel) {
        if(completeRound()){
            this.privateTicks = 0;
            ChallengeAltarData.resetAltar(this);
            bossEvent.removeAllPlayers();
            serverLevel.setData(CHALLENGE_ALTAR, ChallengeAltarData.newRound(altarData().maxRound));
            sendLevelPlayersNotification(serverLevel, "Trial Successful", SoundRegister.END_TRIAL.get(), 40);
        }
    }

    private void sendLevelPlayersNotification(ServerLevel serverLevel, String message, SoundEvent soundEvents, int fadeCalc) {
        ModHelpers.sendPacketsToPlayerDistance(this.getBlockPos().getCenter(), 200, serverLevel,
            serverPlayer -> {
                serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(fadeCalc, fadeCalc + 10, fadeCalc - 10));
                serverPlayer.connection.send(new ClientboundSetTitleTextPacket(ModHelpers.withStyleComponent(message, ColourStore.PERK_GREEN)));
                serverPlayer.playNotifySound(soundEvents, SoundSource.NEUTRAL, 1,1);
            }
        );
    }

    private boolean completeRound() {
        return altarData().round() > 0 && altarData().round() == altarData().maxRound();
    }

    private boolean removeKilledMobs() {
        for (var activeMob : altarData().activeMobs()) {
            if(this.level instanceof ServerLevel serverLevel){
                var entity = serverLevel.getEntity(activeMob);
                if(entity != null && !entity.isAlive()){
                    altarData().removeMob(activeMob);
                    ChallengeAltarData.incrementKilledMobs(this);
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

