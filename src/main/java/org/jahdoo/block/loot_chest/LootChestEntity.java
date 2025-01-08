package org.jahdoo.block.loot_chest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.block.SyncedBlockEntity;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

import static org.jahdoo.entities.EntityAnimations.*;

public class LootChestEntity extends SyncedBlockEntity implements GeoBlockEntity {
    public final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int privateTicks;
    boolean isOpen = false;
    public String getTexture;

    public LootChestEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.LOOT_CHEST_BE.get(), pPos, pBlockState);
        getTexture = ModHelpers.getRandomListElement(
            List.of("loot_chest.png", "loot_chest_1.png", "loot_chest_2.png", "loot_chest_3.png")
        );
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState blockState) {
        updateBlock();
        privateTicks++;

        if(privateTicks == 1){
            ModHelpers.getSoundWithPosition(pLevel, pPos, SoundEvents.ENDER_EYE_LAUNCH, 1f, 2f);
            ModHelpers.getSoundWithPosition(pLevel, pPos, SoundEvents.ENDER_EYE_DEATH, 1f, 2f);
        }

        if(privateTicks == 7){
            var volume = 2;
            var pitch = 0.4f;
            var pitch2 = 0.2f;
            ModHelpers.getSoundWithPosition(pLevel, pPos, SoundEvents.VAULT_PLACE, volume, pitch);
            ModHelpers.getSoundWithPosition(pLevel, pPos, SoundEvents.IRON_GOLEM_STEP, volume, pitch2);
        }

        if(pLevel instanceof ServerLevel serverLevel){
//            if(privateTicks >= 20) serverLevel.destroyBlock(pPos, false);
            serverLevel.sendBlockUpdated(pPos, blockState, blockState, 2);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, this::animation));
    }

    private PlayState animation(AnimationState<LootChestEntity> state) {
        if(this.isOpen) return state.setAndContinue(OPEN_LOOT);
        return state.setAndContinue(SPAWN_CHEST);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.putInt("loot_chest.private", privateTicks);
        pTag.putBoolean("isOpen", isOpen);
        pTag.putString("texture", getTexture);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        privateTicks = pTag.getInt("loot_chest.private");
        isOpen = pTag.getBoolean("isOpen");
        getTexture = pTag.getString("texture");
    }

}

