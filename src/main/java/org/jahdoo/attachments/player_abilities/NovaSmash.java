package org.jahdoo.attachments.player_abilities;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
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
import org.jahdoo.ability.AbilityBuilder;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.abilities.ability_data.NovaSmashAbility;
import org.jahdoo.attachments.AbstractAttachment;
import org.jahdoo.components.DataComponentHelper;
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
import static org.jahdoo.components.DataComponentHelper.*;
import static org.jahdoo.particle.ParticleHandlers.bakedParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.*;
import static org.jahdoo.registers.AttachmentRegister.NOVA_SMASH;
import static org.jahdoo.registers.AttributesRegister.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.registers.AttributesRegister.MYSTIC_MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;
import static org.jahdoo.utils.DamageUtil.*;
import static org.jahdoo.utils.ModHelpers.*;
import static org.jahdoo.utils.PositionGetters.*;

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
            var getHolder = player.getItemInHand(player.getUsedItemHand()).get(WAND_ABILITY_HOLDER);
            var getValue = (float) getSpecificValue(NovaSmashAbility.abilityId.getPath().intern(), getHolder,AbilityBuilder.DAMAGE);
            this.getDamage = ModHelpers.attributeModifierCalculator(player, getValue, false, MAGIC_DAMAGE_MULTIPLIER, MYSTIC_MAGIC_DAMAGE_MULTIPLIER);
            player.setDeltaMovement(player.getDeltaMovement().add(0, -1.5, 0));
            if(player.onGround()){
                this.setAbilityEffects(player);
                this.setKnockbackAndDamage(player);
                this.highestDelta = 0;
                this.canSmash = false;
                BouncyFoot.setBouncyFoot(player, 160);
            }
        }

        if(player instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new NovaSmashS2CPacket(highestDelta, canSmash, getDamage));
        }
    }

    private void setAbilityEffects(Player player){
        getSoundWithPosition(player.level(), player.blockPosition(), SoundEvents.PLAYER_BIG_FALL);
        getSoundWithPosition(player.level(), player.blockPosition(), SoundRegister.EXPLOSION.get(), 1,0.6f);
        System.out.println(getDamage);
        this.clientDiggingParticles(player, player.level());

        if(player.level() instanceof ServerLevel){
            var position = player.position();
            var knockback = Math.max( Math.min((double) this.highestDelta / 4, 1), 0.2);

            getOuterRingOfRadiusRandom(position, 1, 100, (pos) -> setParticleNova(pos, player, knockback));
            getOuterRingOfRadiusRandom(position, 0.5, Math.max(this.getDamage * 20, 20),
                    worldPosition -> this.setParticleNova(player, worldPosition, 5, player.level())
            );
        }

    }

    public static void setParticleNova(Vec3 worldPosition, Entity entity, double speed){
        var directions = worldPosition.subtract(entity.position());
        var getMysticElement = ElementRegistry.MYSTIC.get();
        var colourPrimary = getMysticElement.particleColourPrimary();
        var colourSecondary = getMysticElement.particleColourSecondary();
        var genericParticle = genericParticleOptions(SOFT_PARTICLE_SELECTION, colourPrimary, colourSecondary, 10, 0.1F, true, 0);

        ParticleHandlers.sendParticles(
            entity.level(), genericParticle, worldPosition, 0, directions.x, directions.y + 0.1, directions.z, speed
        );
    }

    private void setKnockbackAndDamage(Player player){
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
                        var knockback = (double) this.highestDelta / 2;
                        this.knockback(livingEntity, Math.max(knockback, 0.3), -deltaX / length, -deltaZ / length);
                        damageWithJahdoo(livingEntity, player, this.getDamage);
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
                var d0 = livingEntity.getX() + (double) Mth.randomBetween(randomsource, -1.5F, 1.5F);
                var d1 = livingEntity.getY();
                var d2 = livingEntity.getZ() + (double) Mth.randomBetween(randomsource, -1.5F, 1.5F);
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

        var size = Random.nextFloat(1f, 2f);
        var colourPrimary = getElement().particleColourPrimary();
        var colourSecondary = rgbToInt(255, 255, 255);
        var genericParticle = genericParticleOptions(GENERIC_PARTICLE_SELECTION, 3, size, colourPrimary, colourSecondary, true);

        var size1 = Random.nextFloat(5f, 7f);
        var bakedParticle = bakedParticleOptions(getElement().getTypeId(), (int) particleMultiplier, size1, false);

        var getRandomParticle = List.of(bakedParticle, genericParticle);
        var pType = getRandomParticle.get(Random.nextInt(2));
        var pSpeed = Random.nextDouble(0.3, 1.0);

        ParticleHandlers.sendParticles(level, pType, worldPosition, 0, directions.x, directions.y, directions.z, pSpeed);
    }

}
