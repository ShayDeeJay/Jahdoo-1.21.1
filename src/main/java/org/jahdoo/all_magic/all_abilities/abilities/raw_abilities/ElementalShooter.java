package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.all_abilities.abilities.ElementalShooterAbility;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.AttributesRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.all_magic.effects.CustomMobEffect;
import org.jahdoo.utils.GeneralHelpers;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.all_magic.AbilityBuilder.*;

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
            this.damage = GeneralHelpers.attributeModifierCalculator(
                (LivingEntity) player,
                (float) damage,
                element,
                AttributesRegister.MAGIC_DAMAGE_MULTIPLIER,
                true
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
    public double getTag(String name) {
        var wandAbilityHolder = this.genericProjectile.wandAbilityHolder();
        var ability = ElementalShooterAbility.abilityId.getPath().intern();
        return GeneralHelpers.getModifierValue(wandAbilityHolder, ability).get(name).actualValue();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        if(!(this.genericProjectile.level() instanceof ServerLevel serverLevel)) return;
        if(blockBounce == numberOfRicochets) this.genericProjectile.discard();

        GeneralHelpers.getSoundWithPosition(this.genericProjectile.level(), this.genericProjectile.blockPosition(), getElement().getElementSound(), 0.4f);
        ParticleHandlers.spawnPoof(serverLevel, this.genericProjectile.position(), 2, getElement().getParticleGroup().bakedSlow());
        this.setReboundBehaviour(blockHitResult);
    }

    private AbstractElement getElement(){
        var elementRegistry = ElementRegistry.getElementByTypeId((int) getTag(SET_ELEMENT_TYPE));
        return elementRegistry.isEmpty() ? ElementRegistry.MYSTIC.get() : elementRegistry.getFirst();
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        if(!(this.genericProjectile.level() instanceof ServerLevel serverLevel)) return;
        ParticleHandlers.spawnPoof(serverLevel, this.genericProjectile.position(), 1, getElement().getParticleGroup().bakedSlow());
        this.applyEffect(hitEntity, getElement().elementEffect());
        this.setDamageByOwner(hitEntity);
        this.genericProjectile.discard();
    }

    @Override
    public void onTickMethod() {
        ParticleHandlers.GenericProjectile(
            this.genericProjectile,
            new BakedParticleOptions(getElement().getTypeId(), 2, 0.25f, true),
            genericParticleOptions(ParticleStore.SOFT_PARTICLE_SELECTION, getElement(), 5, 1.2f),
            0.015
        );
    }

    @Override
    public void discardCondition() {
        if (this.genericProjectile.getOwner() != null && this.genericProjectile.distanceTo(this.genericProjectile.getOwner()) > 50f) {
            if(!(this.genericProjectile.level() instanceof ServerLevel serverLevel)) return;
            ParticleHandlers.spawnPoof(serverLevel, this.genericProjectile.position(), 1, getElement().getParticleGroup().bakedSlow());
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
            if (GeneralHelpers.Random.nextInt(0, this.effectChance == 0 ? 1 : (int) this.effectChance) == 0) {
                livingEntity.addEffect(new CustomMobEffect(mobEffect, (int) effectDuration, (int) effectStrength));
            }
        }
    }

    private void setDamageByOwner(LivingEntity target){
        if (this.genericProjectile.getOwner() != null) {
            target.hurt(
                this.genericProjectile.damageSources().playerAttack((Player) this.genericProjectile.getOwner()),
                (float) this.damage
            );
        } else  {
            target.hurt(this.genericProjectile.damageSources().magic(), (float) damage);
        }
//        if(!target.isAlive()) throwNewItem(target, new ItemStack(ItemsRegister.AUGMENT_ITEM.get()));
    }

    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("elemental_shooter_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new ElementalShooter();
    }
}
