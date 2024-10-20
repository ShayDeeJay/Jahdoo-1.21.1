package org.jahdoo.capabilities.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.capabilities.AbstractAttachment;
import org.jahdoo.items.wand.WandItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.jahdoo.registers.AttachmentRegister.BOUNCY_FOOT;
import static org.jahdoo.registers.DataComponentRegistry.INFINITE_ITEM;

public class SaveData implements AbstractAttachment {

    private final List<ItemStack> itemStacks = new ArrayList<>();

    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        var localTag = new CompoundTag();
        if(!this.itemStacks.isEmpty()){
            for (ItemStack itemStack : this.itemStacks){
                System.out.println(itemStack);
                localTag.put(itemStack.getDescriptionId(), itemStack.save(provider));
            }
            System.out.println(this.itemStacks);
        }
        nbt.put("itemStacks", localTag);
    }

    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        var allKeys = nbt.getCompound("itemStacks");
        for (String keys : allKeys.getAllKeys()) {
            Optional<ItemStack> itemStack = ItemStack.parse(provider, allKeys.get(keys));
            System.out.println(itemStack);
            this.itemStacks.add(itemStack.orElse(ItemStack.EMPTY));
        }

    }

    public void addAllItems(Player player){
        var list = player.getInventory().items;
        var wands = list.stream().filter(itemStack -> itemStack.has(INFINITE_ITEM) && Boolean.TRUE.equals(itemStack.get(INFINITE_ITEM.value()))).toList();
        this.itemStacks.addAll(wands);
        wands.forEach(items -> player.getInventory().removeItem(items));
        System.out.println(this.itemStacks);
    }

    public void takeAllItems(Player player){
        System.out.println(this.itemStacks);
        for (ItemStack itemStack : this.itemStacks) player.addItem(itemStack);
        this.itemStacks.clear();
    }

}
