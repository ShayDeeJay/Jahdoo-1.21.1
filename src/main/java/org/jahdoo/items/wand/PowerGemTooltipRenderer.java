package org.jahdoo.items.wand;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.utils.ModHelpers;
import org.joml.Matrix4f;

import java.util.List;

import static net.minecraft.client.renderer.LightTexture.FULL_BRIGHT;

public class PowerGemTooltipRenderer implements ClientTooltipComponent {
    public static final ResourceLocation SOCKET_SLOT = ModHelpers.res("textures/gui/gui_general_slot.png");

    private final SocketComponent comp;
    private int spacing = Minecraft.getInstance().font.lineHeight + 4;
    private int shiftY = 11;

    public PowerGemTooltipRenderer(SocketComponent comp) {
        this.comp = comp;
    }

    @Override
    public int getHeight() {
        return this.spacing * this.comp.gems.size() + (!comp.gems.isEmpty() ? shiftY + 2 : 0);
    }

    @Override
    public int getWidth(Font font) {
        int maxWidth = 0;
        for (ItemStack inst : this.comp.gems) {
            maxWidth = Math.max(maxWidth, font.width(inst.toString()) + 12);
        }
        return maxWidth;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics gfx) {
        var pose = gfx.pose();
        for (int i = 0; i < this.comp.gems.size(); i++) {
            int size = 15;
            gfx.blit(SOCKET_SLOT, x-1, y + this.spacing * i + shiftY - 1, 0, 0, 0, size, size, size, size);
        }

        for (ItemStack inst : this.comp.gems()) {
            pose.pushPose();
            pose.scale(0.5F, 0.5F, 1);
            gfx.renderFakeItem(inst, 2 * x + 5, (2 * y + 5) + (shiftY * 2));
            pose.popPose();
            y += this.spacing;
        }
    }

    @Override
    public void renderText(Font font, int mouseX, int mouseY, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {
        if(!this.comp.gems.isEmpty()){
            font.drawInBatch("Upgrade Slots", mouseX, mouseY - 13 + this.spacing, 0xAABBCC, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, FULL_BRIGHT);
        }
        for (int i = 0; i < this.comp.gems.size(); i++) {
            var itemStack = this.comp.gems.get(i);
            font.drawInBatch(itemStack.isEmpty() ? "Empty slot" : itemStack.getHoverName().getString(), mouseX + 15, mouseY + 3 + this.spacing * i + shiftY, 0xAABBCC, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, FULL_BRIGHT);
        }
    }

    public record SocketComponent(ItemStack socket, List<ItemStack> gems) implements TooltipComponent {}

}
