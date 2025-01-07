package org.jahdoo.block.loot_chest;

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
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import static org.jahdoo.block.challange_altar.ChallengeAltarAnim.idleParticleAnim;
import static org.jahdoo.block.challange_altar.ChallengeAltarAnim.onActivationAnim;
import static org.jahdoo.entities.ProjectileAnimations.*;
import static org.jahdoo.registers.AttachmentRegister.CHALLENGE_ALTAR;


public class LootChestEntity extends BlockEntity implements GeoBlockEntity {
    public ServerBossEvent bossEvent;
    public final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int privateTicks;
    boolean isOpen = false;

    public LootChestEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.LOOT_CHEST_BE.get(), pPos, pBlockState);
    }

    @Override
    public boolean isRemoved() {
        return super.isRemoved();
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState blockState) {
        if(!pLevel.isClientSide){
            privateTicks++;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
            new AnimationController<>(this,
                state -> {
                    if(this.isOpen) return state.setAndContinue(OPEN_LOOT);
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

