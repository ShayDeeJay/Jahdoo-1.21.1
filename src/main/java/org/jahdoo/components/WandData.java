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
    String selectedAbility
){
    public static final WandData DEFAULT = new WandData(4, new ArrayList<>(), new ArrayList<>(),"");

    public WandData setAbilitySlots(int abilitySlots){
        return new WandData(abilitySlots, this.upgradeSlots, this.abilitySet, this.selectedAbility);
    }

    public WandData setUpgradeSlots(int allowedSlots){
        var newList = new ArrayList<String>();
        for(int i = 0; i < allowedSlots; i++) newList.add("Empty slot");
        return new WandData(this.abilitySlots, newList, this.abilitySet,this.selectedAbility);
    }

    public WandData setAbilityOrder(List<String> abilities){
        return new WandData(this.abilitySlots, this.upgradeSlots, abilities, this.selectedAbility);
    }

    public WandData setSelectedAbility(String selectedAbility){
        return new WandData(this.abilitySlots, this.upgradeSlots, this.abilitySet, selectedAbility);
    }

    public void serialise(FriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeInt(abilitySlots);
        friendlyByteBuf.writeCollection(upgradeSlots, FriendlyByteBuf::writeUtf);
        friendlyByteBuf.writeCollection(abilitySet,  FriendlyByteBuf::writeUtf);
        friendlyByteBuf.writeUtf(selectedAbility);
    }

    public static WandData deserialise(FriendlyByteBuf friendlyByteBuf){
        return new WandData(
            friendlyByteBuf.readInt(),
            friendlyByteBuf.readCollection(Lists::newArrayListWithCapacity, FriendlyByteBuf::readUtf),
            friendlyByteBuf.readCollection(Lists::newArrayListWithCapacity, FriendlyByteBuf::readUtf),
            friendlyByteBuf.readUtf()
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
            Codec.STRING.fieldOf("selected_ability").forGetter(WandData::selectedAbility)
        ).apply(instance, WandData::new)
    );
}
