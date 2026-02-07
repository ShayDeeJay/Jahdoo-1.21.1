package org.jahdoo.common.block.power_up_station;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.SoundReg;
import org.shaydee.shaydeeapi.Helpers;
import org.shaydee.shaydeeapi.block.AbstractBEInventory;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.entity.ai.targeting.TargetingConditions.DEFAULT;
import static org.jahdoo.common.block.power_up_station.PowerUpStation.TYPE;
import static org.jahdoo.common.block.power_up_station.PowerUpStation.colourByState;
import static org.jahdoo.common.entities.generic_projectile.GenericProjectile.POWER_UP_KEY;
import static org.jahdoo.common.particle.ParticleHandlers.getNonBakedParticles;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;
import static org.jahdoo.trial_nexus.utils.PositionFinders.getOuterRingOfRadiusRandom;


public class PowerUpStationEntity extends AbstractBEInventory {

    private final List<LivingEntity> localTargets = new ArrayList<>();

    public PowerUpStationEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityReg.POWER_UP_BE.get(), pPos, pBlockState, 64);
    }

    @Override
    public int setInputSlots() {
        return 1;
    }

    @Override
    public int setOutputSlots() {
        return 0;
    }

    @Override
    public int getMaxSlotSizeOutput() {
        return 0;
    }

    @Override
    public int getMaxSlotSizeInput() {
        return 1;
    }

    public int getCount(){
        return getItem().getCount();
    }

    public ItemStack getItem() {
        return this.getInputItemHandler().getStackInSlot(0);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if(getItem().isEmpty()) {
            if(Random.nextInt(5) == 0){
                getOuterRingOfRadiusRandom(pos.getCenter().subtract(0, 0.5, 0), 0.4, 20, this::particleSetter);
            }
            return;
        }

        onFilledCore(level, pos);
        resetData();
        getNearbyTargets(level, pos);
    }

    private void particleSetter(Vec3 positions) {
        if(getLevel() instanceof ServerLevel serverLevel){
            var primary = colourByState(this.getBlockState().getValue(TYPE));
            sendParticles(
                serverLevel, getNonBakedParticles(primary, primary, 20, 1.2F), positions.offsetRandom(RandomSource.create(), 0.2f),
                0, 0, Random.nextDouble(0.02, 0.2), 0, 1
            );
        }
    }

    private void getNearbyTargets(Level level, BlockPos pos) {
        if(getItem().isEmpty() || CoreData.isFull(getItem())) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        var scale = 5;
        var area = new AABB(pos).inflate(scale, scale, scale);
        var getNearby = level.getNearbyEntities(LivingEntity.class, DEFAULT, null, area);

        for (var livingEntity : getNearby) {
            if (!localTargets.contains(livingEntity)) localTargets.add(livingEntity);
        }

        var iterator = localTargets.iterator();
        while (iterator.hasNext()) {
            var nearbyEntity = iterator.next();
            if (nearbyEntity.isDeadOrDying()) {
                doOnDeath(level, pos, serverLevel, nearbyEntity);
                iterator.remove(); // safe removal
            }
        }
    }

    private void resetData() {
        if(!this.localTargets.isEmpty() && getItem().isEmpty()) localTargets.clear();
    }

    private void onFilledCore(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        if(!getItem().has(ComponentReg.CORE_DATA)) {
            var pos1 = pos.getCenter();
            Helpers.getSoundWithPosition(serverLevel, pos, SoundReg.QUEST_COMPLETE.get(), SoundSource.BLOCKS, 4F, 0.8F);

            var newItemEntity = new ItemEntity(serverLevel, pos1.x, pos1.y+0.5, pos1.z, getItem());
            serverLevel.addFreshEntity(newItemEntity);

            this.getInputItemHandler().setStackInSlot(0, ItemStack.EMPTY);
        }
    }

    private void doOnDeath(Level level, BlockPos pos, ServerLevel serverLevel, LivingEntity livingEntity) {
        if(!(livingEntity instanceof Player)){
            var add = livingEntity.position().add(0, livingEntity.getBbHeight()/2, 0);
            var genericProjectile = new GenericProjectile(add, level);
            var eastDirection = pos.getCenter().subtract(add).normalize();
            var entityValue = livingEntity.getPersistentData().getInt(POWER_UP_KEY);

            genericProjectile.getPersistentData().putInt(POWER_UP_KEY, Math.max(1, entityValue));
            genericProjectile.shoot(eastDirection.x, eastDirection.y, eastDirection.z, 0.5f, 0);
            Helpers.getSoundWithPosition(level, add, SoundReg.VITALITY_ABILITY.get(), SoundSource.BLOCKS, 1, 1.4F);
            Helpers.getSoundWithPosition(level, add, SoundEvents.SOUL_ESCAPE.value(), SoundSource.BLOCKS,2, 1);
            serverLevel.addFreshEntity(genericProjectile);
        }
    }

}

