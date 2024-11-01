package org.jahdoo.all_magic.effects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.particle.ParticleHandlers.spawnElectrifiedParticles;

public class EffectParticles {

    public static void setEffectParticle(
        int getRandomChance,
        LivingEntity targetEntity,
        ServerLevel serverLevel,
        AbstractElement particleGroup,
        SoundEvent soundEvents
    ){
        boolean isChance = getRandomChance == 0;
        if(targetEntity.isAlive()){
            if (isChance) {
                ModHelpers.getSoundWithPosition(targetEntity.level(), targetEntity.blockPosition(), soundEvents, 0.3f);
                spawnElectrifiedParticles(serverLevel, targetEntity.position(), particleGroup.getParticleGroup().magicSlow(), 3, targetEntity, -0.3, -1);
            }
            spawnElectrifiedParticles(serverLevel, targetEntity.position(), particleGroup.getParticleGroup().bakedSlow(), isChance ? 10 : 1, targetEntity, isChance ? 0.1 : 0.08);
            spawnElectrifiedParticles(serverLevel, targetEntity.position(), particleGroup.getParticleGroup().magic(), 1, targetEntity, 0.08);
        }
    }

}
