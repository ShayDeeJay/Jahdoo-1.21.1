package org.jahdoo.common.block.loot_chest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.common.block.SyncedBlockEntity;
import org.jahdoo.common.items.KeyItem;
import org.jahdoo.common.registers.BlockEntityReg;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

import static org.jahdoo.common.entities.EntityAnimations.*;
import static org.jahdoo.common.particle.ParticleHandlers.*;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.ascension.utils.PositionFinders.getInnerRingOfRadiusRandom;

public class LootChestEntity extends SyncedBlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int privateTicks;
    public boolean isOpen = false;
    public String getTexture;
    public int getRarity;

    public LootChestEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.LOOT_CHEST_BE.get(), pos, state);
        getRarity = Random.nextInt(4);
        getTexture = List.of("loot_chest.png", "loot_chest_1.png", "loot_chest_2.png", "loot_chest_3.png").get(getRarity);
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("loot_chest.private", privateTicks);
        tag.putBoolean("isOpen", isOpen);
        tag.putString("texture", getTexture);
        tag.putInt("getRarity", getRarity);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        privateTicks = tag.getInt("loot_chest.private");
        isOpen = tag.getBoolean("isOpen");
        getTexture = tag.getString("texture");
        getRarity = tag.getInt("getRarity");
    }

    public void tick(Level level, BlockPos pos, BlockState blockState) {
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

                    level.addParticle(particleColour, vec3.x, vec3.y, vec3.z, 0, ySpeed, 0);
                }
            }
        }

        if(privateTicks == 1){
            getSoundWithPosition(level, pos, SoundEvents.ENDER_EYE_LAUNCH, 1f, 2f);
            getSoundWithPosition(level, pos, SoundEvents.ENDER_EYE_DEATH, 1f, 2f);
        }

        if(privateTicks == 7){
            var volume = 2;
            var pitch = 0.4f;
            var pitch2 = 0.2f;
            getSoundWithPosition(level, pos, SoundEvents.VAULT_PLACE, volume, pitch);
            getSoundWithPosition(level, pos, SoundEvents.IRON_GOLEM_STEP, volume, pitch2);
        }

        if(level instanceof ServerLevel serverLevel){
//            if(privateTicks >= 20) serverLevel.destroyBlock(pPos, false);
            serverLevel.sendBlockUpdated(pos, blockState, blockState, 2);
        }
    }

}

