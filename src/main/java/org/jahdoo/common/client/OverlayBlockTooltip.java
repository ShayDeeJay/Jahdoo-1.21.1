package org.jahdoo.common.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.common.block.chaos_cube.ChaosCubeEntity;
import org.jahdoo.common.block.creator.CreatorEntity;
import org.jahdoo.common.block.shopping_table.ShoppingTableBlock;
import org.jahdoo.common.block.shopping_table.ShoppingTableEntity;
import org.jahdoo.common.block.tank.TankBlockEntity;
import org.jahdoo.common.block.ticket_bureau.TicketBureauBlock;
import org.jahdoo.common.block.ticket_bureau.TicketBureauBlockEntity;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.mod.AbilityReg;
import org.jahdoo.trial_nexus.ability.AbilityComponentHelper;
import org.shaydee.shaydeeapi.block.AbstractBEInventory;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.ArrayList;
import java.util.Optional;

import static net.minecraft.client.gui.screens.Screen.getTooltipFromItem;
import static net.neoforged.neoforge.client.event.RenderGuiLayerEvent.Post;
import static org.jahdoo.common.block.shopping_table.ShoppingTableBlock.HALF;
import static org.jahdoo.common.registers.mod.ElementReg.utility;


public class  OverlayBlockTooltip {

    public static void overlayEvent(Post event) {
        var instance = Minecraft.getInstance();
        var player = instance.player;
        var level = instance.level;
        if (player == null || level == null) return;

        var partialTicks = event.getPartialTick().getGameTimeDeltaTicks();
        var pick = player.pick(player.blockInteractionRange(), partialTicks, false);

        if(pick instanceof BlockHitResult result){
            var pos = result.getBlockPos();
            var state = level.getBlockState(pos);
            if(!(state.getBlock() instanceof ShoppingTableBlock)) return;

            var isUpper = state.getValue(HALF).equals(DoubleBlockHalf.UPPER);
            var getBottomHalf = level.getBlockEntity(isUpper ? pos.below() : pos);

            if (getBottomHalf instanceof AbstractBEInventory tableEntity){
                renderShoppingTableTooltip(event, player, tableEntity);
            }
        }
    }

    private static void renderShoppingTableTooltip(
        Post event,
        Player player,
        AbstractBEInventory tableEntity
    ) {
        var instance = Minecraft.getInstance();
        var graphics = event.getGuiGraphics();
        var width = graphics.guiWidth() / 2;
        var height = graphics.guiHeight() / 2;
        var font = instance.font;
        var pose = event.getGuiGraphics().pose();

        if(player.isShiftKeyDown() && tableEntity instanceof ChaosCubeEntity chaosCubeEntity){
            if (chaosCubeEntity.getHolder() != null) {

                var ability = AbilityReg.getFirstSpellByTypeId(chaosCubeEntity.getHolder().abilityName());
                if(ability.isPresent()){
                    var list = new ArrayList<Component>();
                    AbilityComponentHelper.onlyToolTip(ability.get(), chaosCubeEntity.getHolder(), false, player.level(), list);

                    var mouseY = height - (list.size() * 5);
                    pose.pushPose();
                    pose.translate(0,0,1);
                    graphics.renderTooltip(font, list, Optional.empty(), ItemStack.EMPTY, width + 30, mouseY);
                    pose.popPose();
                }
            }
        }

        if(tableEntity instanceof CreatorEntity cEntity){
            var needed = cEntity.neededNexite();
            var tank = cEntity.getTankEntity();
            if(tank == null) return;
            var count = tank.getCount();

            if(needed == -1) return;
            if(needed <= count) return;

            var list = new ArrayList<Component>();
            list.add(TextHelpers.withStyleComponent("Needed: " + needed, ColourHelpers.getMagnetRangeGreen()));
            list.add(TextHelpers.withStyleComponent("In Tank: " + count, ColourHelpers.getMagnetStrengthRed()));

            var mouseY = height - (list.size() * 5);
            pose.pushPose();
            pose.translate(0,0,1);
            graphics.renderTooltip(font, list, Optional.empty(), ItemStack.EMPTY, width, mouseY + 12);
            pose.popPose();
        }

        var handler = tableEntity.getInputItemHandler();
        if(handler.getSlots() == 0) return;

        var stackInSlot = handler.getStackInSlot(0);
        var tooltip = getTooltipFromItem(instance, stackInSlot);

        if(tableEntity instanceof TankBlockEntity){
            var mouseY = height - (tooltip.size() * 5);
            var components = new ArrayList<Component>();
            var slotLimit = handler.getSlotLimit(0);
            var count = stackInSlot.getCount();
            components.add(TextHelpers.withStyleComponentTrans("block.jahdoo.tank", utility().textColourB()));
            components.add(TextHelpers.withStyleComponent(count +"/"+ slotLimit, ColourHelpers.colourByPercent(slotLimit, count, true)));
            pose.pushPose();
            pose.translate(0,0,1);
            graphics.renderTooltip(font, components, Optional.empty(), width, mouseY + 6);
            pose.popPose();
        }

        if(stackInSlot.isEmpty()) return;

        if(tableEntity instanceof ShoppingTableEntity){
            var getState = tableEntity.getBlockState().getValue(ShoppingTableBlock.TEXTURE);
            var canRender = tooltip.size() > 1 && getState != 3 && !stackInSlot.isEmpty();
            if (canRender) {
                var mouseY = height - (tooltip.size() * 5);
                pose.pushPose();
                pose.translate(0, 0, 1);
                graphics.renderTooltip(font, stackInSlot, width + 60, mouseY);
                pose.popPose();

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
