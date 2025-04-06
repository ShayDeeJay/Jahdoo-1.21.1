package org.jahdoo.common.components;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jahdoo.ascension.utils.Helpers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public record AbilityHolder(String abilityName, AbilityData data) {

    public static final AbilityHolder DEFAULT = new AbilityHolder("", new AbilityData(new LinkedHashMap<>()));

    private void serialise(FriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeUtf(abilityName);
        friendlyByteBuf.writeJsonWithCodec(AbilityData.CODEC, data);
    }

    public static final StreamCodec<FriendlyByteBuf, AbilityHolder> STREAM_CODEC = StreamCodec.ofMember(
        AbilityHolder::serialise,
        AbilityHolder::deserialise
    );

    private static AbilityHolder deserialise(FriendlyByteBuf friendlyByteBuf){
        return new AbilityHolder(
            friendlyByteBuf.readUtf(),
            friendlyByteBuf.readJsonWithCodec(AbilityData.CODEC)
        );
    }

    public static final Codec<AbilityHolder> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(AbilityHolder::abilityName),
            AbilityData.CODEC.fieldOf("ability_data").forGetter(AbilityHolder::data)
        ).apply(instance, AbilityHolder::new)
    );


    public static void saveListHolders(List<AbilityHolder> unlockedAbilities, CompoundTag tag){
        var collectionHolders = new CompoundTag();

        for (var unlockedAbility : unlockedAbilities) {
            var abilityTag = new CompoundTag();
            writeTag(unlockedAbility, abilityTag);

            collectionHolders.put(unlockedAbility.abilityName(), abilityTag);
        }

        tag.put("holder", collectionHolders);
    }


    public static List<AbilityHolder> readListHolders(CompoundTag tag){
        var unlockedAbilities = new ArrayList<AbilityHolder>();

        if (tag.contains("holder")) {
            var collectionHolder = tag.getCompound("holder");

            for (String abilityId : collectionHolder.getAllKeys()) {
                AbilityHolder abilityHolder = readTag(collectionHolder.getCompound(abilityId), abilityId);
                unlockedAbilities.add(abilityHolder);
            }
        }

        return unlockedAbilities;
    }

    public static void writeTag(
        AbilityHolder holder,
        CompoundTag compoundTag
    ){
        var storedAbility = new CompoundTag();
        var abilityHolder = holder.data().abilityProperties().entrySet();
        var index = new AtomicInteger();

        if(!abilityHolder.isEmpty()){
            abilityHolder.forEach(
                key -> {
                    var value = key.getValue();
                    storedAbility.put(
                        key.getKey(), Helpers.nbtDoubleList(
                            value.actualValue(),
                            value.highestValue(),
                            value.lowestValue(),
                            value.step(),
                            value.setValue(),
                            value.baseCost(),
                            value.upgradeMultiplier(),
                            value.isHigherBetter() ? 0 : 1,
                            index.get()
                        )
                    );
                    index.incrementAndGet();
                }
            );
        }
        compoundTag.put("abilities", storedAbility);
    }

    public static AbilityHolder readTag(CompoundTag compoundTag, String abilityId) {
        var holder = new LinkedHashMap<String, AbilityData.AbilityModifiers>();
        var withPos = new ArrayList<Pair<Integer, Pair<String, AbilityData.AbilityModifiers>>>();
        var abilities = compoundTag.getCompound("abilities");

        abilities.getAllKeys().forEach(
            key -> {
                var value = abilities.getList(key, CompoundTag.TAG_DOUBLE);
                var modifier = new AbilityData.AbilityModifiers(
                    value.getDouble(0), value.getDouble(1),
                    value.getDouble(2), value.getDouble(3),
                    value.getDouble(4), value.getDouble(5),
                    value.getDouble(6), value.getDouble(7) == 0
                );
                int position = (int) value.getDouble(6);
                withPos.add(Pair.of(position, Pair.of(key, modifier)));
            }
        );

        withPos.sort(Comparator.comparing(Pair::getFirst));
        withPos.forEach(o -> holder.put(o.getSecond().getFirst(),o.getSecond().getSecond()));

        var abilityHolder = new AbilityData(holder);
        return new AbilityHolder(abilityId, abilityHolder);
    }


}
