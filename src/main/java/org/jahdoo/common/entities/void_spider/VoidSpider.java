package org.jahdoo.common.entities.void_spider;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.common.entities.ITamableEntity;
import org.jahdoo.common.registers.*;

import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.ascension.MobManager.spawnAroundEntity;
import static org.jahdoo.common.particle.ParticleHandlers.*;
import static org.jahdoo.common.registers.ElementReg.*;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.ascension.utils.PositionFinders.getOuterRingOfRadiusRandom;

public class VoidSpider extends Spider implements ITamableEntity {
    protected boolean triggerDeathSpawn;
    boolean isAdult = this.getScale() == 1.5F;

    public VoidSpider(EntityType<? extends Spider> entityType, Level level) {
        super(entityType, level);
    }

    public VoidSpider(Level level) {
        super(EntityReg.VOID_SPIDER.get(), level);
    }

    @Override
    public LivingEntity getOwner() {
        return null;
    }

    public AbstractElement getElement(){
        return mystic();
    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        onDeath();
    }

    private void onDeath() {
        var onDeath = !this.level().isClientSide && !this.isAlive() && !triggerDeathSpawn;

        if(onDeath) {
            spawnBabies();
            this.triggerDeathSpawn = true;
            if(this.getScale() != 1.5F) level().setBlockAndUpdate(this.blockPosition(), Blocks.COBWEB.defaultBlockState());
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

    public Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        if (this.onGround()) {
            return Vec3.ZERO;
        } else {
            var f = player.xxa * 0.5F;
            var f1 = player.zza;
            if (f1 <= 0.0F) f1 *= 0.25F;
            return new Vec3(f, 0.0, f1);
        }
    }

    private void setParticleNova(Vec3 worldPosition){
        var positionScrambler = worldPosition.offsetRandom(RandomSource.create(), 0.3f);
        var directions = positionScrambler.subtract(worldPosition).normalize();
        var size = Random.nextDouble(1, 3);
        var getRandomParticle = getAllParticleTypes(getElement(), 10, (float) size);
        var randomSpeed = Random.nextDouble(0.3, 0.9);

        sendParticles(level(), getRandomParticle, worldPosition, 0, directions.x, directions.y , directions.z, randomSpeed);
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

    private void spawnBabies() {
        if(this.level().isClientSide || !isAdult) return;

        var credit = getKillCredit();
        var counter = new AtomicInteger();

        this.playSound(SoundEvents.EVOKER_PREPARE_ATTACK, 1.5F, 1.7F);
        this.playSound(SoundReg.HEAL.get(), 1.5F, 1);

        getOuterRingOfRadiusRandom(credit == null ? this.position() : credit.position(), 1, 300, this::setParticleNova);
        spawnAroundEntity(level(), BlockPos.containing(credit == null ? this.position() : credit.position()), 4, 200,
            blockPos -> {
                if(counter.get() < 5){
                    var newSpider = EntityReg.VOID_SPIDER_SPAWN.get().create(level());
                    if (newSpider != null) {
                        newSpider.moveTo(blockPos.above().getCenter());
                        level().addFreshEntity(newSpider);
                        if (credit != null) newSpider.setTarget(credit);
                        counter.incrementAndGet();
                    }
                }
            }
        );

        //If there was not enought space to spawn around the player, remainder will spawn by mother
        if(counter.get() >= 5) return;
        getOuterRingOfRadiusRandom(this.position(), 1, 300, this::setParticleNova);
        for (var i = 0; i < (5 - counter.get()); i++) {
            var newSpider = EntityReg.VOID_SPIDER_SPAWN.get().create(level());
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

    static class SpiderTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

        public SpiderTargetGoal(Spider spider, Class<T> entityTypeToTarget) { super(spider, entityTypeToTarget, true); }

        public boolean canUse() { return super.canUse(); }

    }

    static class SpiderAttackGoal extends MeleeAttackGoal {

        public SpiderAttackGoal(Spider spider) { super(spider, 1.0F, true); }

        public boolean canUse() { return super.canUse() && !this.mob.isVehicle();}

        public boolean canContinueToUse() {
            if (Random.nextInt(100) == 0) {
                this.mob.setTarget(null);
                return false;
            } else {
                return super.canContinueToUse();
            }
        }

    }
}
