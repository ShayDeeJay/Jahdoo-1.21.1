package org.jahdoo.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import org.jahdoo.block.shopping_table.ShoppingTableBlock;
import org.jahdoo.block.shopping_table.ShoppingTableEntity;

import static org.jahdoo.event.event_helpers.OverlayEvent.crosshairManager;
import static org.jahdoo.event.event_helpers.OverlayEvent.simpleGui;

public class OverlayBlockTooltip {
    public static int renderHeight;

    public static void overlayEvent(RenderGuiLayerEvent.Pre event) {
        var instance = Minecraft.getInstance();
        var player = instance.player;

        if (player == null) return;

        crosshairManager(event);
        simpleGui(event, player);

        var partialTicks = event.getPartialTick().getGameTimeDeltaTicks();
        HitResult pick = player.pick(3, partialTicks, false);
        BlockPos containing = BlockPos.containing(pick.getLocation());

        renderShoppingTableTooltip(event, player, containing);
        renderShoppingTableTooltip(event, player, containing.below());
    }

    private static void renderShoppingTableTooltip(RenderGuiLayerEvent.Pre event, Player player, BlockPos pos) {
        var instance = Minecraft.getInstance();
        var entity = player.level().getBlockEntity(pos);

        if (entity instanceof ShoppingTableEntity tableEntity){
            var guiGraphics = event.getGuiGraphics();
            var width = guiGraphics.guiWidth() / 2;
            var height = guiGraphics.guiHeight() / 2;
            var itemStack = tableEntity.getItem().getStackInSlot(0);
            var tooltip = Screen.getTooltipFromItem(instance, itemStack);
            var getState = tableEntity.getBlockState().getValue(ShoppingTableBlock.TEXTURE);
            var canRender = instance.screen == null && tooltip.size() > 1 && getState != 3;

            if (canRender) {
                var mouseY = height - (tooltip.size() * 5);
                guiGraphics.renderTooltip(instance.font, itemStack, width + 60, mouseY);
            }
        }


    }

}
