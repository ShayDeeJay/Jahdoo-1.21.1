package org.jahdoo.common.items;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.trial_nexus.attachments.RunData;
import org.jahdoo.trial_nexus.utils.IItemEntityBehaviour;
import org.jahdoo.common.networking.server2client.WalletSyncS2CP;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.SoundReg;

import static net.neoforged.neoforge.network.PacketDistributor.sendToPlayer;
import static org.jahdoo.trial_nexus.attachments.PlayerWallet.CoinProperties;
import static org.jahdoo.trial_nexus.utils.Helpers.getSoundWithPosition;
import static org.jahdoo.trial_nexus.utils.Helpers.withStyleComponent;
import static org.jahdoo.trial_nexus.utils.LocalLootBeamData.COIN;
import static org.shaydee.loot_beams_neoforge.data_component.DataComponentsReg.INSTANCE;

public class CoinItem extends Item implements IItemEntityBehaviour {

    public CoinItem() {
        super(new Properties().component(INSTANCE.getLOOT_BEAM_DATA(), COIN));
    }

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
                sendToPlayer(player, new WalletSyncS2CP(getWallet.getWallet()));
            }

            getSoundWithPosition(serverLevel, entity.blockPosition(), SoundReg.COIN.get(), 1, 0.8F);
            
            if(level instanceof CustomLevel cLevel && entity instanceof LivingEntity lEntity) {
                RunData.incrementCoin(cLevel, lEntity, data == null ? 0 : data.value(), count);
            }
        }

        stack.setCount(0);
    }

    @Override
    public Component getName(ItemStack stack) {
        var data = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        if(data == null) return withStyleComponent("Bronze Coin", CoinProperties.BRONZE.getTextColour());
        var value = switch (data.value()) {
            case 1 -> CoinProperties.SILVER;
            case 2 -> CoinProperties.GOLD;
            default -> CoinProperties.PLATINUM;
        };

        return withStyleComponent(value.getSerializedName() + " Coin", value.getTextColour());
    }

    @Override
    public boolean onItemInteraction(ItemEntity itemEntity, LivingEntity livingEntity) {
        addCoinToWallet(itemEntity.getItem(), itemEntity.level(), livingEntity);
        return true;
    }
}
