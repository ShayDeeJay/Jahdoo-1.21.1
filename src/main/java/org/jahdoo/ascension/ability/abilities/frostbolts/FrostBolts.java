package org.jahdoo.ascension.ability.abilities.frostbolts;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.ability.abilities.EtherealArrow;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.items.wand.CastHelper;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.EntityDataReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.ascension.utils.Helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.ascension.ability.abilities.frostbolts.FrostboltsAbility.NUMBER_OF_PROJECTILES;
import static org.jahdoo.common.items.wand.CastHelper.castAnimation;
import static org.jahdoo.common.items.wand.WandAnimations.SINGLE_CAST_ID;
import static org.jahdoo.common.particle.ParticleHandlers.getAllParticleTypes;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.common.registers.AttributeReg.FROST_MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.common.registers.AttributeReg.MAGIC_DAMAGE_MULTIPLIER;

public class FrostBolts  extends DefaultEntityBehaviour {

    private static final ResourceLocation abilityId = Helpers.res("frost_bolts_property");
    private final List<GenericProjectile> assignArrows = new ArrayList<>();
    private double projectileMultiplier;
    private double effectDuration;
    private double effectStrength;
    private int currentShotCount;
    private double effectChance;
    private double castDistance;
    private LivingEntity hitTarget;
    private double cooldown;
    private double damage;
    private double mana;
    private UUID uuid;

    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        this.projectileMultiplier = this.getTag(NUMBER_OF_PROJECTILES);
        if(this.generic.getOwner() != null) {
            var player = this.generic.getOwner();
            this.damage = Helpers.attributeModifierCalculator(
                (LivingEntity) player, (float) this.getTag(DAMAGE), true, MAGIC_DAMAGE_MULTIPLIER, FROST_MAGIC_DAMAGE_MULTIPLIER
            );
        }
        this.effectChance = this.getTag(EFFECT_CHANCE);
        this.effectStrength = this.getTag(EFFECT_STRENGTH);
        this.effectDuration = this.getTag(EFFECT_DURATION);
        this.castDistance = this.getTag(CASTING_DISTANCE);
        this.mana = this.getTag(MANA_COST);
        this.cooldown = this.getTag(COOLDOWN);
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return this.generic.wandAbilityHolder();
    }

    @Override
    public String abilityId() {
        return FrostboltsAbility.abilityId.getPath().intern();
    }

    @Override
    public AbstractElement getElementType() {
        return ElementReg.frost();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        sendNoTargetMessage();
        this.generic.discard();
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new FrostBolts();
    }

    @Override
    public void onTickMethod() {
        reassignTarget();
        if(hitTarget != null){
            if(this.currentShotCount < this.projectileMultiplier){
                this.generic.moveTo(this.hitTarget.position());
                this.shootArrowsAtTarget(hitTarget, (Player) this.generic.getOwner(), generic.tickCount);
            }
        }
    }

    private void reassignTarget() {
        if(this.uuid != null){
            var level = this.generic.level();
            if(level instanceof ServerLevel serverLevel){
                var foundTarget = serverLevel.getEntity(this.uuid);
                if(foundTarget instanceof LivingEntity livingEntity){
                    this.hitTarget = livingEntity;
                }
            }
        }
    }

    private void discardAll() {
        if(!this.assignArrows.isEmpty()){
            this.assignArrows.forEach(
                arrows -> {
                    if(!arrows.isAlive()) {
                        assignArrows.forEach(Entity::discard);
                        this.generic.discard();
                    }
                }
            );
        }
    }

    public static void onExistenceChange(LivingEntity livingEntity, AbstractElement element) {
        for (int i = 0; i < 10; i++) {
            sendParticles(
                livingEntity.level(),
                getAllParticleTypes(element, 10, 1.6f),
                livingEntity.position().add(0,livingEntity.getBbHeight()/2,0),
                15,
                0,
                livingEntity.getBbHeight()/4, 0, 0.15f);
        }
    }

    private void sendNoTargetMessage(){
        if (this.generic.getOwner() instanceof Player player){
            CastHelper.failedCastNotification(player);
            var value = String.valueOf(Math.round(castDistance));
            var element = this.getElementType().partColourA();
            var targetDistance = Helpers.withStyleComponent(value, element);
            player.displayClientMessage(Helpers.withStyleComponentTrans("ability.jahdoo.frost_bolts.no_target", -1, targetDistance), true);
        }
    }

    @Override
    public void discardCondition() {
        if(this.generic != null){
            var player = this.generic.getOwner();

            if(player == null) {
                this.generic.discard();
                return;
            }

            if (this.hitTarget == null && this.generic.tickCount > 1) {
                this.sendNoTargetMessage();
                this.generic.discard();
            }

            if(this.hitTarget == null || !this.hitTarget.isAlive()) discardAll();
        }
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        Player player = (Player) this.generic.getOwner();
        if(player == null) return;
        if (player.isCloseEnough(hitEntity,castDistance + 0.1)) {
            castAnimation(player, SINGLE_CAST_ID);
            this.hitTarget = hitEntity;
            Helpers.getSoundWithPositionV(generic.level(), hitEntity.position(), SoundReg.ORB_CREATE.get(), 2f, 1f);
            onExistenceChange(hitEntity, getElementType());
            CastHelper.chargeManaAndCooldown(FrostboltsAbility.abilityId.getPath().intern(), player);
            player.displayClientMessage(Component.literal(""), true);
            this.generic.setDeltaMovement(0,0,0);
        } else {
            sendNoTargetMessage();
            this.generic.discard();
        }
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putDouble(EFFECT_CHANCE, this.effectChance);
        compoundTag.putDouble(EFFECT_DURATION, this.effectDuration);
        compoundTag.putDouble(EFFECT_STRENGTH, this.effectStrength);
        compoundTag.putDouble(DAMAGE, this.damage);
        compoundTag.putDouble(NUMBER_OF_PROJECTILES, this.projectileMultiplier);
        compoundTag.putDouble(CASTING_DISTANCE, this.castDistance);
        compoundTag.putDouble(MANA_COST, mana);
        compoundTag.putDouble(COOLDOWN, cooldown);
        compoundTag.putInt("hitCount", this.currentShotCount);
        compoundTag.putUUID("hitTarget", this.hitTarget.getUUID());
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.effectChance = compoundTag.getDouble(EFFECT_CHANCE);
        this.effectDuration = compoundTag.getDouble(EFFECT_DURATION);
        this.effectStrength = compoundTag.getDouble(EFFECT_STRENGTH);
        this.damage = compoundTag.getDouble(DAMAGE);
        this.projectileMultiplier = compoundTag.getDouble(NUMBER_OF_PROJECTILES);
        this.castDistance = compoundTag.getDouble(CASTING_DISTANCE);
        this.mana = compoundTag.getDouble(MANA_COST);
        this.cooldown = compoundTag.getDouble(COOLDOWN);
        this.currentShotCount = compoundTag.getInt("hitCount");
        this.uuid = compoundTag.getUUID("hitTarget");
    }

    private void shootArrowsAtTarget(LivingEntity hitEntity, Player player, int tickCount){

        if(tickCount % 2 == 0){
            var randomAngle = 2 * Math.PI * Helpers.Random.nextDouble();
            var randomRadius = (hitEntity.getBbWidth() * 2) * Math.sqrt(Helpers.Random.nextDouble());
            var arrowX = hitEntity.getX() + randomRadius * Math.cos(randomAngle);
            var arrowZ = hitEntity.getZ() + randomRadius * Math.sin(randomAngle);
            var arrowY = hitEntity.getY() + (hitEntity.getBbHeight() / 2) + 4;
            var arrow = new GenericProjectile(
                player, arrowX, arrowY, arrowZ,
                EntityDataReg.ETHEREAL_ARROW.get().setAbilityId(),
                EtherealArrow.setArrowProperties(damage, effectDuration, effectStrength, effectChance),
                this.getElementType(),
                FrostboltsAbility.abilityId.getPath()
            );

            if (this.generic.level() instanceof ServerLevel serverLevel) {
                var particleOptions = ParticleHandlers.bakedParticle(this.getElementType().id(), 3, 1.7f, false);
                ParticleHandlers.particleBurst(serverLevel, new Vec3(arrowX, arrowY, arrowZ), 1, particleOptions, 0, 0, 0, 0.1f);
            }

            this.assignArrows.add(arrow);
            arrow.setOwner(player);
            arrow.setDeltaMovement(0, 0, 0);

            var directionX = hitEntity.getX() - arrowX;
            var directionY = hitEntity.getY() - arrowY;
            var directionZ = hitEntity.getZ() - arrowZ;

            var length = Math.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
            directionX /= length;
            directionY /= length;
            directionZ /= length;


            arrow.shoot(directionX, directionY, directionZ, 0.8F, 0);
            this.generic.level().addFreshEntity(arrow);
            this.currentShotCount++;

            Helpers.getSoundWithPosition(generic.level(), hitEntity.blockPosition(), SoundEvents.BREEZE_SHOOT, 0.4f, 2f);
            Helpers.getSoundWithPosition(generic.level(), hitEntity.blockPosition(), SoundReg.ORB_CREATE.get(), 0.2f, 1.8f);
        }
    }

}
