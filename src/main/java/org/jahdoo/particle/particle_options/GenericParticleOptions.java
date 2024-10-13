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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;

import static org.jahdoo.particle.ParticleStore.getColouredParticle;

public record GenericParticleOptions(
    int type,
    int colour,
    int fade,
    int lifetime,
    float size,
    boolean setStaticSize,
    double speed
) implements ParticleOptions {

    @Override
    public @NotNull ParticleType<?> getType() {
        return getColouredParticle.get(type);
    }


    public static StreamCodec<? super ByteBuf, GenericParticleOptions> STREAM_CODEC = StreamCodec.of(
        (buf, option) -> {
            buf.writeInt(option.type);
            buf.writeInt(option.colour);
            buf.writeInt(option.fade);
            buf.writeInt(option.lifetime);
            buf.writeFloat(option.size);
            buf.writeBoolean(option.setStaticSize);
            buf.writeDouble(option.speed);
        },
        (buf) -> new GenericParticleOptions(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readFloat(), buf.readBoolean(), buf.readDouble())
    );

    public static MapCodec<GenericParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(object ->
        object.group(
            Codec.INT.fieldOf("type").forGetter(GenericParticleOptions::type),
            Codec.INT.fieldOf("colour").forGetter(GenericParticleOptions::colour),
            Codec.INT.fieldOf("fade").forGetter(GenericParticleOptions::fade),
            Codec.INT.fieldOf("lifetime").forGetter(GenericParticleOptions::lifetime),
            Codec.FLOAT.fieldOf("size").forGetter(GenericParticleOptions::size),
            Codec.BOOL.fieldOf("setStaticSize").forGetter(GenericParticleOptions::setStaticSize),
            Codec.DOUBLE.fieldOf("speed").forGetter(GenericParticleOptions::speed)
        ).apply(object, GenericParticleOptions::new)
    );

}
