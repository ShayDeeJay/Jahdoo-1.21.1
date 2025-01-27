package org.jahdoo.attachments.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.attachments.AbstractAttachment;
import org.jahdoo.items.runes.RuneItem;
import org.jahdoo.items.runes.rune_data.RuneData;
import org.jahdoo.items.wand.WandItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.jahdoo.items.runes.rune_data.RuneData.*;

public class SaveData implements AbstractAttachment {

    private final List<ItemStack> itemStacks = new ArrayList<>();

    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        var localTag = new CompoundTag();
        for(int i = 0; i < itemStacks.size(); i++){
            localTag.put("item" + i, itemStacks.get(i).save(provider));
        }
        nbt.put("itemStacks", localTag);
    }

    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        var allKeys = nbt.getCompound("itemStacks");
        for (String keys : allKeys.getAllKeys()) {
            Optional<ItemStack> itemStack = ItemStack.parse(provider, allKeys.get(keys));
            this.itemStacks.add(itemStack.orElse(ItemStack.EMPTY));
        }
    }

    public void addAllItems(Player player){
        var list = player.getInventory().items;
        var item = list.stream().filter(RuneHelpers::hasDestinyBond).toList();
        var wandOnly = item.stream().filter(itemStack -> !itemStack.isEmpty() && !(itemStack.getItem() instanceof RuneItem)).toList();
        this.itemStacks.addAll(wandOnly);
        item.forEach(items -> player.getInventory().removeItem(items));
    }

    public void takeAllItems(Player player){
        for (ItemStack itemStack : this.itemStacks) {
            if(itemStack.getItem() instanceof ArmorItem armorItem){
                var slot = armorItem.getEquipmentSlot();
                player.setItemSlot(slot, itemStack);
//                if(!player.hasItemInSlot(slot)){
//                } else player.addItem(itemStack);
            } else {
                player.addItem(itemStack);
            }
        }
        this.itemStacks.clear();
    }

}
