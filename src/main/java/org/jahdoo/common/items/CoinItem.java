package org.jahdoo.common.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.ascension.attachments.PlayerWallet;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.IItemEntityBehaviour;
import org.jahdoo.common.networking.server2client.WalletSyncS2CP;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.SoundReg;

public class CoinItem extends Item implements IItemEntityBehaviour {

    public CoinItem() { super(new Properties()); }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        addCoinToWallet(stack, level, entity);
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    private static void addCoinToWallet(ItemStack stack, Level level, Entity entity) {
        if(level instanceof ServerLevel serverLevel){
            var data = stack.get(DataComponents.CUSTOM_MODEL_DATA);
            var getWallet = entity.getData(AttachmentReg.PLAYER_WALLET);
            var count = stack.getCount();

            if (data != null) {
                switch (data.value()) {
                    case 1 -> getWallet.addSilver(count);
                    case 2 -> getWallet.addGold(count);
                    case 3 -> getWallet.addPlatinum(count);
                }
            } else getWallet.addBronze(count);

            if (entity instanceof ServerPlayer player) {
                PacketDistributor.sendToPlayer(player, new WalletSyncS2CP(getWallet.getWallet()));
            }

            Helpers.getSoundWithPosition(serverLevel, entity.blockPosition(), SoundReg.COIN.get());
        }
        stack.setCount(0);
    }

    @Override
    public Component getName(ItemStack stack) {
        var data = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        if(data == null) return Component.literal("Bronze Coin");

        return Component.literal(
            switch (data.value()) {
                case 1 -> "Silver";
                case 2 -> "Gold";
                default -> "Platinum";
            } + " Coin"
        );
    }

    @Override
    public boolean onItemInteraction(ItemEntity itemEntity, LivingEntity livingEntity) {
        addCoinToWallet(itemEntity.getItem(), itemEntity.level(), livingEntity);
        return true;
    }
}
