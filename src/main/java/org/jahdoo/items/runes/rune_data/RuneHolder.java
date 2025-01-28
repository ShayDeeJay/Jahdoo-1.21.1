package org.jahdoo.items.runes.rune_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.registers.DataComponentRegistry.RUNE_HOLDER;

public record RuneHolder(
    List<ItemStack> runeSlots
){
    public static RuneHolder DEFAULT = new RuneHolder(new ArrayList<>());

    public RuneHolder insertNewHolder(List<ItemStack> holder){
        return new RuneHolder(holder);
    }

    public RuneHolder insertNewRuneSlots(int allowedSlots){
        var newList = new ArrayList<ItemStack>();
        for(int i = 0; i < allowedSlots; i++) newList.add(ItemStack.EMPTY);
        return new RuneHolder(newList);
    }

    public void serialise(RegistryFriendlyByteBuf friendlyByteBuf){
        ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(friendlyByteBuf, runeSlots);
    }

    public static RuneHolder getRuneholder(ItemStack itemStack){
        return itemStack.has(RUNE_HOLDER) ? itemStack.get(RUNE_HOLDER) : DEFAULT;
    }

    public static void createNewRuneSlots(ItemStack itemStack, int runeSlots){
        itemStack.update(RUNE_HOLDER, RuneHolder.DEFAULT, data -> data.insertNewRuneSlots(runeSlots));
    }

    public static void updateRuneSlots(ItemStack itemStack, List<ItemStack> upgrades){
        itemStack.update(RUNE_HOLDER, RuneHolder.DEFAULT, data -> data.insertNewHolder(upgrades));
    }

    public static RuneHolder deserialise(RegistryFriendlyByteBuf friendlyByteBuf){
        return new RuneHolder(
            ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(friendlyByteBuf)
        );
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, RuneHolder> STREAM_CODEC = StreamCodec.ofMember(
        RuneHolder::serialise,
        RuneHolder::deserialise
    );

    public static final Codec<RuneHolder> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.list(ItemStack.OPTIONAL_CODEC).fieldOf("rune_slots").forGetter(RuneHolder::runeSlots)
            ).apply(instance, RuneHolder::new)
    );

    public static RuneHolder makeRuneSlots(int slots){
        var runeSlots = RuneHolder.DEFAULT;
        return runeSlots.insertNewRuneSlots(slots);
    }
}
