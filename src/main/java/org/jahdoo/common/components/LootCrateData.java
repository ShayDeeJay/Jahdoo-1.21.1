package org.jahdoo.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record LootCrateData(int level, int multiplier, int completionTime, String difficulty) {

    private void serialise(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(level);
        friendlyByteBuf.writeInt(multiplier);
        friendlyByteBuf.writeInt(completionTime);
        friendlyByteBuf.writeUtf(difficulty);
    }

    private static LootCrateData deserialise(FriendlyByteBuf byteBuf){
        return new LootCrateData(byteBuf.readInt(), byteBuf.readInt(), byteBuf.readInt(), byteBuf.readUtf());
    }

    public static final StreamCodec<FriendlyByteBuf, LootCrateData> STREAM_CODEC = StreamCodec.ofMember(
        LootCrateData::serialise,
        LootCrateData::deserialise
    );

    public static final Codec<LootCrateData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.INT.fieldOf("player_level").forGetter(LootCrateData::level),
            Codec.INT.fieldOf("multiplier").forGetter(LootCrateData::multiplier),
            Codec.INT.fieldOf("rooms_cleared").forGetter(LootCrateData::completionTime),
            Codec.STRING.fieldOf("difficulty").forGetter(LootCrateData::difficulty)
        ).apply(instance, LootCrateData::new)
    );

}
