package org.jahdoo.block.crafter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.components.WandData;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.items.wand.WandSlotManager;
import org.jahdoo.registers.DataComponentRegistry;

import static org.jahdoo.registers.AttributesRegister.*;
import static org.jahdoo.registers.AttributesRegister.MANA_COST_REDUCTION;

public class ResultRecipeConfig {


//    private void spawnWand(Level level, BlockPos blockPos){
//        if(getCurrentRecipe(level).isEmpty()) return;
//        ItemStack itemStack = this.getOutputResult();
//        if(itemStack.getItem() instanceof WandItem){
//            WandSlotManager.createNewSlotsForWand(itemStack, 3);
//            itemStack.update(DataComponentRegistry.WAND_DATA.get(), WandData.DEFAULT, wandData -> wandData.setAbilitySlots(5));
//            replaceOrAddAttribute(itemStack, COOLDOWN_REDUCTION_PREFIX, COOLDOWN_REDUCTION, 50, EquipmentSlot.MAINHAND);
//            replaceOrAddAttribute(itemStack, MANA_COST_REDUCTION_PREFIX, MANA_COST_REDUCTION, 10, EquipmentSlot.MAINHAND);
//            this.spawnSuccessfulWandCraftParticles(level, blockPos, itemStack);
//            this.outputItemHandler.setStackInSlot(0, itemStack);
//        }
//    }

}
