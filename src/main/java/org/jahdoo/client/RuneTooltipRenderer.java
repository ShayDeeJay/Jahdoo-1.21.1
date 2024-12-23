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
import static org.jahdoo.components.RuneData.RuneHelpers.standAloneAttributes;

public class RuneTooltipRenderer implements ClientTooltipComponent {
    private final int spacing = Minecraft.getInstance().font.lineHeight + 4;
    private final RuneComponent component;

    public RuneTooltipRenderer(RuneComponent component) {
        this.component = component;
    }

    @Override
    public int getHeight() {
        return this.spacing * this.component.runes.size() + 2;
    }

    @Override
    public int getWidth(Font font) {
        int maxWidth = 0;
        for (ItemStack inst : this.component.runes) {
            maxWidth = Math.max(maxWidth, font.width(standAloneAttributes(inst)) + 18);
        }
        return maxWidth;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics gfx) {
        var pose = gfx.pose();
        for (int i = 0; i < this.component.runes.size(); i++) {
            int size = 15;
            gfx.blit(IconLocations.GUI_GENERAL_SLOT, x-1, y + this.spacing * i - 1, 0, 0, 0, size, size, size, size);
        }

        for (ItemStack inst : this.component.runes()) {
            pose.pushPose();
            pose.scale(0.5F, 0.5F, 1);
            gfx.renderFakeItem(inst, 2 * x + 5, (2 * y + 5));
            pose.popPose();
            y += this.spacing;
        }
    }

    @Override
    public void renderText(Font font, int mouseX, int mouseY, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {
        var spacer = new AtomicInteger();
        for (ItemStack itemStack : this.component.runes()) {
            var components = standAloneAttributes(itemStack);
            var getLabel = itemStack.isEmpty() ? Component.literal("Empty Slot") : components;
            var posY = mouseY + 3 + this.spacing * spacer.get();
            font.drawInBatch(getLabel, mouseX + 15, posY, ColourStore.HEADER_COLOUR, true, matrix, bufferSource, Font.DisplayMode.SEE_THROUGH, 0, FULL_BRIGHT);
            spacer.set(spacer.get() + 1);
        }
    }

    public record RuneComponent(ItemStack socket, List<ItemStack> runes) implements TooltipComponent {}

}
