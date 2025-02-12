package org.jahdoo.ability.abilities.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.abilities.ability_data.OverchargedAbility;
import org.jahdoo.ability.ability_components.SoulSiphonNova;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.ability.abilities.ability_data.OverchargedAbility.*;
import static org.jahdoo.particle.ParticleHandlers.bakedParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.GENERIC_PARTICLE_SELECTION;
import static org.jahdoo.registers.AttributesRegister.*;
import static org.jahdoo.registers.EntityPropertyRegister.*;

public class Overcharge extends DefaultEntityBehaviour {
    public static final ResourceLocation abilityId = ModHelpers.res("overcharge_property");

    int privateTicks;
    double velocity = 0.2;
    boolean hasHitEntity;
    boolean isPrimed;
    int counter;
    int fuse;

    double damage;
    double range;
    double healValue;
    double pulses;

    @Override
    public void getElementProjectile(ElementProjectile elementProjectile) {
        super.getElementProjectile(elementProjectile);
        elementProjectile.setShowTrailParticles(!isPrimed);
        if(this.elementProjectile.getOwner() != null){
            var player = this.elementProjectile.getOwner();
            var damage = this.getTag(DAMAGE);
            this.damage = ModHelpers.attributeModifierCalculator(
                (LivingEntity) player, (float) damage, true,
                MAGIC_DAMAGE_MULTIPLIER, VITALITY_MAGIC_DAMAGE_MULTIPLIER
            );
        }
        this.range = this.getTag(RANGE);
        this.healValue = this.getTag(HEAL_VALUE);
        this.pulses = this.getTag(PULSES);
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putDouble("velocity", this.velocity);
        compoundTag.putInt("private_ticks", this.privateTicks);
        compoundTag.putBoolean("is_primed", this.isPrimed);
        compoundTag.putBoolean("has_hit_entity", this.hasHitEntity);
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
        this.range = compoundTag.getDouble(RANGE);
        this.healValue = compoundTag.getDouble(HEAL_VALUE);
        this.pulses = compoundTag.getDouble(PULSES);
        this.damage = compoundTag.getDouble(DAMAGE);
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return this.elementProjectile.getwandabilityholder();
    }

    @Override
    public String abilityId() {
        return OverchargedAbility.abilityId.getPath().intern();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        this.elementProjectile.discard();
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        this.hasHitEntity = true;
    }

    @Override
    public void onTickMethod() {
        privateTicks++;

        var projectile = this.elementProjectile;
        applyInertia(projectile, 0.96f);
        this.setPrimed();

        if (privateTicks >= 20 || this.hasHitEntity) {
            orbEnergyParticles();
            if (velocity <= 0.8) velocity += 0.02;
            if (projectile.getOwner() != null) {
                if(privateTicks % 40 == 0) {
                    counter++;
                    createModule(projectile.position().add(0, 0.2, 0));
                    ModHelpers.getSoundWithPosition(projectile.level(), projectile.blockPosition(), SoundRegister.HEAL.get(), 1f, 0.8f);
                }
            }
        }
    }

    public void setPrimed(){
        if (privateTicks >= 20 || this.hasHitEntity || this.isPrimed) {
            if(!this.isPrimed){
                ModHelpers.getSoundWithPosition(
                    this.elementProjectile.level(),
                    this.elementProjectile.blockPosition(),
                    SoundRegister.ORB_CREATE.get()
                );
            }
            this.elementProjectile.setDeltaMovement(0, 0, 0);
            elementProjectile.setShowTrailParticles(false);
            elementProjectile.setAnimation(7);
            this.isPrimed = true;
        }
    }

    @Override
    public void discardCondition() {
        if(counter >= pulses){
            if(fuse == 0) fuse = privateTicks;
            if(fuse + 20 == privateTicks){
                var pos = this.elementProjectile.blockPosition();
                ModHelpers.getSoundWithPosition(level(), pos, SoundRegister.HEAL.get(), 2f, 0.7f);
                ModHelpers.getSoundWithPosition(level(), pos, SoundRegister.EXPLOSION.get(), 2f);
                this.elementProjectile.discard();
            }
        }
    }

    private Level level(){
        return this.elementProjectile.level();
    }

    private void createModule(Vec3 location){
        var aoeCloud = new AoeCloud(
            level(), (LivingEntity) this.elementProjectile.getOwner(), 0.2f,
            SOUL_SIPHON_NOVA.get().setAbilityId(),
            SoulSiphonNova.setModifiers(damage, range, healValue),
            OverchargedAbility.abilityId.getPath().intern()
        );
        aoeCloud.setPos(location.x, location.y, location.z);
        aoeCloud.level().addFreshEntity(aoeCloud);
    }

    void orbEnergyParticles(){
        var velocityA = ModHelpers.getRandomParticleVelocity(this.elementProjectile, 0.1);
        var velocityB = ModHelpers.getRandomParticleVelocity(this.elementProjectile, 0.05);
        var reducedPointsInRadius = 1;

        var particleOptionsOne = genericParticleOptions(GENERIC_PARTICLE_SELECTION, this.getElementType(), 10,2.5F);
        var particleOptionsTwo = bakedParticleOptions(this.getElementType().getTypeId(), 8, 2.5F, false);

        PositionGetters.getRandomSphericalPositions(this.elementProjectile, 1,  reducedPointsInRadius,
            position -> ParticleHandlers.sendParticles(
                level(), particleOptionsOne, position.add(0,0.2,0), 1,
                velocityA.x, velocityA.y, velocityA.z, 0.1
            )
        );

        PositionGetters.getRandomSphericalPositions(this.elementProjectile, 1,  reducedPointsInRadius * 2,
            position -> ParticleHandlers.sendParticles(
                level(), particleOptionsTwo, position.add(0,0.2,0), 1,
                velocityB.x, velocityB.y, velocityB.z, 0.1
            )
        );
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.VITALITY.get();
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new Overcharge();
    }
}
