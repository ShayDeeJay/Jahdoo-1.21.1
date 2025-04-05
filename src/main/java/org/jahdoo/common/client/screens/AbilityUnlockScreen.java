package org.jahdoo.common.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.Maths;
import org.jahdoo.common.block.augment_modification_station.AugmentModificationScreen;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.networking.client2server.AbilityHolderC2SP;
import org.jahdoo.common.registers.AbilityReg;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.ElementReg;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;
import static org.jahdoo.common.client.Icons.*;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.button.ToggleComponent.menuButtonSoundAbilities;

public class AbilityUnlockScreen extends AbstractPanableScreen {
    List<Component> components = new ArrayList<>();
    int baseSpacing = 90;
    int baseXOffset = 50; // Original spacing between elements
    int size;
    double scaledSpacing;
    double scaledXOffset; // Scale X spacing
    double centerX; // Screen center
    double centerY; // Screen center
    double scale;
    int colour;

    public AbilityUnlockScreen(
        int size,
        double panX,
        double panY,
        double zoomX,
        double scaledSpacing,
        double scaledXOffset,
        double centerX,
        double centerY
    ){
        this.size = size;
        this.scaledSpacing = scaledSpacing;
        this.scaledXOffset = scaledXOffset;
        this.centerX = centerX;
        this.centerY = centerY;
        this.panX = panX;
        this.panY = panY;
        this.zoomX = zoomX;
    }

    public AbilityUnlockScreen(){}

    @Override
    protected void init() {
        super.init();

        this.size = (int) (16 + 30 * Maths.singleFormattedDouble(this.zoomX));
        this.scaledSpacing = baseSpacing * (size / 90.0);
        this.scaledXOffset = baseXOffset * (size / 24.0); // Scale X spacing

        // Always scale relative to the screen center
        this.centerX = (double) this.width / 2 + panX;
        this.centerY = (double) this.height / 2 + panY;

        // Adjusted offsets to keep elements centered
        double startX = centerX - (10.24 * scaledXOffset); // Center the first element

        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                    test(guiGraphics, startX);
                }
            }
        );

        withElementObjects(startX, scaledSpacing, centerY, size, ElementReg.frost(), GUI_BUTTON_FROST);
        withElementObjects(startX + 5 * scaledXOffset, scaledSpacing, centerY, size, ElementReg.inferno(), GUI_BUTTON_INFERNO);
        withElementObjects(startX + 10 * scaledXOffset, scaledSpacing, centerY, size, ElementReg.mystic(), GUI_BUTTON_MYSTIC);
        withElementObjects(startX + 15 * scaledXOffset, scaledSpacing, centerY, size, ElementReg.vitality(), GUI_BUTTON_VITALITY);
        withElementObjects(startX + 20 * scaledXOffset, scaledSpacing, centerY, size, ElementReg.utility(), GUI_BUTTON_UTILITY);

        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                    test2(guiGraphics);

                }
            }
        );
    }

    private void test2(@NotNull GuiGraphics guiGraphics) {
        guiGraphics.disableScissor();
//        var x = (float) this.width / 2;
//        var y = (float) this.height / 2;
//        guiGraphics.pose().translate(-x, -y, 0);
    }

    private void test(@NotNull GuiGraphics guiGraphics, double startX) {
        guiGraphics.enableScissor(3, 90, width - 3, height - 4);
//        var x = (float) this.width / 2;
//        var y = (float) this.height / 2;
//        guiGraphics.pose().translate(x, y, 0);
    }

    @Override
    public void customRenderBackground(GuiGraphics guiGraphics, int centerX, int centerY) {

    }

    private void withElementObjects(double centerX, double scaledSpacing, double centerY, int size, AbstractElement element, ResourceLocation background) {
        var withElement = AbilityReg.getWithElement(element);
        var buttonCount = withElement.size();
        var radius = 2.6 * scaledSpacing;
        var angleStep = 2 * Math.PI / buttonCount;

        double currentAngle = buttonCount % 2 == 0 ? 0 : 4.7;

        renderButton(centerX - ((double) size /2), centerY - ((double) size /2), element.iconTexture(), size * 2, withElement.getFirst(), true, BLANK, 0);

        for (Ability abilityRegistrar : withElement) {
            var res = Helpers.res(ABILITY_PREFIX + abilityRegistrar.setAbilityId() + ".png");
            var buttonX = centerX + radius * Math.cos(currentAngle);
            var buttonY = centerY + radius * Math.sin(currentAngle);

            renderButton(buttonX, buttonY, res, size, abilityRegistrar, false, background, size);
            currentAngle += angleStep;
        }
    }

    private void renderButton(double posX, double posY, ResourceLocation icons, int size, Ability ability, boolean isDummy, ResourceLocation background, int scaleHover) {
        var player = getMinecraft().player;
        if(player == null) return;

        var holder = ability.setModifiers();
        var data = player.getData(AttachmentReg.CASTER_DATA);
        var unlocked = holder != null && data.hasAbility(holder);
        var button = new WidgetSprites(background, background);
        var playerHolder = data.getHolderOptional(ability.setAbilityId());
        var holderType = playerHolder.orElse(holder);

        this.addRenderableWidget(
            menuButtonSoundAbilities(
                (int) posX, (int) posY, (Button) -> onClick(ability, holderType, unlocked),
                icons, size, isDummy, scaleHover, button, (/*!unlocked &&*/ isDummy),
                () -> onHover(ability, isDummy, holderType)
            )
        );
    }

    private void onHover(Ability ability, boolean isDummy, AbilityHolder holderType) {
        var elementType = ability.getElemenType();
        this.colour = elementType.partColourB();
        if(!isDummy) {
            this.components = SharedUI.getComponents(ability, holderType, getMinecraft().level);
        } else {
            var list = new ArrayList<Component>();

            list.add(Helpers.withStyleComponent(elementType.name(),elementType.textColourB()));


            var message = "this is just a kinda long message to show something is working";
            var maxWidth = 200;
            var formattedText = new StringSplitter((a, b) -> 10).splitLines(message, maxWidth, Style.EMPTY);
            for (var lines : formattedText) {
                list.add(Helpers.withStyleComponent(lines.getString(), elementType.textColourA()));
            }

            this.components = list;
        }
    }

    private void onClick(Ability ability, AbilityHolder abilityHolder, boolean unlocked){
        var payload = new AbilityHolderC2SP(abilityHolder);
        var guiScreen = new AugmentModificationScreen(abilityHolder, ability, size, panX, panY, zoomX, scaledSpacing, scaledXOffset, centerX, centerY);
        if(unlocked) getMinecraft().setScreen(guiScreen); else sendToServer(payload);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(!components.isEmpty()) return false;
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(!components.isEmpty()) return false;
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    protected void baseRender(GuiGraphics guiGraphics, int mouseX, int mouseY, LocalPlayer player, float centerX, float centerY, Minecraft mc) {
        var pose = guiGraphics.pose();
        var fade = SharedUI.getFadedColourBackground(0.8F);
        var i = 70;
        var j = 70;
        var x = 3F;

        boxMaker(guiGraphics, this.width/2 - 3 - i, 20, i, 20, 0, fade, fade);
        boxMaker(guiGraphics, this.width/2 - 3 - i, 65, j, 10, 0, fade, fade);
        pose.pushPose();
        pose.scale(x, x, x);
        guiGraphics.drawCenteredString(font, Helpers.withStyleComponent("Abilities", ColourStore.ABSORPTION_TEXT_YELLOW).copy(), (int)( centerX / x), 10, -1);
        pose.popPose();
        guiGraphics.drawCenteredString(font, Helpers.withStyleComponent("Perks", ColourStore.SYMPATHISER_ORANGE).copy(), (int) centerX, 70, -1);
//        guiGraphics.blit(Helpers.res("textures/359173.png"), 3,3, 0,0, this.width - 6, this.height - 6);


        if (components.isEmpty()) {
            this.rebuildWidgets();
            this.scale = 0;
        } else {
            this.scale = Math.min(this.scale + 0.08, 1);
            pose.pushPose();
            pose.translate((float) mouseX, (float) mouseY, 0);
            pose.scale((float) this.scale, (float) this.scale, 1.0f);
            pose.translate(-(float) mouseX, -(float) mouseY, 0);
            guiGraphics.renderTooltip(font, this.components, Optional.empty(), mouseX, mouseY);
            pose.popPose();
        }

        boxMaker(guiGraphics, 3, 3, (int) (centerX - 3), (int) (centerY - 3), this.colour, fade, FastColor.ARGB32.color(100, this.colour));
//        this.colour = ColourStore.HEADER_COLOUR;

        this.components = new ArrayList<>();
        super.baseRender(guiGraphics, mouseX, mouseY, player, centerX, centerY, mc);
    }

    @Override
    protected void renderWithScale(
        GuiGraphics graphics,
        int mouseX,
        int mouseY,
        LocalPlayer player,
        float centerX,
        float centerY,
        Minecraft mc
    ){
    }

}
