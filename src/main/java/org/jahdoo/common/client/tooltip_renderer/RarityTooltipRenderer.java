package org.jahdoo.common.client.tooltip_renderer;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.joml.Matrix4f;

import java.util.List;

import static net.minecraft.client.Minecraft.getInstance;
import static net.minecraft.client.renderer.MultiBufferSource.BufferSource;

public class RarityTooltipRenderer implements ClientTooltipComponent {

    public record RarityTag(ItemStack gearPiece, List<Either<FormattedText, TooltipComponent>> allComponents) implements TooltipComponent {}
    private final int spacing = getInstance().font.lineHeight + 1;
    private final RarityTag component;

    public RarityTooltipRenderer(RarityTag component) {
        this.component = component;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getWidth(Font font) {
        return 0;
    }

    @Override
    public void renderText(Font font, int mouseX, int mouseY, Matrix4f matrix, BufferSource bufferSource) {}

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics gfx){
        var pose = gfx.pose();
        var hasRarity = this.component.gearPiece.get(ComponentReg.JAHDOO_RARITY);

        if(hasRarity != null){
            var rarityId = hasRarity.intValue();
            var icon = JahdooRarity.getAllRarities(rarityId);
            var width = 48;
            var height = 9;

            pose.pushPose();
            pose.translate(0, 0, 1);
            gfx.blit(icon.getTag(), x, y - 10, 0, 0, 0, width, height, width, height);
            pose.popPose();
        }
    }

}
