package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities;

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
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.all_abilities.abilities.FrostboltsAbility;
import org.jahdoo.all_magic.all_abilities.ability_components.EtherealArrow;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.items.wand.CastHelper;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.AttributesRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.utils.GeneralHelpers;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.all_magic.all_abilities.abilities.FrostboltsAbility.NUMBER_OF_PROJECTILES;
import static org.jahdoo.all_magic.AbilityBuilder.*;

public class FrostBolts extends DefaultEntityBehaviour {
    List<GenericProjectile> assignArrows = new ArrayList<>();

    double damage;
    double effectChance;
    double effectStrength;
    double effectDuration;
    double projectileMultiplier;
    double castDistance;
    double mana;
    double cooldown;

    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        if(this.genericProjectile.getOwner() != null) {
            var player = this.genericProjectile.getOwner();
            var damage = this.getTag(DAMAGE);
            this.damage = GeneralHelpers.attributeModifierCalculator(
                (LivingEntity) player,
                (float) damage,
                this.getElementType(),
                AttributesRegister.MAGIC_DAMAGE_MULTIPLIER,
                true
            );
        }
        this.effectChance = this.getTag(EFFECT_CHANCE);
        this.effectStrength = this.getTag(EFFECT_STRENGTH);
        this.effectDuration = this.getTag(EFFECT_DURATION);
        this.projectileMultiplier = this.getTag(NUMBER_OF_PROJECTILES);
        this.castDistance = this.getTag(CASTING_DISTANCE);
        this.mana = this.getTag(MANA_COST);
        this.cooldown = this.getTag(COOLDOWN);
    }

    @Override
    public double getTag(String name) {
        var wandAbilityHolder = this.genericProjectile.wandAbilityHolder();
        var ability = FrostboltsAbility.abilityId.getPath().intern();
        return GeneralHelpers.getModifierValue(wandAbilityHolder, ability).get(name).actualValue();
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.FROST.get();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        sendNoTargetMessage();
        this.genericProjectile.discard();
    }

    @Override
    public void onEntityHit(LivingEntity hitEntity) {
        Player player = (Player) this.genericProjectile.getOwner();
        if(player == null) return;
        if (player.distanceTo(hitEntity) <= castDistance + 0.1) {
            this.shootArrowsAtTarget(hitEntity, player);
            CastHelper.chargeMana(FrostboltsAbility.abilityId.getPath().intern(), mana, player);
            CastHelper.chargeCooldown(FrostboltsAbility.abilityId.getPath().intern(), cooldown, player);
            player.displayClientMessage(Component.literal(""), true);
        } else {
            sendNoTargetMessage();
        }
        this.genericProjectile.discard();
    }

    private void shootArrowsAtTarget(LivingEntity hitEntity, Player player){
        for (int i = 0; i < projectileMultiplier; i++) {

            double randomAngle = 2 * Math.PI * GeneralHelpers.Random.nextDouble();
            double randomRadius = (hitEntity.getBbWidth() * 2) * Math.sqrt(GeneralHelpers.Random.nextDouble());
            double arrowX = hitEntity.getX() + randomRadius * Math.cos(randomAngle);
            double arrowZ = hitEntity.getZ() + randomRadius * Math.sin(randomAngle);
            double arrowY = hitEntity.getY() + (hitEntity.getBbHeight()/2) + 4;
            GenericProjectile arrow = new GenericProjectile(
                player, arrowX, arrowY, arrowZ,
                EntityPropertyRegister.ETHEREAL_ARROW.get().setAbilityId(),
                EtherealArrow.setArrowProperties((int) damage, (int) effectDuration, (int) effectStrength, (int) effectChance),
                this.getElementType(),
                FrostboltsAbility.abilityId.getPath()
            );

            if(this.genericProjectile.level() instanceof ServerLevel serverLevel){
                ParticleHandlers.spawnPoof(serverLevel, new Vec3(arrowX, arrowY, arrowZ), 1, new BakedParticleOptions(this.getElementType().getTypeId(), 10, 1.7f, false), 0,0,0,0.07f);
            }

            this.assignArrows.add(arrow);
            arrow.setOwner(player);
            arrow.setDeltaMovement(0, 0, 0);

            // Calculate the direction vector
            double directionX = hitEntity.getX() - arrowX;
            double directionY = hitEntity.getY() - arrowY;
            double directionZ = hitEntity.getZ() - arrowZ;

            // Normalize the direction vector
            double length = Math.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
            directionX /= length;
            directionY /= length;
            directionZ /= length;

            // Set the arrow's motion
            float velocity = 1F; // Change this value to adjust the speed of the arrow
            arrow.shoot(directionX, directionY, directionZ, velocity, 0);
            this.genericProjectile.level().addFreshEntity(arrow);
        }
        GeneralHelpers.getSoundWithPosition(genericProjectile.level(), hitEntity.blockPosition(), SoundEvents.WITHER_SHOOT, 1, 1.6f);
    }

    @Override
    public void onTickMethod() {
        if(!this.assignArrows.isEmpty()){
            this.assignArrows.forEach(
                arrows -> {
                    if(!arrows.isAlive()) {
                        assignArrows.forEach(Entity::discard);
                        this.genericProjectile.discard();
                    }
                }
            );
        }
    }

    @Override
    public void discardCondition() {
        if(this.genericProjectile != null){
            var player = this.genericProjectile.getOwner();

            if(player == null) {
                this.genericProjectile.discard();
                return;
            }

            if (this.genericProjectile.getOwner() != null && this.genericProjectile.tickCount > 1) {
                this.sendNoTargetMessage();
                this.genericProjectile.discard();
            }
        }
    }

    private void sendNoTargetMessage(){
        if (this.genericProjectile.getOwner() instanceof Player player){
            var value = String.valueOf(Math.round(castDistance));
            var element = this.getElementType().particleColourPrimary();
            var targetDistance = GeneralHelpers.withStyleComponent(value, element);
            player.displayClientMessage(Component.translatable("abilities.jahdoo.frost_bolts.no_target", targetDistance), true);
        }
    }

    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("frost_bolts_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new FrostBolts();
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
    }
}
