package org.jahdoo.common.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.common.registers.AbilityReg;
import org.jahdoo.common.registers.ElementReg;

import static org.jahdoo.ascension.utils.Helpers.res;
import static org.jahdoo.common.client.Icons.ABILITY_PREFIX;
import static org.jahdoo.common.client.Icons.GUI_BUTTON;
import static org.jahdoo.common.client.button.ToggleComponent.menuButtonSound;
import static org.jahdoo.common.items.augments.AugmentItemHelper.getAugmentWithAbility;

public class AbilityUnlockScreen extends AbstractPanableScreen {

    ItemStack itemStack = ItemStack.EMPTY;

    @Override
    protected void init() {
        super.init();
        var size = (int) (30 + 30 * this.zoomX);
        var baseSpacing = 90;
        var scaledSpacing = baseSpacing * (size / 60.0);

        var baseXOffset = 50; // Original spacing between elements
        var scaledXOffset = baseXOffset * (size / 26.0); // Scale X spacing

        var centerX = (double) this.width / 2 + panX - 8; // Screen center
        var centerY = (double) this.height / 2 + panY - 30; // Screen center

        withElementObjects(centerX, scaledSpacing, centerY, size, ElementReg.frost());
        withElementObjects(centerX + scaledXOffset, scaledSpacing, centerY, size, ElementReg.inferno());
        withElementObjects(centerX + 2 * scaledXOffset, scaledSpacing, centerY, size, ElementReg.mystic());
        withElementObjects(centerX + 3 * scaledXOffset, scaledSpacing, centerY, size, ElementReg.vitality());
    }

    private void withElementObjects(double centerX, double scaledSpacing, double centerY, int size, AbstractElement element) {
        renderButton(centerX - 1.5 * scaledSpacing, centerY, element.iconTexture(), size, ItemStack.EMPTY);

        var spacer = 60;
        var index = 1.2;

        for (var abilityRegistrar : AbilityReg.getWithElement(element)) {
            var res = res(ABILITY_PREFIX + abilityRegistrar.setAbilityId() + ".png");
            var augment = getAugmentWithAbility(abilityRegistrar);
            var withPanY = centerY + (index * spacer * (size / 60.0));

            renderButton(centerX - 1.5 * scaledSpacing, withPanY, res, size, augment);
            index++;
        }
    }

    private void renderButton(double withPanX, double withPanY, ResourceLocation icons, int size, ItemStack stack) {
        var posX = (int) (withPanX - 15 - (double) size / 2);
        var posY = (int) (withPanY - 15 - (double) size / 2);
        this.addRenderableWidget(
            menuButtonSound(
                posX, posY,
                (Button) -> { },
                icons, size, false, 0, new WidgetSprites(GUI_BUTTON, GUI_BUTTON), true,
                () -> this.itemStack = stack
            )
        );
    }

    @Override
    protected void renderWithScale(GuiGraphics graphics, int mouseX, int mouseY, LocalPlayer player, float centerX, float centerY, Minecraft mc) {
    }

    @Override
    protected void baseRender(GuiGraphics graphics, int mouseX, int mouseY, LocalPlayer player, float centerX, float centerY, Minecraft mc) {
        this.rebuildWidgets();
        if(!this.itemStack.isEmpty()) graphics.renderTooltip(font, this.itemStack, mouseX, mouseY);
        this.itemStack = ItemStack.EMPTY;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.rebuildWidgets();
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        this.rebuildWidgets();
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
}
