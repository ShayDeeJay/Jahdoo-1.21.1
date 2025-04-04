package org.jahdoo.common.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.augment_modification_station.AugmentModificationScreen;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.networking.client2server.AbilityHolderC2SP;
import org.jahdoo.common.registers.AbilityReg;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.ElementReg;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;
import static org.jahdoo.common.client.Icons.ABILITY_PREFIX;
import static org.jahdoo.common.client.Icons.GUI_BUTTON;
import static org.jahdoo.common.client.button.ToggleComponent.menuButtonSound;

public class AbilityUnlockScreen extends AbstractPanableScreen {
    List<Component> components = new ArrayList<>();
    int baseSpacing = 90;
    int baseXOffset = 50; // Original spacing between elements
    int size;
    double scaledSpacing;
    double scaledXOffset; // Scale X spacing
    double centerX; // Screen center
    double centerY; // Screen center
    boolean cameFromPreviousScreen;

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
        this.cameFromPreviousScreen = true;
        this.panX = panX;
        this.panY = panY;
        this.zoomX = zoomX;
    }

    public AbilityUnlockScreen(){}

    @Override
    protected void init() {
        super.init();

        this.size = (int) (20 + 30 * this.zoomX);
        this.scaledSpacing = baseSpacing * (size / 90.0);
        this.scaledXOffset = baseXOffset * (size / 30.0); // Scale X spacing

        // Always scale relative to the screen center
        this.centerX = (double) this.width / 2 + panX;
        this.centerY = (double) this.height / 2 + panY;
        if(!this.cameFromPreviousScreen) {
        }

        // Adjusted offsets to keep elements centered
        double startX = centerX - (9.6 * scaledXOffset); // Center the first element

        withElementObjects(startX, scaledSpacing, centerY, size, ElementReg.frost());
        withElementObjects(startX + 5 * scaledXOffset, scaledSpacing, centerY, size, ElementReg.inferno());
        withElementObjects(startX + 10 * scaledXOffset, scaledSpacing, centerY, size, ElementReg.mystic());
        withElementObjects(startX + 15 * scaledXOffset, scaledSpacing, centerY, size, ElementReg.vitality());
        withElementObjects(startX + 20 * scaledXOffset, scaledSpacing, centerY, size, ElementReg.utility());
    }


    private void withElementObjects(double centerX, double scaledSpacing, double centerY, int size, AbstractElement element) {
        var withElement = AbilityReg.getWithElement(element);
        var buttonCount = withElement.size();
        var radius = 1.5 * scaledSpacing;
        var angleStep = 2 * Math.PI / buttonCount;
        double currentAngle = (buttonCount % 2 == 0) ? 0 : (-Math.PI / buttonCount) + 2.85;

        renderButton(centerX, centerY, element.iconTexture(), size, withElement.getFirst(), true);

        for (Ability abilityRegistrar : withElement) {
            var res = Helpers.res(ABILITY_PREFIX + abilityRegistrar.setAbilityId() + ".png");
            var buttonX = centerX + radius * Math.cos(currentAngle);
            var buttonY = centerY + radius * Math.sin(currentAngle);

            renderButton(buttonX, buttonY, res, size, abilityRegistrar, false);
            currentAngle += angleStep;
        }
    }

    private void renderButton(double withPanX, double withPanY, ResourceLocation icons, int size, Ability ability, boolean isDummy) {
        var posX = (int) (withPanX - 15 - (double) size / 2);
        var posY = (int) (withPanY - 15 - (double) size / 2);
        var player = getMinecraft().player;
        if(player == null) return;


        var holder = ability.setModifiers();
        var data = player.getData(AttachmentReg.CASTER_DATA);
        var unlocked = holder != null && data.hasAbility(holder);
        var button = new WidgetSprites(GUI_BUTTON, GUI_BUTTON);

        var playerHolder = data.getHolderOptional(ability.setAbilityId());

//        if(playerHolder.isPresent() && player.tickCount % 50 == 0){
//            System.out.println(playerHolder);
//        }

        var holderType = playerHolder.orElse(holder);

        this.addRenderableWidget(
            menuButtonSound(
                posX, posY, (Button) -> onClick(ability, holderType, unlocked),
                icons, size, isDummy, 0, button, (/*!unlocked &&*/ !isDummy),
                () -> {
                    if(!isDummy){
                        this.components = SharedUI.getComponents(ability, holderType, getMinecraft().level);
                    }
                }
            )
        );
    }

    private void onClick(Ability ability, AbilityHolder abilityHolder, boolean unlocked){
        if(!unlocked){
            sendToServer(new AbilityHolderC2SP(abilityHolder));
        } else {
            getMinecraft().setScreen(new AugmentModificationScreen(abilityHolder, ability, size, panX, panY, zoomX, scaledSpacing, scaledXOffset, centerX, centerY));
            getMinecraft().setScreen(new AugmentModificationScreen(abilityHolder, ability, size, panX, panY, zoomX, scaledSpacing, scaledXOffset, centerX, centerY));
        }
    }

    @Override
    protected void baseRender(GuiGraphics guiGraphics, int mouseX, int mouseY, LocalPlayer player, float centerX, float centerY, Minecraft mc) {
        this.rebuildWidgets();
        guiGraphics.renderTooltip(font, this.components, Optional.empty(), mouseX, mouseY);
        super.baseRender(guiGraphics, mouseX, mouseY, player, centerX, centerY, mc);
        this.components = new ArrayList<>();
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
    ){}

}
