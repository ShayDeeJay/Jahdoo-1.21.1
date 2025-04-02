package org.jahdoo.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.utils.Helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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
        AbilityHolder wandAbilityHolder,
        CompoundTag compoundTag
    ){
        var storedAbility = new CompoundTag();
        var abilityHolder = wandAbilityHolder.data();

        if(abilityHolder != null){
            abilityHolder.abilityProperties().forEach((key, value) -> storedAbility.putDouble(key, value.actualValue()));
        }

        compoundTag.put("wand_abilities", storedAbility);
    }

    public static AbilityHolder readTag(CompoundTag compoundTag, String abilityId){
        var holder = new HashMap<String, AbilityData.AbilityModifiers>();

        compoundTag.getCompound("wand_abilities").getAllKeys().forEach(
            keys -> {
                var actualValue = compoundTag.getCompound("wand_abilities").getDouble(keys);
                var modifier = new AbilityData.AbilityModifiers(actualValue, 0, 0, 0, actualValue,true);
                holder.put(keys, modifier);
            }
        );

        var abilityHolder = new AbilityData(holder);
        return new AbilityHolder(abilityId, abilityHolder);
    }
}
