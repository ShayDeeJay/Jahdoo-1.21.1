package org.jahdoo.attachments.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.attachments.AbstractAttachment;
import org.jahdoo.networking.packet.server2client.NovaSmashS2CPacket;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.AttributesRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.DamageUtil;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.List;

import static net.neoforged.neoforge.common.CommonHooks.onLivingKnockBack;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.*;
import static org.jahdoo.registers.AttachmentRegister.NOVA_SMASH;
import static org.jahdoo.registers.AttributesRegister.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.registers.AttributesRegister.MYSTIC_MAGIC_DAMAGE_MULTIPLIER;

public class NovaSmash implements AbstractAttachment {

    private int highestDelta;
    private boolean canSmash;
    private float getDamage;

    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putInt("highestDelta", highestDelta);
        nbt.putBoolean("canSmash", canSmash);
    }

    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        this.highestDelta = nbt.getInt("highestDelta");
        this.canSmash = nbt.getBoolean("canSmash");
    }

    public void setCanSmash(boolean canSmash){
        this.canSmash = canSmash;
    }

    public void setHighestDelta(int highestDelta){
        this.highestDelta = highestDelta;
    }

    public void setGetDamage(float getDamage) {
        this.getDamage = getDamage;
    }


    public static void novaSmashTickEvent(Player player){
        player.getData(NOVA_SMASH).onTick(player);
    }

    private void onTick(Player player){
        var getCurrentDelta = (int) Math.abs(Math.round(player.getDeltaMovement().y));
        this.highestDelta = Math.max(this.highestDelta, getCurrentDelta);

        if (this.canSmash){
            System.out.println(getCurrentDelta);
            this.getDamage = ModHelpers.attributeModifierCalculator(
                player, this.highestDelta, false, MAGIC_DAMAGE_MULTIPLIER, MYSTIC_MAGIC_DAMAGE_MULTIPLIER
            );
            player.setDeltaMovement(player.getDeltaMovement().add(0, -1.5, 0));
            if(player.onGround()){

                PositionGetters.getOuterRingOfRadiusRandom(player.position(), 2, 100, (pos) -> setParticleNova(pos, player, (double) this.highestDelta /10));
                this.setAbilityEffects(player, this.highestDelta);
                this.setKnockbackAndDamage(player, this.highestDelta);
                this.highestDelta = 0;
                this.canSmash = false;
                BouncyFoot.setBouncyFoot(player, 160);
            }
        }
        if(player instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new NovaSmashS2CPacket(highestDelta, canSmash, getDamage));
        }
    }

    private void setAbilityEffects(Player player, int getMaxDeltaMovement){
        ModHelpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundEvents.PLAYER_BIG_FALL);
        ModHelpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundRegister.EXPLOSION.get(), getMaxDeltaMovement,0.6f);

        this.clientDiggingParticles(player, player.level());
        PositionGetters.getOuterRingOfRadiusRandom(player.position().add(0, 0.1, 0), 0.5, Math.max(getMaxDeltaMovement * 40, 20),
            worldPosition -> this.setParticleNova(player, worldPosition, 5, player.level())
        );
    }

    public static void setParticleNova(Vec3 worldPosition, Entity entity, double speed){
        var directions = worldPosition.subtract(entity.position());
        var getMysticElement = ElementRegistry.MYSTIC.get();

        var genericParticle = genericParticleOptions(
            SOFT_PARTICLE_SELECTION, 5,
            0.06f,
            getMysticElement.particleColourPrimary(),
            getMysticElement.particleColourSecondary(),
            true
        );

        ParticleHandlers.sendParticles(
            entity.level(), genericParticle, worldPosition, 0, directions.x, directions.y + 0.1, directions.z, speed
        );
    }

    private void setKnockbackAndDamage(Player player, int getMaxDeltaMovement){
        player.level().getNearbyEntities(
            LivingEntity.class, TargetingConditions.DEFAULT, player,
            player.getBoundingBox().inflate(6, 2, 6)
        ).forEach(
            livingEntity -> {
                if(DefaultEntityBehaviour.canDamageEntity(livingEntity, player)){
                    var deltaX = livingEntity.getX() - player.getX();
                    var deltaY = livingEntity.getY() - player.getY();
                    var deltaZ = livingEntity.getZ() - player.getZ();
                    var length = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                    if (livingEntity != player) {
                        this.knockback(livingEntity, Math.max((double) getMaxDeltaMovement / 2, 0.3), -deltaX / length, -deltaZ / length);
                        DamageUtil.damageWithJahdoo(livingEntity, player, this.getDamage * 3);
                    }
                }
            }
        );
    }

    private void knockback(LivingEntity targetEntity, double pStrength, double pX, double pZ) {
        var event = onLivingKnockBack(targetEntity, (float) pStrength, pX, pZ);
        if(event.isCanceled()) return;
        pStrength = event.getStrength();
        pX = event.getRatioX();
        pZ = event.getRatioZ();
        pStrength *= 1.0D - targetEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        if (!(pStrength <= 0.0D)) {
            targetEntity.hasImpulse = true;
            var vec3 = targetEntity.getDeltaMovement();
            var vec31 = (new Vec3(pX, 0.0D, pZ)).normalize().scale(pStrength);
            targetEntity.setDeltaMovement(vec3.x / 2.0D - vec31.x, targetEntity.onGround() ? Math.min(0.8D, vec3.y / 2.0D + pStrength) : vec3.y, vec3.z / 2.0D - vec31.z);
        }
    }

    public void clientDiggingParticles(LivingEntity livingEntity, Level level) {
        var randomsource = livingEntity.getRandom();
        var blockstate = livingEntity.getBlockStateOn();
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            for (int i = 0; i < 45; ++i) {
                double d0 = livingEntity.getX() + (double) Mth.randomBetween(randomsource, -1.5F, 1.5F);
                double d1 = livingEntity.getY();
                double d2 = livingEntity.getZ() + (double) Mth.randomBetween(randomsource, -1.5F, 1.5F);
                ParticleHandlers.sendParticles(level, new BlockParticleOption(ParticleTypes.BLOCK, blockstate), new Vec3(d0, d1, d2), 2, 0, 0.5,0,0.5);
            }
        }
    }

    private AbstractElement getElement(){
        return ElementRegistry.MYSTIC.get();
    }

    private void setParticleNova(Player player, Vec3 worldPosition, double particleMultiplier, Level level){
        var positionScrambler = worldPosition.offsetRandom(RandomSource.create(), 0.1f);
        var directions = positionScrambler.subtract(player.position()).normalize();
        var genericParticle = genericParticleOptions(
            GENERIC_PARTICLE_SELECTION, 3, ModHelpers.Random.nextFloat(1f, 2f), getElement().particleColourPrimary(),
            rgbToInt(255,255,255), true
        );

        var bakedParticle = new BakedParticleOptions(
            getElement().getTypeId(),
            (int) particleMultiplier,
            ModHelpers.Random.nextFloat(5f, 7f),
            false
        );

        var getRandomParticle = List.of(bakedParticle, genericParticle);

        ParticleHandlers.sendParticles(
            level, getRandomParticle.get(ModHelpers.Random.nextInt(2)) ,worldPosition, 0, directions.x, directions.y, directions.z, ModHelpers.Random.nextDouble(0.3,1.0)
        );
    }

}
