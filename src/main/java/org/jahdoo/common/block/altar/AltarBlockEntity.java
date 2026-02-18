package org.jahdoo.common.block.altar;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jahdoo.common.block.altar.altar_states.ActiveAltar;
import org.jahdoo.common.block.altar.altar_states.EndAltar;
import org.jahdoo.common.block.altar.altar_states.StartAltar;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.level_manager.InstanceDifficulty;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.jetbrains.annotations.NotNull;
import org.shaydee.shaydeeapi.block.SyncedBlockEntity;
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

import static org.jahdoo.common.entities.EntityAnimations.ALTAR_IDLE;
import static org.jahdoo.trial_nexus.level_manager.InstanceDifficulty.getFromLevel;


public class AltarBlockEntity extends SyncedBlockEntity implements GeoBlockEntity {

    public int mobsSpawned;
    public boolean started;
    public boolean spawnedChampion;
    public boolean spawnedSafe;

    public String roomId;
    public Direction direction;
    public List<LivingEntity> spawnableMobs = new ArrayList<>();
    public List<UUID> onField = new ArrayList<>();
    public final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AltarBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.CHALLENGE_ALTAR_BE.get(), pos, state);
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

    public static @NotNull AABB roomBounding(BlockPos pos) {
        var i = 24;
        return new AABB(
            pos.getX() - i, pos.getY() - 2, pos.getZ() - i,
            pos.getX() + i, pos.getY() + 10, pos.getZ() + i
        );
    }

    public InstanceData data(){
        if(!(getLevel() instanceof CustomLevel cLevel && LevelGenerator.isNexus(cLevel))) return new InstanceData();
        return cLevel.getData(AttachmentReg.INSTANCE_DATA);
    }

    @NotNull
    public InstanceDifficulty getInstanceDifficulty() {
        return getFromLevel(data());
    }

    public static Iterable<BlockPos> getAllBlockPos(AABB box) {
        return BlockPos.betweenClosed(
            Mth.floor(box.minX), Mth.floor(box.minY), Mth.floor(box.minZ),
            Mth.floor(box.maxX), Mth.floor(box.maxY), Mth.floor(box.maxZ)
        );
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if(!(level instanceof ServerLevel serverLevel)) return;

        StartAltar.onStartAltar(this, serverLevel);
        ActiveAltar.onActiveAltar(this, serverLevel);
        EndAltar.onEndAlter(this, serverLevel);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("started", started);
        tag.putInt("spawned", mobsSpawned);
        tag.putString("roomId", roomId);
        tag.putString("direction", direction.name());
        tag.putBoolean("spawned_champion", spawnedChampion);
        tag.putBoolean("spawned_safe", spawnedSafe);

        var allowedMobs = new CompoundTag();
        for (var spawnedMob : spawnableMobs) {
            allowedMobs.putUUID(String.valueOf(spawnedMob.getUUID()), spawnedMob.getUUID());
        }
        tag.put("allowedMobs", allowedMobs);

        var spawnedMobs = new CompoundTag();
        for (var spawnedMob : onField) {
            spawnedMobs.putUUID(String.valueOf(spawnedMob), spawnedMob);
        }
        tag.put("uuid", spawnedMobs);

    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        started = tag.getBoolean("started");
        mobsSpawned = tag.getInt("spawned");
        roomId = tag.getString("roomId");
        spawnedChampion = tag.getBoolean("spawned_champion");
        spawnedSafe = tag.getBoolean("spawned_safe");

        for (var direction1 : Direction.stream().toList()) {
            if(tag.getString("direction").equals(direction1.name())){
                direction = direction1;
            }
        }

        var allowedMobs = tag.getCompound("allowedMobs");
        for (var uuid : allowedMobs.getAllKeys()) {
            if(level instanceof ServerLevel serverLevel){
                spawnableMobs.add((LivingEntity) serverLevel.getEntity(allowedMobs.getUUID(uuid)));
            }
        }

        var uuids = tag.getCompound("uuid");
        for (var uuid : uuids.getAllKeys()) {
            onField.add(uuids.getUUID(uuid));
        }
    }
}

