package org.jahdoo.common.block.altar;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jahdoo.ascension.mobs.MobManager;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.SyncedBlockEntity;
import org.jahdoo.common.entities.ITamableEntity;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.SoundReg;
import org.jetbrains.annotations.NotNull;
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

import static net.minecraft.core.BlockPos.containing;
import static net.minecraft.world.ItemInteractionResult.FAIL;
import static net.minecraft.world.ItemInteractionResult.SUCCESS;
import static org.jahdoo.ascension.level_manager.BlockSetupManager.*;
import static org.jahdoo.ascension.level_manager.StructureManager.placeLocksWithData;
import static org.jahdoo.ascension.mobs.MobManager.addAndPositionEntity;
import static org.jahdoo.ascension.utils.Helpers.getSoundWithPosition;
import static org.jahdoo.ascension.utils.Helpers.listRandom;
import static org.jahdoo.ascension.utils.PositionFinders.innerRadiusRandom;
import static org.jahdoo.common.block.altar.AltarAnim.idleParticleAnim;
import static org.jahdoo.common.block.altar.AltarAnim.onActivationAnim;
import static org.jahdoo.common.entities.EntityAnimations.ALTAR_IDLE;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;


public class AltarBlockEntity extends SyncedBlockEntity implements GeoBlockEntity {

    public final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int privateTicks;
    public int mobsSpawned;
    public double animateTick;
    public boolean started;
    public boolean beginSpawning;
    public Direction direction;
    public String roomId;
    public List<LivingEntity> spawnableMobs = new ArrayList<>();
    public List<UUID> onField = new ArrayList<>();

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

    public void summonMobs() {
        this.started = true;
        this.updateBlock();
    }

    private void removeKilledMobs(ServerLevel serverLevel) {
        for (var activeMob : this.onField) {
            var entity = serverLevel.getEntity(activeMob);
            if(entity == null || !entity.isAlive()){
                this.onField.remove(activeMob);
                this.mobsSpawned++;
                return;
            }
        }
    }

    private static @NotNull AABB roomBounding(BlockPos pos) {
        return new AABB(
            pos.getX() - 23, pos.getY() - 3, pos.getZ() - 23,
            pos.getX() + 24, pos.getY(), pos.getZ() + 24
        );
    }

    private void autoStartAltar(ServerLevel serverLevel, BlockPos pos) {
        if(!this.started){
            var getWithBounding = roomBounding(pos);
            for (var entity : serverLevel.getEntities(null, getWithBounding)) {
                if(entity instanceof Player) startAltar(pos, this, serverLevel);
            }
        }
    }

    public static ItemInteractionResult startAltar(BlockPos pos, AltarBlockEntity altarE, ServerLevel serverLevel) {
        if(!altarE.started){
            blockExitBarrier(serverLevel, pos);
            altarE.setData(INSTANCE_DATA, serverLevel.getData(INSTANCE_DATA));
            Helpers.getSoundWithPosition(serverLevel, pos, SoundReg.START_TRIAL.get(), 2);
            altarE.summonMobs();
            return SUCCESS;
        }

        return FAIL;
    }

    private void reAssignTarget(Level getLevel, BlockPos pos){
        if(!(getLevel instanceof CustomLevel level)) return;
        var asList = new ArrayList<Mob>();
        var getWithBounding = roomBounding(pos);
        level.getEntities(null, getWithBounding).forEach(
            e -> {
                if ((!(e instanceof ITamableEntity t && t.getOwner() != null) || !(e instanceof Player)) && e instanceof Mob mob) {
                    asList.add(mob);
                }
            }
        );

        for (var entity : asList) {
            if(entity.getTarget() == null){
                var validTargets = level.getEntities(null, getWithBounding).stream().filter(
                    livEnt -> livEnt instanceof ITamableEntity t && t.getOwner() != null || livEnt instanceof Player
                ).toList();

                if(!validTargets.isEmpty()) entity.setTarget((LivingEntity) Helpers.listRandom(validTargets));
            }
        }
    }

    public int getMaxAllowedMobsOnField(ServerLevel serverLevel){
        var data = serverLevel.getData(AttachmentReg.INSTANCE_DATA);
        return switch (data.getDifficulty()){
            case Helpers.MEDIUM -> 40;
            case Helpers.HARD -> 60;
            default -> 20;
        };
    }

    private void onCompleteAltar(BlockPos pos, ServerLevel serverLevel) {
        var data = serverLevel.getData(AttachmentReg.INSTANCE_DATA);
        var clearedRooms = data.getClearedRooms();

        data.incrementClearedRooms();
        placeLocksWithData(serverLevel, pos.below(2), false);
        serverLevel.destroyBlock(pos, false);
        getSoundWithPosition(serverLevel, pos, SoundReg.END_TRIAL.get(), 2, 1.5F);

        Helpers.sendPacketsToPlayerDistance(getBlockPos().getCenter(), 400, serverLevel,
            (serverPlayer) -> {
                serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(5, 20, 20));
                serverPlayer.connection.send(new ClientboundSetTitleTextPacket(Helpers.withStyleComponent("ALTAR COMPLETE", ColourStore.MAGNET_RANGE_GREEN)));
            }
        );

        if(clearedRooms % 2 == 0) {
            setPerkTable(serverLevel, pos, 2);
        } else {
            setLootChests(serverLevel, pos, direction, -1, true);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("private_ticks", privateTicks);
        tag.putBoolean("beginSpawn", beginSpawning);
        tag.putDouble("animate", this.animateTick);
        tag.putBoolean("started", this.started);
        tag.putInt("spawned", this.mobsSpawned);
        tag.putString("roomId", this.roomId);

        var allowedMobs = new CompoundTag();
        for (var spawnedMob : this.spawnableMobs) {
            allowedMobs.putUUID(String.valueOf(spawnedMob.getUUID()), spawnedMob.getUUID());
        }
        tag.put("allowedMobs", allowedMobs);

        var spawnedMobs = new CompoundTag();
        for (var spawnedMob : this.onField) {
            spawnedMobs.putUUID(String.valueOf(spawnedMob), spawnedMob);
        }
        tag.put("uuid", spawnedMobs);

    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        privateTicks = tag.getInt("private_ticks");
        beginSpawning = tag.getBoolean("beginSpawning");
        animateTick = tag.getDouble("animate");
        started = tag.getBoolean("started");
        mobsSpawned = tag.getInt("spawned");
        roomId = tag.getString("roomId");

        var allowedMobs = tag.getCompound("allowedMobs");
        for (var uuid : allowedMobs.getAllKeys()) {
            if(level instanceof ServerLevel serverLevel){
                this.spawnableMobs.add((LivingEntity) serverLevel.getEntity(allowedMobs.getUUID(uuid)));
            }
        }

        var uuids = tag.getCompound("uuid");
        for (var uuid : uuids.getAllKeys()) {
            this.onField.add(uuids.getUUID(uuid));
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if(!(level instanceof ServerLevel serverLevel)) return;

        autoStartAltar(serverLevel, pos);
        if(this.started) {
            this.privateTicks++;
            this.reAssignTarget(level, pos);


            if(privateTicks % 2 == 0){
                if(!this.spawnableMobs.isEmpty() && onField.size() < getMaxAllowedMobsOnField(serverLevel)){
                    var randomPoses = innerRadiusRandom(pos.below(2).getCenter(), 20, 200)
                        .stream()
                        .filter(pos1 -> serverLevel.getBlockState(containing(pos1)).isAir() && serverLevel.getBlockState(containing(pos1).above()).isAir())
                        .toList();

                    var entity = listRandom(this.spawnableMobs);
                    var position = listRandom(randomPoses);
                    addAndPositionEntity(serverLevel, containing(position), entity);
                    this.onField.add(entity.getUUID());
                    this.spawnableMobs.remove(entity);
                }
            }

            this.updateBlock();
            idleParticleAnim(pos, privateTicks, level);
        }

        removeKilledMobs(serverLevel);

        if(privateTicks == 30){
            MobManager.summonEntities(this, roomId);
            this.beginSpawning = false;
        }

        if (privateTicks == 1) onActivationAnim(level, pos, privateTicks);
        if (privateTicks > 30 && started && this.onField.isEmpty() && spawnableMobs.isEmpty()) {
            onCompleteAltar(pos, serverLevel);
        }
    }

}

