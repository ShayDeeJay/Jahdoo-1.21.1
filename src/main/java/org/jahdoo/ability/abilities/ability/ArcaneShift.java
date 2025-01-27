package org.jahdoo.ability.abilities.ability;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ability.AbstractAbility;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.ability.abilities.ability_data.ArcaneShiftAbility;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.utils.ModHelpers.Random;


public class ArcaneShift extends AbstractAbility {

    Player player;
    WandAbilityHolder wandAbilityHolder;

    public ArcaneShift(Player player) {
        this.player = player;
        this.wandAbilityHolder = player.getItemInHand(player.getUsedItemHand()).get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return wandAbilityHolder;
    }

    @Override
    public String abilityId() {
        return ArcaneShiftAbility.abilityId.getPath().intern();
    }

    public void teleportToHome(){
        var damages = getTag(DAMAGE);
        var distances = getTag(CASTING_DISTANCE);
        var maxEntity = getTag(ArcaneShiftAbility.maxEntities);
        var lifeTimes = getTag(ArcaneShiftAbility.lifeTime);
        var position = player.pick(distances, 0, false).getLocation();;
        for(int i = 0; i < 50; i++){
            var particle = ParticleHandlers.getAllParticleTypes(ElementRegistry.MYSTIC.get(), 10, 1.5f);
            var x = player.getRandomX(1);
            var y = player.getRandomY();
            var z = player.getRandomZ(1);
            player.level().addParticle(particle, x, y, z, Random.nextDouble(0.1, 0.3) - 0.2, Random.nextDouble(0.2, 0.5), Random.nextDouble(0.1, 0.3)- 0.2);
        }

        if(!player.level().isClientSide) {
            var blockPos = BlockPos.containing(position).getCenter();
            player.teleportTo(blockPos.x, blockPos.y + 0.5, blockPos.z);
            ModHelpers.getSoundWithPositionV(player.level(), position, SoundEvents.ENDERMAN_TELEPORT, 0.5f, 0.8f);
            ModHelpers.getSoundWithPositionV(player.level(), position, SoundEvents.ILLUSIONER_PREPARE_MIRROR, 0.5f, 2f);
            player.resetFallDistance();
        }


        for(int i = 0; i < 50; i++){
            var particle = ParticleHandlers.getAllParticleTypes(ElementRegistry.MYSTIC.get(), 10, 1.5f);
            var x = getRandomX(position.x, 1, 1.5);
            var y = getY(position.y + 1, Random.nextDouble(), 2);
            var z = getRandomZ(position.z, 1, 1.5);
            player.level().addParticle(particle, x, y, z, Random.nextDouble(0.1, 0.3) - 0.2, Random.nextDouble(0.2, 0.5), Random.nextDouble(0.1, 0.3)- 0.2);
        }
    }

    public double getX(double posX, double scale, double width) {
        return posX + width * scale;
    }

    public double getRandomX(double posX, double scale, double width) {
        return this.getX(posX, ((double)2.0F * Random.nextDouble() - (double)1.0F) * scale, width);
    }

    public double getY(double posY, double scale, double width) {
        return posY + width * scale;
    }

    public double getZ(double posZ, double scale, double width) {
        return posZ + width * scale;
    }

    public double getRandomZ(double posZ, double scale, double width) {
        return this.getZ(posZ, ((double)2.0F * Random.nextDouble() - (double)1.0F) * scale, width);
    }

//    private void shootSpikesRandomly(Player player, int maxEntities, double velocity, int discardTime){
//        double centerY = player.getY() + player.getBbHeight() / 2;
//        double angleIncrement = 2 * Math.PI / maxEntities;
//        for (int i = 0; i < maxEntities; i++) {
//            float speeds = ModHelpers.Random.nextFloat((float) velocity - 0.3f, (float) velocity);
//            double theta = i * angleIncrement;
//
//            double x = Math.cos(theta);
//            double z = Math.sin(theta);
//
//            GenericProjectile genericProjectile = new GenericProjectile(
//                player, player.getX(), centerY - 0.5, player.getZ(),
//                ProjectileProperties.ELEMENTAL_SHOOTER.get().setAbilityId(), wandAbilityHolder
//            );
//            genericProjectile.setCustomDiscardTime(discardTime);
//            genericProjectile.setOwner(player);
//            genericProjectile.shoot(x, 0, z, speeds, 0); // Set the y component of direction to 0
//            player.level().addFreshEntity(genericProjectile);
//        }
//    }
}
