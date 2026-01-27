package org.jahdoo.common.block.altar;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.commands.arguments.EntityAnchorArgument;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jahdoo.common.block.SyncedBlockEntity;
import org.jahdoo.common.block.loot_pot.LootPotBlockEntity;
import org.jahdoo.common.entities.ITamableEntity;
import org.jahdoo.common.entities.safe.Safe;
import org.jahdoo.common.event.TriggerEvents;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.level_manager.BlockSetupManager;
import org.jahdoo.trial_nexus.mobs.MobManager;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.trial_nexus.utils.Maths;
import org.jahdoo.trial_nexus.utils.ModTags;
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
import static net.minecraft.world.level.block.Blocks.STONE;
import static org.jahdoo.common.block.altar.AltarAnim.idleParticleAnim;
import static org.jahdoo.common.block.altar.AltarAnim.onActivationAnim;
import static org.jahdoo.common.entities.EntityAnimations.ALTAR_IDLE;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.RunData.incrementClearedRoomExp;
import static org.jahdoo.trial_nexus.level_manager.BlockSetupManager.blockExitBarrier;
import static org.jahdoo.trial_nexus.level_manager.BlockSetupManager.setCoinLootChest;
import static org.jahdoo.trial_nexus.level_manager.InstanceDifficulty.*;
import static org.jahdoo.trial_nexus.level_manager.StructureManager.placeLocksWithData;
import static org.jahdoo.trial_nexus.mobs.MobManager.addAndPositionEntity;
import static org.jahdoo.trial_nexus.mobs.MobManager.championSpawn;
import static org.jahdoo.trial_nexus.utils.Helpers.*;
import static org.jahdoo.trial_nexus.utils.PositionFinders.innerRadiusRandom;


public class AltarBlockEntity extends SyncedBlockEntity implements GeoBlockEntity {

    public final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int mobsSpawned;
    public double animateTick;
    public boolean started;
    public boolean beginSpawning;
    public Direction direction;
    public String roomId;
    public List<LivingEntity> spawnableMobs = new ArrayList<>();
    public List<UUID> onField = new ArrayList<>();
    public boolean spawnedChampion;
    public boolean spawnedSafe;
    public boolean placedFloor;

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

    public static @NotNull AABB roomBounding(BlockPos pos) {
        return new AABB(
            pos.getX() - 24, pos.getY() - 2, pos.getZ() - 24,
            pos.getX() + 24, pos.getY() + 10, pos.getZ() + 24
        );
    }

    private static @NotNull AABB support(BlockPos pos) {
        return new AABB(
            pos.getX() - 24, pos.getY() - 4, pos.getZ() - 24,
            pos.getX() + 24, pos.getY() - 4, pos.getZ() + 24
        );
    }

    private static @NotNull AABB spawnFloor(BlockPos pos) {
        var x = pos.getX();
        var y = pos.getY() - 2;
        var z = pos.getZ();
        return new AABB(x - 10, y, z - 10, x + 10, y, z + 10);
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

//            Helpers.getSoundWithPosition(serverLevel, pos, SoundReg.START_TRIAL.get(), 2);
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

                if(!validTargets.isEmpty()) entity.setTarget((LivingEntity) listRandom(validTargets));
            }
        }
    }

    public int getMaxAllowedMobsOnField(ServerLevel serverLevel){
        var data = serverLevel.getData(AttachmentReg.INSTANCE_DATA);
        return switch (data.getDifficulty()){
            case MEDIUM -> 40;
            case HARD -> 60;
            default -> 20;
        };
    }

    private void onCompleteAltar(BlockPos pos, ServerLevel serverLevel) {
        var data = serverLevel.getData(AttachmentReg.INSTANCE_DATA);

        data.incrementClearedRooms();
        placeLocksWithData(serverLevel, pos.below(2), false, false);
        serverLevel.destroyBlock(pos, false);
        getSoundWithPosition(serverLevel, pos, SoundReg.END_TRIAL.get(), 2, 1.5F);

        sendPacketsToPlayerDistance(getBlockPos().getCenter(), 400, serverLevel,
            (serverPlayer) -> {
                var connection = serverPlayer.connection;
                connection.send(new ClientboundSetTitlesAnimationPacket(5, 20, 20));
                connection.send(new ClientboundSetTitleTextPacket(withStyleComponent("ALTAR COMPLETE", ColourStore.MAGNET_RANGE_GREEN)));
                TriggerEvents.triggerRoomClearEvent(serverPlayer, serverLevel);
            }
        );

        var box = roomBounding(pos);
        var bounding = getAllBlockPos(box);
        for (BlockPos blockPos : bounding) {
            var state = serverLevel.getBlockState(blockPos);
            if (state.is(BlockReg.LOOT_POT)) {
                if(serverLevel.getBlockEntity(blockPos) instanceof LootPotBlockEntity potBlock){
                    potBlock.setTheItem(ItemStack.EMPTY);
                    serverLevel.destroyBlock(blockPos, false);
                }
            }
            if (state.is(ModTags.Block.MINEABLE_NEXUS)) serverLevel.destroyBlock(blockPos, false);
        }

        for (var entity : serverLevel.getEntities().getAll()) {
            if(entity instanceof Safe safe) safe.kill();
        }

        for (var player : serverLevel.players()) {
            incrementClearedRoomExp(player, data.getDifficulty());
        }

        var clearedRooms = data.getClearedRooms();
        var difficulty = data.getDifficulty();
        var interval = difficulty.equals(NOVICE.getSerializedName()) ? 5 : difficulty.equals(EXPERT.getSerializedName()) ? 10 : 20;
        if(clearedRooms % interval == 0){
            BlockSetupManager.setPerkTable(serverLevel, pos, 4);
        } else {
            setCoinLootChest(serverLevel, pos, direction, -1, true, clearedRooms-1);
        }
    }

    private static Iterable<BlockPos> getAllBlockPos(AABB box) {
        var start = new BlockPos((int) box.minX, (int) box.minY, (int) box.minZ);
        var end = new BlockPos((int) box.maxX, (int) box.maxY, (int) box.maxZ);
        return BlockPos.betweenClosed(start, end);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("beginSpawn", beginSpawning);
        tag.putDouble("animate", animateTick);
        tag.putBoolean("started", started);
        tag.putInt("spawned", mobsSpawned);
        tag.putString("roomId", roomId);
        tag.putString("direction", direction.name());
        tag.putBoolean("spawned_champion", spawnedChampion);
        tag.putBoolean("spawned_safe", spawnedSafe);
        tag.putBoolean("placed_floor", placedFloor);

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
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        beginSpawning = tag.getBoolean("beginSpawning");
        animateTick = tag.getDouble("animate");
        started = tag.getBoolean("started");
        mobsSpawned = tag.getInt("spawned");
        roomId = tag.getString("roomId");
        spawnedChampion = tag.getBoolean("spawned_champion");
        spawnedSafe = tag.getBoolean("spawned_safe");
        placedFloor = tag.getBoolean("placed_floor");

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

    public void tick(Level level, BlockPos pos, BlockState state) {
        if(!(level instanceof ServerLevel serverLevel)) return;
        if(!placedFloor){
            var box = support(pos);
            var bounding = getAllBlockPos(box);
            for (BlockPos blockPos : bounding) {
                serverLevel.setBlockAndUpdate(blockPos, STONE.defaultBlockState());
            }
            placedFloor = true;
        }

        autoStartAltar(serverLevel, pos);
        if(this.started) {
            this.privateTicks++;
            this.reAssignTarget(level, pos);
            var randomPoses = innerRadiusRandom(pos.below(2).getCenter(), 20, 200)
                .stream()
                .filter(
                    pos1 -> {
                        var aPos = containing(pos1);
                        var spotA = serverLevel.getBlockState(aPos);
                        var spotB = serverLevel.getBlockState(aPos.above());
                        return spotA.isAir() && spotB.isAir();
                    }
                )
                .filter(
                    pos1 -> {
                        var aPos = containing(pos1);
                        var spotC = serverLevel.getBlockState(aPos.below());
                        return spotC.getFluidState().isEmpty();
                    }
                )
                .toList();

            var x = randomPoses.isEmpty() ? pos.getCenter() : Helpers.listRandom(randomPoses);

            if(privateTicks % 2 == 0){
                if(!this.spawnableMobs.isEmpty() && onField.size() < getMaxAllowedMobsOnField(serverLevel)){

                    var entity = listRandom(this.spawnableMobs);
                    addAndPositionEntity(serverLevel, containing(x), entity);

                    if(!this.spawnedChampion){
                        this.spawnedChampion = championSpawn(serverLevel, entity);
                    }

                    this.onField.add(entity.getUUID());
                    this.spawnableMobs.remove(entity);
                }
            }

            if(!this.spawnedSafe && Random.nextInt(200 - privateTicks) == 0){
                var difficulty = getFromLevel(serverLevel);
                var id = difficulty.getId();
                var percentageChance = (2 + id) * 10;
                if (Maths.percentageChance(percentageChance)) {
                    var origin = id * 100;
                    var getSafe = new Safe(level, Random.nextInt(origin, origin * 2), 20);
                    getSafe.moveTo(x);
                    getSafe.lookAt(EntityAnchorArgument.Anchor.EYES, this.getBlockPos().getCenter());
                    level.addFreshEntity(getSafe);
                    getSoundWithPositionV(serverLevel, x, SoundReg.SWORD_THUD.value(), 5, 1.8F);
                }
                this.spawnedSafe = true;
            }

            this.updateBlock();
            idleParticleAnim(pos, privateTicks, level);
        }

        removeKilledMobs(serverLevel);

        if(privateTicks == 30){
            MobManager.summonEntities(this, roomId);
            this.beginSpawning = false;
        }

        if (privateTicks == 1) onActivationAnim(level, pos);
        if (privateTicks > 30 && started && this.onField.isEmpty() && spawnableMobs.isEmpty()) {
            onCompleteAltar(pos, serverLevel);
        }
    }

}

