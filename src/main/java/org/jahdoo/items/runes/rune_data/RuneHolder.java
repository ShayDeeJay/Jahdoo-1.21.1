package org.jahdoo.items.runes.rune_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.items.wand.WandData;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.registers.DataComponentRegistry.RUNE_HOLDER;
import static org.jahdoo.registers.DataComponentRegistry.WAND_DATA;

public record RuneHolder(
    List<ItemStack> runeSlots,
    int refinementPotential
){
    public static RuneHolder DEFAULT = new RuneHolder(new ArrayList<>(), 0);

    public RuneHolder insertNewHolder(List<ItemStack> holder){
        return new RuneHolder(holder, this.refinementPotential);
    }

    public RuneHolder insertNewRuneSlots(int allowedSlots, int refinementPotential){
        var newList = new ArrayList<ItemStack>();
        for(int i = 0; i < allowedSlots; i++) newList.add(ItemStack.EMPTY);
        return new RuneHolder(newList, refinementPotential);
    }

    public static RuneHolder getRuneholder(ItemStack itemStack){
        return itemStack.has(RUNE_HOLDER) ? itemStack.get(RUNE_HOLDER) : DEFAULT;
    }

    public static void createNewRuneSlots(ItemStack itemStack, int runeSlots, int refinementPotential){
        itemStack.update(RUNE_HOLDER, RuneHolder.DEFAULT, data -> data.insertNewRuneSlots(runeSlots, refinementPotential));
    }

    public static void createRefinementPotential(ItemStack itemStack, int refinementPotential){
        itemStack.update(RUNE_HOLDER, DEFAULT, data -> data.setRefinementPotential(refinementPotential));
    }

    private RuneHolder chargeRefinementPotential(int refinementPotential){
        return new RuneHolder(this.runeSlots, this.refinementPotential - refinementPotential);
    }

    public static int potential(ItemStack itemStack){
        return itemStack.getOrDefault(RUNE_HOLDER, DEFAULT).refinementPotential();
    }

    public static void updateRuneSlots(ItemStack itemStack, List<ItemStack> upgrades){
        itemStack.update(RUNE_HOLDER, RuneHolder.DEFAULT, data -> data.insertNewHolder(upgrades));
    }

    public RuneHolder setRefinementPotential(int refinementPotential){
        return new RuneHolder(this.runeSlots, refinementPotential);
    }

    public static RuneHolder deserialise(RegistryFriendlyByteBuf friendlyByteBuf){
        return new RuneHolder(
            ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(friendlyByteBuf),
            friendlyByteBuf.readInt()
        );
    }

    public void serialise(RegistryFriendlyByteBuf friendlyByteBuf){
        ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(friendlyByteBuf, runeSlots);
        friendlyByteBuf.writeInt(refinementPotential);
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, RuneHolder> STREAM_CODEC = StreamCodec.ofMember(
        RuneHolder::serialise,
        RuneHolder::deserialise
    );

    public static final Codec<RuneHolder> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.list(ItemStack.OPTIONAL_CODEC).fieldOf("rune_slots").forGetter(RuneHolder::runeSlots),
            Codec.INT.fieldOf("refinement_potential").forGetter(RuneHolder::refinementPotential)
        ).apply(instance, RuneHolder::new)
    );

    public static RuneHolder makeRuneSlots(int slots, int refinementPotential){
        var runeSlots = RuneHolder.DEFAULT;
        return runeSlots.insertNewRuneSlots(slots, refinementPotential);
    }
}
