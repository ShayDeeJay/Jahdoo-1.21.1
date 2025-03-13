package org.jahdoo.ascension.ability.abilities.elemental_shooter;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.ability.effects.JahdooMobEffect;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.DamageUtils;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.ElementReg;

import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.common.particle.ParticleHandlers.*;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE;
import static org.jahdoo.common.registers.AttributeReg.MAGIC_DAMAGE_MULTIPLIER;

public class ElementalShooter extends DefaultEntityBehaviour {

    private static final ResourceLocation abilityId = Helpers.res("elemental_shooter_property");
    private double numberOfRicochets;
    private double effectStrength;
    private double effectDuration;
    private double effectChance;
    private double damage;
    private int blockBounce;

    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        this.numberOfRicochets = getTag(ElementalShooterAbility.NUMBER_OF_RICOCHET);
        this.effectChance = getTag(EFFECT_CHANCE);
        this.effectStrength = getTag(EFFECT_STRENGTH);
        this.effectDuration = getTag(EFFECT_DURATION);

        if(this.generic.getOwner() != null){
            var player = this.generic.getOwner();
            var damage = this.getTag(DAMAGE);
            var elementId = getTag(SET_ELEMENT_TYPE);
            var element = ElementReg.fromId((int) elementId);
            element.ifPresent(
                getElement -> {
                    this.damage = Helpers.attributeModifierCalculator(
                        (LivingEntity) player,
                        (float) damage,
                        true,
                        MAGIC_DAMAGE_MULTIPLIER,
                        getElement.damageAmplifier()
                    );
                }
            );
        }
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return this.generic.wandAbilityHolder();
    }

    @Override
    public String abilityId() {
        return ElementalShooterAbility.abilityId.getPath().intern();
    }

    @Override
    public void onTickMethod() {
        animateParticles(this.generic, getElement());
    }

    private void setDamageByOwner(LivingEntity target){
        DamageUtils.damageWithJahdoo(target, this.generic.getOwner(), this.damage);
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new ElementalShooter();
    }

    private AbstractElement getElement(){
        var elementId = (int) getTag(SET_ELEMENT_TYPE);
        return ElementReg.fromId(elementId).orElseThrow();
    }

    private void applyEffect(LivingEntity livingEntity, Holder<MobEffect> mobEffect){
        if(!livingEntity.hasEffect(mobEffect)){
            if (Helpers.Random.nextInt(0, this.effectChance == 0 ? 1 : (int) this.effectChance) == 0) {
                livingEntity.addEffect(new JahdooMobEffect(mobEffect, (int) effectDuration, (int) effectStrength));
            }
        }
    }

    @Override
    public void discardCondition() {
        if (this.generic.getOwner() != null && this.generic.distanceTo(this.generic.getOwner()) > 30f) {
            if(!(this.generic.level() instanceof ServerLevel serverLevel)) return;
            ParticleHandlers.particleBurst(serverLevel, this.generic.position(), 1, getElement().getParticleGroup().bakedSlow());
            this.generic.discard();
        }
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        this.applyEffect(hitEntity, getElement().effect());
        if(!(this.generic.level() instanceof ServerLevel serverLevel)) return;
        ParticleHandlers.particleBurst(serverLevel, this.generic.position(), 1, getElement().getParticleGroup().bakedSlow());
        this.setDamageByOwner(hitEntity);
        this.generic.discard();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        if(blockBounce == numberOfRicochets) this.generic.discard();

        Helpers.getSoundWithPosition(this.generic.level(), this.generic.blockPosition(), getElement().sound(), 0.4f);
        if(!(this.generic.level() instanceof ServerLevel serverLevel)) return;
        ParticleHandlers.particleBurst(serverLevel, this.generic.position(), 1, getElement().getParticleGroup().bakedSlow());
        this.setReboundBehaviour(blockHitResult);
    }

    private void setReboundBehaviour(BlockHitResult blockHitResult){
        Vec3 normal = Vec3.atLowerCornerOf(blockHitResult.getDirection().getNormal());
        Vec3 motion = this.generic.getDeltaMovement();
        Vec3 reflection = motion.subtract(normal.scale(2 * motion.dot(normal)));
        this.generic.setDeltaMovement(reflection);
        blockBounce++;
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putInt("blockBounce", this.blockBounce);
        compoundTag.putDouble(ElementalShooterAbility.NUMBER_OF_RICOCHET, this.numberOfRicochets);
        compoundTag.putDouble(EFFECT_CHANCE, this.effectChance);
        compoundTag.putDouble(EFFECT_DURATION, this.effectDuration);
        compoundTag.putDouble(EFFECT_STRENGTH, this.effectStrength);
        compoundTag.putDouble(DAMAGE, this.damage);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.blockBounce = compoundTag.getInt("blockBounce");
        this.numberOfRicochets = compoundTag.getDouble(ElementalShooterAbility.NUMBER_OF_RICOCHET);
        this.effectChance = compoundTag.getDouble(EFFECT_CHANCE);
        this.effectDuration = compoundTag.getDouble(EFFECT_DURATION);
        this.effectStrength = compoundTag.getDouble(EFFECT_STRENGTH);
        this.damage = compoundTag.getDouble(DAMAGE);
    }


    public static void animateParticles(Projectile projectile, AbstractElement element) {
        if(projectile.tickCount > 1){
            var baked = bakedParticle(element.id(), 2, 1.5f, false);
            var pos = projectile.position().add(0, 0.1, 0);
            genericProjPart(projectile.level(), pos, 1, baked, 0.03f);
            playParticles3(
                genericParticle(SOFT_PARTICLE, element, 2, 1f, false),
                projectile, 10, 0.01
            );
        }
    }

}
