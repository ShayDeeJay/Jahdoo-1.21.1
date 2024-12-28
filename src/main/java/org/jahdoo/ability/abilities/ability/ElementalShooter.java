package org.jahdoo.ability.abilities.ability;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.abilities.ability_data.ElementalShooterAbility;
import org.jahdoo.ability.effects.CustomMobEffect;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.DamageUtil;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.particle.ParticleHandlers.*;
import static org.jahdoo.particle.ParticleStore.SOFT_PARTICLE_SELECTION;
import static org.jahdoo.registers.AttributesRegister.MAGIC_DAMAGE_MULTIPLIER;

public class ElementalShooter extends DefaultEntityBehaviour {
    private int blockBounce;
    double numberOfRicochets;
    double effectChance;
    double effectStrength;
    double effectDuration;
    double damage;


    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        this.numberOfRicochets = getTag(ElementalShooterAbility.numberOfRicochet);
        this.effectChance = getTag(EFFECT_CHANCE);
        this.effectStrength = getTag(EFFECT_STRENGTH);
        this.effectDuration = getTag(EFFECT_DURATION);

        if(this.genericProjectile.getOwner() != null){
            var player = this.genericProjectile.getOwner();
            var damage = this.getTag(DAMAGE);
            var elementId = getTag(SET_ELEMENT_TYPE);
            var element = ElementRegistry.getElementByTypeId((int) elementId).getFirst();
            this.damage = ModHelpers.attributeModifierCalculator(
                (LivingEntity) player,
                (float) damage,
                true,
                MAGIC_DAMAGE_MULTIPLIER,
                element.getDamageTypeAmplifier().getSecond()
            );
        }
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putInt("blockBounce", this.blockBounce);
        compoundTag.putDouble(ElementalShooterAbility.numberOfRicochet, this.numberOfRicochets);
        compoundTag.putDouble(EFFECT_CHANCE, this.effectChance);
        compoundTag.putDouble(EFFECT_DURATION, this.effectDuration);
        compoundTag.putDouble(EFFECT_STRENGTH, this.effectStrength);
        compoundTag.putDouble(DAMAGE, this.damage);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.blockBounce = compoundTag.getInt("blockBounce");
        this.numberOfRicochets = compoundTag.getDouble(ElementalShooterAbility.numberOfRicochet);
        this.effectChance = compoundTag.getDouble(EFFECT_CHANCE);
        this.effectDuration = compoundTag.getDouble(EFFECT_DURATION);
        this.effectStrength = compoundTag.getDouble(EFFECT_STRENGTH);
        this.damage = compoundTag.getDouble(DAMAGE);
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return this.genericProjectile.wandAbilityHolder();
    }

    @Override
    public String abilityId() {
        return ElementalShooterAbility.abilityId.getPath().intern();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        if(blockBounce == numberOfRicochets) this.genericProjectile.discard();

        ModHelpers.getSoundWithPosition(this.genericProjectile.level(), this.genericProjectile.blockPosition(), getElement().getElementSound(), 0.4f);
        if(!(this.genericProjectile.level() instanceof ServerLevel serverLevel)) return;
        ParticleHandlers.particleBurst(serverLevel, this.genericProjectile.position(), 1, getElement().getParticleGroup().bakedSlow());
        this.setReboundBehaviour(blockHitResult);
    }

    private AbstractElement getElement(){
        var elementRegistry = ElementRegistry.getElementByTypeId((int) getTag(SET_ELEMENT_TYPE));
        return elementRegistry.isEmpty() ? ElementRegistry.MYSTIC.get() : elementRegistry.getFirst();
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        this.applyEffect(hitEntity, getElement().elementEffect());
        if(!(this.genericProjectile.level() instanceof ServerLevel serverLevel)) return;
        ParticleHandlers.particleBurst(serverLevel, this.genericProjectile.position(), 1, getElement().getParticleGroup().bakedSlow());
        this.setDamageByOwner(hitEntity);
        this.genericProjectile.discard();
    }

    @Override
    public void onTickMethod() {
        animateParticles(this.genericProjectile, getElement());
    }

    public static void animateParticles(Projectile projectile, AbstractElement element) {
        if(projectile.tickCount > 1){
            var baked = bakedParticleOptions(element.getTypeId(), 2, 1.5f, false);
            var pos = projectile.position().add(0, 0.1, 0);
            genericProjPart(projectile.level(), pos, 1, baked, 0.03f);
            playParticles3(
                genericParticleOptions(SOFT_PARTICLE_SELECTION, element, 2, 0.9f, false),
                projectile, 8, 0.01
            );
        }
    }

    @Override
    public void discardCondition() {
        if (this.genericProjectile.getOwner() != null && this.genericProjectile.distanceTo(this.genericProjectile.getOwner()) > 30f) {
            if(!(this.genericProjectile.level() instanceof ServerLevel serverLevel)) return;
            ParticleHandlers.particleBurst(serverLevel, this.genericProjectile.position(), 1, getElement().getParticleGroup().bakedSlow());
            this.genericProjectile.discard();
        }
    }

    private void setReboundBehaviour(BlockHitResult blockHitResult){
        Vec3 normal = Vec3.atLowerCornerOf(blockHitResult.getDirection().getNormal());
        Vec3 motion = this.genericProjectile.getDeltaMovement();
        Vec3 reflection = motion.subtract(normal.scale(2 * motion.dot(normal)));
        this.genericProjectile.setDeltaMovement(reflection);
        blockBounce++;
    }

    private void applyEffect(LivingEntity livingEntity, Holder<MobEffect> mobEffect){
        if(!livingEntity.hasEffect(mobEffect)){
            if (ModHelpers.Random.nextInt(0, this.effectChance == 0 ? 1 : (int) this.effectChance) == 0) {
                livingEntity.addEffect(new CustomMobEffect(mobEffect, (int) effectDuration, (int) effectStrength));
            }
        }
    }

    private void setDamageByOwner(LivingEntity target){
        DamageUtil.damageWithJahdoo(target, this.genericProjectile.getOwner(), this.damage);
    }

    ResourceLocation abilityId = ModHelpers.res("elemental_shooter_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new ElementalShooter();
    }
}
