package org.jahdoo.registers;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.particle.particle_options.GenericParticleOptions;

import java.util.function.Supplier;

public class ParticlesRegister {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
        DeferredRegister.create(Registries.PARTICLE_TYPE, JahdooMod.MOD_ID);

    //Non specific colourable particles
    public static final Supplier<ParticleType<GenericParticleOptions>> GENERIC = register(
        "generic", GenericParticleOptions.MAP_CODEC, GenericParticleOptions.STREAM_CODEC
    );
    public static final Supplier<ParticleType<GenericParticleOptions>> MAGIC = register(
        "magic", GenericParticleOptions.MAP_CODEC, GenericParticleOptions.STREAM_CODEC
    );
    public static final Supplier<ParticleType<GenericParticleOptions>> SOFT = register(
        "soft", GenericParticleOptions.MAP_CODEC, GenericParticleOptions.STREAM_CODEC
    );
    public static final Supplier<ParticleType<GenericParticleOptions>> ELECTRIC = register(
        "electric", GenericParticleOptions.MAP_CODEC, GenericParticleOptions.STREAM_CODEC
    );

    //Colour baked particles
    public static final Supplier<ParticleType<BakedParticleOptions>> BAKED_INFERNO = register(
        "baked_inferno", BakedParticleOptions.MAP_CODEC, BakedParticleOptions.STREAM_CODEC
    );
    public static final Supplier<ParticleType<BakedParticleOptions>> BAKED_FROST = register(
        "baked_frost", BakedParticleOptions.MAP_CODEC, BakedParticleOptions.STREAM_CODEC
    );
    public static final Supplier<ParticleType<BakedParticleOptions>> BAKED_LIGHTNING = register(
        "baked_lightning", BakedParticleOptions.MAP_CODEC, BakedParticleOptions.STREAM_CODEC
    );
    public static final Supplier<ParticleType<BakedParticleOptions>> BAKED_MYSTIC = register(
        "baked_mystic", BakedParticleOptions.MAP_CODEC, BakedParticleOptions.STREAM_CODEC
    );
    public static final Supplier<ParticleType<BakedParticleOptions>> BAKED_VITALITY = register(
        "baked_vitality", BakedParticleOptions.MAP_CODEC, BakedParticleOptions.STREAM_CODEC
    );
    public static final Supplier<ParticleType<BakedParticleOptions>> BAKED_UTILITY = register(
        "baked_utility", BakedParticleOptions.MAP_CODEC, BakedParticleOptions.STREAM_CODEC
    );
    public static final Supplier<ParticleType<BakedParticleOptions>> HEAL = register(
        "heal", BakedParticleOptions.MAP_CODEC, BakedParticleOptions.STREAM_CODEC
    );

    private static <T extends ParticleOptions> Supplier<ParticleType<T>> register(
        String pKey,
        final MapCodec<T> pCodecFactory,
        StreamCodec<? super RegistryFriendlyByteBuf, T>  streamCodec
    ) {
        return PARTICLE_TYPES.register(pKey, () ->
            new ParticleType<T>(true) {
                @Override
                public MapCodec<T> codec() {
                    return pCodecFactory;
                }

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                    return streamCodec;
                }
            }
        );
    }

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }

}
