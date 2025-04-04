package org.jahdoo.common.components;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.utils.Helpers;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.common.registers.ComponentReg.ABILITY_HOLDER;

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

    public static AbilityHolder getHolderFromWand(Player player){
        var component = ABILITY_HOLDER.get();
        return Helpers.getUsedItem(player).get(component);
    }

    private static AbilityHolder deserialise(FriendlyByteBuf friendlyByteBuf){
        return new AbilityHolder(
            friendlyByteBuf.readUtf(),
            friendlyByteBuf.readJsonWithCodec(AbilityData.CODEC)
        );
    }

    public static AbilityHolder getHolder(ItemStack itemStack){
        var getHolder = itemStack.get(ABILITY_HOLDER);
        if(getHolder != null) return getHolder;
        return DEFAULT;
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

        tag.put("wand_collection", collectionHolders);
    }


    public static List<AbilityHolder> readListHolders(CompoundTag tag){
        var unlockedAbilities = new ArrayList<AbilityHolder>();

        if (tag.contains("wand_collection")) {
            var collectionHolder = tag.getCompound("wand_collection");

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
                            value.isHigherBetter() ? 0 : 1,
                            index.get()
                        )
                    );
                    index.incrementAndGet();
                }
            );
        }



        compoundTag.put("wand_abilities", storedAbility);
    }

    public static AbilityHolder readTag(CompoundTag compoundTag, String abilityId) {
        var holder = new LinkedHashMap<String, AbilityData.AbilityModifiers>();
        var wandAbilities = compoundTag.getCompound("wand_abilities");

        // Create a list with nulls up to the maximum possible index
        int maxIndex = wandAbilities.getAllKeys().stream()
            .mapToInt(key -> (int) wandAbilities.getList(key, CompoundTag.TAG_DOUBLE).getDouble(6))
            .max()
            .orElse(-1) + 1;

        List<Pair<String, AbilityData.AbilityModifiers>> orderedList = new ArrayList<>(Collections.nCopies(maxIndex, null));

        wandAbilities.getAllKeys().forEach(
            key -> {
                var actualValue = wandAbilities.getList(key, CompoundTag.TAG_DOUBLE);
                var modifier = new AbilityData.AbilityModifiers(
                    actualValue.getDouble(0),
                    actualValue.getDouble(1),
                    actualValue.getDouble(2),
                    actualValue.getDouble(3),
                    actualValue.getDouble(4),
                    actualValue.getDouble(5) == 0
                );
                int position = (int) actualValue.getDouble(6);
                orderedList.set(position, Pair.of(key, modifier));
            }
        );

        // Add non-null entries to the map in order
        orderedList.stream()
            .filter(Objects::nonNull)
            .forEach(pair -> holder.put(pair.getFirst(), pair.getSecond()));

        var abilityHolder = new AbilityData(holder);
        return new AbilityHolder(abilityId, abilityHolder);
    }
}
