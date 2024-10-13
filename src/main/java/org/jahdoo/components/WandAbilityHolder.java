package org.jahdoo.components;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.LinkedHashMap;
import java.util.Map;

public record WandAbilityHolder(Map<String, AbilityHolder> abilityProperties) {

    public static final WandAbilityHolder DEFAULT = new WandAbilityHolder(new LinkedHashMap<>());

    public static final StreamCodec<FriendlyByteBuf, WandAbilityHolder> STREAM_CODEC = StreamCodec.ofMember(
        WandAbilityHolder::serialise,
        WandAbilityHolder::deserialise
    );

    private void serialise(FriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeMap(abilityProperties, ByteBufCodecs.STRING_UTF8, AbilityHolder.STREAM_CODEC);
    }

    private static WandAbilityHolder deserialise(FriendlyByteBuf friendlyByteBuf){
        return new WandAbilityHolder(
            friendlyByteBuf.readMap(Maps::newLinkedHashMapWithExpectedSize, ByteBufCodecs.STRING_UTF8, AbilityHolder.STREAM_CODEC)
        );
    }

    public static final Codec<WandAbilityHolder> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, AbilityHolder.CODEC)
                .fieldOf("wand_ability_properties")
                .forGetter(WandAbilityHolder::abilityProperties)
        ).apply(instance, WandAbilityHolder::new)
    );

}
