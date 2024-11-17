package org.jahdoo.all_magic.all_abilities.ability_components;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.all_magic.AbilityBuilder;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.all_magic.AbilityBuilder.*;


public class LightningTrail extends DefaultEntityBehaviour {
    double randomFactor;
    public static final String EASING = "ease";
    public static final String SHOULD_EASE = "should_ease";

    double damage;
    double easing;
    double lifetime;
    double shouldEase;

    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        if(this.genericProjectile.getOwner() != null){
            this.damage = this.getTag(DAMAGE);
        }
        this.easing = this.getTag(EASING);
        this.lifetime = this.getTag(LIFETIME);
        this.shouldEase = this.getTag(SHOULD_EASE);
    }

    @Override
    public double getTag(String name) {
        var wandAbilityHolder = this.genericProjectile.wandAbilityHolder();
        var ability = abilityId.getPath().intern();
        var modifier = ModHelpers.getModifierValue(wandAbilityHolder, ability).get(name);
        return modifier != null ? modifier.actualValue() : 0;
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putDouble(DAMAGE, this.damage);
        compoundTag.putDouble(EASING, this.easing);
        compoundTag.putDouble(LIFETIME, this.lifetime);
        compoundTag.putDouble(SHOULD_EASE, this.shouldEase);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.damage = compoundTag.getDouble(DAMAGE);
        this.easing = compoundTag.getDouble(EASING);
        this.lifetime = compoundTag.getDouble(LIFETIME);
        this.shouldEase = compoundTag.getDouble(SHOULD_EASE);
    }

    public static WandAbilityHolder getLightningTrailModifiers(double damage, double easing, double lifetime, double shouldEase){
        return new AbilityBuilder(null, abilityId.getPath().intern())
            .setModifierWithoutBounds(DAMAGE, damage)
            .setModifierWithoutBounds(EASING, easing)
            .setModifierWithoutBounds(LIFETIME, lifetime)
            .setModifierWithoutBounds(SHOULD_EASE, shouldEase)
            .buildAndReturn();
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.LIGHTNING.get();
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        hitEntity.hurt(
            this.genericProjectile.damageSources().playerAttack((Player) this.genericProjectile.getOwner()),
            (float) damage
        );
    }

    @Override
    public void onTickMethod() {
        if(shouldEase == 0){
            if (this.randomFactor < 2) this.randomFactor += easing;
        } else {
            this.randomFactor = easing;
        }

        if(this.genericProjectile != null){
            moveLikeLightningBolt(this.genericProjectile);
            ParticleHandlers.GenericProjectile(
                genericProjectile,
                new BakedParticleOptions(getElementType().getTypeId(), 1, 0.3f, true),
                genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, ElementRegistry.LIGHTNING.get(), 2, 1.5f),
                0.015
            );
        }
    }

    @Override
    public void discardCondition() {
        if(this.genericProjectile != null){
            if (genericProjectile.tickCount > ModHelpers.Random.nextInt((int) (lifetime - 2), (int) lifetime)) {
                genericProjectile.discard();
            }
        }
    }

    private void moveLikeLightningBolt(Projectile projectile) {
        Vec3 currentMovement = projectile.getDeltaMovement();
        if (ModHelpers.Random.nextDouble() < 0.98) {
            double dx = (ModHelpers.Random.nextDouble() - 0.5) * randomFactor;
            double dy = (ModHelpers.Random.nextDouble() - 0.5) * randomFactor;
            double dz = (ModHelpers.Random.nextDouble() - 0.5) * randomFactor;
            Vec3 newMovement = currentMovement.add(dx, dy, dz).normalize().scale(currentMovement.length());
            projectile.setDeltaMovement(newMovement);
        }

        Vec3 vec3 = projectile.getDeltaMovement();
        projectile.setPos(projectile.getX() + vec3.x, projectile.getY() + vec3.y, projectile.getZ() + vec3.z);
    }

    public static final ResourceLocation abilityId = ModHelpers.res("lightning_trail_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new LightningTrail();
    }
}
