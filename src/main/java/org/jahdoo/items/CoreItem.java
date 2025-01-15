package org.jahdoo.items;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.attachments.player_abilities.ChallengeAltarData;
import org.jahdoo.registers.AttachmentRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.challenge.LevelGenerator;

public class CoreItem extends Item  {
    public CoreItem() {
        super(new Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if(level instanceof ServerLevel serverLevel){
            if(player.getMainHandItem().getItem() == ItemsRegister.AUGMENT_HYPER_CORE.get()){
//                LevelGenerator.debugLevels(serverLevel);
                for (var allEntity : serverLevel.getAllEntities()) {
                    if(!(allEntity instanceof Player)){
                        allEntity.kill();
                    }
                }
//                System.out.println(level.getData(AttachmentRegister.CHALLENGE_ALTAR.get()));
//                LevelGenerator.createNewWorld(player, serverLevel, ChallengeAltarData.DEFAULT);
            } else {
                LevelGenerator.removeCustomLevels(serverLevel);
            }
        }

        return InteractionResultHolder.fail(player.getMainHandItem());
    }
}
