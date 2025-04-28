package org.jahdoo.common.items.runes.rune_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record RuneData(
    String name,
    int tier
){
    public static final String SUFFIX = "Rune";
    public static final String DEFAULT_NAME = "Unidentified";
    public static final int NO_ELEMENT = -1;
    public static final int NO_VALUE = -1;
    public static final RuneData DEFAULT = new RuneData(DEFAULT_NAME, 0);

    public static final StreamCodec<RegistryFriendlyByteBuf, RuneData> STREAM_CODEC = StreamCodec.ofMember(
        RuneData::serialise,
        RuneData::deserialise
    );

    public static RuneData deserialise(RegistryFriendlyByteBuf friendlyByteBuf){
        return new RuneData(
            friendlyByteBuf.readUtf(),
            friendlyByteBuf.readInt()
        );
    }

    public void serialise(RegistryFriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeUtf(this.name);
        friendlyByteBuf.writeInt(this.tier);
    }

    public static final Codec<RuneData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(RuneData::name),
            Codec.INT.fieldOf("tier").forGetter(RuneData::tier)
            ).apply(instance, RuneData::new)
    );

}
