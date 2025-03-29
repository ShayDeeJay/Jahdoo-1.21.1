package org.jahdoo.ascension.attachments;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.CustomModelData;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.items.runes.RuneItem;
import org.jahdoo.common.items.runes.rune_data.RuneHelpers;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.ItemReg;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class SaveData implements IAttachment {

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
        for (var keys : allKeys.getAllKeys()) {
            var itemStack = ItemStack.parse(provider, allKeys.get(keys));
            this.itemStacks.add(itemStack.orElse(ItemStack.EMPTY));
        }
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

        if(player.level() instanceof CustomLevel customLevel){
            getLostItemReceipt(player, customLevel);
        }
    }

    private void getLostItemReceipt(Player player, ServerLevel serverLevel) {
        var receipt = new ItemStack(ItemReg.RECOVERY_RECEIPT);
        var list = new ArrayList<ItemStack>();

        //Save inventory items
        for (var item : player.getInventory().items) {
            if(!item.isEmpty()) list.add(item);
        }

        //Save curious items
        var curioSlotsItems = CuriosApi.getCuriosInventory(player);
        if(curioSlotsItems.isPresent()){
            var withSlots = curioSlotsItems.get().getEquippedCurios();
            var slots = withSlots.getSlots();
            for (int i = 0; i < slots; i++) {
                var getCurioItem = withSlots.getStackInSlot(i);

                if(!getCurioItem.isEmpty()) list.add(getCurioItem);
            }
        }

        //Save armor slot items
        for (var allSlot : player.getArmorSlots()) {
            if(!allSlot.isEmpty()) list.add(allSlot);
        }

        //Save main hand only as using sand hand slots dupes main hand item
        var offhand = player.getOffhandItem();
        if(!offhand.isEmpty()) list.add(offhand);

        if(!list.isEmpty()){//Add as bundle for now as item that saves may have tooltip
            receipt.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(list));
            var data = serverLevel.getData(AttachmentReg.INSTANCE_DATA);
            var value = new CustomModelData(
                switch (data.getDifficulty()){
                    case Helpers.MEDIUM -> 2;
                    case Helpers.HARD -> 3;
                    default -> 1;
                }
            );
            receipt.set(DataComponents.CUSTOM_MODEL_DATA, value);
            this.itemStacks.add(receipt);
        }
    }

}
