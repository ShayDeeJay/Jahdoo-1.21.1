package org.jahdoo.block.loot_chest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.block.SyncedBlockEntity;
import org.jahdoo.items.KeyItem;
import org.jahdoo.particle.ParticleHandlers;
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
import static org.jahdoo.particle.ParticleHandlers.*;
import static org.jahdoo.utils.ModHelpers.*;
import static org.jahdoo.utils.ModHelpers.Random;
import static org.jahdoo.utils.PositionGetters.getInnerRingOfRadiusRandom;

public class LootChestEntity extends SyncedBlockEntity implements GeoBlockEntity {
    public final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int privateTicks;
    public boolean isOpen = false;
    public String getTexture;
    public int getRarity;

    public LootChestEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.LOOT_CHEST_BE.get(), pPos, pBlockState);
        getRarity = Random.nextInt(4);
        getTexture = List.of("loot_chest.png", "loot_chest_1.png", "loot_chest_2.png", "loot_chest_3.png").get(getRarity);
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void tick(Level pLevel, BlockPos pos, BlockState blockState) {
        updateBlock();
        privateTicks++;
        var id = getRarity;

        if(!this.isOpen){
            if(this.privateTicks % (6 - getRarity) == 0){
                for (var vec3 : getInnerRingOfRadiusRandom(pos.getCenter().subtract(0, 0.35, 0), 0.55, Math.max(3, 5 * id))) {
                    var colour1 = KeyItem.getJahdooRarity(new CustomModelData(id));
                    var darker = getColourDarker(colour1.getColour(), 0.5f);
                    var size = Random.nextFloat(1.2f, 1.6f) - ((float) getRarity / 30);
                    var lifetime = 6 + id + Random.nextInt(2, 5);
                    var particleColour = getNonBakedParticles(colour1.getColour(), darker, lifetime, size);
                    var ySpeed = /*0.1 +*/ ((double) id / 60) + Random.nextDouble(0.07, 0.13);

                    pLevel.addParticle(particleColour, vec3.x, vec3.y, vec3.z, 0, ySpeed, 0);
                }
            }
        }

        if(privateTicks == 1){
            getSoundWithPosition(pLevel, pos, SoundEvents.ENDER_EYE_LAUNCH, 1f, 2f);
            getSoundWithPosition(pLevel, pos, SoundEvents.ENDER_EYE_DEATH, 1f, 2f);
        }

        if(privateTicks == 7){
            var volume = 2;
            var pitch = 0.4f;
            var pitch2 = 0.2f;
            getSoundWithPosition(pLevel, pos, SoundEvents.VAULT_PLACE, volume, pitch);
            getSoundWithPosition(pLevel, pos, SoundEvents.IRON_GOLEM_STEP, volume, pitch2);
        }

        if(pLevel instanceof ServerLevel serverLevel){
//            if(privateTicks >= 20) serverLevel.destroyBlock(pPos, false);
            serverLevel.sendBlockUpdated(pos, blockState, blockState, 2);
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
        pTag.putInt("getRarity", getRarity);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        privateTicks = pTag.getInt("loot_chest.private");
        isOpen = pTag.getBoolean("isOpen");
        getTexture = pTag.getString("texture");
        getRarity = pTag.getInt("getRarity");
    }

}

