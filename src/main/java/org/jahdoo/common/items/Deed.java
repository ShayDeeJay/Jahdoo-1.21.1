package org.jahdoo.common.items;

import kotlin.Pair;
import net.blay09.mods.waystones.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.shaydee.shaydeeapi.helpers.ItemHelpers;

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
                var findLevel = LevelGenerator.findLevel(LevelGenerator.getPlayerHome(player), serverLevel);
                if(findLevel.isPresent()){
                    serverPlayer.teleportTo(findLevel.get(), pos.x, pos.y, pos.z, 180, 0);
                } else {
                    var transition = LevelGenerator.generateNewHome(serverLevel, player, blockPos);
                    var nLevel = transition.newLevel();
                    ItemHelpers.throwOrAddItem(player, new ItemStack(ModItems.warpStone));

                    serverPlayer.setRespawnPosition(nLevel.dimension(), blockPos, 0, false, false);
                }
            }
            itemInHand.shrink(1);
            return InteractionResultHolder.success(itemInHand);
        }

        return InteractionResultHolder.fail(itemInHand);
    }
}
