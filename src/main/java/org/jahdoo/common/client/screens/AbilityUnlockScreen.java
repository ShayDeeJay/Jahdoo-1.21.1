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
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.attachments.CastingData;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.common.block.augment_modification_station.AbilityModificationScreen;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.items.augments.AugmentItemHelper;
import org.jahdoo.common.networking.client2server.AbilityHolderC2SP;
import org.jahdoo.common.networking.client2server.AddAbilityC2SP;
import org.jahdoo.common.networking.client2server.RemoveAbilityC2SP;
import org.jahdoo.common.registers.AbilityReg;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.SoundReg;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.mojang.blaze3d.platform.InputConstants.KEY_LSHIFT;
import static com.mojang.blaze3d.platform.InputConstants.isKeyDown;
import static java.lang.String.valueOf;
import static net.minecraft.util.FastColor.ARGB32.color;
import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.res;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
import static org.jahdoo.common.client.Icons.*;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.button.ToggleComponent.menuButtonAbility;
import static org.jahdoo.common.client.button.ToggleComponent.menuButtonSoundAbilities;
import static org.jahdoo.common.items.augments.AugmentItemHelper.getAllAbilityModifiers;

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
    Vec3 pos;

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

        this.size = (int) (5  + (30 * this.zoomX));
        this.scaledSpacing = baseSpacing * (size / 90.0);
        this.scaledXOffset = baseXOffset * (size / 8.0); // Scale X spacing

        // Always scale relative to the screen center
        this.centerX = (double) this.width / 2 + panX + 2;
        this.centerY = (double) this.height / 2 + panY + 34;

        // Adjusted offsets to keep elements centered
        var startX = centerX - (10.1 * scaledXOffset); // Center the first element

        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                    guiGraphics.enableScissor(4, 90, width - 4, height - 5);
                }
            }
        );


        var skillHeight = centerY + (scaledSpacing / 2) - (size * 25);
        var startXS = centerX - (3.2 * scaledXOffset); // Center the first element
        skillButton(startXS, skillHeight, size);
        skillButton(startXS + 2 * scaledXOffset, skillHeight, size);
        skillButton(startXS + 4 * scaledXOffset, skillHeight, size);
        skillButton(startXS + 6 * scaledXOffset, skillHeight, size);


        withElementObjects(startX, scaledSpacing, centerY, size, ElementReg.frost(), GUI_BUTTON_FROST, GUI_BUTTON_FROST_SQUARE);
        withElementObjects(startX + 5 * scaledXOffset, scaledSpacing, centerY, size, ElementReg.inferno(), GUI_BUTTON_INFERNO, GUI_BUTTON_INFERNO_SQUARE);
        withElementObjects(startX + 10 * scaledXOffset, scaledSpacing, centerY, size, ElementReg.utility(), GUI_BUTTON_UTILITY, GUI_BUTTON_UTILITY_SQUARE);
        withElementObjects(startX + 15 * scaledXOffset, scaledSpacing, centerY, size, ElementReg.mystic(), GUI_BUTTON_MYSTIC, GUI_BUTTON_MYSTIC_SQUARE);
        withElementObjects(startX + 20 * scaledXOffset, scaledSpacing, centerY, size, ElementReg.vitality(), GUI_BUTTON_VITALITY, GUI_BUTTON_VITALITY_SQUARE);

        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                    guiGraphics.disableScissor();
                    var c = SharedUI.getFadedColourBackground(0.4F);
                    SharedUI.boxMaker(guiGraphics, width / 2 - 158, 57, 157, 14, c, c, c);
                }
            }
        );

        var spacer = 0;
        for (int i = 0; i < 12; i++){
            renderButton((double) this.width / 2 + spacer - 144, 30,  i);
            spacer += 26;
        }

        screenTab();
    }

    private void screenTab() {
        this.addRenderableWidget(
            menuButtonAbility(
                10, 10, (Button) -> { getMinecraft().setScreen(new StatScreen()); },
                STAT, 30, false, () -> {}, 0, false, "Stats"
            )
        );

        this.addRenderableWidget(
            menuButtonAbility(
                50, 10, (Button) -> { getMinecraft().setScreen(new StatScreen()); },
                ABILITY, 30, false, () -> {}, 0, true, "Abilities"
            )
        );
    }

    @Override
    public void customRenderBackground(GuiGraphics guiGraphics, int centerX, int centerY) {}

    private void skillButton(
        double centerX,
        double centerY,
        int size
    ) {
        var player = getMinecraft().player;
        if(player == null) return;

        var data = player.getData(AttachmentReg.CASTER_DATA);
        var button = new WidgetSprites(GUI_BUTTON_SKILL, GUI_BUTTON_SKILL);

        this.addRenderableWidget(
            menuButtonSoundAbilities(
                (int) centerX, (int) centerY, (Button) -> { },
                CLOCK, size * 2, false, 0, button, false, () -> {},
                false, false, false
            )
        );
    }

    private void withElementObjects(
        double centerX,
        double scaledSpacing,
        double centerY,
        int size,
        AbstractElement element,
        ResourceLocation background,
        ResourceLocation passive
    ) {
        var withElement = AbilityReg.getWithElement(element);
        var buttonCount = withElement.size();
        var radius = 8 * scaledSpacing;
        var angleStep = 2 * Math.PI / buttonCount;
        var spacer = 0;
        var currentAngle = 4.71;

        renderAbilityButton(centerX - (size * 2), centerY - (size * 2), element.iconTexture(), size * 5, withElement.getFirst(), true, BLANK, 0);

        for (Ability abilityRegistrar : withElement) {
//            renderAbilityButton(centerX - (size * 1.5), centerY + spacer + (scaledSpacing * 14), passive, size * 4, withElement.getFirst(), true, BLANK, 0);
            spacer += (int) (scaledSpacing * 8);
        }

        for (Ability ability : withElement) {
            var res = res(ABILITY_PREFIX + ability.setAbilityId() + ".png");
            var buttonX = centerX - ((double) size /2) + radius * Math.cos(currentAngle);
            var buttonY = centerY - ((double) size /2)  + radius * Math.sin(currentAngle);

            renderAbilityButton(buttonX, buttonY, res, size * 2, ability, false, background, 0);
            currentAngle += angleStep;
        }
    }

    private void renderAbilityButton(double posX, double posY, ResourceLocation icons, int size, Ability ability, boolean isDummy, ResourceLocation background, int scaleHover) {
        var player = getMinecraft().player;
        if(player == null) return;

        var holder = ability.setModifiers();
        var data = player.getData(AttachmentReg.CASTER_DATA);
        var unlocked = holder != null && data.hasAbility(holder);
        var button = new WidgetSprites(background, background);
        var playerHolder = data.getHolderOptional(ability.setAbilityId());
        var holderType = playerHolder.orElse(holder);

        var hasDependency = !CastingData.hasAbility(player, ability.requiredUnlock()) && !Objects.equals(ability.requiredUnlock(), Ability.NON);
        this.addRenderableWidget(
            menuButtonSoundAbilities(
                (int) posX, (int) posY, (Button) -> onClick(ability, holderType, unlocked, hasDependency),
                icons, size, isDummy, scaleHover, button, isDummy,
                () -> onHover(ability, isDummy, holderType, (int) posX, (int) posY, unlocked),
                isDummy, !unlocked, hasDependency
            )
        );
    }

    private void renderButton(
        double posX,
        int size,
        int index
    ) {
        var player = getMinecraft().player;
        if(player == null) return;

        var data = player.getData(AttachmentReg.CASTER_DATA);

        if(data.getAbilitySlots().size() > index){
            var typeId = data.getAbilitySlots().get(index);
            var newAbility = AbilityReg.getFirstSpellByTypeId(typeId);
            var getA = newAbility.map(Ability::getAbilityIconLocation).orElse(null);

            this.addRenderableWidget(
                menuButtonAbility(
                    (int) posX - size / 2, 56,
                    (Button) -> sendToServer(new RemoveAbilityC2SP(typeId)),
                    getA, size, true, () -> {}, index, false, ""
                )
            );
        }
    }

    private void onHover(Ability ability, boolean isDummy, AbilityHolder holder, int posX, int posY, boolean isLocked) {
        var elementType = ability.getElemenType();
        var player = getMinecraft().player;
        this.colour = elementType.partColourB();
        this.pos = new Vec3(posX, posY, 0);

        if(!isDummy) {
            var components = AugmentItemHelper.shiftForDetails(isLocked);
            var allAbilityModifiers = getAllAbilityModifiers(ability, holder, components.isEmpty(), true, player);
            allAbilityModifiers.addAll(components);
            this.components = allAbilityModifiers;
        } else {
            var list = new ArrayList<Component>();
            list.add(withStyleComponent(elementType.name(),elementType.textColourB()));
            var message = elementType.elementDescription();
            var maxWidth = 200;
            var formattedText = new StringSplitter((a, b) -> 10).splitLines(message, maxWidth, Style.EMPTY);
            for (var lines : formattedText) {
                list.add(withStyleComponent(lines.getString(), elementType.textColourA()));
            }
            this.components = list;
        }
    }

    private void onClick(Ability ability, AbilityHolder abilityHolder, boolean unlocked, boolean dependency){
        var guiScreen = new AbilityModificationScreen(abilityHolder, ability, size, panX, panY, zoomX, scaledSpacing, scaledXOffset, centerX, centerY);
        var player = getMinecraft().player;
        if(player == null) return;
        if(unlocked) {
            if(isKeyDown(getMinecraft().getWindow().getWindow(), KEY_LSHIFT)){
                sendToServer(new AddAbilityC2SP(ability.setAbilityId()));
            } else {
                getMinecraft().setScreen(guiScreen);
            }
        } else {

            if(!dependency && CastingData.checkAndConsume(player, ability.getAbilityCost())){
                sendToServer(new AbilityHolderC2SP(abilityHolder));
                player.playSound(SoundReg.UNLOCK_NOTIFICATION.get());
                this.components = new ArrayList<>();
                this.rebuildWidgets();
            } else {
                player.playSound(SoundReg.REJECT.get(), 0.5F, 1.2F);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
//        if(!components.isEmpty()) return false;
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
        var fade = SharedUI.getFadedColourBackground(1F);
        var adjustY = -6;
        var i = 70;
        var x = 3F;

        boxMaker(guiGraphics, this.width/2 - i, 20 + adjustY, i, 20, 0, fade, fade);
        pose.pushPose();
        pose.scale(x, x, x);
        guiGraphics.drawCenteredString(font, withStyleComponent("Abilities", ABSORPTION_YELLOW).copy(), (int)( centerX / x) + 1, (int) (10 + (adjustY / x)), -1);
        pose.popPose();

        this.rebuildWidgets();

        if (components.isEmpty()) {
            this.scale = 0;
        } else {
            this.scale = Math.min(this.scale + 0.08, 1);
            pose.pushPose();
            pose.translate((float) mouseX, (float) mouseY, 0);
            pose.scale((float) this.scale, (float) this.scale, 1.0f);
            pose.translate(-(float) mouseX, -(float) mouseY, 0);
            guiGraphics.renderTooltip(font, this.components, Optional.empty(), mouseX, mouseY + 20);
            pose.popPose();
        }

        boxMaker(guiGraphics, 3, 3, (int) (centerX - 3), (int) (centerY - 3), AETHER_BLUE, fade, color(100, UNIQUE_B));
        overlaySkillPoints(guiGraphics, player, 74);
        this.components = new ArrayList<>();
        super.baseRender(guiGraphics, mouseX, mouseY, player, centerX, centerY, mc);
        guiGraphics.hLine(3, this.width - 5, 89,  AETHER_BLUE);
    }

    private void overlaySkillPoints(GuiGraphics guiGraphics, LocalPlayer player, int spacer) {
        var size = 24;
        var skillPoints = CastingData.getAbilityPoints(player);
        var fade1 = SharedUI.getFadedColourBackground(0.5F);
        var length = valueOf(skillPoints).length();
        var i1 = this.width / 2 + spacer;
        var i2 = -6;
        boxMaker(guiGraphics, i1, 20 + i2, 15 + (length * length), 10, 1, fade1, fade1);
        guiGraphics.drawString(font, withStyleComponent(skillPoints + "", PERK_GREEN), i1 + 22, 26 + i2, -1);
        guiGraphics.blit(SKILL_POINT, i1 - 2, 18 + i2, 0, 0, size, size, size, size);
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
