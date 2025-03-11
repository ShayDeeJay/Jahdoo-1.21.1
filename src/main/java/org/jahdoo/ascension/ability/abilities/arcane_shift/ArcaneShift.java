package org.jahdoo.ascension.ability.abilities.arcane_shift;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.AbstractAbility;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.ElementReg;

import static org.jahdoo.ascension.ability.AbilityBuilder.CASTING_DISTANCE;
import static org.jahdoo.ascension.utils.Helpers.Random;


public class ArcaneShift extends AbstractAbility {

    private final Player player;
    private final WandAbilityHolder wandAbilityHolder;

    public ArcaneShift(Player player) {
        this.player = player;
        this.wandAbilityHolder = WandAbilityHolder.getHolderFromWand(player);
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return wandAbilityHolder;
    }

    @Override
    public String abilityId() {
        return ArcaneShiftAbility.abilityId.getPath().intern();
    }

    public static double getX(double posX, double scale, double width) {
        return posX + width * scale;
    }

    public static double getRandomX(double posX, double scale, double width) {
        return getX(posX, ((double)2.0F * Random.nextDouble() - (double)1.0F) * scale, width);
    }

    public static double getY(double posY, double scale, double width) {
        return posY + width * scale;
    }

    public static double getZ(double posZ, double scale, double width) {
        return posZ + width * scale;
    }

    public static double getRandomZ(double posZ, double scale, double width) {
        return getZ(posZ, ((double)2.0F * Random.nextDouble() - (double)1.0F) * scale, width);
    }

    public void teleportToHome(){
//        var damages = getTag(DAMAGE);
//        var maxEntity = getTag(ArcaneShiftAbility.maxEntities);
//        var lifeTimes = getTag(ArcaneShiftAbility.lifeTime);
        var distances = getTag(CASTING_DISTANCE);
        var position = player.pick(distances, 0, false).getLocation();
        var level = player.level();
        for(int i = 0; i < 50; i++){
            var particle = ParticleHandlers.getAllParticleTypes(ElementReg.mystic(), 10, 1.5f);
            var x = player.getRandomX(1);
            var y = player.getRandomY();
            var z = player.getRandomZ(1);
            level.addParticle(particle, x, y, z, Random.nextDouble(0.1, 0.3) - 0.2, Random.nextDouble(0.2, 0.5), Random.nextDouble(0.1, 0.3)- 0.2);
        }

        if(!level.isClientSide) {
            var blockPos = BlockPos.containing(position);
            var isEmptyA = level.getBlockState(blockPos.above()).isAir();
            var isEmptyB = level.getBlockState(blockPos.above(2)).isAir();
            var center = blockPos.getCenter();

            if(isEmptyA && isEmptyB){
                player.teleportTo(center.x, center.y + 0.5, center.z);
            } else {
                var inFront = blockPos.relative(player.getDirection(),-1).getCenter();
                player.teleportTo(inFront.x, inFront.y + 0.5, inFront.z);
            }

            Helpers.getSoundWithPositionV(level, position, SoundEvents.ENDERMAN_TELEPORT, 0.5f, 0.8f);
            Helpers.getSoundWithPositionV(level, position, SoundEvents.ILLUSIONER_PREPARE_MIRROR, 0.5f, 2f);
            player.resetFallDistance();
        }

        for(int i = 0; i < 50; i++){
            var particle = ParticleHandlers.getAllParticleTypes(ElementReg.mystic(), 10, 1.5f);
            var x = getRandomX(position.x, 1, 1.5);
            var y = getY(position.y + 1, Random.nextDouble(), 2);
            var z = getRandomZ(position.z, 1, 1.5);
            level.addParticle(particle, x, y, z, Random.nextDouble(0.1, 0.3) - 0.2, Random.nextDouble(0.2, 0.5), Random.nextDouble(0.1, 0.3)- 0.2);
        }
    }

}
