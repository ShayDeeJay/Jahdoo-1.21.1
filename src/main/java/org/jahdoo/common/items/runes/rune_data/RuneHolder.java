package org.jahdoo.common.items.runes.rune_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.common.registers.ComponentReg.RUNE_HOLDER;

public record RuneHolder(
    List<ItemStack> runeSlots,
    List<Integer> repairSlots,
    int refinementPotential
){
    public static RuneHolder DEFAULT = new RuneHolder(new ArrayList<>(), new ArrayList<>(), 0);

    public RuneHolder setRuneSlots(List<ItemStack> runeSlots){
        return new RuneHolder(runeSlots, this.repairSlots, this.refinementPotential);
    }

    public RuneHolder setRepairSlots(List<Integer> repairSlots){
        return new RuneHolder(this.runeSlots, repairSlots, this.refinementPotential);
    }

    public RuneHolder setRefinementPotential(int refinementPotential){
        return new RuneHolder(this.runeSlots, this.repairSlots, refinementPotential);
    }

    public RuneHolder insertNewRuneSlots(int allowedRuneSlots, int allowedRepairSlots, int refinementPotential){
        var runeSlots = new ArrayList<ItemStack>();
        var repairSlots = new ArrayList<Integer>();

        for(int i = 0; i < allowedRuneSlots; i++) runeSlots.add(ItemStack.EMPTY);
        for(int i = 0; i < allowedRepairSlots; i++) repairSlots.add(1);

        return new RuneHolder(runeSlots, repairSlots, refinementPotential);
    }

    public static boolean canUpgrade(ItemStack itemStack){
        var holder = getRuneholder(itemStack);
        return holder.repairSlots.contains(1);
    }

    public static int checkAndRepair(ItemStack itemStack){
        var holder = getRuneholder(itemStack);
        var originalSlots = holder.repairSlots;
        var hasRepairSlots = originalSlots.contains(1);
        var newRepair = new ArrayList<Integer>();

        if(hasRepairSlots) {
            var foundSlot = false;
            var index = 0;
            for (var repairSlot : originalSlots) {
                if(repairSlot == 1 && !foundSlot) {
                    newRepair.add(0);
                    foundSlot = true;
                    index = originalSlots.indexOf(repairSlot);
                } else {
                    newRepair.add(repairSlot);
                }
            }
            updateRepairSlots(itemStack, newRepair);
            return index;
        }

        return -1;
    }

    public static int totalRepairs(ItemStack itemStack){
        var holder = getRuneholder(itemStack);
        var originalSlots = holder.repairSlots;
        var hasRepairSlots = originalSlots.contains(1);

        if(hasRepairSlots) {
            var index = 0;
            for (var repairSlot : originalSlots) {
                if(repairSlot == 1) return originalSlots.indexOf(repairSlot);
            }
            return index;
        }

        return -1;
    }

    public static RuneHolder getRuneholder(ItemStack itemStack){
        return itemStack.has(RUNE_HOLDER) ? itemStack.get(RUNE_HOLDER) : DEFAULT;
    }

    public static void createNewRuneSlots(ItemStack itemStack, int runeSlots, int repairSlots, int refinementPotential){
        var newHolder = RuneHolder.DEFAULT;
        itemStack.set(RUNE_HOLDER, newHolder.insertNewRuneSlots(runeSlots, repairSlots, refinementPotential));
    }

    public static int getItemPotential(ItemStack itemStack){
        return itemStack.getOrDefault(RUNE_HOLDER, DEFAULT).refinementPotential();
    }

    public static void updateRuneSlots(ItemStack itemStack, List<ItemStack> upgrades){
        itemStack.update(RUNE_HOLDER, RuneHolder.DEFAULT, data -> data.setRuneSlots(upgrades));
    }

    public static void updateRefinementPotential(ItemStack itemStack, int refinementPotential){
        itemStack.update(RUNE_HOLDER, DEFAULT, data -> data.setRefinementPotential(Math.max(refinementPotential, 0)));
    }

    public static void updateRepairSlots(ItemStack itemStack, List<Integer> upgrades){
        itemStack.update(RUNE_HOLDER, RuneHolder.DEFAULT, data -> data.setRepairSlots(upgrades));
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, RuneHolder> STREAM_CODEC = StreamCodec.ofMember(
        RuneHolder::serialise,
        RuneHolder::deserialise
    );

    public void serialise(RegistryFriendlyByteBuf friendlyByteBuf){
        ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(friendlyByteBuf, runeSlots);
        friendlyByteBuf.writeCollection(repairSlots, FriendlyByteBuf::writeInt);
        friendlyByteBuf.writeInt(refinementPotential);
    }

    private static RuneHolder deserialise(RegistryFriendlyByteBuf friendlyByteBuf){
        return new RuneHolder(
            ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(friendlyByteBuf),
            friendlyByteBuf.readList(FriendlyByteBuf::readInt),
            friendlyByteBuf.readInt()
        );
    }

    public static final Codec<RuneHolder> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.list(ItemStack.OPTIONAL_CODEC).fieldOf("rune_slots").forGetter(RuneHolder::runeSlots),
            Codec.list(Codec.INT).fieldOf("repair_slots").forGetter(RuneHolder::repairSlots),
            Codec.INT.fieldOf("refinement_potential").forGetter(RuneHolder::refinementPotential)
        ).apply(instance, RuneHolder::new)
    );
}
