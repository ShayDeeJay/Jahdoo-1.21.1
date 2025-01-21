package org.jahdoo.items;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.attachments.player_abilities.ChallengeAltarData;
import org.jahdoo.registers.AttachmentRegister;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.challenge.LevelGenerator;

import static org.jahdoo.block.TrialPortalBlock.*;
import static org.jahdoo.registers.ItemsRegister.*;

public class CoreItem extends Item  {
    public CoreItem() {
        super(new Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var hand = player.getItemInHand(player.getUsedItemHand());
        if(level instanceof ServerLevel serverLevel){
            var block = BlocksRegister.TRAIL_PORTAL.get();


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
//                System.out.println(level.getData());
//                System.out.println(level.getData(AttachmentRegister.CHALLENGE_ALTAR.get()));
//                LevelGenerator.createNewWorld(player, serverLevel, ChallengeAltarData.DEFAULT);
            } else if (item == ADVANCED_AUGMENT_CORE.get()){
                var setBlockState = block.defaultBlockState().setValue(DIMENSION_KEY, KEY_TRIAL);
                level.setBlockAndUpdate(BlockPos.containing(player.position()), setBlockState);
            } else if (item == AUGMENT_CORE.get()){
                LevelGenerator.removeCustomLevels(serverLevel);
            }
        }

        return InteractionResultHolder.fail(hand);
    }
}
