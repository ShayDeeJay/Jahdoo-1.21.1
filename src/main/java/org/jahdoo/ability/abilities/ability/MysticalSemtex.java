package org.jahdoo.ability.abilities.ability;

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
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.abilities.ability_data.MysticalSemtexAbility;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.entities.EntityMovers;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.registers.*;
import org.jahdoo.utils.DamageUtil;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.List;
import java.util.UUID;

import static org.jahdoo.ability.AbilityBuilder.DAMAGE;
import static org.jahdoo.particle.ParticleHandlers.*;
import static org.jahdoo.particle.ParticleStore.*;
import static org.jahdoo.registers.AttributesRegister.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.registers.AttributesRegister.MYSTIC_MAGIC_DAMAGE_MULTIPLIER;

public class MysticalSemtex extends DefaultEntityBehaviour {

    private boolean isAttached;
    private int explosionDelay;
    private double aoe = 0.1;
    private LivingEntity target;
    private Vec3 localOffset;
    private UUID targetId;

    double setExplosionDelay;
    double additionalProjectiles;
    double additionalProjectileChance;
    double explosionRadius;
    double damage;

    @Override
    public void getElementProjectile(ElementProjectile elementProjectile) {
        super.getElementProjectile(elementProjectile);
        var player = this.elementProjectile.getOwner();
        if(player != null && !(player instanceof Player)){
            var damage = this.getTag(DAMAGE);
            this.damage = ModHelpers.attributeModifierCalculator(
                (LivingEntity) player,
                (float) damage,
                true,
                MAGIC_DAMAGE_MULTIPLIER,
                MYSTIC_MAGIC_DAMAGE_MULTIPLIER
            );
        } else {
            this.damage = this.getTag(DAMAGE);
        }
        this.setExplosionDelay = this.getTag(MysticalSemtexAbility.explosionDelays);
        this.additionalProjectiles = this.getTag(MysticalSemtexAbility.additionalProjectile);
        this.additionalProjectileChance = this.getTag(MysticalSemtexAbility.clusterChance);
        this.explosionRadius = this.getTag(MysticalSemtexAbility.explosionRadius);
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putInt("explosion", this.explosionDelay);
        compoundTag.putBoolean("attached", isAttached);
        compoundTag.putDouble(MysticalSemtexAbility.explosionDelays, this.setExplosionDelay);
        compoundTag.putDouble(MysticalSemtexAbility.additionalProjectile, this.additionalProjectiles);
        compoundTag.putDouble(MysticalSemtexAbility.explosionDelays, this.explosionRadius);
        compoundTag.putDouble(DAMAGE, this.damage);
        if(localOffset != null){
            compoundTag.put("offset", ModHelpers.nbtDoubleList(localOffset.x, localOffset.y, localOffset.z));
        }
        if(target != null){
            compoundTag.putUUID("new_target", target.getUUID());
        }
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        var listTag = compoundTag.getList("offset", 6);
        setExplosionDelay = compoundTag.getDouble(MysticalSemtexAbility.explosionDelays);
        additionalProjectiles = compoundTag.getDouble(MysticalSemtexAbility.additionalProjectile);
        additionalProjectileChance = compoundTag.getDouble(MysticalSemtexAbility.clusterChance);
        explosionRadius = compoundTag.getDouble(MysticalSemtexAbility.explosionRadius);
        damage = compoundTag.getDouble(DAMAGE);
        this.localOffset = new Vec3(listTag.getDouble(0), listTag.getDouble(1), listTag.getDouble(2));
        this.explosionDelay = compoundTag.getInt("explosion");
        this.isAttached = compoundTag.getBoolean("attached");
        if(compoundTag.hasUUID("new_target")){
            this.targetId = compoundTag.getUUID("new_target");
        }
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return this.elementProjectile.getwandabilityholder();
    }

    @Override
    public String abilityId() {
        return MysticalSemtexAbility.abilityId.getPath().intern();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        this.elementProjectile.discard();
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        targetHit(hitEntity);
    }

    @Override
    public void onTickMethod() {
        if(!(this.elementProjectile.level() instanceof ServerLevel serverLevel)) return;
        if(target == null) elementProjectile.setShowTrailParticles(true);

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
    public void discardCondition() {
        if (this.elementProjectile.tickCount > 300) this.elementProjectile.discard();
    }

    private void attachBombAndFollow() {
        if (target != null && isOpp(target)) {
            if (!isAttached) {
                this.elementProjectile.setDeltaMovement(0, 0, 0);
                localOffset = this.elementProjectile.position().subtract(target.position());
                isAttached = true;
            } else {
                if(!(this.elementProjectile.level() instanceof ServerLevel serverLevel)) return;
                this.elementProjectile.setShowTrailParticles(false);
                if(aoe != 0.1) return;
                var newPosition = target.position().add(localOffset);
                this.elementProjectile.moveTo(newPosition.x, newPosition.y, newPosition.z);
                if(this.elementProjectile.tickCount % 4 != 0) return;

                ModHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.getOnPos(), SoundRegister.TIMER.get());
                var partColour = this.getElementType().particleColourPrimary();
                var partColour2 = this.getElementType().particleColourSecondary();
                var particle = new GenericParticleOptions(GENERIC_PARTICLE_SELECTION, partColour, partColour2, 10, 3, false, 1.4);
                var position = this.elementProjectile.position().add(0,0.2,0);

                particleBurst(serverLevel, position, 1, particle);
            }
        }
    }

    private void onExplosion() {
        if (target != null && (explosionDelay == 0 || !target.isAlive())) {
            if(aoe == 0.1) {
                elementProjectile.setShowTrailParticles(false);
                this.elementProjectile.setInvisible(true);
                ModHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.getOnPos(), SoundRegister.EXPLOSION.get(),2F, 0.8F);
                ModHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.getOnPos(), SoundEvents.AMETHYST_BLOCK_BREAK, 2F, 0.6f);
                additionalProjectileSpread();
                if(this.elementProjectile.level() instanceof ServerLevel serverLevel){
                    particleBurst(
                        serverLevel, this.elementProjectile.position().add(0,0.2,0), 15,
                        genericParticleOptions(SOFT_PARTICLE_SELECTION, this.getElementType(), 5, 1.4f),
                        0, 1.5, 0, 0.1f
                    );
                }
            }

            if(aoe < explosionRadius) aoe *= 1.5; else aoe += 0.1;
            if(aoe >= explosionRadius) this.elementProjectile.discard();

            novaDamageBehaviour();
            if(aoe < 1.5){
                PositionGetters.getOuterRingOfRadiusRandom(this.elementProjectile.position(), 1.5, explosionRadius * 4, this::setParticleNova);
            }
        }
    }

    private void setParticleNova(Vec3 worldPosition){
        var positionScrambler = worldPosition.offsetRandom(RandomSource.create(), (float) Math.min((float) this.aoe, 0.8));
        var directions = positionScrambler.subtract(this.elementProjectile.position()).normalize();
        var lifetime = (int) this.explosionRadius + 2;
        var size = 5;
        var bakedParticle = bakedParticleOptions(this.getElementType().getTypeId(), lifetime, size, false);
        var col1 = this.getElementType().particleColourPrimary();
        var col2 = this.getElementType().particleColourFaded();
        var genericParticle = genericParticleOptions(GENERIC_PARTICLE_SELECTION, lifetime, size, col1, col2, false);
        var getRandomParticle = List.of(bakedParticle, genericParticle);

        ParticleHandlers.sendParticles(
            elementProjectile.level(), getRandomParticle.get(ModHelpers.Random.nextInt(2)), worldPosition, 0, directions.x, directions.y, directions.z, Math.min(this.aoe, 2)
        );
    }

    private void adjustProjectileArc() {
        if (target == null) this.elementProjectile.setDeltaMovement(this.elementProjectile.getDeltaMovement().subtract(0, 0.01, 0));
    }

    private void novaDamageBehaviour(){
        var owner = this.elementProjectile.getOwner();
        this.elementProjectile.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            (LivingEntity) owner,
            this.elementProjectile
                .getBoundingBox()
                .inflate(aoe,0, aoe)
                .deflate(0,1,0 )
        ).forEach(
            livingEntity -> {
                if(!isOpp(livingEntity)) return;
                DamageUtil.damageWithJahdoo(livingEntity, this.elementProjectile.getOwner(), Math.max(damage - aoe, 1));
            }
        );
    }

    private boolean isOpp(LivingEntity livingEntity) {
        return canDamageEntity(livingEntity, (LivingEntity) this.elementProjectile.getOwner());
    }

    private void additionalProjectileSpread() {
        var projectile = this.elementProjectile;
        if(projectile.getAdditionalRestriction()) return;
        if (ModHelpers.Random.nextInt(0, (int) this.additionalProjectileChance) != 0) return;
        var getType = EntitiesRegister.MYSTIC_ELEMENT_PROJECTILE.get();
        var abilityId = EntityPropertyRegister.MYSTICAL_SEMTEX.get().setAbilityId();
        var abilityHolder = projectile.getwandabilityholder();
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
        ModHelpers.getSoundWithPosition(projectile.level(), this.target.blockPosition(), SoundRegister.ORB_FIRE.get(), 0.05f);
    }

    private void targetHit(LivingEntity hitTarget) {
        if(isOpp(hitTarget)){
            explosionDelay = (int) setExplosionDelay;
            target = hitTarget;
            elementProjectile.setAnimation(6);
            ModHelpers.getSoundWithPosition(this.elementProjectile.level(), this.elementProjectile.getOnPos(), SoundEvents.SLIME_BLOCK_BREAK);
        }
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.MYSTIC.get();
    }

    ResourceLocation abilityId = ModHelpers.res("mystical_semtex_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new MysticalSemtex();
    }
}