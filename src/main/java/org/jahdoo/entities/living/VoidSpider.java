package org.jahdoo.entities.living;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.abilities.ability_data.FrostboltsAbility;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.entities.TamableEntity;
import org.jahdoo.registers.*;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

import static org.jahdoo.ability.ability_components.EtherealArrow.*;
import static org.jahdoo.particle.ParticleHandlers.*;
import static org.jahdoo.registers.ElementRegistry.*;
import static org.jahdoo.registers.EntityPropertyRegister.*;
import static org.jahdoo.utils.ModHelpers.*;
import static org.jahdoo.utils.PositionGetters.getOuterRingOfRadiusRandom;

public class VoidSpider extends Spider implements TamableEntity {
    protected boolean triggerDeathSpawn;
    boolean isAdult = this.getScale() == 1.5F;

    public VoidSpider(EntityType<? extends Spider> entityType, Level level) {
        super(entityType, level);
    }

    public VoidSpider(Level level) {
        super(EntitiesRegister.VOID_SPIDER.get(), level);
    }

    @Override
    public LivingEntity getOwner() {
        return null;
    }

    public @NotNull Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        if (this.onGround()) {
            return Vec3.ZERO;
        } else {
            var f = player.xxa * 0.5F;
            var f1 = player.zza;
            if (f1 <= 0.0F) f1 *= 0.25F;
            return new Vec3(f, 0.0, f1);
        }
    }

    private void spawnBabies() {
        if(this.level().isClientSide || !isAdult) return;
        getOuterRingOfRadiusRandom(this.position(), 1, 300, this::setParticleNova);
        this.playSound(SoundEvents.EVOKER_PREPARE_ATTACK, 1.5F, 1.7F);
        this.playSound(SoundRegister.HEAL.get(), 1.5F, 1);
        for (var i = 0; i < 5; i++) {
            var newSpider = EntitiesRegister.VOID_SPIDER_SPAWN.get().create(level());
            if (newSpider != null) {
                //Removed random spawn as to not have entities to get stuck in adjacent blocks
                var x = this.getRandomX(0.5);
                var z = this.getRandomZ(0.5);
                var spawnPos = new Vec3(x, this.getY(), z);
                newSpider.moveTo(spawnPos);
                level().addFreshEntity(newSpider);
            }
        }
    }

    private void setParticleNova(Vec3 worldPosition){
        var positionScrambler = worldPosition.offsetRandom(RandomSource.create(), 0.3f);
        var directions = positionScrambler.subtract(this.position()).normalize();
        var size = Random.nextDouble(1, 3);
        var getRandomParticle = getAllParticleTypes(getElement(), 10, (float) size);
        var randomSpeed = Random.nextDouble(0.3, 0.9);

        sendParticles(level(), getRandomParticle, worldPosition, 0, directions.x, directions.y , directions.z, randomSpeed);
    }

    private void shooterAbility(LivingEntity pTarget) {
        var properties = setArrowProperties(10, 20, 1, 1);
        var type = FrostboltsAbility.abilityId.getPath().intern();
        var projSelect = ETHEREAL_ARROW.get().setAbilityId();
        var arrow = new GenericProjectile(this, getX(), getY() + 1, getZ(), projSelect, properties, getElement(), type);
        fireProjectile(pTarget, arrow, 0.9, 1.6F);
    }

    @Override
    public void tick() {
        super.tick();
        var nearestPlayer = this.getTarget();
        var canShoot = isAdult && nearestPlayer != null && random.nextInt(40) == 0;

        if(canShoot) shooterAbility(nearestPlayer);

        if(!this.level().isClientSide && !this.isAlive() && !triggerDeathSpawn) {
            spawnBabies();
            this.triggerDeathSpawn = true;
        }
    }

    public AbstractElement getElement(){
        return MYSTIC.get();
    }

    private void fireProjectile(LivingEntity pTarget, Projectile projectile, double offset, float velocity) {
        projectile.setOwner(this);
        double d0 = pTarget.getX() - this.getX();
        double d1 = pTarget.getY(0.3333333333333333D) - projectile.getY() - offset;
        double d2 = pTarget.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        projectile.shoot(d0, d1 + d3 * (double)0.2F, d2, velocity, 0);
        this.playSound(SoundRegister.ORB_CREATE.get(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(projectile);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.5F));
        this.goalSelector.addGoal(3, new SpiderAttackGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new SpiderTargetGoal<>(this, Player.class));
    }

    static class SpiderAttackGoal extends MeleeAttackGoal {
        public SpiderAttackGoal(Spider spider) {
            super(spider, 1.0F, true);
        }

        public boolean canUse() {
            return super.canUse() && !this.mob.isVehicle();
        }

        public boolean canContinueToUse() {
            if (Random.nextInt(100) == 0) {
                this.mob.setTarget(null);
                return false;
            } else {
                return super.canContinueToUse();
            }
        }
    }

    static class SpiderTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        public SpiderTargetGoal(Spider spider, Class<T> entityTypeToTarget) {
            super(spider, entityTypeToTarget, true);
        }

        public boolean canUse() {
            return super.canUse();
        }
    }

    public static AttributeSupplier.Builder createMain() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 22.0F)
            .add(Attributes.MOVEMENT_SPEED, 0.3F)
            .add(Attributes.SCALE, 1.5f);
    }

    public static AttributeSupplier.Builder createBaby() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 8.0F)
            .add(Attributes.MOVEMENT_SPEED, 0.4F)
            .add(Attributes.SCALE, 0.5f);
    }
}
