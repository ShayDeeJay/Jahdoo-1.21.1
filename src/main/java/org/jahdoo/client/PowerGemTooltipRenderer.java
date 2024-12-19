package org.jahdoo.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;
import org.joml.Matrix4f;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.client.renderer.LightTexture.FULL_BRIGHT;
import static org.jahdoo.components.PowerGemData.PowerGemHelpers.standAloneAttributes;

public class PowerGemTooltipRenderer implements ClientTooltipComponent {
    public static final ResourceLocation SLOT_TEXTURE = ModHelpers.res("textures/gui/gui_general_slot.png");

    private final SocketComponent comp;
    private int spacing = Minecraft.getInstance().font.lineHeight + 4;
    private int shiftY = 0;

    public PowerGemTooltipRenderer(SocketComponent comp) {
        this.comp = comp;
    }

    @Override
    public int getHeight() {
        return this.spacing * this.comp.gems.size() + 2;
    }

    @Override
    public int getWidth(Font font) {
        int maxWidth = 0;
        for (ItemStack inst : this.comp.gems) {
            maxWidth = Math.max(maxWidth, font.width(standAloneAttributes(inst)) + 18);
        }
        return maxWidth;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics gfx) {
        var pose = gfx.pose();
        for (int i = 0; i < this.comp.gems.size(); i++) {
            int size = 15;
            gfx.blit(SLOT_TEXTURE, x-1, y + this.spacing * i + shiftY - 1, 0, 0, 0, size, size, size, size);
        }

        for (ItemStack inst : this.comp.gems().reversed()) {
            pose.pushPose();
            pose.scale(0.5F, 0.5F, 1);
            gfx.renderFakeItem(inst, 2 * x + 5, (2 * y + 5) + (shiftY * 2));
            pose.popPose();
            y += this.spacing;
        }
    }

    @Override
    public void renderText(Font font, int mouseX, int mouseY, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {
        var spacer = new AtomicInteger();
        for (ItemStack itemStack : this.comp.gems().reversed()) {
            var components = standAloneAttributes(itemStack);
            var getLabel = itemStack.isEmpty() ? Component.literal("Empty Slot") : components;
            var posY = mouseY + 3 + this.spacing * spacer.get() + shiftY;
            font.drawInBatch(getLabel, mouseX + 15, posY, ColourStore.HEADER_COLOUR, true, matrix, bufferSource, Font.DisplayMode.SEE_THROUGH, 0, FULL_BRIGHT);
            spacer.set(spacer.get() + 1);
        }
    }

    public record SocketComponent(ItemStack socket, List<ItemStack> gems) implements TooltipComponent {}

}
