package org.jahdoo.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jahdoo.entities.goals.*;
import org.jahdoo.registers.EntitiesRegister;

import java.util.UUID;

public class CustomZombie extends Zombie implements TamableEntity {
    LivingEntity owner;
    UUID ownerUUID;

    public CustomZombie(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    public CustomZombie(Level level, LivingEntity owner) {
        super(EntitiesRegister.CUSTOM_ZOMBIE.get(), level);
        this.owner = owner;
    }

    public LivingEntity getOwner(){
        return this.owner;
    }

    @Override
    public boolean canAttackType(EntityType<?> type) {
        return super.canAttackType(type);
    }

    @Override
    public void tick() {
        super.tick();
        reassignPlayer();

    }

    private void reassignPlayer() {
        if(!(this.level() instanceof ServerLevel serverLevel)) return;
        if(this.owner == null && this.ownerUUID != null) this.owner = serverLevel.getPlayerByUUID(this.ownerUUID);
    }

    public PathNavigation getNavigation() {
        Entity var2 = this.getControlledVehicle();
        PathNavigation var10000;
        if (var2 instanceof Mob mob) {
            var10000 = mob.getNavigation();
        } else {
            var10000 = this.navigation;
        }
        return var10000;
    }

    @Override
    protected void registerGoals() {
        addBehaviourGoals();
    }

    @Override
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(1, new ZombieAttackGoal(this, 1.0F, false));
        this.goalSelector.addGoal(2, new AttackNearbyMonsters<>(this, Player.class, false, 4, 100));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new FollowGoal(this, 1.0D, 5.0F, 2.0F, false));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

//        this.targetSelector.addGoal(1, new GenericHurtByTargetGoal(this));
//        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new GenericOwnerHurtByTargetGoal(this, this::getOwner));
        this.targetSelector.addGoal(3, new GenericOwnerHurtTargetGoal(this, this::getOwner));
    }


    public static AttributeSupplier.Builder createMobAttributes() {
        return Mob.createLivingAttributes()
            .add(Attributes.FOLLOW_RANGE, 35.0F)
            .add(Attributes.MOVEMENT_SPEED, 0.23F)
            .add(Attributes.ATTACK_DAMAGE, 3.0F)
            .add(Attributes.ARMOR, 2.0F)
            .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if(this.owner != null) compound.putUUID("saveOwner", owner.getUUID());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if(compound.hasUUID("saveOwner")) this.ownerUUID = compound.getUUID("saveOwner");
    }
}
