package org.jahdoo.attachments.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.attachments.AbstractAttachment;
import org.jahdoo.items.runes.RuneItem;
import org.jahdoo.items.runes.rune_data.RuneData;
import org.jahdoo.items.wand.WandItem;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    public void addAllItems(Player player) {
        var mainInventoryItems = player.getInventory().items;

        var additionalSlotsItems = StreamSupport
            .stream(player.getAllSlots().spliterator(), false)
            .toList();

        var filteredMainInventory = mainInventoryItems
            .stream()
            .filter(RuneHelpers::hasDestinyBond)
            .toList();

        var filteredAdditionalSlots = additionalSlotsItems
            .stream()
            .filter(RuneHelpers::hasDestinyBond)
            .toList();

        var allFilteredItems = new ArrayList<>(filteredMainInventory);

        allFilteredItems.addAll(filteredAdditionalSlots);

        var curioSlotsItems = CuriosApi.getCuriosInventory(player);
        if(curioSlotsItems.isPresent()){
            var withSlots = curioSlotsItems.get().getEquippedCurios();
            var slots = withSlots.getSlots();
            for (int i = 0; i < slots; i++) {
                var getCurioItem = withSlots.getStackInSlot(i);
                if(RuneHelpers.hasDestinyBond(getCurioItem)){
                    allFilteredItems.add(getCurioItem);
                }
            }
        }

        var wandItems = allFilteredItems
            .stream()
            .distinct()
            .filter(itemStack -> !itemStack.isEmpty() && !(itemStack.getItem() instanceof RuneItem))
            .toList();

        this.itemStacks.addAll(wandItems);
        allFilteredItems.forEach(player.getInventory()::removeItem);
    }

    public void takeAllItems(Player player){
        for (ItemStack itemStack : this.itemStacks) {
            var item = itemStack.getItem();
            if (item instanceof ArmorItem armorItem) {
                var slot = armorItem.getEquipmentSlot();
                var isSlotEmpty = !player.hasItemInSlot(slot);

                if(isSlotEmpty) player.setItemSlot(slot, itemStack); else player.addItem(itemStack);
            } else {
                player.addItem(itemStack);
            }
        }
        this.itemStacks.clear();
    }

}
