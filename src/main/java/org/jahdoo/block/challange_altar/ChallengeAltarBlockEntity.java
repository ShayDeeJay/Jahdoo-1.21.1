package org.jahdoo.block.challange_altar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.attachments.player_abilities.ChallengeAltarData;
import org.jahdoo.challenge.MobManager;
import org.jahdoo.networking.packet.server2client.AltarBlockS2C;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import static net.minecraft.core.Direction.SOUTH;
import static org.jahdoo.block.challange_altar.ChallengeAltarAnim.idleParticleAnim;
import static org.jahdoo.block.challange_altar.ChallengeAltarAnim.onActivationAnim;
import static org.jahdoo.block.loot_chest.LootChestBlock.*;
import static org.jahdoo.entities.EntityAnimations.*;
import static org.jahdoo.registers.AttachmentRegister.CHALLENGE_ALTAR;
import static org.jahdoo.registers.BlocksRegister.*;


public class ChallengeAltarBlockEntity extends BlockEntity implements GeoBlockEntity {
    public ServerBossEvent bossEvent;
    public final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int privateTicks;
    public int initiateSpawning;
    public boolean beginSpawning;

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

    private ChallengeAltarData altarData(){
        return ChallengeAltarData.getProperties(this);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState blockState) {
        if(pLevel instanceof ServerLevel serverLevel){
            manageActivePlayers(pPos);
            tickBossEvent();
            activeSubRoundEvent(pLevel, pPos);
            resetSubRound();
            this.completeRound(serverLevel);
            if (privateTicks == 1) onActivationAnim(pLevel, pPos, privateTicks);
            this.updatePacket();
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
        if(altarData().killedMobs > 0 && altarData().killedMobs == altarData().maxMobs()){
            ChallengeAltarData.resetSubRoundAltar(this);
            bossEvent.removeAllPlayers();
            this.privateTicks = 0;
        }
    }

    private void completeRound(ServerLevel serverLevel) {
        if(altarData().round() > 0 && altarData().round() == altarData().maxRound()){
            ChallengeAltarData.resetAltar(this);
            bossEvent.removeAllPlayers();
            serverLevel.setBlockAndUpdate(getBlockPos(), LOOT_CHEST.get().defaultBlockState().setValue(FACING, SOUTH));
            this.privateTicks = 0;
        }
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
        if(isSubRoundActive()) {
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
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        privateTicks = pTag.getInt("challenge_altar.private");
    }



}

