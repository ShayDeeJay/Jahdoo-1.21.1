package org.jahdoo.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.ascension.attachments.PlayerWallet;
import org.jahdoo.common.registers.BlockReg;

import static org.jahdoo.ascension.attachments.PlayerWallet.updateWallet;
import static org.jahdoo.common.block.TrialPortalBlock.DIMENSION_KEY;
import static org.jahdoo.common.block.TrialPortalBlock.KEY_TRADING_POST;
import static org.jahdoo.common.registers.ItemReg.*;

public class CoreItem extends Item  {
    public CoreItem() {
        super(new Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var hand = player.getItemInHand(usedHand);

        var block = BlockReg.TRAIL_PORTAL.get();
        var item = hand.getItem();
        if(item == AUGMENT_HYPER_CORE.get()){
            var setBlockState = block.defaultBlockState().setValue(DIMENSION_KEY, KEY_TRADING_POST);
            level.setBlockAndUpdate(BlockPos.containing(player.position()), setBlockState);
//                LevelGenerator.debugLevels(serverLevel);
//                for (var allEntity : serverLevel.getAllEntities()) {
//                    if(!(allEntity instanceof Player)){
//                        allEntity.kill();
//                    }
//                }
            return InteractionResultHolder.success(hand);
        } else if (item == ADVANCED_AUGMENT_CORE.get()){
            if(player.isShiftKeyDown()){
                if(level instanceof ServerLevel serverLevel){
                    for (var allEntity : serverLevel.getAllEntities()) {
                        if (!(allEntity instanceof Player)) {
                            allEntity.kill();
                        }
                    }
                }
            } else {
                updateWallet(player, new PlayerWallet.CurrencyConverter(0, 0, 0, 0));
            }
            return InteractionResultHolder.success(hand);

        } else if (item == AUGMENT_CORE.get()){
            return InteractionResultHolder.success(hand);
        }

        return InteractionResultHolder.pass(hand);
    }
}
