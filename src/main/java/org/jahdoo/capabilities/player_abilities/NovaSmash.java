package org.jahdoo.capabilities.player_abilities;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.capabilities.AbstractAttachment;
import org.jahdoo.networking.packet.NovaSmashS2CPacket;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.AttributesRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.GeneralHelpers;

import java.util.List;

import static net.neoforged.neoforge.common.CommonHooks.onLivingKnockBack;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.GENERIC_PARTICLE_SELECTION;
import static org.jahdoo.particle.ParticleStore.rgbToInt;
import static org.jahdoo.registers.AttachmentRegister.NOVA_SMASH;

public class NovaSmash implements AbstractAttachment {

    private int highestDelta;
    private boolean canSmash;
    private float getDamage;

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("highestDelta", highestDelta);
        nbt.putBoolean("canSmash", canSmash);
    }

    public void loadNBTData(CompoundTag nbt) {
        this.highestDelta = nbt.getInt("highestDelta");
        this.canSmash = nbt.getBoolean("canSmash");
    }

    public void setCanSmash(boolean canSmash){
        this.canSmash = canSmash;
    }

    public void setHighestDelta(int highestDelta){
        this.highestDelta = highestDelta;
    }

    public boolean getCanSmash(){
        return this.canSmash;
    }

    public int getSetHighestDelta(){
        return this.highestDelta;
    }

    public static void novaSmashTickEvent(Player player){
        player.getData(NOVA_SMASH).onTick(player);
    }

    public void onTick(Player player){
        int getCurrentDelta = (int) Math.abs(Math.round(player.getDeltaMovement().y));
        this.highestDelta = Math.max(this.highestDelta, getCurrentDelta);
        this.getDamage = GeneralHelpers.attributeModifierCalculator(
            player,
            this.highestDelta,
            this.getElement(),
            AttributesRegister.MAGIC_DAMAGE_MULTIPLIER,
            false
        );

        if (this.canSmash){
            player.setDeltaMovement(player.getDeltaMovement().add(0, -1.5, 0));
            BouncyFoot.setBouncyFoot(player, 300);
//            player.resetFallDistance();
            this.clientDataSync(player);
            if(player.onGround()){
                this.setAbilityEffects(player, this.highestDelta);
                this.setKnockbackAndDamage(player, this.highestDelta);
                player.setDeltaMovement(player.getDeltaMovement().add(0, Math.max(Math.min(5, (double) this.highestDelta / 4), 2.2), 0));
                this.highestDelta = 0;
                this.canSmash = false;
            }
        }
    }

    private void setAbilityEffects(Player player, int getMaxDeltaMovement){
        GeneralHelpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundEvents.PLAYER_BIG_FALL);
        GeneralHelpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundRegister.EXPLOSION.get(), 0.5f,1.5f);

        if(player.level() instanceof ServerLevel serverLevel){
            this.clientDiggingParticles(player, serverLevel);
            GeneralHelpers.getOuterRingOfRadiusRandom(player.position().add(0, 0.1, 0), 0.5, Math.max(getMaxDeltaMovement * 40, 20),
                worldPosition -> this.setParticleNova(player, worldPosition, 5, serverLevel)
            );
        }
    }


    public void clientDataSync(Player player){
        if(player instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new NovaSmashS2CPacket(this.highestDelta, this.canSmash));
        }
    }

    private void setKnockbackAndDamage(Player player, int getMaxDeltaMovement){
        player.level().getNearbyEntities(
            LivingEntity.class, TargetingConditions.DEFAULT, player,
            player.getBoundingBox().inflate(6, 2, 6)
        ).forEach(
            livingEntity -> {
                double deltaX = livingEntity.getX() - player.getX();
                double deltaY = livingEntity.getY() - player.getY();
                double deltaZ = livingEntity.getZ() - player.getZ();
                double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                if(livingEntity != player){
                    this.knockback(livingEntity, Math.max((double) getMaxDeltaMovement / 2, 0.3), -deltaX / length, -deltaZ / length);

                    livingEntity.hurt(player.damageSources().playerAttack(player), this.getDamage);
                }
            }
        );
    }

    private void knockback(LivingEntity targetEntity, double pStrength, double pX, double pZ) {
        LivingKnockBackEvent event = onLivingKnockBack(targetEntity, (float) pStrength, pX, pZ);
        if(event.isCanceled()) return;
        pStrength = event.getStrength();
        pX = event.getRatioX();
        pZ = event.getRatioZ();
        pStrength *= 1.0D - targetEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        if (!(pStrength <= 0.0D)) {
            targetEntity.hasImpulse = true;
            Vec3 vec3 = targetEntity.getDeltaMovement();
            Vec3 vec31 = (new Vec3(pX, 0.0D, pZ)).normalize().scale(pStrength);
            targetEntity.setDeltaMovement(vec3.x / 2.0D - vec31.x, targetEntity.onGround() ? Math.min(0.8D, vec3.y / 2.0D + pStrength) : vec3.y, vec3.z / 2.0D - vec31.z);
        }
    }

    public void clientDiggingParticles(LivingEntity livingEntity, ServerLevel serverLevel) {
        RandomSource randomsource = livingEntity.getRandom();
        BlockState blockstate = livingEntity.getBlockStateOn();
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            for (int i = 0; i < 45; ++i) {
                double d0 = livingEntity.getX() + (double) Mth.randomBetween(randomsource, -1.5F, 1.5F);
                double d1 = livingEntity.getY();
                double d2 = livingEntity.getZ() + (double) Mth.randomBetween(randomsource, -1.5F, 1.5F);
                GeneralHelpers.generalHelpers.sendParticles(serverLevel, new BlockParticleOption(ParticleTypes.BLOCK, blockstate), new Vec3(d0, d1, d2), 2, 0, 0.5,0,0.5);
            }
        }
    }

    private AbstractElement getElement(){
        return ElementRegistry.MYSTIC.get();
    }

    private void setParticleNova(Player player, Vec3 worldPosition, double particleMultiplier, ServerLevel serverLevel){
        AbstractElement element = ElementRegistry.MYSTIC.get();

        Vec3 positionScrambler = worldPosition.offsetRandom(RandomSource.create(), 0.1f);
        Vec3 directions = positionScrambler.subtract(player.position()).normalize();
        ParticleOptions genericParticle = genericParticleOptions(
            GENERIC_PARTICLE_SELECTION, 3, GeneralHelpers.Random.nextFloat(1f, 2f),element.particleColourPrimary(),
            rgbToInt(255,255,255), true
        );

        ParticleOptions bakedParticle = new BakedParticleOptions(
            element.getTypeId(),
            (int) (particleMultiplier * 30),
            GeneralHelpers.Random.nextFloat(0.3f, 0.5f),
            true
        );

        List<ParticleOptions> getRandomParticle = List.of(bakedParticle, genericParticle);

        GeneralHelpers.generalHelpers.sendParticles(
            serverLevel, getRandomParticle.get(GeneralHelpers.Random.nextInt(2)) ,worldPosition, 0, directions.x, directions.y, directions.z, GeneralHelpers.Random.nextDouble(0.3,1.0)
        );

    }

}
