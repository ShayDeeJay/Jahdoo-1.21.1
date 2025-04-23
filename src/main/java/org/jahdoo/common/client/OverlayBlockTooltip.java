package org.jahdoo.common.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.common.block.shopping_table.ShoppingTableBlock;
import org.jahdoo.common.block.shopping_table.ShoppingTableEntity;

import static net.minecraft.client.gui.screens.Screen.getTooltipFromItem;
import static net.neoforged.neoforge.client.event.RenderGuiLayerEvent.Post;

public class OverlayBlockTooltip {

    public static void overlayEvent(Post event) {
        var instance = Minecraft.getInstance();
        var player = instance.player;
        if (player == null) return;

        var partialTicks = event.getPartialTick().getGameTimeDeltaTicks();
        var pick = player.pick(player.blockInteractionRange(), partialTicks, false);
        if(pick instanceof BlockHitResult result){
            var pos = result.getBlockPos();
            renderShoppingTableTooltip(event, player, pos);
            renderShoppingTableTooltip(event, player, pos.below());
        }
    }

    private static void renderShoppingTableTooltip(
        Post event,
        Player player,
        BlockPos pos
    ) {
        var instance = Minecraft.getInstance();
//        if(instance.screen != null) return;;

        var entity = player.level().getBlockEntity(pos);

        if (entity instanceof ShoppingTableEntity tableEntity){
            var graphics = event.getGuiGraphics();
            var width = graphics.guiWidth() / 2;
            var height = graphics.guiHeight() / 2;
            var itemStack = tableEntity.getItem().getStackInSlot(0);
            var tooltip = getTooltipFromItem(instance, itemStack);
            var getState = tableEntity.getBlockState().getValue(ShoppingTableBlock.TEXTURE);
            var canRender = /*instance.screen == null && */tooltip.size() > 1 && getState != 3 && !itemStack.isEmpty();

            if (canRender) {
                var mouseY = height - (tooltip.size() * 5);
                graphics.renderTooltip(instance.font, itemStack, width + 60, mouseY);
            }
        }
    }

}
