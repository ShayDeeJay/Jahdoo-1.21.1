package org.jahdoo.ascension.ability.abilities_combat.arcane_shift;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.ability.AbstractAbility;
import org.jahdoo.ascension.ability.effects.JahdooMobEffect;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.registers.EffectReg;
import org.jahdoo.common.registers.ElementReg;

import static net.minecraft.world.entity.EntitySelector.LIVING_ENTITY_STILL_ALIVE;
import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.ascension.utils.PositionFinders.innerRadiusRandom;
import static org.jahdoo.common.particle.ParticleHandlers.getAllParticleTypes;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;


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

    public void teleportToHome(){
//        var maxEntity = getTag(ArcaneShiftAbility.maxEntities);
        var distances = getTag(CASTING_DISTANCE);
        var position = player.pick(distances, 0, false).getLocation();
        var level = player.level();

        doOnTeleport(player);

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

        doOnTeleport(player);
    }

    private void doOnTeleport(Player player) {
        var level = player.level();
        var areaOfEffect = getTag(AOE);
        var effectDuration = getTag(EFFECT_DURATION);

        for (int i = 0; i < 50; i++) {
            var particle = getAllParticleTypes(element(), 10, 1.5f);
            level.addParticle(particle, player.getRandomX(1), player.getRandomY(), player.getRandomZ(1), Random.nextDouble(0.1, 0.3) - 0.2, Random.nextDouble(0.2, 0.5), Random.nextDouble(0.1, 0.3) - 0.2);
        }

        var list = level.getEntities((Entity) null, player.getBoundingBox().inflate(areaOfEffect, 4, areaOfEffect), LIVING_ENTITY_STILL_ALIVE);

        for (var entity : list) {
            if (entity instanceof LivingEntity livingEntity && entity != player) {
                launchParticle(level, livingEntity.position(), livingEntity.getBbWidth());
                livingEntity.addEffect(new JahdooMobEffect(EffectReg.MYSTIC_EFFECT, (int) effectDuration, 1));
            }
        }
    }

    public void launchParticle(Level level, Vec3 position, double radius){
        innerRadiusRandom(position, radius, radius * 40,
            pos -> sendParticles(level, getAllParticleTypes(element(), 10, 2F), pos, 0, 0, 1, 0, Random.nextDouble(0.1, 0.4))
        );
    }

    public AbstractElement element(){
        return ElementReg.mystic();
    }

}
