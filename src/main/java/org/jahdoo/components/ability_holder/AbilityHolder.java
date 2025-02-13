package org.jahdoo.components.ability_holder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Map;

public record AbilityHolder(Map<String, AbilityModifiers> abilityProperties) {

    private void serialise(FriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeMap(abilityProperties, ByteBufCodecs.STRING_UTF8, AbilityModifiers.STREAM_CODEC);
    }

    private static AbilityHolder deserialise(FriendlyByteBuf friendlyByteBuf){
        return new AbilityHolder(friendlyByteBuf.readMap(ByteBufCodecs.STRING_UTF8, AbilityModifiers.STREAM_CODEC));
    }

    public static final StreamCodec<FriendlyByteBuf, AbilityHolder> STREAM_CODEC = StreamCodec.ofMember(
        AbilityHolder::serialise,
        AbilityHolder::deserialise
    );

    public static final Codec<AbilityHolder> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, AbilityModifiers.CODEC)
                .fieldOf("ability_properties")
                .forGetter(AbilityHolder::abilityProperties)
        ).apply(instance, AbilityHolder::new)
    );

    public record AbilityModifiers(
        double actualValue,
        double highestValue,
        double lowestValue,
        double step,
        double setValue,
        boolean isHigherBetter
    ){
        private void serialise(FriendlyByteBuf friendlyByteBuf){
            friendlyByteBuf.writeDouble(actualValue);
            friendlyByteBuf.writeDouble(highestValue);
            friendlyByteBuf.writeDouble(lowestValue);
            friendlyByteBuf.writeDouble(step);
            friendlyByteBuf.writeDouble(setValue);
            friendlyByteBuf.writeBoolean(isHigherBetter);
        }

        private static AbilityModifiers deserialise(FriendlyByteBuf friendlyByteBuf){
            return new AbilityModifiers(
                friendlyByteBuf.readDouble(),
                friendlyByteBuf.readDouble(),
                friendlyByteBuf.readDouble(),
                friendlyByteBuf.readDouble(),
                friendlyByteBuf.readDouble(),
                friendlyByteBuf.readBoolean()
            );
        }

        private static final StreamCodec<FriendlyByteBuf, AbilityModifiers> STREAM_CODEC = StreamCodec.ofMember(
            AbilityModifiers::serialise,
            AbilityModifiers::deserialise
        );

        private static final Codec<AbilityModifiers> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                Codec.DOUBLE.fieldOf("actual_value").forGetter(AbilityModifiers::actualValue),
                Codec.DOUBLE.fieldOf("highest_value").forGetter(AbilityModifiers::highestValue),
                Codec.DOUBLE.fieldOf("lowest").forGetter(AbilityModifiers::lowestValue),
                Codec.DOUBLE.fieldOf("step").forGetter(AbilityModifiers::step),
                Codec.DOUBLE.fieldOf("set_value").forGetter(AbilityModifiers::setValue),
                Codec.BOOL.fieldOf("is_higher_better").forGetter(AbilityModifiers::isHigherBetter)
            ).apply(instance, AbilityModifiers::new)
        );
    }

}
