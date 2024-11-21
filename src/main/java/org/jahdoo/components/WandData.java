package org.jahdoo.components;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public record WandData(
    int abilitySlots,
    List<String> upgradeSlots,
    List<String> abilitySet,
    String selectedAbility,
    int rarityId
){
    public static final int INIT_SLOTS = 4;
    public static final WandData DEFAULT = new WandData(INIT_SLOTS, new ArrayList<>(), populatedList(INIT_SLOTS),"", 0);

    public static List<String> populatedList(int initValue){
        var list = new ArrayList<String>();
        for(int i =0; i < initValue; i++) list.add("empty" + i);
        return list;
    }

    public WandData insertNewSlot(int abilitySlots){
        var newList = new ArrayList<>(this.abilitySet);
        newList.add("empty" + (abilitySlots - 1));
        return new WandData(abilitySlots, this.upgradeSlots, newList, this.selectedAbility, this.rarityId);
    }

    public WandData insertNewSlots(int abilitySlots){
        var newList = new ArrayList<>(this.abilitySet);
        for(int i = this.abilitySlots; i < abilitySlots; i++) newList.add("empty" + i);
        return new WandData(abilitySlots, this.upgradeSlots, newList, this.selectedAbility, this.rarityId);
    }

    public WandData setUpgradeSlots(int allowedSlots){
        var newList = new ArrayList<String>();
        for(int i = 0; i < allowedSlots; i++) newList.add("Empty slot");
        return new WandData(this.abilitySlots, newList, this.abilitySet, this.selectedAbility, this.rarityId);
    }

    public WandData setAbilityOrder(List<String> abilities){
        return new WandData(this.abilitySlots, this.upgradeSlots, abilities, this.selectedAbility, this.rarityId);
    }


    public WandData setSelectedAbility(String selectedAbility){
        return new WandData(this.abilitySlots, this.upgradeSlots, this.abilitySet, selectedAbility, this.rarityId);
    }

    public WandData setRarity(int rarity){
        return new WandData(this.abilitySlots, this.upgradeSlots, this.abilitySet, selectedAbility, rarity);
    }

    public void serialise(FriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeInt(abilitySlots);
        friendlyByteBuf.writeCollection(upgradeSlots, FriendlyByteBuf::writeUtf);
        friendlyByteBuf.writeCollection(abilitySet,  FriendlyByteBuf::writeUtf);
        friendlyByteBuf.writeUtf(selectedAbility);
        friendlyByteBuf.writeInt(rarityId);
    }

    public static WandData deserialise(FriendlyByteBuf friendlyByteBuf){
        return new WandData(
            friendlyByteBuf.readInt(),
            friendlyByteBuf.readCollection(Lists::newArrayListWithCapacity, FriendlyByteBuf::readUtf),
            friendlyByteBuf.readCollection(Lists::newArrayListWithCapacity, FriendlyByteBuf::readUtf),
            friendlyByteBuf.readUtf(),
            friendlyByteBuf.readInt()
        );
    }

    public static final StreamCodec<FriendlyByteBuf, WandData> STREAM_CODEC = StreamCodec.ofMember(
        WandData::serialise,
        WandData::deserialise
    );

    public static final Codec<WandData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.INT.fieldOf("ability_slots").forGetter(WandData::abilitySlots),
            Codec.list(Codec.STRING).fieldOf("upgrade_slots").forGetter(WandData::upgradeSlots),
            Codec.list(Codec.STRING).fieldOf("ability_set").forGetter(WandData::abilitySet),
            Codec.STRING.fieldOf("selected_ability").forGetter(WandData::selectedAbility),
            Codec.INT.fieldOf("rarity_id").forGetter(WandData::rarityId)
        ).apply(instance, WandData::new)
    );
}
