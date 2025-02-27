package org.jahdoo.ascension.element;

import net.minecraft.core.particles.ParticleOptions;

public record ElementProperties(
    ParticleOptions baked,
    ParticleOptions bakedSlow,
    ParticleOptions magic,
    ParticleOptions genericSlow,
    ParticleOptions magicSlow,
    ParticleOptions generic
){}