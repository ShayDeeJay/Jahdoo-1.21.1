package org.jahdoo.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Map;

public record AbilityData(Map<String, AbilityModifiers> abilityProperties) {

    private void serialise(FriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeMap(abilityProperties, ByteBufCodecs.STRING_UTF8, AbilityModifiers.STREAM_CODEC);
    }

    private static AbilityData deserialise(FriendlyByteBuf byteBuf){
        return new AbilityData(byteBuf.readMap(ByteBufCodecs.STRING_UTF8, AbilityModifiers.STREAM_CODEC));
    }

    public static final StreamCodec<FriendlyByteBuf, AbilityData> STREAM_CODEC = StreamCodec.ofMember(
        AbilityData::serialise,
        AbilityData::deserialise
    );

    public static final Codec<AbilityData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, AbilityModifiers.CODEC)
                .fieldOf("ability_properties")
                .forGetter(AbilityData::abilityProperties)
        ).apply(instance, AbilityData::new)
    );

    public record AbilityModifiers(
        double actualValue,
        double highestValue,
        double lowestValue,
        double step,
        double setValue,
        double baseCost,
        double upgradeMultiplier,
        boolean isHigherBetter
    ){
        private static final StreamCodec<FriendlyByteBuf, AbilityModifiers> STREAM_CODEC = StreamCodec.ofMember(
            AbilityModifiers::serialise,
            AbilityModifiers::deserialise
        );

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
                friendlyByteBuf.readDouble(),
                friendlyByteBuf.readDouble(),
                friendlyByteBuf.readBoolean()
            );
        }

        private static final Codec<AbilityModifiers> CODEC = RecordCodecBuilder.create( instance ->
            instance.group(
                Codec.DOUBLE.fieldOf("actual_value").forGetter(AbilityModifiers::actualValue),
                Codec.DOUBLE.fieldOf("highest_value").forGetter(AbilityModifiers::highestValue),
                Codec.DOUBLE.fieldOf("lowest").forGetter(AbilityModifiers::lowestValue),
                Codec.DOUBLE.fieldOf("step").forGetter(AbilityModifiers::step),
                Codec.DOUBLE.fieldOf("set_value").forGetter(AbilityModifiers::setValue),
                Codec.DOUBLE.fieldOf("baseCost").forGetter(AbilityModifiers::baseCost),
                Codec.DOUBLE.fieldOf("upgrade_multiplier").forGetter(AbilityModifiers::upgradeMultiplier),
                Codec.BOOL.fieldOf("is_higher_better").forGetter(AbilityModifiers::isHigherBetter)
            ).apply(instance, AbilityModifiers::new)
        );

        @Override
        public String toString() {
            return "AbilityModifiers {\n" +
                "  Actual Value: " + actualValue + ",\n" +
                "  Highest Value: " + highestValue + ",\n" +
                "  Lowest Value: " + lowestValue + ",\n" +
                "  Step: " + step + ",\n" +
                "  Set Value: " + setValue + ",\n" +
                "  Base Cost: " + setValue + ",\n" +
                "  Upgrade Multiplier: " + setValue + ",\n" +
                "  Is Higher Better: " + isHigherBetter + "\n" +
                "}";
        }
    }

}
