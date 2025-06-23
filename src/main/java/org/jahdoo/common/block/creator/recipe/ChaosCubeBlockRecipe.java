package org.jahdoo.common.block.creator.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jahdoo.common.block.creator.CreatorEntity;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.ComponentReg;

import java.util.List;

public class ChaosCubeBlockRecipe implements CreatorRecipes {

    @Override
    public boolean canCraft(List<ItemStack> inputItems, CreatorEntity creator) {
        var core = 0;
        var eye = 0;

        for (var inputItem : inputItems) {
            if(inputItem.is(BlockReg.MODULAR_CHAOS_CUBE.get().asItem())) core++;
            if(inputItem.is(Items.DIAMOND)) eye++;
        }

        System.out.println(core);
        return core == 1 && eye == 7 && creator.getHolder() != null;
    }

    @Override
    public ItemStack result(CreatorEntity creator) {
        var stack = new ItemStack(BlockReg.MODULAR_CHAOS_CUBE.get().asItem());
        stack.set(ComponentReg.ABILITY_HOLDER, creator.getHolder());
        return stack;
    }

    @Override
    public String recipeId() {
        return "chaos_cube_with_holder";
    }

    @Override
    public int nexiteCost() {
        return 64;
    }

}
