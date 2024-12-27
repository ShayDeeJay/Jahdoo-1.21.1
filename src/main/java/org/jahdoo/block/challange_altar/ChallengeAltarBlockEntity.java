package org.jahdoo.block.challange_altar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
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
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.block.AbstractTankUser;
import org.jahdoo.block.wand.WandBlockEntity;
import org.jahdoo.entities.AncientGolem;
import org.jahdoo.entities.CustomZombie;
import org.jahdoo.entities.EternalWizard;
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

import static org.jahdoo.entities.ProjectileAnimations.*;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.SOFT_PARTICLE_SELECTION;
import static org.jahdoo.registers.ElementRegistry.getElementByWandType;
import static org.jahdoo.utils.ModHelpers.Random;


public class ChallengeAltarBlockEntity extends AbstractBEInventory implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int privateTicks;
    private boolean isSetActive;
    private List<LivingEntity> getCurrentRoundEntities = new ArrayList<>();

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

    public void tick(Level pLevel, BlockPos pPos, BlockState blockState) {
        this.getCurrentRoundEntities.removeIf(getCurrentRoundEntity -> !getCurrentRoundEntity.isAlive());
        if(this.isSetActive) this.privateTicks++;

        if(this.isSetActive){
            if(privateTicks % (privateTicks <= 62 ? 10 : 4) == 0){
                var element = ElementRegistry.getRandomElement();
                var par1 = ParticleHandlers.bakedParticleOptions(element.getTypeId(), 20, 1.5f, false);
                var par2 = ParticleHandlers.genericParticleOptions(
                    ParticleStore.GENERIC_PARTICLE_SELECTION, element, 20, 1.5f, false, 0.3
                );
                PositionGetters.getInnerRingOfRadiusRandom(pPos, 0.25, 2,
                    positions -> this.placeParticle(level, positions, element, Random.nextInt(0, 3) == 0 ? par1 : par2)
                );
            }

            if(this.getCurrentRoundEntities.size() < 20){
                if (this.privateTicks % 50 == 0) {
                    this.summonEntities(pPos);
                }
            }

            if(privateTicks > 62){
                if (Random.nextInt(20) == 0) {
                    var normal = SoundEvents.TRIAL_SPAWNER_AMBIENT;
                    var ominous = SoundEvents.TRIAL_SPAWNER_AMBIENT_OMINOUS;
                    var randomSound = List.of(normal, ominous).get(Random.nextInt(2));
                    ModHelpers.getSoundWithPosition(pLevel, pPos, randomSound, 1, 2f);
                }
            }
        }

        if(privateTicks == 7) {
            PositionGetters.getOuterRingOfRadius(
                pPos.getCenter().subtract(0,0.03,0), 0.1, 50, pos -> {
                    setShockwaveNova(pos.subtract(0, 0,0));
                }
            );
            ModHelpers.getSoundWithPosition(pLevel, pPos, SoundEvents.DEEPSLATE_BREAK, 1, 0.6f);
            ModHelpers.getSoundWithPosition(pLevel, pPos, SoundEvents.VAULT_OPEN_SHUTTER, 1, 0f);
        }
    }

    public void summonEntities(BlockPos pPos){
        var player = this.getLevel().getNearestPlayer(TargetingConditions.DEFAULT, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
        if(player != null /*&& player.distanceToSqr(pPos.getCenter()) < 300 */&& player.onGround()){
            System.out.println("player around");
            for (var vec3 : PositionGetters.getInnerRingOfRadiusRandom(player.position(), 8, 6)) {
                var pos = BlockPos.containing(vec3);
                var above = this.getLevel().getBlockState(pos.above());
                var below = this.getLevel().getBlockState(pos);
                if(above.isAir() && !below.isAir()){
                    onPosition(vec3);
                }
            }
//            PositionGetters.getInnerRingOfRadiusRandom(player.position(), 8 , 6, this::onPosition);
        }
    }

    public void onPosition(Vec3 pos){
        if(this.getLevel() instanceof ServerLevel serverLevel){

//            var actualEntity = new CustomZombie(serverLevel, null);
            var skelly = EntityType.SKELETON.create(serverLevel);
            var wizard = new EternalWizard(serverLevel, null, -1, 5);

            if(skelly == null || wizard == null) return;

            wizard.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemsRegister.WAND_ITEM_VITALITY.get()));
            skelly.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
            var actualEntity = List.of(wizard, skelly).get(Random.nextInt(2));


            actualEntity.moveTo(pos);
            this.getCurrentRoundEntities.add(actualEntity);
            serverLevel.addFreshEntity(actualEntity);

            var getEntity = actualEntity.level().getNearestPlayer(actualEntity, 30);
            actualEntity.setTarget(getEntity);
            if (getEntity != null) {
                // Calculate the direction vector
                var zomboPos = actualEntity.position();
                var targetPos = getEntity.position();
                double dx = targetPos.x - zomboPos.x;
                double dz = targetPos.z - zomboPos.z;

                // Calculate yaw (rotation around Y-axis)
                float yaw = (float) (Math.atan2(-dx, dz) * (180 / Math.PI));
                actualEntity.setYRot(yaw);
                actualEntity.yHeadRot = yaw; // Update head rotation as well
            }

//            var getEntity = zombo.level().getNearbyEntities(
//                LivingEntity.class,
//                TargetingConditions.DEFAULT,
//                zombo,
//                zombo.getBoundingBox().inflate(20)
//            );

        }
    }

    public void placeParticle(Level level, Vec3 pos, AbstractElement element, ParticleOptions par1){
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

