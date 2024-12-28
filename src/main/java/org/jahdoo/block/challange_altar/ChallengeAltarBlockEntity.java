package org.jahdoo.block.challange_altar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.effects.CustomMobEffect;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.block.AbstractTankUser;
import org.jahdoo.block.wand.WandBlockEntity;
import org.jahdoo.entities.AncientGolem;
import org.jahdoo.entities.CustomZombie;
import org.jahdoo.entities.EternalWizard;
import org.jahdoo.networking.packet.server2client.AltarBlockS2C;
import org.jahdoo.networking.packet.server2client.EnchantedBlockS2C;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.entities.ProjectileAnimations.*;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.SOFT_PARTICLE_SELECTION;
import static org.jahdoo.registers.ElementRegistry.getElementByWandType;
import static org.jahdoo.utils.ModHelpers.Random;


public class ChallengeAltarBlockEntity extends AbstractBEInventory implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int privateTicks;
    private boolean isSetActive;
    private RoundGenerator roundGenerator;
    public int aliveEntities;
    public int totalEntities;

    @Override
    public int setInputSlots() {
        return 1;
    }

    @Override
    public int setOutputSlots() {
        return 2;
    }

    @Override
    public int getMaxSlotSize() {
        return 1;
    }

    public ChallengeAltarBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.CHALLENGE_ALTAR_BE.get(), pPos, pBlockState, 5);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> ChallengeAltarBlockEntity.this.privateTicks;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> ChallengeAltarBlockEntity.this.privateTicks = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public ItemStack getInputAndOutputRenderer() {
        if(this.outputItemHandler.getStackInSlot(0).isEmpty()) {
            return this.inputItemHandler.getStackInSlot(0);
        }
        return this.outputItemHandler.getStackInSlot(0);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.putInt("challenge_altar.private", privateTicks);
    }

    public void setSetActive(){
        this.isSetActive = !this.isSetActive;
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        privateTicks = pTag.getInt("challenge_altar.private");

    }

    public void setRoundGenerator(RoundGenerator roundGenerator){
        this.roundGenerator = roundGenerator;
    }

    public RoundGenerator getRoundGenerator(){
        return this.roundGenerator;
    }

    private void updatePacket(ServerLevel serverLevel){
        ModHelpers.sendPacketsToPlayerDistance(this.getBlockPos().getCenter(), 64, serverLevel, new AltarBlockS2C(this.roundGenerator.getRegisterMobs().size(), this.getBlockPos()));
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState blockState) {
        if(this.isSetActive) this.privateTicks++;
        if(this.isSetActive){
            if(privateTicks % (privateTicks <= 62 ? 10 : 4) == 0){
                var element = ElementRegistry.getRandomElement();
                var par1 = ParticleHandlers.bakedParticleOptions(element.getTypeId(), 20, 1.5f, false);
                var par2 = ParticleHandlers.genericParticleOptions(
                    ParticleStore.GENERIC_PARTICLE_SELECTION, element, 20, 1.5f, false, 0.3
                );
                PositionGetters.getInnerRingOfRadiusRandom(pPos, 0.25, 2,
                    positions -> this.placeParticle(positions, Random.nextInt(0, 3) == 0 ? par1 : par2)
                );
            }

            if(this.roundGenerator != null){
                if(this.getLevel() instanceof ServerLevel serverLevel){
                    updatePacket(serverLevel);
                }
                if (this.roundGenerator.getActiveMobs().size() < 20) {
                    if (this.privateTicks % 150 == 0) {
                        this.summonEntities(pPos);
                    }
                }
                this.roundGenerator.getActiveMobs().removeIf(getCurrentRoundEntity -> !getCurrentRoundEntity.isAlive());
            }

            if(privateTicks > 62){
                if (Random.nextInt(20) == 0) {
                    var normal = SoundEvents.TRIAL_SPAWNER_AMBIENT;
                    var ominous = SoundEvents.TRIAL_SPAWNER_AMBIENT_OMINOUS;
                    var randomSound = List.of(normal, ominous).get(Random.nextInt(2));
                    ModHelpers.getSoundWithPosition(pLevel, pPos, randomSound, 1, 2f);
                }
            }

            if(this.roundGenerator != null && this.roundGenerator.getActiveMobs().isEmpty() && this.roundGenerator.getRegisterMobs().isEmpty()){
                this.isSetActive = false;
                this.privateTicks = 0;
                this.roundGenerator = null;
            }
        }

        if(privateTicks == 7) {
            PositionGetters.getOuterRingOfRadius(
                pPos.getCenter().subtract(0,0.03,0), 0.1, 50, pos -> {
                    setShockwaveNova(pos.subtract(0, 0,0));
                }
            );
            ModHelpers.getSoundWithPosition(pLevel, pPos, SoundEvents.DEEPSLATE_BREAK, 0.4f, 0.6f);
            ModHelpers.getSoundWithPosition(pLevel, pPos, SoundEvents.VAULT_OPEN_SHUTTER, 0.4f, 0f);
        }
    }

    public void summonEntities(BlockPos pPos){
        var player = this.getLevel().getNearestPlayer(TargetingConditions.DEFAULT, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
        var counter = new AtomicInteger();
        if(player != null /*&& player.distanceToSqr(pPos.getCenter()) < 300 */){
            var spawnPos = player.onGround() ? player.blockPosition() : pPos;
            var radius = Random.nextInt(4, 9);
            var points = 200;
            for (var blockPos : PositionGetters.getRandomSphericalBlockPositions(spawnPos, radius, points)) {
                if(counter.get() < (Math.min(2, this.roundGenerator.getRound() / 2))){
                    var above = this.getLevel().getBlockState(blockPos.above());
                    var main = this.getLevel().getBlockState(blockPos);
                    var below = this.getLevel().getBlockState(blockPos.below());
                    if (above.isAir() && main.isAir() && !below.isAir()) {
                        onPosition(blockPos);
                        counter.set(counter.get() + 1);
                    }
                }
            }
        }
    }

    public void onPosition(BlockPos pos){
        if(this.getLevel() instanceof ServerLevel serverLevel){
            if(this.roundGenerator != null){
                var actualEntity = this.roundGenerator.getMob();
                actualEntity.ifPresent(
                    livingEntity -> {
                        livingEntity.moveTo(pos.getCenter());
                        serverLevel.addFreshEntity(livingEntity);
                        var getEntity = livingEntity.level().getNearestPlayer(livingEntity, 30);
                        if (livingEntity instanceof Mob mob) mob.setTarget(getEntity);
                        if (Random.nextInt(3) == 0) {
                            livingEntity.addEffect(new CustomMobEffect(MobEffects.MOVEMENT_SPEED, 200, Random.nextInt(0, 3)));
                        }
                    }
                );
            }
        }
    }

    public void placeParticle(Vec3 pos, ParticleOptions par1){
        double randomY = ModHelpers.Random.nextDouble(0.0, 0.4);
        ParticleHandlers.sendParticles(this.getLevel(), par1, pos.subtract(0,0.4,0), 0, 0, randomY,0,0.7);
    }

    private void setShockwaveNova(Vec3 worldPosition){
        var directions = worldPosition.subtract(this.worldPosition.getCenter()).normalize();
        var lifetime = 3;
        var col1 = -8487298;
        var col2 = -13355980;
        var genericParticle = genericParticleOptions(SOFT_PARTICLE_SELECTION, lifetime, 0.06f, col1, col2, true);

        ParticleHandlers.sendParticles(
            getLevel(), genericParticle, worldPosition, 0, directions.x, directions.y, directions.z, Random.nextDouble(0.2, 0.6)
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
            new AnimationController<>(this,
                state -> {
                    if(this.isSetActive) {
                        if(this.privateTicks <= 62){
                            return state.setAndContinue(ALTAR_INITIATE);
                        } else {
                            return state.setAndContinue(ALTAR_IDLE);
                        }
                    }
                    return state.setAndContinue(null);
                }
            )
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

}

