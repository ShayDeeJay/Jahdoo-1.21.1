package org.jahdoo.block.challange_altar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.attachments.player_abilities.ChallengeAltarData;
import org.jahdoo.challenge_game_mode.MobManager;
import org.jahdoo.networking.packet.server2client.AltarBlockS2C;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import static org.jahdoo.block.challange_altar.ChallengeAltarAnim.idleParticleAnim;
import static org.jahdoo.block.challange_altar.ChallengeAltarAnim.onActivationAnim;
import static org.jahdoo.entities.ProjectileAnimations.*;
import static org.jahdoo.registers.AttachmentRegister.CHALLENGE_ALTAR;


public class ChallengeAltarBlockEntity extends BlockEntity implements GeoBlockEntity {
    public final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int privateTicks;
    public int initiateSpawning;
    public boolean beginSpawning;

    public ChallengeAltarBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.CHALLENGE_ALTAR_BE.get(), pPos, pBlockState);
        this.setData(CHALLENGE_ALTAR, ChallengeAltarData.DEFAULT);
    }

    private void updatePacket(){
        if(getLevel() instanceof ServerLevel sLevel){
            var pos = this.getBlockPos();
            var payloads = new AltarBlockS2C(pos, altarData(), privateTicks);
            ModHelpers.sendPacketsToPlayerDistance(pos.getCenter(), 64, sLevel, payloads);
        }
    }

    private ChallengeAltarData altarData(){
        return ChallengeAltarData.getProperties(this);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState blockState) {
        if(altarData().isActive()){
            this.privateTicks++;
            idleParticleAnim(pPos, privateTicks, this.getLevel());
            if(extracted()) return;
            if(altarData().activeMobs().isEmpty()){
                resetSubRound();
                if(privateTicks == 93) onActivationAnim(pLevel, pPos, privateTicks);
                if(privateTicks == 96) summonMobs();
            }
        }
        this.completeRound();
        if(privateTicks == 1) onActivationAnim(pLevel, pPos, privateTicks);
        this.updatePacket();
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
            this.privateTicks = 0;
        }
    }

    private void completeRound() {
        if(altarData().round() > 0 && altarData().round() == altarData().maxRound()){
            ChallengeAltarData.resetAltar(this);
            this.privateTicks = 0;
        }
    }

    private boolean extracted() {
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
        controllers.add(
            new AnimationController<>(this,
                state -> {
                    if(altarData().isActive()) {
                        var animation = privateTicks <= 100 ? ALTAR_SPAWNING : ALTAR_IDLE;
                        return state.setAndContinue(animation);
                    }
                    return PlayState.STOP;
                }
            )
        );
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

