package org.jahdoo.particle;

import net.minecraft.core.particles.ParticleType;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.registers.ParticlesRegister;

import java.util.List;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;

public record ParticleStore(int r, int g, int b){
    public static final int GENERIC_PARTICLE_SELECTION = 0;
    public static final int MAGIC_PARTICLE_SELECTION = 1;
    public static final int SOFT_PARTICLE_SELECTION = 2;
    public static final int ELECTRIC_PARTICLE_SELECTION = 3;

    public static List<ParticleType<?>> getBakedByType = List.of(
        ParticlesRegister.BAKED_INFERNO.get(),
        ParticlesRegister.BAKED_FROST.get(),
        ParticlesRegister.BAKED_LIGHTNING.get(),
        ParticlesRegister.BAKED_MYSTIC.get(),
        ParticlesRegister.BAKED_VITALITY.get(),
        ParticlesRegister.BAKED_UTILITY.get(),
        ParticlesRegister.HEAL.get()
    );

    public static List<ParticleType<?>> getColouredParticle = List.of(
        ParticlesRegister.GENERIC.get(),
        ParticlesRegister.MAGIC.get(),
        ParticlesRegister.SOFT.get(),
        ParticlesRegister.ELECTRIC.get()
    );

    public static GenericParticleOptions genericParticleFast(int colour, int fade){
        return genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION,6, 0.55f, colour, fade);
    }

    public static GenericParticleOptions genericParticleSlow(int colour, int fade){
        return genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, 20, 1f, colour, fade);
    }

    public static BakedParticleOptions bakedParticleSlow(int type){
        return new BakedParticleOptions(type,4,1f, false);
    }

    public static BakedParticleOptions bakedParticleFast(int type){
        return new BakedParticleOptions(type,6,0.55f, false);
    }

    public static int rgbToInt(int red, int green, int blue) {
        // Ensure the RGB components are within the valid range
        if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
            throw new IllegalArgumentException("RGB components must be in the range 0-255");
        }

        // Combine the RGB components into a single integer
        int rgbInt = (red << 16) | (green << 8) | blue;

        return rgbInt;
    }
}
