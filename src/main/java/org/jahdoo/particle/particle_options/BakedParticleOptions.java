package org.jahdoo.particle.particle_options;


import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import static org.jahdoo.particle.ParticleStore.getBakedByType;

public record BakedParticleOptions(
    int type,
    int lifetime,
    float size,
    boolean setStaticSize
) implements ParticleOptions {

    @Override
    public @NotNull ParticleType<?> getType() { return getBakedByType.get(type-1); }


    public static StreamCodec<? super ByteBuf, BakedParticleOptions> STREAM_CODEC = StreamCodec.of(
        (buf, option) -> {
            buf.writeInt(option.type);
            buf.writeInt(option.lifetime);
            buf.writeFloat(option.size);
            buf.writeBoolean(option.setStaticSize);
        },
        (buf) -> new BakedParticleOptions(buf.readInt(), buf.readInt(), buf.readFloat(), buf.readBoolean())
    );

    public static MapCodec<BakedParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(object ->
        object.group(
            Codec.INT.fieldOf("type").forGetter(BakedParticleOptions::type),
            Codec.INT.fieldOf("lifetime").forGetter(BakedParticleOptions::lifetime),
            Codec.FLOAT.fieldOf("size").forGetter(BakedParticleOptions::size),
            Codec.BOOL.fieldOf("setStaticSize").forGetter(BakedParticleOptions::setStaticSize)
        ).apply(object, BakedParticleOptions::new)
    );

}
