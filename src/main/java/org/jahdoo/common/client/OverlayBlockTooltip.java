package org.jahdoo.common.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.block.shopping_table.ShoppingTableBlock;
import org.jahdoo.common.block.shopping_table.ShoppingTableEntity;
import org.jahdoo.common.block.tank.TankBlockEntity;
import org.jahdoo.common.block.ticket_bureau.TicketBureauBlock;
import org.jahdoo.common.block.ticket_bureau.TicketBureauBlockEntity;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.utils.Helpers;

import java.util.ArrayList;
import java.util.Optional;

import static net.minecraft.client.gui.screens.Screen.getTooltipFromItem;
import static net.neoforged.neoforge.client.event.RenderGuiLayerEvent.Post;

public class  OverlayBlockTooltip {

    public static void overlayEvent(Post event) {
        var instance = Minecraft.getInstance();
        var player = instance.player;
        if (player == null) return;

        var partialTicks = event.getPartialTick().getGameTimeDeltaTicks();
        var pick = player.pick(player.blockInteractionRange(), partialTicks, false);
        if(pick instanceof BlockHitResult result){
            var pos = result.getBlockPos();
            var lookingAt = player.level().getBlockEntity(pos);
            if (lookingAt instanceof AbstractBEInventory tableEntity){
                renderShoppingTableTooltip(event, player, tableEntity);
                return;
            }

            var lookingAtBelow = player.level().getBlockEntity(pos.below());
            if(lookingAt instanceof BarrelBlockEntity){
                if (lookingAtBelow instanceof AbstractBEInventory tableEntity) {
                    renderShoppingTableTooltip(event, player, tableEntity);
                }
            }
        }
    }

    private static void renderShoppingTableTooltip(
        Post event,
        Player player,
        AbstractBEInventory tableEntity
    ) {
        var instance = Minecraft.getInstance();
        if(instance.screen != null) return;

        var graphics = event.getGuiGraphics();
        var width = graphics.guiWidth() / 2;
        var height = graphics.guiHeight() / 2;
        var handler = tableEntity.inputItemHandler;
        var stackInSlot = handler.getStackInSlot(0);
        var tooltip = getTooltipFromItem(instance, stackInSlot);

        var font = instance.font;

        if(tableEntity instanceof TankBlockEntity){
            var mouseY = height - (tooltip.size() * 5);
            var components = new ArrayList<Component>();
            var slotLimit = handler.getSlotLimit(0);
            var count = stackInSlot.getCount();
            components.add(Helpers.withStyleComponentTrans("block.jahdoo.tank", ElementReg.utility().textColourB()));
            components.add(Helpers.withStyleComponent(count +"/"+ slotLimit, Helpers.colourByPercent(slotLimit, count, true)));
            graphics.renderTooltip(font, components, Optional.empty(), width + 10, mouseY + 6);
        }

        if(stackInSlot.isEmpty()) return;

        if(tableEntity instanceof ShoppingTableEntity){
            var getState = tableEntity.getBlockState().getValue(ShoppingTableBlock.TEXTURE);
            var canRender = tooltip.size() > 1 && getState != 3 && !stackInSlot.isEmpty();


            if (canRender) {
                var mouseY = height - (tooltip.size() * 5);
                graphics.renderTooltip(font, stackInSlot, width + 60, mouseY);
            }
        }

        if(tableEntity instanceof TicketBureauBlockEntity && player.isShiftKeyDown()){
            var mouseY = height - ((tooltip.size() + font.lineHeight)) * 2;
            var mainHandItem = player.getMainHandItem();
            var hasComparator = mainHandItem.is(ItemReg.STAMP);
            graphics.renderTooltip(font, stackInSlot, !hasComparator ? (width + 30) : (width - 230), mouseY);
            if(hasComparator){
                var size = 32;
                graphics.blit(Icons.DIRECTION_ARROW_FORWARD, width - size/2 - 1, height - size/2, 0, 0, size, size, size, size);
                var copy = stackInSlot.copy();
                TicketBureauBlock.addStampToTicket(mainHandItem, copy);
                var tooltip1 = getTooltipFromItem(instance, copy);
                var mouseY1 = height - ((tooltip1.size() + font.lineHeight)) * 2;
                graphics.renderTooltip(font, copy, width + 30, mouseY1);
            }
        }
    }

}
