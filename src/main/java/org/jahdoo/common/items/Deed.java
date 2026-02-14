package org.jahdoo.common.items;

import kotlin.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.jahdoo.common.registers.ItemReg;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.trial_nexus.level_manager.StructureManager.placeStructure;

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
        if(level instanceof ServerLevel serverLevel){
            placeStructure(serverLevel, player.blockPosition().below(29).north(73).west(72), new StructurePlaceSettings() , "base");

            return InteractionResultHolder.success(ItemStack.EMPTY);
        }

        return InteractionResultHolder.fail(player.getItemInHand(usedHand));
    }
}
