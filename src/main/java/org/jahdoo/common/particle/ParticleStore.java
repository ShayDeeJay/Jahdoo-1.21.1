package org.jahdoo.common.particle;

import net.minecraft.core.particles.ParticleType;
import org.jahdoo.common.particle.particle_options.BakedParticleOptions;
import org.jahdoo.common.particle.particle_options.GenericParticleOptions;
import org.jahdoo.common.registers.ParticleReg;

import java.util.List;

import static org.jahdoo.common.particle.ParticleHandlers.genericParticleOptions;

public record ParticleStore(int r, int g, int b){
    public static final int GENERIC_PARTICLE_SELECTION = 0;
    public static final int MAGIC_PARTICLE_SELECTION = 1;
    public static final int SOFT_PARTICLE_SELECTION = 2;
    public static final int ELECTRIC_PARTICLE_SELECTION = 3;

    public static final List<ParticleType<?>> getBakedByType = List.of(
        ParticleReg.BAKED_FROST.get(),
        ParticleReg.BAKED_INFERNO.get(),
        ParticleReg.BAKED_MYSTIC.get(),
        ParticleReg.BAKED_VITALITY.get(),
        ParticleReg.BAKED_UTILITY.get(),
        ParticleReg.HEAL.get()
    );

    public static final List<ParticleType<?>> getColouredParticle = List.of(
        ParticleReg.GENERIC.get(),
        ParticleReg.MAGIC.get(),
        ParticleReg.SOFT.get(),
        ParticleReg.ELECTRIC.get()
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
        return new BakedParticleOptions(type, 6, 0.55f, false);
    }

    public static int rgbToInt(int red, int green, int blue) {
        if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
            throw new IllegalArgumentException("RGB components must be in the range 0-255");
        }

        return (red << 16) | (green << 8) | blue;
    }
}
