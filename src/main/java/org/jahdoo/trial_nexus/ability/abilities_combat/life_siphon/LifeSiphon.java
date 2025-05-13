package org.jahdoo.trial_nexus.ability.abilities_combat.life_siphon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.trial_nexus.ability.DefaultEntityBehaviour;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.trial_nexus.utils.PositionFinders;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;

import static org.jahdoo.trial_nexus.ability.AbilityBuilder.DAMAGE;
import static org.jahdoo.trial_nexus.ability.AbilityBuilder.RANGE;
import static org.jahdoo.trial_nexus.ability.abilities_combat.life_siphon.LifeSiphonAbility.HEAL_VALUE;
import static org.jahdoo.trial_nexus.ability.abilities_combat.life_siphon.LifeSiphonAbility.PULSES;
import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;
import static org.jahdoo.common.particle.ParticleStore.GENERIC_PARTICLE;
import static org.jahdoo.common.registers.AttributeReg.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.common.registers.AttributeReg.VITALITY_MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.common.registers.mod.EntityDataReg.SOUL_SIPHON_NOVA;

public class LifeSiphon extends DefaultEntityBehaviour {

    public static final ResourceLocation abilityId = Helpers.res("life_siphon_property");
    private int fuse;
    private int privateTicks;
    private int pulseCounter;
    private int pulseSpacer;
    private boolean isPrimed;
    private boolean hasHitEntity;
    private double velocity = 0.2;

    private double damage;
    private double range;
    private double healValue;
    private double pulses;

    @Override
    public void getElementProjectile(ElementProjectile elementProjectile) {
        super.getElementProjectile(elementProjectile);
        elementProjectile.setShowTrailParticles(!isPrimed);

        if(this.element.getOwner() != null){
            var player = (LivingEntity) this.element.getOwner();
            var damage = (float) this.getTag(DAMAGE);
            this.damage = Helpers.attributeModifierCalculator(
                player, damage, true, MAGIC_DAMAGE_MULTIPLIER, VITALITY_MAGIC_DAMAGE_MULTIPLIER
            );
        }
        this.range = this.getTag(RANGE);
        this.healValue = this.getTag(HEAL_VALUE);
        this.pulses = this.getTag(PULSES);
    }

    @Override
    public AbilityHolder getAbilityHolder() {
        return this.element.getAbilityHolder();
    }

    @Override
    public String abilityId() {
        return LifeSiphonAbility.abilityId.getPath().intern();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        this.hasHitEntity = true;
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        this.hasHitEntity = true;
    }

    private Level level(){
        return this.element.level();
    }

    @Override
    public AbstractElement getElementType() {
        return ElementReg.vitality();
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new LifeSiphon();
    }

    @Override
    public void onTickMethod() {
        privateTicks++;
        applyInertia(this.element, 0.96f);
        this.setPrimed();
        this.pulseBehaviour();
    }

    @Override
    public void discardCondition() {
        if(pulseCounter >= pulses){
            if(fuse == 0) fuse = privateTicks;
            if(fuse + 20 == privateTicks){
                var pos = this.element.blockPosition();
                Helpers.getSoundWithPosition(level(), pos, getElementType().sound(), 2f, 0.7f);
                Helpers.getSoundWithPosition(level(), pos, SoundReg.IMPACT.get(), 2f);
                if(level() instanceof ServerLevel serverLevel){
                    for (int i = 0; i  < 10; i++){
                        ParticleHandlers.particleBurst(serverLevel, this.element.position(), 1,
                            ParticleHandlers.getAllParticleTypes(this.getElementType(), 40, 4), 0, 0, 0, 0.5F
                        );
                    }

                }
                this.element.discard();
            }
        }
    }

    private void createModule(Vec3 location){
        var aoeCloud = new AoeCloud(
            level(), (LivingEntity) this.element.getOwner(), 0.2f,
            SOUL_SIPHON_NOVA.get().setAbilityId(),
            LifeSiphonNova.setModifiers(damage, range, healValue),
            LifeSiphonAbility.abilityId.getPath().intern()
        );
        aoeCloud.setPos(location.x, location.y, location.z);
        aoeCloud.level().addFreshEntity(aoeCloud);
    }

    public void setPrimed(){
        if (privateTicks >= 20 || this.hasHitEntity || this.isPrimed) {
            if(!this.isPrimed){
                Helpers.getSoundWithPosition(
                    level(),
                    this.element.blockPosition(),
                    getElementType().sound(),
                    1, 1.8F
                );
                Helpers.getSoundWithPosition(
                    level(),
                    this.element.blockPosition(),
                    SoundReg.SUSPEND.get(),
                    2,0.8F
                );
            }
            this.element.setDeltaMovement(0, 0, 0);
            element.setShowTrailParticles(false);
            element.setAnimation(7);
            this.isPrimed = true;
        }
    }

    private void pulseBehaviour() {
        var projectile = this.element;
        if (privateTicks >= 20 || this.hasHitEntity) {
            orbEnergyParticles();
            if (velocity <= 0.8) velocity += 0.02;
            if (projectile.getOwner() != null) {
                pulseSpacer++;
                var spacePulseBy = pulseSpacer % 20 == 0 && pulseSpacer % 40 != 0;
                if(spacePulseBy) {
                    pulseCounter++;
                    createModule(projectile.position().add(0, 0.2, 0));
                    element.playSound(getElementType().sound(), 1f, 1.2f);
                    element.playSound(SoundReg.IMPACT.get(), 1f, 1.5f);
                }
            }
        }
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putDouble("velocity", this.velocity);
        compoundTag.putInt("private_ticks", this.privateTicks);
        compoundTag.putBoolean("is_primed", this.isPrimed);
        compoundTag.putBoolean("has_hit_entity", this.hasHitEntity);
        compoundTag.putInt("pulse_counter", this.pulseCounter);
        compoundTag.putInt("pulse_spacer", this.pulseSpacer);
        compoundTag.putInt("fuse", this.fuse);
        compoundTag.putDouble(RANGE, this.range);
        compoundTag.putDouble(HEAL_VALUE, this.healValue);
        compoundTag.putDouble(PULSES, this.pulses);
        compoundTag.putDouble(DAMAGE, this.damage);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.velocity = compoundTag.getDouble("velocity");
        this.privateTicks = compoundTag.getInt("private_ticks");
        this.isPrimed = compoundTag.getBoolean("is_primed");
        this.hasHitEntity = compoundTag.getBoolean("has_hit_entity");
        this.pulseCounter = compoundTag.getInt("pulse_counter");
        this.pulseSpacer = compoundTag.getInt("pulse_spacer");
        this.fuse = compoundTag.getInt("fuse");
        this.range = compoundTag.getDouble(RANGE);
        this.healValue = compoundTag.getDouble(HEAL_VALUE);
        this.pulses = compoundTag.getDouble(PULSES);
        this.damage = compoundTag.getDouble(DAMAGE);
    }

    void orbEnergyParticles(){
        var reducedPointsInRadius = 1;
        var projectile = this.element;
        var velocityA = Helpers.getRandomParticleVelocity(projectile, 0.1);
        var velocityB = Helpers.getRandomParticleVelocity(projectile, 0.05);
        var particleOptionsOne = ParticleHandlers.genericParticle(GENERIC_PARTICLE, this.getElementType(), 10,2.5F);
        var particleOptionsTwo = bakedParticle(this.getElementType().id(), 8, 2.5F, false);

        PositionFinders.getRandomSphericalPositions(projectile, 1,  reducedPointsInRadius,
            position -> ParticleHandlers.sendParticles(
                level(), particleOptionsOne, position.add(0,0.2,0), 1,
                velocityA.x, velocityA.y, velocityA.z, 0.1
            )
        );

        PositionFinders.getRandomSphericalPositions(projectile, 1,  reducedPointsInRadius * 2,
            position -> ParticleHandlers.sendParticles(
                level(), particleOptionsTwo, position.add(0,0.2,0), 1,
                velocityB.x, velocityB.y, velocityB.z, 0.1
            )
        );
    }

}
