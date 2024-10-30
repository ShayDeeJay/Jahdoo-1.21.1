package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.SharedFireProperties;
import org.jahdoo.all_magic.all_abilities.abilities.HellfireAbility;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.AttributesRegister;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.all_magic.effects.CustomMobEffect;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.List;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.all_magic.AbilityBuilder.*;

public class HellFire extends DefaultEntityBehaviour {

    double reductionSpeed = 0.1;
    int countdownTimer;
    float yaw;

    double damage;
    double range;
    double effectStrength;
    double effectDuration;
    Vec3 playerOriginalPosition;

    @Override
    public void getAoeCloud(AoeCloud aoeCloud) {
        super.getAoeCloud(aoeCloud);
        if(this.aoeCloud.getOwner() != null){
            var player = this.aoeCloud.getOwner();
            var damage = this.getTag(DAMAGE);
            this.damage = GeneralHelpers.attributeModifierCalculator(
                player,
                (float) damage,
                this.getElementType(),
                AttributesRegister.MAGIC_DAMAGE_MULTIPLIER,
                true
            );
            this.playerOriginalPosition = this.aoeCloud.getOwner().position();
        }
        this.range = this.getTag(RANGE);
        this.effectStrength = this.getTag(EFFECT_STRENGTH);
        this.effectDuration = this.getTag(EFFECT_DURATION);
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.put("position", GeneralHelpers.nbtDoubleList(this.playerOriginalPosition.x, this.playerOriginalPosition.y, this.playerOriginalPosition.z));
        compoundTag.putInt("countdown_timer", this.countdownTimer);
        compoundTag.putDouble("reduction", this.reductionSpeed);
        compoundTag.putFloat("yaw", this.yaw);
        compoundTag.putDouble(DAMAGE, this.damage);
        compoundTag.putDouble(RANGE, this.range);
        compoundTag.putDouble(EFFECT_STRENGTH, this.effectStrength);
        compoundTag.putDouble(EFFECT_DURATION, this.effectDuration);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        ListTag list = compoundTag.getList("position", Tag.TAG_DOUBLE);
        this.playerOriginalPosition = new Vec3(list.getDouble(0), list.getDouble(1), list.getDouble(2));
        this.countdownTimer = compoundTag.getInt("countdown_timer");
        this.reductionSpeed = compoundTag.getDouble("reduction");
        this.yaw = compoundTag.getFloat("yaw");
        this.damage = compoundTag.getDouble(DAMAGE);
        this.range = compoundTag.getDouble(RANGE);
        this.effectDuration = compoundTag.getDouble(EFFECT_DURATION);
        this.effectStrength = compoundTag.getDouble(EFFECT_STRENGTH);
    }

    @Override
    public void onTickMethod() {

        if(!(this.aoeCloud.level() instanceof ServerLevel serverLevel)) return;
        if(yaw == 0) yaw = this.aoeCloud.getOwner().getYRot();
        this.reductionSpeed *= 1.03;
        aoeCloud.setInvisible(true);
        if(aoeCloud.getRadius() < this.damage) this.updateRadius(); else countdownTimer++;
        novaBehaviour(serverLevel);
    }

    private void updateRadius(){
        aoeCloud.setRadius((float) (aoeCloud.getRadius() + reductionSpeed));
    }

    private void novaBehaviour(ServerLevel serverLevel){
        double radius =  aoeCloud.getRadius() * 3;
        if(countdownTimer > 0) return;
        var positions = PositionGetters.getSemicircle(aoeCloud.position(), radius, Math.max(radius / 2.5 , 6), yaw, 30);
        this.novaSoundManager(positions);

        positions.forEach(
            positionsA -> {
                BlockPos blockPos = BlockPos.containing(positionsA);
                SharedFireProperties.fireTrailVegetationRemover(this.aoeCloud.level().getBlockState(blockPos), blockPos, this.aoeCloud, this.aoeCloud.getOwner());
                this.novaParticles(serverLevel, positionsA);
                this.setNovaDamage(positionsA);
                if (this.playerOriginalPosition.distanceTo(positionsA) >= this.range) aoeCloud.discard();
            }
        );
    }

    private void novaSoundManager(List<Vec3> positions){
        if(aoeCloud.tickCount == 1) GeneralHelpers.getSoundWithPosition(aoeCloud.level(), BlockPos.containing(positions.getFirst()), SoundRegister.DASH_EFFECT.get(), 0.6f,1.4f);
        if (aoeCloud.tickCount % 3 == 0) GeneralHelpers.getSoundWithPosition(aoeCloud.level(), BlockPos.containing(positions.getFirst()), SoundEvents.FIRECHARGE_USE,0.4f,0.8f);
    }

    private void novaParticles(ServerLevel serverLevel, Vec3 positionsA){

        double vx1 = (GeneralHelpers.Random.nextDouble() - 0.5) * 0.5;
        double vy1 = (GeneralHelpers.Random.nextDouble()) * this.aoeCloud.getRadius()/15;

        boolean range = aoeCloud.getRadius() > this.range;
        double particle1 = range ? 0.2 : 0.1  ;
        double particle2 = range ? 0.2  : Math.max(((10 - this.aoeCloud.getRadius()) / 50), 0.01);
        var randomSource = RandomSource.create();

        GeneralHelpers.generalHelpers.sendParticles(
            serverLevel,
            new BakedParticleOptions(this.getElementType().getTypeId(), 4, 3f, false),
            positionsA.add(0,0.6, 0).offsetRandom(randomSource, this.aoeCloud.getRadius()),
            1, vy1, vy1, vy1, particle1
        );

        GeneralHelpers.generalHelpers.sendParticles(
            serverLevel,
            genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, this.getElementType(), 6, 1.5f),
            positionsA.add(0, 0.6, 0).offsetRandom(randomSource, this.aoeCloud.getRadius()),
            1, vx1, vy1, vx1, particle2
        );
    }

    private void setNovaDamage(Vec3 positionsA){
        LivingEntity livingEntity = this.getEntityInRange(positionsA);
        if (livingEntity == null) return;
        if(!this.damageEntity(livingEntity)) return;

        livingEntity.addEffect(new CustomMobEffect(EffectsRegister.FIRE_EFFECT.getDelegate(), (int) effectDuration, (int) effectStrength));
        livingEntity.hurt(aoeCloud.damageSources().playerAttack((Player) aoeCloud.getOwner()), (float) damage);
    }

    private LivingEntity getEntityInRange(Vec3 positionsA){
        return aoeCloud.level().getNearestEntity(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            aoeCloud.getOwner(),
            positionsA.x, positionsA.y, positionsA.z,
            new AABB(BlockPos.containing(positionsA)).deflate(1, 2, 1)
        );
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.INFERNO.get();
    }

    @Override
    public double getTag(String name) {
        var wandAbilityHolder = this.aoeCloud.getwandabilityholder();
        var ability = HellfireAbility.abilityId.getPath().intern();
        return GeneralHelpers.getModifierValue(wandAbilityHolder, ability).get(name).actualValue();
    }

    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("hellfire_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new HellFire();
    }
}
