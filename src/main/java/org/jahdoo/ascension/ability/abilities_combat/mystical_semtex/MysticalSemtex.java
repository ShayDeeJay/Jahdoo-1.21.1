package org.jahdoo.ascension.ability.abilities_combat.mystical_semtex;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.DamageUtils;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.PositionFinders;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.EntityMovers;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.particle_options.GenericParticleOptions;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.EntityDataReg;
import org.jahdoo.common.registers.EntityReg;
import org.jahdoo.common.registers.SoundReg;

import java.util.List;
import java.util.UUID;

import static org.jahdoo.ascension.ability.AbilityBuilder.DAMAGE;
import static org.jahdoo.common.particle.ParticleHandlers.*;
import static org.jahdoo.common.particle.ParticleStore.GENERIC_PARTICLE;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE;
import static org.jahdoo.common.registers.AttributeReg.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.common.registers.AttributeReg.MYSTIC_MAGIC_DAMAGE_MULTIPLIER;

public class MysticalSemtex extends DefaultEntityBehaviour {

    private boolean isAttached;
    private int explosionDelay;
    private double aoe = 0.1;
    private LivingEntity target;
    private Vec3 localOffset;
    private UUID targetId;

    private double setExplosionDelay;
    private double additionalProjectiles;
    private double additionalProjectileChance;
    private double explosionRadius;
    private double damage;

    @Override
    public void getElementProjectile(ElementProjectile elementProjectile) {
        super.getElementProjectile(elementProjectile);
        var player = this.element.getOwner();
        if(player != null && !(player instanceof Player)){
            var damage = this.getTag(DAMAGE);
            this.damage = Helpers.attributeModifierCalculator(
                (LivingEntity) player,
                (float) damage,
                true,
                MAGIC_DAMAGE_MULTIPLIER,
                MYSTIC_MAGIC_DAMAGE_MULTIPLIER
            );
        } else {
            this.damage = this.getTag(DAMAGE);
        }
        this.setExplosionDelay = this.getTag(MysticalSemtexAbility.EXPLOSION_DELAYS);
        this.additionalProjectiles = this.getTag(MysticalSemtexAbility.ADDITIONAL_PROJECTILE);
        this.additionalProjectileChance = this.getTag(MysticalSemtexAbility.CLUSTER_CHANCE);
        this.explosionRadius = this.getTag(MysticalSemtexAbility.EXPLOSION_RADIUS);
    }

    @Override
    public AbilityHolder getAbilityHolder() {
        return this.element.getAbilityHolder();
    }

    @Override
    public String abilityId() {
        return MysticalSemtexAbility.abilityId.getPath().intern();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        this.element.discard();
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        targetHit(hitEntity);
    }

    @Override
    public void discardCondition() {
        if (this.element.tickCount > 300) this.element.discard();
    }

    private void adjustProjectileArc() {
        if (target == null) this.element.setDeltaMovement(this.element.getDeltaMovement().subtract(0, 0.01, 0));
    }

    private boolean isOpp(LivingEntity livingEntity) {
        return canDamageEntity(livingEntity, (LivingEntity) this.element.getOwner());
    }

    @Override
    public AbstractElement getElementType() {
        return ElementReg.mystic();
    }

    ResourceLocation abilityId = Helpers.res("mystical_semtex_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new MysticalSemtex();
    }

    private void targetHit(LivingEntity hitTarget) {
        if(isOpp(hitTarget)){
            explosionDelay = (int) setExplosionDelay;
            target = hitTarget;
            element.setAnimation(6);
            Helpers.getSoundWithPosition(this.element.level(), this.element.getOnPos(), SoundEvents.SLIME_BLOCK_BREAK);
            Helpers.getSoundWithPosition(this.element.level(), this.element.getOnPos(), getElementType().sound());
        }
    }

    private void novaDamageBehaviour(){
        var owner = this.element.getOwner();
        this.element.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            (LivingEntity) owner,
            this.element
                .getBoundingBox()
                .inflate(aoe,0, aoe)
                .deflate(0,1,0 )
        ).forEach(
            livingEntity -> {
                if(!isOpp(livingEntity)) return;
                DamageUtils.damageWithJahdoo(livingEntity, this.element.getOwner(), Math.max(damage - aoe, 1), getElementType().damageTypeResourceKey());
            }
        );
    }

    @Override
    public void onTickMethod() {
        if(!(this.element.level() instanceof ServerLevel serverLevel)) return;
        if(target == null) element.setShowTrailParticles(true);

        if(this.target == null && this.targetId != null ){
            var living = serverLevel.getEntity(this.targetId);
            if(living instanceof LivingEntity livingEntity) this.target = livingEntity;
        }

        if (explosionDelay > 0) explosionDelay--;
        adjustProjectileArc();
        attachBombAndFollow();
        onExplosion();
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putInt("explosion", this.explosionDelay);
        compoundTag.putBoolean("attached", isAttached);
        compoundTag.putDouble(MysticalSemtexAbility.EXPLOSION_DELAYS, this.setExplosionDelay);
        compoundTag.putDouble(MysticalSemtexAbility.ADDITIONAL_PROJECTILE, this.additionalProjectiles);
        compoundTag.putDouble(MysticalSemtexAbility.EXPLOSION_DELAYS, this.explosionRadius);
        compoundTag.putDouble(DAMAGE, this.damage);
        if(localOffset != null){
            compoundTag.put("offset", Helpers.nbtDoubleList(localOffset.x, localOffset.y, localOffset.z));
        }
        if(target != null){
            compoundTag.putUUID("new_target", target.getUUID());
        }
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        var listTag = compoundTag.getList("offset", 6);
        setExplosionDelay = compoundTag.getDouble(MysticalSemtexAbility.EXPLOSION_DELAYS);
        additionalProjectiles = compoundTag.getDouble(MysticalSemtexAbility.ADDITIONAL_PROJECTILE);
        additionalProjectileChance = compoundTag.getDouble(MysticalSemtexAbility.CLUSTER_CHANCE);
        explosionRadius = compoundTag.getDouble(MysticalSemtexAbility.EXPLOSION_RADIUS);
        damage = compoundTag.getDouble(DAMAGE);
        this.localOffset = new Vec3(listTag.getDouble(0), listTag.getDouble(1), listTag.getDouble(2));
        this.explosionDelay = compoundTag.getInt("explosion");
        this.isAttached = compoundTag.getBoolean("attached");
        if(compoundTag.hasUUID("new_target")){
            this.targetId = compoundTag.getUUID("new_target");
        }
    }

    private void setParticleNova(Vec3 worldPosition){
        var positionScrambler = worldPosition.offsetRandom(RandomSource.create(), (float) Math.min((float) this.aoe, 0.8));
        var directions = positionScrambler.subtract(this.element.position()).normalize();
        var lifetime = (int) this.explosionRadius + 2;
        var size = 5;
        var bakedParticle = bakedParticle(this.getElementType().id(), lifetime, size, false);
        var col1 = this.getElementType().partColourA();
        var col2 = this.getElementType().partColourFade();
        var genericParticle = genericParticle(GENERIC_PARTICLE, lifetime, size, col1, col2, false);
        var getRandomParticle = List.of(bakedParticle, genericParticle);

        ParticleHandlers.sendParticles(
            element.level(), getRandomParticle.get(Helpers.Random.nextInt(2)), worldPosition, 0, directions.x, directions.y, directions.z, Math.min(this.aoe, 2)
        );
    }

    private void attachBombAndFollow() {
        if (target != null && isOpp(target)) {
            if (!isAttached) {
                this.element.setDeltaMovement(0, 0, 0);
                localOffset = this.element.position().subtract(target.position());
                isAttached = true;
            } else {
                if(!(this.element.level() instanceof ServerLevel serverLevel)) return;
                this.element.setShowTrailParticles(false);
                if(aoe != 0.1) return;
                var newPosition = target.position().add(localOffset);
                this.element.moveTo(newPosition.x, newPosition.y, newPosition.z);
                if(this.element.tickCount % 4 != 0) return;

                Helpers.getSoundWithPosition(this.element.level(), this.element.getOnPos(), SoundReg.TIMER.get());
                var partColour = this.getElementType().partColourA();
                var partColour2 = this.getElementType().partColourB();
                var particle = new GenericParticleOptions(GENERIC_PARTICLE, partColour, partColour2, 10, 3, false, 1.4);
                var position = this.element.position().add(0,0.2,0);

                particleBurst(serverLevel, position, 1, particle);
            }
        }
    }

    private void onExplosion() {
        if (target != null && (explosionDelay == 0 || !target.isAlive())) {
            if(aoe == 0.1) {
                element.setShowTrailParticles(false);
                this.element.setInvisible(true);
                Helpers.getSoundWithPosition(this.element.level(), this.element.getOnPos(), getElementType().sound(), 2F, 1F);
//                Helpers.getSoundWithPosition(this.element.level(), this.element.getOnPos(), SoundEvents.AMETHYST_BLOCK_BREAK, 2F, 0.6f);
                additionalProjectileSpread();
                if(this.element.level() instanceof ServerLevel serverLevel){
                    particleBurst(
                        serverLevel, this.element.position().add(0,0.2,0), 15,
                        genericParticle(SOFT_PARTICLE, this.getElementType(), 5, 1.4f),
                        0, 1.5, 0, 0.1f
                    );
                }
            }

            if(aoe < explosionRadius) aoe *= 1.5; else aoe += 0.1;
            if(aoe >= explosionRadius) this.element.discard();

            novaDamageBehaviour();
            if(aoe < 1.5){
                PositionFinders.getOuterRingOfRadiusRandom(this.element.position(), 1.5, explosionRadius * 4, this::setParticleNova);
            }
        }
    }

    private void additionalProjectileSpread() {
        var projectile = this.element;
        if(projectile.getAdditionalRestriction()) return;
        if (Helpers.Random.nextInt(0, (int) this.additionalProjectileChance) != 0) return;
        var getType = EntityReg.MYSTIC_ELEMENT_PROJECTILE.get();
        var abilityId = EntityDataReg.MYSTICAL_SEMTEX.get().setAbilityId();
        var abilityHolder = projectile.getAbilityHolder();
        var abilityName = MysticalSemtexAbility.abilityId.getPath().intern();

        EntityMovers.moveEntitiesRelativeToPlayer(this.target, additionalProjectiles,
            positions -> {
                if(!(projectile.getOwner() instanceof LivingEntity livingEntity)) return;
                ElementProjectile newElementProjectile = new ElementProjectile(
                    getType, livingEntity, abilityId, 0, abilityHolder, abilityName
                );
                newElementProjectile.setAdditionalRestrictionBound(true);
                newElementProjectile.setOwner(projectile.getOwner());
                newElementProjectile.moveTo(projectile.getX(), projectile.getY() + projectile.getBbHeight() - 0.35, projectile.getZ());
                newElementProjectile.setPredicate(0);
                newElementProjectile.shoot(positions.x, positions.y, positions.z, 0.8f, 0);
                newElementProjectile.setDeltaMovement(newElementProjectile.getDeltaMovement());
                this.target.level().addFreshEntity(newElementProjectile);
            }
        );
        Helpers.getSoundWithPosition(projectile.level(), this.target.blockPosition(), getElementType().sound(), 0.05f);
    }
}