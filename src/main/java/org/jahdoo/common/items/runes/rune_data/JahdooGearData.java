package org.jahdoo.common.items.runes.rune_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.common.registers.ComponentReg.JAHDOO_GEAR_DATA;

public record JahdooGearData(
    List<ItemStack> runeSlots,
    List<Integer> repairSlots,
    int refinementPotential
){
    public static JahdooGearData DEFAULT = new JahdooGearData(new ArrayList<>(), new ArrayList<>(), 0);

    public JahdooGearData setRuneSlots(List<ItemStack> runeSlots){
        return new JahdooGearData(runeSlots, this.repairSlots, this.refinementPotential);
    }

    public JahdooGearData setRepairSlots(List<Integer> repairSlots){
        return new JahdooGearData(this.runeSlots, repairSlots, this.refinementPotential);
    }

    public JahdooGearData setRefinementPotential(int refinementPotential){
        return new JahdooGearData(this.runeSlots, this.repairSlots, refinementPotential);
    }

    public JahdooGearData insertNewRuneSlots(int allowedRuneSlots, int allowedRepairSlots, int refinementPotential){
        var runeSlots = new ArrayList<ItemStack>();
        var repairSlots = new ArrayList<Integer>();

        for(int i = 0; i < allowedRuneSlots; i++) runeSlots.add(ItemStack.EMPTY);
        for(int i = 0; i < allowedRepairSlots; i++) repairSlots.add(1);

        return new JahdooGearData(runeSlots, repairSlots, refinementPotential);
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

    public static JahdooGearData getRuneholder(ItemStack itemStack){
        return itemStack.has(JAHDOO_GEAR_DATA) ? itemStack.get(JAHDOO_GEAR_DATA) : DEFAULT;
    }

    public static void createNewRuneSlots(ItemStack itemStack, int runeSlots, int repairSlots, int refinementPotential){
        var newHolder = JahdooGearData.DEFAULT;
        itemStack.set(JAHDOO_GEAR_DATA, newHolder.insertNewRuneSlots(runeSlots, repairSlots, refinementPotential));
    }

    public static int getItemPotential(ItemStack itemStack){
        return itemStack.getOrDefault(JAHDOO_GEAR_DATA, DEFAULT).refinementPotential();
    }

    public static void updateRuneSlots(ItemStack itemStack, List<ItemStack> upgrades){
        itemStack.update(JAHDOO_GEAR_DATA, JahdooGearData.DEFAULT, data -> data.setRuneSlots(upgrades));
    }

    public static void updateRefinementPotential(ItemStack itemStack, int refinementPotential){
        itemStack.update(JAHDOO_GEAR_DATA, DEFAULT, data -> data.setRefinementPotential(Math.max(refinementPotential, 0)));
    }

    public static void updateRepairSlots(ItemStack itemStack, List<Integer> upgrades){
        itemStack.update(JAHDOO_GEAR_DATA, JahdooGearData.DEFAULT, data -> data.setRepairSlots(upgrades));
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, JahdooGearData> STREAM_CODEC = StreamCodec.ofMember(
        JahdooGearData::serialise,
        JahdooGearData::deserialise
    );

    public void serialise(RegistryFriendlyByteBuf friendlyByteBuf){
        ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(friendlyByteBuf, runeSlots);
        friendlyByteBuf.writeCollection(repairSlots, FriendlyByteBuf::writeInt);
        friendlyByteBuf.writeInt(refinementPotential);
    }

    private static JahdooGearData deserialise(RegistryFriendlyByteBuf friendlyByteBuf){
        return new JahdooGearData(
            ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(friendlyByteBuf),
            friendlyByteBuf.readList(FriendlyByteBuf::readInt),
            friendlyByteBuf.readInt()
        );
    }

    public static final Codec<JahdooGearData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.list(ItemStack.OPTIONAL_CODEC).fieldOf("rune_slots").forGetter(JahdooGearData::runeSlots),
            Codec.list(Codec.INT).fieldOf("repair_slots").forGetter(JahdooGearData::repairSlots),
            Codec.INT.fieldOf("refinement_potential").forGetter(JahdooGearData::refinementPotential)
        ).apply(instance, JahdooGearData::new)
    );
}
