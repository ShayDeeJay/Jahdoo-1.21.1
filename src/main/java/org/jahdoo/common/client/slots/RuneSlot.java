package org.jahdoo.common.client.slots;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jahdoo.ascension.attachments.PlayerWallet;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.block.rune_table.RuneTableEntity;
import org.jahdoo.common.block.rune_table.RuneTableMenu;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.items.runes.RuneItem;
import org.jahdoo.common.items.runes.rune_data.JahdooGearData;
import org.jahdoo.common.items.runes.rune_data.RuneHelpers;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.RuneReg;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jahdoo.common.items.runes.rune_data.RuneHelpers.getCostFromRune;
import static org.jahdoo.common.registers.ComponentReg.JAHDOO_GEAR_DATA;

public class RuneSlot extends SlotItemHandler {

    private final int maxStackSize;
    private final AbstractBEInventory entity;
    private RuneTableMenu menu;
    private boolean isActive = true;

    public RuneSlot(
        IItemHandler inputItemHandler,
        int index,
        int xPosition,
        int yPosition,
        AbstractBEInventory entity,
        RuneTableMenu menu,
        int maxStackSize
    ) {
        super(inputItemHandler, index, xPosition, yPosition);
        this.entity = entity;
        this.maxStackSize = maxStackSize;
        this.menu = menu;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return maxStackSize == 0 ? 64 : maxStackSize;
    }

    @Override
    public boolean isActive() {
        return menu.hideSlot;
    }

    @Override
    public boolean isHighlightable() {
        return canPlace(menu.getCarried(), entity);
    }

    @Override
    public void setChanged() {
        if(this.entity == null) return;
        var getAllSlots = this.entity.inputItemHandler.getStackInSlot(0);
        var getData = getAllSlots.get(JAHDOO_GEAR_DATA);

        if (getData != null) {
            var index = new AtomicInteger(4);
            var list = new ArrayList<ItemStack>();
            for (ItemStack ignored : getData.runeSlots()) {
                list.add(this.entity.inputItemHandler.getStackInSlot(index.get()));
                index.set(index.get() + 1);
            }
            JahdooGearData.updateRuneSlots(getAllSlots, list);
        }
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        var level = player.level();
        var pos = entity.getBlockPos();
        Helpers.getSoundWithPosition(level, pos, SoundEvents.VAULT_INSERT_ITEM, 1F, 1.6F);
        Helpers.getSoundWithPosition(level, pos, SoundReg.UNLOCK_NOTIFICATION.get(), 1F, 1.4F);
        super.onTake(player, stack);
    }

    @Override
    public Optional<ItemStack> tryRemove(int count, int decrement, Player player) {
        if(entity instanceof RuneTableEntity runeTable){
            var coreCost = removeRuneCost(getItem());
            var canRemove = runeTable.checkAndChargeCores(coreCost, true);
            var hasCoins = PlayerWallet.CurrencyConverter.checkAndPurchase(PlayerWallet.CurrencyConverter.convertToCoins(removeCurrencyCost(getItem())), player);
            if(canRemove && hasCoins) {
                return super.tryRemove(count, decrement, player);
            } else {
                Helpers.getSoundWithPosition(player.level(), runeTable.getBlockPos(), SoundReg.REJECT.get(), 0.4F, 1F);
            }
        }
        return Optional.empty();
    }

    public static Item removeRuneCost(ItemStack stack) {
        var runeData = RuneHelpers.getRuneData(stack);
        var runeReg = RuneReg.getRuneFromId(runeData.name());
        var rarity = runeReg.runeRarity().getId();
        return SharedUI.getCore().get(Math.min(rarity/2, 2));
    }

    public static int removeCurrencyCost(ItemStack stack) {
        var runeData = RuneHelpers.getRuneData(stack);
        var runeReg = RuneReg.getRuneFromId(runeData.name());
        var rarity = runeReg.runeRarity().getId()+1;
        var baseCost = (rarity * 10) * 25;
        var tierMultiplier = ((runeData.tier()+1) * 2);

        return baseCost * tierMultiplier;
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        var getEntity = entity;
        var level = getEntity.getLevel();
        var pos = getEntity.getBlockPos();
        if (level == null || level.isClientSide) return false;

        if (itemStack.getItem() instanceof RuneItem && isActive && getItem().isEmpty()) {
            if (canPlace(itemStack, getEntity)) {
                var cost = getCostFromRune(itemStack);
                var potential = JahdooGearData.getItemPotential(getEntity.inputItemHandler.getStackInSlot(0));
                Helpers.getSoundWithPosition(level, pos, SoundEvents.VAULT_INSERT_ITEM, 0.4F, 1.2F);
                Helpers.getSoundWithPosition(level, pos, SoundReg.UNLOCK_NOTIFICATION.get(), 1F, 0.6F);
                JahdooGearData.updateRefinementPotential(getEntity.inputItemHandler.getStackInSlot(0), potential - cost);
                return true;
            }
        }

        if(getItem().isEmpty()) Helpers.getSoundWithPosition(level, pos, SoundReg.REJECT.get(), 0.4F, 1F);
        return false;
    }

    private static boolean canPlace(ItemStack itemStack, AbstractBEInventory getEntity) {
        if(!(itemStack.getItem() instanceof RuneItem) || getEntity == null) return false;

        var cost = getCostFromRune(itemStack);
        var potential = JahdooGearData.getItemPotential(getEntity.inputItemHandler.getStackInSlot(0));
        return potential >= cost;
    }
}