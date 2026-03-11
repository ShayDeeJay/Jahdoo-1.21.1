package org.jahdoo.common.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.client.button.SimpleButton;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jetbrains.annotations.NotNull;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.jahdoo.common.client.button.ToggleComponent.menuButton;
import static org.jahdoo.common.client.screens.RunScreen.heightRestriction;
import static org.jahdoo.trial_nexus.utils.Configuration.CLIENT_CONFIG;
import static org.jahdoo.trial_nexus.utils.Configuration.UI_COLOUR;

public class SettingsScreen extends AbstractPanableScreen {
    public static final int WIDTH_OFFSET = 80;
    private int contentBottom = 0;
    private boolean hideUi;

    public static final List<Integer> uiColourHelpers = List.of(
        ColourHelpers.getNetheriteBox(),
        ColourHelpers.getSympathiserOrange(),
        ElementReg.utility().textColourB(),
        ColourHelpers.getCosmicPurple(),
        ColourHelpers.getUniqueA(),
        ColourHelpers.getUniqueB()
    );

    @Override
    protected void init() {
        var spacerX = 0;
        var sharedX = this.width / 2 - 84;
        var neSpac = panY + 0;
        var values = CLIENT_CONFIG.getValues();
        var vals = values.entrySet();

        this.contentBottom = 2;

        this.addRenderableOnly(
            new Overlay() {
                public void render(GuiGraphics graphics, int i, int i1, float v) {
                    graphics.enableScissor(0, 56, graphics.guiWidth(), graphics.guiHeight() - 6);
                }
            }
        );

        uiColour(spacerX, sharedX, (int) (WIDTH_OFFSET + panY));

        for (var val : vals) {
            if (val.getValue() instanceof ModConfigSpec.BooleanValue bool) {
                neSpac += 44;
                booleanButtons(spacerX, sharedX, (int) (WIDTH_OFFSET + neSpac), bool, TextHelpers.stringIdToName(val.getKey()));
            }
        }

        this.addRenderableOnly(
            new Overlay() {
                public void render(GuiGraphics graphics, int i, int i1, float v) {
                    graphics.disableScissor();
                }
            }
        );

        super.init();
    }



    private void booleanButtons(int spacer, int sharedX, int sharedY, ModConfigSpec.BooleanValue bool, String key) {
        var width1 = 30;
        var height1 = 18;
        var spacerXButton = sharedX + 53;

        var setTrue = new SimpleButton(
            width/2 - (width1/2),
            sharedY,
            width1,
            height1,
            true,
            (b) -> {
                bool.set(!bool.get());
                this.rebuildWidgets();
            },
            bool.isTrue() ? "true" : "false",
            () -> {
                if(!this.hideUi){
                    this.hideUi = true;
                }
            }
        );

        this.addRenderableWidget(setTrue);

        this.addRenderableOnly(

            new Overlay() {
                public void render(GuiGraphics graphics, int i, int i1, float v) {
                    graphics.drawCenteredString(Minecraft.getInstance().font, key, sharedX + 84, sharedY - 12, ColourHelpers.getOffWhite());
                }
            }
        );

        contentBottom++;
    }

    private void uiColour(int spacer, int sharedX, int sharedY) {

        for (var uiColour : uiColourHelpers) {

            int finalSpacer = spacer;

            var spec = UI_COLOUR.getSpec();
            var comment = getComment(spec);
            var value = uiColourHelpers.indexOf(uiColour);

            this.addRenderableWidget(
                menuButton(sharedX + spacer, sharedY, (button) -> {
                    this.rebuildWidgets();
                    UI_COLOUR.set(value);
                }, Icons.BLANK, 20, 0, "")
            );

            this.addRenderableOnly(
                new Overlay() {
                    public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
                        SharedUI.boxMaker(guiGraphics, sharedX + 5 + finalSpacer, sharedY + 5, 5, 5, uiColour, uiColour);
                        guiGraphics.drawCenteredString(Minecraft.getInstance().font, comment, sharedX + 84, sharedY - 12, ColourHelpers.getOffWhite());
                    }
                }
            );

            spacer += 30;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (canScrollDetails(mouseX, mouseY, height, width)) {
            panY = Math.max(this.height - (contentBottom * 44) - 28, Math.min(panY + Math.round(scrollY * 12), 0));
            this.rebuildWidgets();
            return true;
        }

        return false;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        this.panY = 0;
        this.rebuildWidgets();
        super.resize(minecraft, width, height);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (canScrollDetails(mouseX, mouseY, height, width) ) {
            panY = Math.max(this.height - (contentBottom * 44) - 28, Math.min(panY + Math.round(dragY), 0));
            this.rebuildWidgets();
            return true;
        }

        return false;
    }

    private static boolean canScrollDetails(double mouseX, double mouseY, int height, int width) {
        var v = (double) width / 2;
        var canScrollX = mouseX > 175 && mouseX < (v * 2) - 12;
        return canScrollX && heightRestriction(mouseY, height);
    }

    private static @NotNull String getComment(ModConfigSpec.ValueSpec spec) {
        return Arrays.stream(Objects.requireNonNull(spec.getComment()).split("\\r?\\n")).findFirst().get();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        System.out.println(this.hideUi);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderWithScale(GuiGraphics guiGraphics, int mouseX, int mouseY, LocalPlayer player, float centerX, float centerY, Minecraft mc) {}

}