package org.jahdoo.common.items;

import kotlin.Pair;
import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.jahdoo.trial_nexus.level_manager.PlayerHomeDim;

import java.util.ArrayList;
import java.util.List;

public class Deed extends BaseJahdooItem {

    public Deed() {
        super(new Properties());
    }

    @Override
    public Pair<String, List<ItemStack>> getAdditional() {
        var getItems = new ArrayList<ItemStack>();
        JahdooItem.addItems(2, new ItemStack(ItemReg.CHARGED_ADVANCED_AUGMENT_CORE), getItems);
        JahdooItem.addItems(2, new ItemStack(Items.GRASS_BLOCK), getItems);
        JahdooItem.addItems(4, new ItemStack(ItemReg.LISITE_SHARD), getItems);

        return new Pair<>("test test test", getItems);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var itemInHand = player.getItemInHand(usedHand);
        if(level instanceof ServerLevel serverLevel){
            var blockPos = new BlockPos(0, 90, 1);
            var pos = blockPos.getCenter();
            if(player instanceof  ServerPlayer serverPlayer){
                var x = 10;
                if(!(level instanceof CustomLevel)){
                    var playerHome = PlayerHomeDim.getPlayerHome(player);
                    var findLevel = LevelGenerator.findLevel(playerHome, serverLevel);
                    var hasSpot = itemInHand.get(ComponentReg.BLOCK_POS);

                    if(findLevel.isPresent()){
                        var correctPost = blockPos;
                        if(hasSpot != null){
                            correctPost = hasSpot;
                        }
                        var path = player.level().dimension().location().toString();
                        itemInHand.set(ComponentReg.BLOCK_POS, player.blockPosition());
                        itemInHand.set(ComponentReg.ID, path);
                        serverPlayer.teleportTo(findLevel.get(), correctPost.getX(), correctPost.getY(), correctPost.getZ(), player.yRotO, player.xRotO);
                    } else {
                        var transition = PlayerHomeDim.generateNewHome(serverLevel, player, blockPos);
                        var nLevel = transition.newLevel();
                        serverPlayer.setRespawnPosition(nLevel.dimension(), blockPos, 0, false, false);
                    }


                } else {
                    var playerHome = itemInHand.get(ComponentReg.ID);
                    var blockPos1 = itemInHand.get(ComponentReg.BLOCK_POS);

                    var levels = serverLevel.getServer().getAllLevels();
                    ServerLevel levelGet = null;

                    for (var level1 : levels) {
                        var isLevel = level1.dimension().location().toString().equals(playerHome);
                        if(isLevel){
                            levelGet = level1;
                        }
                    }

                    itemInHand.set(ComponentReg.BLOCK_POS, player.blockPosition());

                    if(levelGet != null){
                        serverPlayer.teleportTo(levelGet, blockPos1.getX(), blockPos1.getY(), blockPos1.getZ(), player.yRotO, player.xRotO);
                    }
                }


            }
            itemInHand.shrink(1);
            return InteractionResultHolder.success(itemInHand);
        }

        return InteractionResultHolder.fail(itemInHand);
    }
}
