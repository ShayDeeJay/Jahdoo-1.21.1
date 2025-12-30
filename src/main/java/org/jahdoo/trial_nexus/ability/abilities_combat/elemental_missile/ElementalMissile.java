package org.jahdoo.trial_nexus.ability.abilities_combat.elemental_missile;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.ability.DefaultEntityBehaviour;
import org.jahdoo.trial_nexus.ability.effects.JahdooMobEffect;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.utils.DamageUtils;
import org.jahdoo.trial_nexus.utils.Helpers;

import static net.minecraft.world.entity.ai.targeting.TargetingConditions.DEFAULT;
import static org.jahdoo.common.particle.ParticleHandlers.*;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE;
import static org.jahdoo.common.registers.AttributeReg.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.trial_nexus.ability.AbilityBuilder.*;

public class ElementalMissile extends DefaultEntityBehaviour {

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
        this.numberOfRicochets = getTag(NUMBER_OF_RICOCHET);
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
    public AbilityHolder getAbilityHolder() {
        return this.generic.getAbilityHolder();
    }

    @Override
    public String abilityId() {
        return generic.getAbilityId();
    }

    @Override
    public void onTickMethod() {
        animateParticles(this.generic, getElement());
    }

    private void setDamageByOwner(LivingEntity target){
        DamageUtils.damageWithJahdoo(target, this.generic.getOwner(), this.damage, getElement().damageTypeResourceKey());
        this.applyEffect(target, getElement().effect());
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new ElementalMissile();
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
        if(!(this.generic.level() instanceof ServerLevel serverLevel)) return;
        altOnHitWithResult(serverLevel);
        if (this.generic.getOwner() != null && this.generic.distanceTo(this.generic.getOwner()) > 30f) {
            ParticleHandlers.particleBurst(serverLevel, this.generic.position(), 1, getElement().getParticleGroup().bakedSlow());
            this.generic.discard();
        }
    }

    public static boolean altOnHitCheck(Player player) {
        var missileAtt = AttributeReg.ELEMENTAL_SHOTGUN;
        return player.getAttributes().getValue(missileAtt) > 0;
    }

    private void altOnHitWithResult(ServerLevel serverLevel){
        if(generic.getOwner() instanceof Player player){
            if (altOnHitCheck(player)) altOnHit(serverLevel);
        }
    }

    private void altOnHit(ServerLevel serverLevel) {
        if(generic.tickCount < 6) return;
        Helpers.getSoundWithPositionV(generic.level(), this.generic.position(), getElement().sound(), 0.2F, 1.4F);
        Helpers.getSoundWithPositionV(generic.level(), this.generic.position(), SoundReg.ELEMENTAL_BULLET.get(), 0.6F, 1F);
        ParticleHandlers.particleBurst(serverLevel, this.generic.position(), 10, ParticleHandlers.bakedParticle(this.getElement().id(), 6, 1, false), 0.12F);
        var targets = serverLevel.getNearbyEntities(
            LivingEntity.class, DEFAULT, null, this.generic.getBoundingBox().inflate(3,3,3)
        );

        for (var target : targets) this.setDamageByOwner(target);
        this.generic.discard();
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        if(!(this.generic.level() instanceof ServerLevel serverLevel)) return;
        altOnHitWithResult(serverLevel);
        Helpers.getSoundWithPositionV(this.generic.level(), hitEntity.position(), SoundReg.ELEMENTAL_BULLET.get(), 1, 0.8F);
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
        Helpers.getSoundWithPosition(serverLevel, blockHitResult.getBlockPos(), SoundReg.ELEMENTAL_BULLET.get(), 0.8F, 1.2F);
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
        compoundTag.putDouble(NUMBER_OF_RICOCHET, this.numberOfRicochets);
        compoundTag.putDouble(EFFECT_CHANCE, this.effectChance);
        compoundTag.putDouble(EFFECT_DURATION, this.effectDuration);
        compoundTag.putDouble(EFFECT_STRENGTH, this.effectStrength);
        compoundTag.putDouble(DAMAGE, this.damage);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.blockBounce = compoundTag.getInt("blockBounce");
        this.numberOfRicochets = compoundTag.getDouble(NUMBER_OF_RICOCHET);
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
