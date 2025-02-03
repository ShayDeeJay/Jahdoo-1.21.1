package org.jahdoo.mixin;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import org.jahdoo.client.overlays.StatScreen;
import org.jahdoo.utils.ModHelpers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static net.neoforged.neoforge.client.DimensionTransitionScreenManager.getScreen;
import static org.jahdoo.utils.ModHelpers.*;

@Mixin(InventoryMenu.class)
public abstract class InventoryMixin {

    @Shadow @Final private Player owner;

    @ModifyConstant(
            method = "<init>",
            constant = @Constant (intValue = 8, ordinal = 1)
    )
    public int changeArmorY(int constant){
        return getScreens(owner, constant, -4);
    }

    @ModifyConstant(
            method = "<init>",
            constant = @Constant (intValue = 8, ordinal = 0)
    )
    public int changeArmorX(int constant){
        return getScreens(owner, constant, 124);
    }

    @ModifyConstant(
            method = "<init>",
            constant = @Constant (intValue = 84)
    )
    public int changeInventoryY(int constant){
        return getScreens(owner, constant, 80);
    }

    @ModifyConstant(
            method = "<init>",
            constant = @Constant (intValue = 142)
    )
    public int changeHotbarY(int constant){
        return getScreens(owner, constant, 80);
    }

    @ModifyConstant(
            method = "<init>",
            constant = @Constant (intValue = 18, ordinal = 1)
    )
    public int changeCraftY(int constant){
        return getScreens(owner, constant, 2000);
    }

    @ModifyConstant(
            method = "<init>",
            constant = @Constant (intValue = 28)
    )
    public int changeOutputY(int constant){
        return getScreens(owner, constant, 2000);
    }

    @ModifyConstant(
            method = "<init>",
            constant = @Constant (intValue = 77)
    )
    public int changeOffhandX(int constant){
        return getScreens(owner, constant, 55);
    }

    @ModifyConstant(
            method = "<init>",
            constant = @Constant (intValue = 62)
    )
    public int changeOffhandY(int constant){
        return getScreens(owner, constant, 18);
    }


}
