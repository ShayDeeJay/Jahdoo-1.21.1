package org.jahdoo.ability.abilities.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.abilities.ability_data.OverchargedAbility;
import org.jahdoo.ability.effects.CustomMobEffect;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.ability.ability_components.LightningTrail.getLightningTrailModifiers;
import static org.jahdoo.entities.EntityMovers.entityMoverNoVertical;
import static org.jahdoo.particle.ParticleHandlers.bakedParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.ELECTRIC_PARTICLE_SELECTION;
import static org.jahdoo.registers.AttributesRegister.LIGHTNING_MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.registers.AttributesRegister.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.utils.ModHelpers.Random;

public class Overcharge extends DefaultEntityBehaviour {

    int privateTicks;
    double orbSize = 1;
    double velocity = 0.2;
    int chance = lifetimeA / 4;
    private static final int lifetimeA = 120;
    private boolean hasHitEntity;
    private boolean isPrimed;

    double damage;
    double gravitationalPull;
    double instability;
    double effectChance;
    double effectDuration;
    double effectStrength;
    WandAbilityHolder abilityHolder;

    @Override
    public void getElementProjectile(ElementProjectile elementProjectile) {
        super.getElementProjectile(elementProjectile);
        elementProjectile.setShowTrailParticles(!isPrimed);
        if(this.elementProjectile.getOwner() != null){
            var player = this.elementProjectile.getOwner();
            var damage = this.getTag(DAMAGE);
            this.damage = ModHelpers.attributeModifierCalculator(
                (LivingEntity) player,
                (float) damage,
                true,
                MAGIC_DAMAGE_MULTIPLIER,
                LIGHTNING_MAGIC_DAMAGE_MULTIPLIER
            );
            abilityHolder = boltModifiers();
        }
        this.gravitationalPull = this.getTag(OverchargedAbility.gravitationalPull);
        this.instability = this.getTag(OverchargedAbility.instability);
        this.effectChance = this.getTag(EFFECT_CHANCE);
        this.effectDuration = this.getTag(EFFECT_DURATION);
        this.effectStrength = this.getTag(EFFECT_STRENGTH);
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putDouble("orb_size", this.orbSize);
        compoundTag.putDouble("velocity", this.velocity);
        compoundTag.putInt("chance", this.chance);
        compoundTag.putInt("private_ticks", this.privateTicks);
        compoundTag.putDouble(EFFECT_CHANCE, this.effectChance);
        compoundTag.putDouble(EFFECT_DURATION, this.effectDuration);
        compoundTag.putDouble(EFFECT_STRENGTH, this.effectStrength);
        compoundTag.putDouble(DAMAGE, this.damage);
        compoundTag.putDouble(OverchargedAbility.gravitationalPull, this.gravitationalPull);
        compoundTag.putDouble(OverchargedAbility.instability, this.instability);
        compoundTag.putBoolean("isPrimed", this.isPrimed);
        compoundTag.putBoolean("hasHitEntity", this.hasHitEntity);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.isPrimed = compoundTag.getBoolean("isPrimed");
        this.orbSize = compoundTag.getDouble("orb_size");
        this.velocity = compoundTag.getDouble("velocity");
        this.chance = compoundTag.getInt("chance");
        this.privateTicks = compoundTag.getInt("private_ticks");
        this.effectChance = compoundTag.getDouble(EFFECT_CHANCE);
        this.effectDuration = compoundTag.getDouble(EFFECT_DURATION);
        this.effectStrength = compoundTag.getDouble(EFFECT_STRENGTH);
        this.damage = compoundTag.getDouble(DAMAGE);
        this.abilityHolder = boltModifiers();
        this.gravitationalPull = compoundTag.getDouble(OverchargedAbility.gravitationalPull);
        this.instability = compoundTag.getDouble(OverchargedAbility.instability);
        this.hasHitEntity = compoundTag.getBoolean("hasHitEntity");
    }

    private WandAbilityHolder boltModifiers(){
        return getLightningTrailModifiers(damage, 2, 8, 1);
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
        if (privateTicks > lifetimeA / 1.5) orbSize += 0.02;

        applyInertia(this.elementProjectile, 0.96f);
        this.setPrimed();

        if (privateTicks > 25 || this.hasHitEntity) {
            orbEnergyParticles();
            if (chance >= instability) chance--;
            if (velocity <= 0.8) velocity += 0.02;
            if (this.elementProjectile.getOwner() != null) {
                pullEntitiesCloser();
                if (privateTicks % Math.min(chance, 20) == 0) shootSpikesRandomly();
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
    public void discardCondition() {if(privateTicks > lifetimeA){
            elementProjectile.setAnimation(8);
            if(privateTicks > lifetimeA + 10) {
                dischargeEffect();
                ModHelpers.getSoundWithPosition(
                    this.elementProjectile.level(),
                    this.elementProjectile.blockPosition(),
                    SoundRegister.BOLT.get(),2f,0.5f
                );
                ModHelpers.getSoundWithPosition(
                    this.elementProjectile.level(),
                    this.elementProjectile.blockPosition(),
                    SoundRegister.EXPLOSION.get(),2f
                );
                this.elementProjectile.discard();
            }
        }
    }

    private void pullEntitiesCloser(){
        var projectile = this.elementProjectile;
        if(projectile.getOwner() == null) return;

        var nearbyEntities = projectile.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            (LivingEntity) projectile.getOwner(),
            projectile.getBoundingBox().inflate(gravitationalPull + 5)
        );


        for (LivingEntity entities : nearbyEntities) {
            if(isOpp(entities)){
                var knockBackRes = entities.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
                var resistance = knockBackRes != null ? knockBackRes.getValue() : 0;
                var velocity = (gravitationalPull + 0.5) - resistance;
                entityMoverNoVertical(projectile, entities, velocity);
            }
        }
    }

    private boolean isOpp(LivingEntity nearbyEntities) {
        return canDamageEntity(nearbyEntities, (LivingEntity) this.elementProjectile.getOwner());
    }

    private void shootSpikesRandomly(){
        var speeds = Random.nextFloat((float) (velocity - 0.2f), (float) velocity);
        for (int entitiesShot = 0; entitiesShot < Random.nextInt(2, 4); entitiesShot++){
            var hasBlockBelow = this.elementProjectile.verticalCollisionBelow;
            var theta = Random.nextDouble() * Math.PI *  (hasBlockBelow ? 1 : 4);
            var phi = Random.nextDouble() * Math.PI;
            var x = Math.sin(phi) * Math.cos(theta);
            var y = Math.sin(phi) * Math.sin(theta);
            var z = Math.cos(phi);
            var newPosition = this.elementProjectile.position().add(this.elementProjectile.getDeltaMovement().scale(4.5));
            if(this.elementProjectile.getOwner() != null){
                var genericProjectile = new GenericProjectile(
                    this.elementProjectile.getOwner(),
                    newPosition.x, newPosition.y, newPosition.z,
                    EntityPropertyRegister.LIGHTNING_TRAIL.get().setAbilityId(),
                    this.abilityHolder,
                    ElementRegistry.LIGHTNING.get(),
                    OverchargedAbility.abilityId.getPath()
                );
                genericProjectile.setOwner(this.elementProjectile.getOwner());
                ModHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.blockPosition(), SoundRegister.BOLT.get(), 0.3f, 0.8f);
                genericProjectile.shoot(x, y, z, speeds  + 0.1f, 0);
                this.elementProjectile.level().addFreshEntity(genericProjectile);
            }
        }
    }

    private void dischargeEffect(){
        var dischargeEffect = (int) (effectStrength + 5);
        var projectile = this.elementProjectile;
        projectile.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            (LivingEntity) projectile.getOwner(),
            projectile.getBoundingBox().inflate(dischargeEffect)
        ).forEach(
            entities -> {
                if(isOpp(entities)){
                    if (Random.nextInt(0, chance) == 0) {
                        entities.addEffect(
                            new CustomMobEffect(
                                EffectsRegister.LIGHTNING_EFFECT.getDelegate(),
                                (int) this.effectDuration,
                                (int) this.effectStrength
                            )
                        );
                    }
                }
            }
        );

        double origin = (double) dischargeEffect / 15;
        double bound = (double) dischargeEffect / 5;
        var pCount = dischargeEffect * 2;

        ParticleHandlers.sendParticles(level(),
            genericParticleOptions(ELECTRIC_PARTICLE_SELECTION, this.getElementType(), Random.nextInt(5,10),5, 1.2),
            projectile.position(),
            pCount,
            Random.nextDouble(origin, bound),
            Random.nextDouble(origin, bound),
            Random.nextDouble(origin, bound),
            0
        );

        ParticleHandlers.sendParticles(level(),
            bakedParticleOptions(this.getElementType().getTypeId(), 4, 3, false),
            projectile.position(),
            pCount,
            Random.nextDouble(origin, bound),
            Random.nextDouble(origin, bound),
            Random.nextDouble(origin, bound),
            0.6
        );
    }

    private Level level(){
        return this.elementProjectile.level();
    }

    void orbEnergyParticles(){
        var velocityA = ModHelpers.getRandomParticleVelocity(this.elementProjectile, 0.1);
        var velocityB = ModHelpers.getRandomParticleVelocity(this.elementProjectile, 0.05);
        var reducedPointsInRadius = (double) privateTicks / 70;

        var particleOptionsOne = genericParticleOptions(ELECTRIC_PARTICLE_SELECTION, this.getElementType(), 10,2);
        var particleOptionsTwo = bakedParticleOptions(this.getElementType().getTypeId(), 8, 2.5f, false);

        PositionGetters.getRandomSphericalPositions(this.elementProjectile, this.orbSize,  reducedPointsInRadius,
            position -> ParticleHandlers.sendParticles(
                level(), particleOptionsOne, position.add(0,0.2,0), 0,
                velocityA.x, velocityA.y, velocityA.z, 0.1
            )
        );

        PositionGetters.getRandomSphericalPositions(this.elementProjectile, this.orbSize,  reducedPointsInRadius * 5,
            position -> ParticleHandlers.sendParticles(
                level(), particleOptionsTwo, position.add(0,0.2,0), 1,
                velocityB.x, velocityB.y, velocityB.z, privateTicks >= 300 ? 0.5 : 0.2
            )
        );
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.LIGHTNING.get();
    }

    ResourceLocation abilityId = ModHelpers.res("overcharge_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new Overcharge();
    }
}
