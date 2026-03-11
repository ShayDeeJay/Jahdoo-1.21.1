package org.jahdoo.common.client.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static com.mojang.blaze3d.systems.RenderSystem.*;
import static org.jahdoo.trial_nexus.utils.Configuration.*;
import static org.jahdoo.trial_nexus.utils.Icons.GUI_GENERAL_SLOT;
import static org.jahdoo.trial_nexus.utils.Icons.GUI_ITEM_SLOT;

public class InventoryOverlay extends AbstractTimedOverlay {

    int storedSelectedIndex;
    float fadeInHotbar;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if(!CUSTOM_UI.get()) return;
        super.render(guiGraphics, deltaTracker);

        var player = Minecraft.getInstance().player;
        if(player == null) return;

        CUSTOM_UI_SCALE.set(1.0);

        inventory(guiGraphics, player);
        fadeInHotbar(player);
    }

    private void inventory(GuiGraphics graphics, LocalPlayer player) {
        var selectedIndex = player.getInventory().selected;
        var alpha = Math.min(0.5F, fadeIn);
        var textColour = -7303024;
        var scale = 1F;
        var baseGap = 20f;
        var y = (int) (graphics.guiHeight() - this.fadeIn - 100);
        var scaledGap = baseGap * scale;
        var spacing = scale + scaledGap;
        var totalWidth = 9 * spacing - scaledGap;
        var centerX = graphics.guiWidth() / 2f;
        var startX = centerX - totalWidth / 2f;


        if(timer > 0){
            var minecraft = Minecraft.getInstance();
            var offset = (1 - scale) / 2f;
            graphics.pose().pushPose();
            graphics.pose().translate(0, y + offset, 0);

            var selected = player.getInventory().getSelected();
            if(!selected.isEmpty()){
                graphics.drawCenteredString(minecraft.font, selected.getHoverName(), (int) centerX, -20, textColour);
            }

            for (int i = 0; i < 9; i++) {
                var item = player.getInventory().getItem(i);
                var isSelected = i == selectedIndex;
                var slotX = startX + i * spacing - (scale * 8);

                graphics.pose().pushPose();

                graphics.pose().translate(slotX + offset, 0, 0);
                graphics.pose().scale(scale, scale, scale);

                renderSlot(
                    graphics, item, 0, 0, isSelected ? GUI_GENERAL_SLOT : GUI_ITEM_SLOT, 0, textColour, isSelected ? 1 : alpha
                );

                graphics.pose().popPose();
            }
            graphics.pose().popPose();
        }
    }

    private void fadeInHotbar(Player player){
        var xp = player.getInventory().selected;
        var alwaysShow = !AUTO_HIDE_HOTBAR.get();

        if(this.storedSelectedIndex != xp || alwaysShow) {
            if(timer == 0) this.fadeIn = 0.5F;
            this.timer = 100;
        }

        this.fadeIn = Math.max(this.fadeIn - 0.5F, 0);
        this.storedSelectedIndex = xp;
    }

    private static void renderSlot(GuiGraphics graphics, ItemStack next, int x, int y, ResourceLocation lit, int index, int textColour, float alpha) {
        var size = 24;

        enableBlend();
        setShaderColor(1f, 1f, 1f, alpha);
        graphics.blit(lit, x - 4, y - 4, 0, 0, size, size, size, size);
        setShaderColor(1f, 1f, 1f, 1f);
        graphics.renderItem(next, x, y);
        graphics.renderItemDecorations(Minecraft.getInstance().font, next, x, y);
        disableBlend();
    }


}
