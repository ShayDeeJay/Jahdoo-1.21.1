package org.jahdoo.common.particle;

import net.minecraft.core.particles.ParticleType;
import org.jahdoo.common.particle.particle_options.BakedParticleOptions;
import org.jahdoo.common.particle.particle_options.GenericParticleOptions;

import java.util.List;

import static org.jahdoo.common.registers.ParticleReg.*;

public record ParticleStore(int r, int g, int b){
    public static final int GENERIC_PARTICLE = 0;
    public static final int MAGIC_PARTICLE = 1;
    public static final int SOFT_PARTICLE = 2;
    public static final int ELECTRIC_PARTICLE = 3;
    public static final int PLUS_PARTICLE = 4;
    public static final int ENCHANT_PARTICLE = 5;

    public static final List<ParticleType<?>> getBakedByType = List.of(
        BAKED_FROST.get(), BAKED_INFERNO.get(), BAKED_MYSTIC.get(), BAKED_VITALITY.get(), BAKED_UTILITY.get(), HEAL.get()
    );

    public static final List<ParticleType<?>> getColouredParticle = List.of(
        GENERIC.get(), MAGIC.get(), SOFT.get(), ELECTRIC.get(), PLUS.get(), ENCHANT.get()
    );

    public static GenericParticleOptions genericParticleFast(int colour, int fade){
        return ParticleHandlers.genericParticle(ParticleStore.GENERIC_PARTICLE,6, 0.55f, colour, fade);
    }

    public static GenericParticleOptions genericParticleSlow(int colour, int fade){
        return ParticleHandlers.genericParticle(ParticleStore.GENERIC_PARTICLE, 20, 1f, colour, fade);
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
