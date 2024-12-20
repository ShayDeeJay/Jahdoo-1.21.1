package org.jahdoo.components;

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
    List<ItemStack> upgradeSlots,
    List<String> abilitySet,
    String selectedAbility,
    int rarityId
){
    public static final int INIT_SLOTS = 3;
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

    public WandData insertNewAbilitySlots(int abilitySlots){
        var newList = new ArrayList<>(this.abilitySet);
        for(int i = this.abilitySlots; i < abilitySlots; i++) newList.add("empty" + i);
        return new WandData(abilitySlots, this.upgradeSlots, newList, this.selectedAbility, this.rarityId);
    }

    public WandData insertNewUpgradeSlots(int allowedSlots){
        var newList = new ArrayList<ItemStack>();
        for(int i = 0; i < allowedSlots; i++) newList.add(ItemStack.EMPTY);
        return new WandData(this.abilitySlots, newList, this.abilitySet, this.selectedAbility, this.rarityId);
    }

    public WandData setAbilityOrder(List<String> abilities){
        return new WandData(this.abilitySlots, this.upgradeSlots, abilities, this.selectedAbility, this.rarityId);
    }

    public WandData setSelectedAbility(String selectedAbility){
        return new WandData(this.abilitySlots, this.upgradeSlots, this.abilitySet, selectedAbility, this.rarityId);
    }

    public WandData setRarity(int rarity){
        return new WandData(this.abilitySlots, this.upgradeSlots, this.abilitySet, this.selectedAbility, rarity);
    }

    public WandData setUpgradeSlots(List<ItemStack> upgradeSlots){
        return new WandData(this.abilitySlots, upgradeSlots, this.abilitySet, this.selectedAbility, this.rarityId);
    }

    public static void createNewSlotsForWand(ItemStack itemStack, int slotCount){
        itemStack.update(WAND_DATA, WandData.DEFAULT, data -> data.insertNewUpgradeSlots(slotCount));
    }

    public static void updateUpgradeSlots(ItemStack itemStack, List<ItemStack> upgrades){
        itemStack.update(WAND_DATA, WandData.DEFAULT, data -> data.setUpgradeSlots(upgrades));
    }

    public static WandData wandData(ItemStack itemStack){
        return itemStack.getOrDefault(WAND_DATA, DEFAULT);
    }

    public void serialise(RegistryFriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeInt(abilitySlots);
        ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(friendlyByteBuf, upgradeSlots);
        friendlyByteBuf.writeCollection(abilitySet, FriendlyByteBuf::writeUtf);
        friendlyByteBuf.writeUtf(selectedAbility);
        friendlyByteBuf.writeInt(rarityId);
    }

    public static WandData deserialise(RegistryFriendlyByteBuf friendlyByteBuf){
        return new WandData(
            friendlyByteBuf.readInt(),
            ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(friendlyByteBuf),
            friendlyByteBuf.readCollection(Lists::newArrayListWithCapacity, FriendlyByteBuf::readUtf),
            friendlyByteBuf.readUtf(),
            friendlyByteBuf.readInt()
        );
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, WandData> STREAM_CODEC = StreamCodec.ofMember(
        WandData::serialise,
        WandData::deserialise
    );

    public static final Codec<WandData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.INT.fieldOf("ability_slots").forGetter(WandData::abilitySlots),
            Codec.list(ItemStack.OPTIONAL_CODEC).fieldOf("upgrade_slots").forGetter(WandData::upgradeSlots),
            Codec.list(Codec.STRING).fieldOf("ability_set").forGetter(WandData::abilitySet),
            Codec.STRING.fieldOf("selected_ability").forGetter(WandData::selectedAbility),
            Codec.INT.fieldOf("rarity_id").forGetter(WandData::rarityId)
        ).apply(instance, WandData::new)
    );
}
