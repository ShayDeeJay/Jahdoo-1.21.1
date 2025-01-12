package org.jahdoo.challenge;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.*;

public class BiomeBuilder {

    public static Biome getTestBiome(){
        return new Biome.BiomeBuilder()
            .hasPrecipitation(false)
            .temperature(2.0F)
            .downfall(0.0F)
            .specialEffects(
                (new BiomeSpecialEffects.Builder())
                    .waterColor(4159204)
                    .waterFogColor(329011)
                    .fogColor(6840176)
                    .skyColor(calculateSkyColor(2.0F))
                    .ambientParticle(new AmbientParticleSettings(ParticleTypes.WHITE_ASH, 0.118093334F))
                    .ambientLoopSound(SoundEvents.AMBIENT_BASALT_DELTAS_LOOP)
                    .ambientMoodSound(new AmbientMoodSettings(SoundEvents.AMBIENT_BASALT_DELTAS_MOOD, 6000, 8, (double)2.0F))
                    .ambientAdditionsSound(new AmbientAdditionsSettings(SoundEvents.AMBIENT_BASALT_DELTAS_ADDITIONS, 0.0111))
                    .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_BASALT_DELTAS)).build()
            )
            .build();
    }


    protected static int calculateSkyColor(float temperature) {
        float $$1 = temperature / 3.0F;
        $$1 = Mth.clamp($$1, -1.0F, 1.0F);
        return Mth.hsvToRgb(0.62222224F - $$1 * 0.05F, 0.5F + $$1 * 0.1F, 1.0F);
    }
}
