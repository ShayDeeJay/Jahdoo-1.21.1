package org.jahdoo.common.client.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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
import org.jahdoo.trial_nexus.utils.Icons;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.mod.AbilityReg;
import org.jahdoo.trial_nexus.magic.AbilityComponentHelper;
import org.shaydee.shaydeeapi.block.AbstractBEInventory;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.minecraft.client.gui.screens.Screen.getTooltipFromItem;
import static org.jahdoo.common.block.shopping_table.ShoppingTableBlock.HALF;
import static org.jahdoo.common.registers.mod.ElementReg.utility;

public class BlockTooltipOverlay extends AbstractTimedOverlay{

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        super.render(guiGraphics, deltaTracker);
        overlayEvent(guiGraphics);
    }

    public static void overlayEvent(GuiGraphics graphics) {
        var instance = Minecraft.getInstance();
        var player = instance.player;
        var level = instance.level;
        if (player == null || level == null) return;

        var partialTicks = instance.getTimer().getGameTimeDeltaPartialTick(true);

        var pick = player.pick(player.blockInteractionRange(), partialTicks, false);

        if(pick instanceof BlockHitResult result){
            var pos = result.getBlockPos();
            var state = level.getBlockState(pos);

            var half = state.getOptionalValue(HALF);

            var isUpper = half.isPresent() && half.get().equals(DoubleBlockHalf.UPPER);
            var getBottomHalf = level.getBlockEntity(isUpper ? pos.below() : pos);

            if (getBottomHalf instanceof AbstractBEInventory tableEntity){
                var width = graphics.guiWidth() / 2;
                var height = graphics.guiHeight() / 2;
                var font = instance.font;

                renderChaosCubeTooltip(graphics, player, tableEntity, width, height, font);
                renderCreatorTooltip(graphics, tableEntity, width, height, font);
                renderTankTooltip(graphics, tableEntity, width, height, font);

                var handler = tableEntity.getInputItemHandler();
                if (handler.getSlots() == 0) return;

                var stack = handler.getStackInSlot(0);
                if (stack.isEmpty()) return;

                renderShoppingTableEntityTooltip(graphics, tableEntity, stack, width, height, font, instance);
                renderTicketBureauTooltip(graphics, player, tableEntity, stack, width, height, font, instance);
            }
        }
    }

    private static void renderChaosCubeTooltip(
        GuiGraphics graphics,
        Player player,
        AbstractBEInventory tableEntity,
        int width,
        int height,
        Font font
    ) {
        if (!(player.isShiftKeyDown() && tableEntity instanceof ChaosCubeEntity cube)) return;
        if (cube.getHolder() == null) return;

        var ability = AbilityReg.getFirstSpellByTypeId(cube.getHolder().abilityName());
        if (ability.isEmpty()) return;

        var list = new ArrayList<Component>();
        AbilityComponentHelper.onlyToolTip(ability.get(), cube.getHolder(), false, player.level(), list);

        renderTooltipList(graphics, font, list, width + 8, height - (list.size() * 4));
    }

    private static void renderCreatorTooltip(
        GuiGraphics graphics,
        AbstractBEInventory tableEntity,
        int width,
        int height,
        Font font
    ) {
        if (!(tableEntity instanceof CreatorEntity creator)) return;

        var tank = creator.getTankEntity();
        if (tank == null) return;

        var needed = creator.neededNexite();
        var count = tank.getCount();
        if (needed == -1 || needed <= count) return;

        var list = new ArrayList<Component>();
        list.add(TextHelpers.withStyleComponent("Needed: " + needed, ColourHelpers.getMagnetRangeGreen()));
        list.add(TextHelpers.withStyleComponent("In Tank: " + count, ColourHelpers.getMagnetStrengthRed()));

        renderTooltipList(graphics, font, list, width, height - (list.size() * 5) + 12);
    }

    private static void renderTankTooltip(
        GuiGraphics graphics,
        AbstractBEInventory tableEntity,
        int width,
        int height,
        Font font
    ) {
        if (!(tableEntity instanceof TankBlockEntity)) return;

        var handler = tableEntity.getInputItemHandler();
        if (handler.getSlots() == 0) return;

        var stack = handler.getStackInSlot(0);
        var slotLimit = handler.getSlotLimit(0);
        var count = stack.getCount();

        var components = new ArrayList<Component>();
        components.add(TextHelpers.withStyleComponentTrans("block.jahdoo.tank", utility().textColourB()));
        components.add(TextHelpers.withStyleComponent(count + "/" + slotLimit, ColourHelpers.colourByPercent(slotLimit, count, true)));

        renderTooltipList(graphics, font, components, width, height + 2);
    }

    private static void renderShoppingTableEntityTooltip(
        GuiGraphics graphics,
        AbstractBEInventory tableEntity,
        ItemStack stack,
        int width,
        int height,
        Font font,
        Minecraft instance
    ) {
        if (!(tableEntity instanceof ShoppingTableEntity)) return;

        var state = tableEntity.getBlockState().getValue(ShoppingTableBlock.TEXTURE);
        var tooltip = getTooltipFromItem(instance, stack);
        if (tooltip.size() <= 1 || state == 3) return;

        renderTooltipItem(graphics, font, stack, width + 10, height + 2);
    }

    private static void renderTicketBureauTooltip(
        GuiGraphics graphics,
        Player player,
        AbstractBEInventory tableEntity,
        ItemStack stack,
        int width,
        int height,
        Font font,
        Minecraft instance
    ) {
        if (!(tableEntity instanceof TicketBureauBlockEntity) || !player.isShiftKeyDown()) return;

        var mouseY = height + 4;
        var mainHand = player.getMainHandItem();
        var hasStamp = mainHand.is(ItemReg.STAMP);

        graphics.renderTooltip(font, stack, !hasStamp ? width  : width - 210, mouseY);

        if (!hasStamp) return;

        var size = 32;
        graphics.blit(Icons.DIRECTION_ARROW_FORWARD, width - size / 2 - 1, height - size / 2, 0, 0, size, size, size, size);

        //Render comparison tooltip
        var copy = stack.copy();
        TicketBureauBlock.addStampToTicket(mainHand, copy);
        graphics.renderTooltip(font, copy,  width + 210, mouseY);
    }

    private static void renderTooltipList(GuiGraphics graphics, Font font, List<Component> list, int x, int y) {
        var pose = graphics.pose();
        pose.pushPose();
        pose.translate(0, 0, 1);
        graphics.renderTooltip(font, list, Optional.empty(), ItemStack.EMPTY, x, y);
        pose.popPose();
    }

    private static void renderTooltipItem(GuiGraphics graphics, Font font, ItemStack stack, int x, int y) {
        var pose = graphics.pose();
        pose.pushPose();
        pose.translate(0, 0, 1);
        graphics.renderTooltip(font, stack, x, y);
        pose.popPose();
    }
}
