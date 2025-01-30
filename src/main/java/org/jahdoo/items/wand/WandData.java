package org.jahdoo.items.wand;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.registers.DataComponentRegistry.WAND_DATA;

public record WandData(
    int abilitySlots,
    List<String> abilitySet,
    String selectedAbility
){
    public static final int INIT_SLOTS = 3;
    public static final WandData DEFAULT = new WandData(INIT_SLOTS, populatedList(INIT_SLOTS),"");

    public static List<String> populatedList(int initValue){
        var list = new ArrayList<String>();
        for(int i =0; i < initValue; i++) list.add("empty" + i);
        return list;
    }

    public WandData insertNewAbilitySlots(int abilitySlots){
        var newList = new ArrayList<>(this.abilitySet);
        for(int i = this.abilitySlots; i < abilitySlots; i++) newList.add("empty" + i);
        return new WandData(abilitySlots, newList, this.selectedAbility);
    }

    public WandData setAbilityOrder(List<String> abilities){
        return new WandData(this.abilitySlots, abilities, this.selectedAbility);
    }

    public WandData setSelectedAbility(String selectedAbility){
        return new WandData(this.abilitySlots, this.abilitySet, selectedAbility);
    }

    public WandData setRarity(int rarity){
        return new WandData(this.abilitySlots, this.abilitySet, this.selectedAbility);
    }

    public static void createNewAbilitySlots(ItemStack itemStack, int abilitySlots){
        itemStack.update(WAND_DATA, WandData.DEFAULT, data -> data.insertNewAbilitySlots(abilitySlots));
    }

    public static void createRarity(ItemStack itemStack, int rarityId){
        itemStack.update(WAND_DATA, WandData.DEFAULT, data -> data.setRarity(rarityId));
    }

    public static WandData wandData(ItemStack itemStack){
        return itemStack.getOrDefault(WAND_DATA, DEFAULT);
    }

    public void serialise(RegistryFriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeInt(abilitySlots);
        friendlyByteBuf.writeCollection(abilitySet, FriendlyByteBuf::writeUtf);
        friendlyByteBuf.writeUtf(selectedAbility);
    }

    public static WandData deserialise(RegistryFriendlyByteBuf friendlyByteBuf){
        return new WandData(
            friendlyByteBuf.readInt(),
            friendlyByteBuf.readCollection(Lists::newArrayListWithCapacity, FriendlyByteBuf::readUtf),
            friendlyByteBuf.readUtf()
        );
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, WandData> STREAM_CODEC = StreamCodec.ofMember(
        WandData::serialise,
        WandData::deserialise
    );

    public static final Codec<WandData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.INT.fieldOf("ability_slots").forGetter(WandData::abilitySlots),
            Codec.list(Codec.STRING).fieldOf("ability_set").forGetter(WandData::abilitySet),
            Codec.STRING.fieldOf("selected_ability").forGetter(WandData::selectedAbility)
        ).apply(instance, WandData::new)
    );
}
