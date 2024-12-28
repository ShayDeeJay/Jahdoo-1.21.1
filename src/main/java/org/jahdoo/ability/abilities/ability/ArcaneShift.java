package org.jahdoo.ability.abilities.ability;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ability.AbstractAbility;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.ability.abilities.ability_data.ArcaneShiftAbility;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.ability.AbilityBuilder.*;


public class ArcaneShift extends AbstractAbility {

    Player player;
    WandAbilityHolder wandAbilityHolder;

    public ArcaneShift(Player player) {
        this.player = player;
        this.wandAbilityHolder = player.getMainHandItem().get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
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

        if(!player.level().isClientSide) {
            player.teleportTo(position.x, position.y + 1, position.z);
//            shootSpikesRandomly(player, (int) maxEntity, 0.8, (int) lifeTimes);

            ModHelpers.getSoundWithPositionV(player.level(), position, SoundEvents.ENDERMAN_TELEPORT, 0.5f, 0.8f);
            ModHelpers.getSoundWithPositionV(player.level(), position, SoundEvents.ILLUSIONER_PREPARE_MIRROR, 0.5f, 2f);
            player.resetFallDistance();
        }
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
