package org.jahdoo.common.items;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BaseJahdooBlockItem extends BlockItem implements JahdooItem {

    public BaseJahdooBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public String descriptionId(ItemStack stack) {
        return "description."+getBlock().getDescriptionId();
    }

}
