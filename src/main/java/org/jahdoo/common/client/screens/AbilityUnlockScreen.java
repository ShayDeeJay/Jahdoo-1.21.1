package org.jahdoo.common.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.networking.client2server.*;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.AbilityReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.SkillReg;
import org.jahdoo.trial_nexus.ability.Ability;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.ability.AbilityComponentHelper;
import org.jahdoo.trial_nexus.ability.skills.AbstractSkill;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jetbrains.annotations.NotNull;
import org.shaydee.shaydeeapi.helpers.ClientHelpers;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.mojang.blaze3d.platform.InputConstants.KEY_LCONTROL;
import static com.mojang.blaze3d.platform.InputConstants.KEY_LSHIFT;
import static net.minecraft.util.FastColor.ARGB32.color;
import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;
import static org.jahdoo.common.client.Icons.*;
import static org.jahdoo.common.client.SharedUI.*;
import static org.jahdoo.common.client.button.ToggleComponent.menuButtonAbility;
import static org.jahdoo.common.client.button.ToggleComponent.menuButtonSoundAbilities;
import static org.jahdoo.trial_nexus.ability.AbilityComponentHelper.getAllAbilityModifiers;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.texture;

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
    double originalScale;

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
        var window = getMinecraft().getWindow();
        this.size = Math.max(3, (int) (((double) window.getWidth() / 500) / (window.getGuiScale()/ 4) + (30 * this.zoomX)));
        this.scaledSpacing = baseSpacing * (size / 90.0);
        this.scaledXOffset = baseXOffset * (size / 10.0);
        this.centerX = (double) this.width / 2 + panX + 2;
        this.centerY = (double) this.height / 2 + panY + 60;


        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                    guiGraphics.enableScissor(4, 55, width - 4, height - 5);
                }
            }
        );

        overlaySkills();
        overlayAbilities();

        this.addRenderableOnly(
            new Overlay() {
                @Override
                public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
                    guiGraphics.disableScissor();
                    //Outline ability slots
                    SharedUI.boxMaker(guiGraphics, width / 2 - 159, 54, 158, 15, uiColour(), uiFade(), uiFade());
                    //Outline loadout slots
                    SharedUI.boxMaker(guiGraphics, width - 25, 54, 11, 29, uiColour(), uiFade(), uiFade());
                    //Outline reset button
                    SharedUI.boxMaker(guiGraphics, width - 25, 111, 11, 11, uiColour(), uiFade(), uiFade());
                }
            }
        );

        overlayAbilitySlots();
        renderResetButton();
        centerViewButton();
        loadoutSelection();
    }

    private void overlayAbilitySlots() {
        var spacer = 0;
        for (int i = 0; i < 12; i++){
            renderButton((double) this.width / 2 + spacer - 144, 30,  i);
            spacer += 26;
        }
    }

    private void overlaySkills() {
        var skillHeight = centerY + (scaledSpacing / 2) - (size * 25);
        var allSkills = SkillReg.getAllSkills();
        var startXS = centerX - (4.2 * scaledXOffset+1); // Center the first element

        var skillSpacer = 0;
        for (var allSkill : allSkills) {
            skillButton(startXS + skillSpacer * scaledXOffset, skillHeight, size, allSkill);
            skillSpacer += 2;
        }
    }

    private void overlayAbilities() {
        var startX = centerX - (10.1 * scaledXOffset);
        withElementObjects(startX, scaledSpacing, centerY, size, ElementReg.frost(), GUI_BUTTON_FROST, GUI_BUTTON_FROST_SQUARE);
        withElementObjects(startX + 5 * scaledXOffset, scaledSpacing, centerY, size, ElementReg.inferno(), GUI_BUTTON_INFERNO, GUI_BUTTON_INFERNO_SQUARE);
        withElementObjects(startX + 10 * scaledXOffset, scaledSpacing, centerY, size, ElementReg.utility(), GUI_BUTTON_UTILITY, GUI_BUTTON_UTILITY_SQUARE);
        withElementObjects(startX + 15 * scaledXOffset, scaledSpacing, centerY, size, ElementReg.mystic(), GUI_BUTTON_MYSTIC, GUI_BUTTON_MYSTIC_SQUARE);
        withElementObjects(startX + 20 * scaledXOffset, scaledSpacing, centerY, size, ElementReg.vitality(), GUI_BUTTON_VITALITY, GUI_BUTTON_VITALITY_SQUARE);
    }

    private void skillButton(
        double centerX,
        double centerY,
        int size,
        AbstractSkill skill
    ) {
        var player = getMinecraft().player;
        if(player == null) return;

        var data = player.getData(AttachmentReg.CASTER_DATA);
        var button = new WidgetSprites(GUI_BUTTON_SKILL, GUI_BUTTON_SKILL);
        var haveSkill = data.getUnlockedSkills().contains(skill.id());
        var dependency = skill.levelRequirement() <= data.getLevel();
        var component = new ArrayList<Component>();
        var headerColour = color(100, 116, 245);

        if(AbilityComponentHelper.shiftForDetails(component, false)){
            component.add(component.size()-1, TextHelpers.withStyleComponent(TextHelpers.stringIdToName(skill.id()), headerColour));
        } else {
            component.addAll(toComponent(skill.description(), TextHelpers.stringIdToName(skill.id()), headerColour, color(161, 171, 255)));
        }

        if(!haveSkill){
            component.addLast(Component.empty());
            var prefix = TextHelpers.withStyleComponent("Cost: ", ColourHelpers.getSubHeaderColour());
            var suffix = TextHelpers.withStyleComponent("◆ " + skill.unlockCost() + " Skill Points", ColourHelpers.getPerkGreen()).copy();
            component.addLast(prefix.copy().append(suffix));

            if(!dependency){
                var prefix1 = TextHelpers.withStyleComponent("Requires Level: ", ColourHelpers.getSubHeaderColour());
                var suffix1 = TextHelpers.withStyleComponent(skill.levelRequirement() + "", headerColour).copy();
                component.addLast(prefix1.copy().append(suffix1));
            }
        }

        if(haveSkill){
            var skillActive = CasterData.hasSkill(player, skill.id());
            var prefix1 = TextHelpers.withStyleComponent("Status: ", ColourHelpers.getSubHeaderColour());
            var suffix1 = TextHelpers.withStyleComponent(skillActive ? "Active" : "Inactive", skillActive ? ColourHelpers.getMagnetRangeGreen() : ColourHelpers.getMagnetStrengthRed()).copy();
            component.addLast(Component.empty());
            component.addLast(prefix1.copy().append(suffix1));
        }

        this.addRenderableWidget(
            menuButtonSoundAbilities(
                (int) centerX, (int) centerY, (Button) -> onClickSkill(skill, haveSkill, !dependency),
                skill.icon(), size * 2, false, 0, button, false,
                () -> this.components = component,
                false, !haveSkill || !dependency, dependency, true
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

        //For future implementation of tree based rewards
//        for (Ability abilityRegistrar : withElement) {
//            renderAbilityButton(centerX - (size * 1.5), centerY + spacer + (scaledSpacing * 14), passive, size * 4, withElement.getFirst(), true, BLANK, 0);
//            spacer += (int) (scaledSpacing * 8);
//        }

        for (var ability : withElement) {
            var res = texture(ABILITY_PREFIX + ability.setAbilityId());
            var buttonX = centerX - ((double) size /2) + radius * Math.cos(currentAngle);
            var buttonY = centerY - ((double) size /2)  + radius * Math.sin(currentAngle);

            renderAbilityButton(buttonX, buttonY, res, size * 2, ability, false, background, 0);
            currentAngle += angleStep;
        }
    }

    private void renderAbilityButton(
        double posX,
        double posY,
        ResourceLocation icons,
        int size,
        Ability ability,
        boolean isDummy,
        ResourceLocation background,
        int scaleHover
    ) {
        var player = getMinecraft().player;
        if(player == null) return;

        var holder = ability.setModifiers();
        var data = player.getData(AttachmentReg.CASTER_DATA);
        var unlocked = holder != null && data.hasAbility(holder);
        var button = new WidgetSprites(background, background);
        var playerHolder = data.getHolderOptional(ability.setAbilityId());
        var holderType = playerHolder.orElse(holder);
        var hasDependency = ability.levelRequirement() <= data.getLevel();

        this.addRenderableWidget(
            menuButtonSoundAbilities(
                (int) posX, (int) posY, (Button) -> onClickAbility(ability, holderType, unlocked, !hasDependency),
                icons, size, isDummy, scaleHover, button, isDummy,
                () -> onHover(ability, isDummy, holderType, (int) posX, (int) posY, unlocked),
                isDummy, !unlocked, hasDependency, false
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
                    (int) posX - size / 2,
                    54,
                    size,
                    index,
                    "",
                    false,
                    true,
                    index < data.getAllowedSlots(),
                    getA,
                    (Button) -> sendToServer(new RemoveAbilityC2SP(typeId)),
                    () -> {}
                )
            );
        }
    }

    private void renderResetButton() {
        var player = getMinecraft().player;
        if(player == null) return;

        var size = 20;
        var posX = this.width - size - 4;
        var posY = 112;
        this.addRenderableWidget(
            menuButtonAbility(
                posX,
                posY,
                size,
                0,
                "",
                false,
                true,
                true,
                REFRESH,
                (Button) -> onResetSkillPress(player),
                this::onResetSkillHover
            )
        );
    }

    private void centerViewButton() {
        var isCenteredView = this.zoomX == 0 && this.panY == 0 && this.panX == 0;

        if(!isCenteredView){
            var i = 40;
            this.addRenderableWidget(
                menuButtonAbility(
                    this.width - i,
                    this.height - i,
                    20,
                    0,
                    "",
                    false,
                    true,
                    true,
                    CENTER,
                    this::centerScreen,
                    () -> {}
                )
            );
        }
    }

    private void loadoutSelection() {
        var size = 40;
        var mc = minecraft;
        if(mc == null) return;

        var player = mc.player;
        if(player == null) return;

        var data = player.getData(AttachmentReg.CASTER_DATA);
        var size1 = 20;
        var yLayout = 40;

        for(int i = 1; i < 4; i++){
            int finalI = i;
            var b1 = data.getLoadoutIndex() == finalI;
            var b = CasterData.hasLoadout(player, finalI);
            var posX = this.width - size + 16;
            var posY = 51 + yLayout;
            this.addRenderableWidget(
                menuButtonAbility(
                    posX,
                    posY,
                    size1,
                    3 - finalI,
                    "",
                    b1,
                    true,
                    !b1,
                    b || ClientHelpers.isKeyDown(KEY_LSHIFT),
                    data.getLoadouts().get(finalI) != null ? DATA : null,
                    (button) -> setLoadout(finalI, b),
                    () -> this.centerScreenHover(b && !b1)
                )
            );
            yLayout -= 18;
        }
    }

     private void setLoadout(int index, boolean canPress){
         var mc = minecraft;
         if(mc == null) return;

         var player = mc.player;
         if(player == null) return;

         var newLoadout = CasterData.isNewLoadout(player);
         var shiftDown = ClientHelpers.isKeyDown(KEY_LSHIFT);
         var leftClick = ClientHelpers.isKeyDown(KEY_LCONTROL);

         var hasLoadout = CasterData.hasLoadout(player, index);
         if (!shiftDown && hasLoadout) sendToServer(new RegretAbilitiesC2SP(false));
         sendToServer(new SaveLoadoutC2SP(index, shiftDown, newLoadout, canPress, leftClick));
     }

     private boolean canAffordXPCost(Player player){
         var reductionAmount = getResetCost(player);
         var currentXp = player.experienceLevel;
         return currentXp >= reductionAmount;
    }

    private void centerScreen(Button button){
        this.zoomX = 0;
        this.panY = 0;
        this.panX = 0;
    }

    private void centerScreenHover(boolean canShow){
//       if(canShow) originalScale = -1;
    }

    private void onResetSkillPress(Player player){
        if(canAffordXPCost(player) && !CasterData.isNewLoadout(player)){
            var reductionAmount = getResetCost(player);
            var currentXp = player.experienceLevel;
            PacketDistributor.sendToServer(new PlayerExpC2SP(currentXp - reductionAmount));
            sendToServer(new RegretAbilitiesC2SP(true));
        }
    }

    private void onResetSkillHover(){
        this.originalScale = -1;
    }

    private void onHover(Ability ability, boolean isDummy, AbilityHolder holder, int posX, int posY, boolean isLocked) {
        var elementType = ability.getElemenType();
        var player = getMinecraft().player;
        this.colour = elementType.partColourB();
        this.pos = new Vec3(posX, posY, 0);

        if(!isDummy) {
            var components = AbilityComponentHelper.shiftForDetails(isLocked, modifiableHolder(holder));
            var allAbilityModifiers = getAllAbilityModifiers(ability, holder, components.isEmpty(), true, player);
            allAbilityModifiers.addAll(!isLocked ? 1 : allAbilityModifiers.size(), components);
            this.components = allAbilityModifiers;
        } else {
            var element = elementType.elementDescription();
            var name = elementType.name();
            var colour1 = elementType.textColourB();
            var colour2 = elementType.textColourA();
            this.components = toComponent(element, name, colour1, colour2);
        }
    }

    public static @NotNull ArrayList<Component> toComponent(String body, String name, int colour1, int colour2) {
        var list = new ArrayList<Component>();
        var maxWidth = 200;
        var formattedText = new StringSplitter((a, b) -> 10).splitLines(body, maxWidth, Style.EMPTY);

        list.add(TextHelpers.withStyleComponent(name, colour1));
        for (var lines : formattedText) {
            list.add(TextHelpers.withStyleComponent(lines.getString(), colour2));
        }
        return list;
    }

    private void onClickSkill(AbstractSkill skill, boolean unlocked, boolean dependency){
        var player = getMinecraft().player;
        if(player == null) return;
        if(unlocked) {
            sendToServer(new UnlockedSkillsC2SP(skill.id(), skill.unlockCost()));
        } else {
            if(!dependency && CasterData.checkAndConsume(player, skill.unlockCost())){
                sendToServer(new UnlockedSkillsC2SP(skill.id(), skill.unlockCost()));
                player.playSound(SoundReg.UNLOCK_NOTIFICATION.get());
            } else {
                player.playSound(SoundReg.REJECT.get(), 0.5F, 1.2F);
            }
        }
    }

    private void onClickAbility(Ability ability, AbilityHolder abilityHolder, boolean unlocked, boolean dependency){
        var guiScreen = new AbilityModificationScreen(abilityHolder, ability, size, panX, panY, zoomX, scaledSpacing, scaledXOffset, centerX, centerY);
        var mc = getMinecraft();
        var player = mc.player;
        if(player == null) return;
        if(unlocked) {
            if(ClientHelpers.isKeyDown(KEY_LSHIFT)){
                var data = player.getData(AttachmentReg.CASTER_DATA);
                var abilitySlots = data.getAbilitySlots();
                var validSlots = abilitySlots.stream().filter(i -> !i.isEmpty()).toList();
                if(validSlots.size() < data.getAllowedSlots() && !abilitySlots.contains(ability.setAbilityId())){
                    sendToServer(new AddAbilityC2SP(ability.setAbilityId()));
                }
            } else {
                var entryStream = modifiableHolder(abilityHolder);
                if(!entryStream) mc.setScreen(guiScreen);
            }
        } else {
            var abilityCost = ability.getAbilityCost();
            if(!dependency && CasterData.checkAndConsume(player, abilityCost)){
                sendToServer(new AbilityHolderC2SP(abilityHolder, abilityCost));
                player.playSound(SoundReg.UNLOCK_NOTIFICATION.get());
            } else {
                player.playSound(SoundReg.REJECT.get(), 0.5F, 1.2F);
            }
        }
    }

    private static boolean modifiableHolder(AbilityHolder abilityHolder) {
        return abilityHolder.data()
            .abilityProperties()
            .entrySet()
            .stream()
            .filter(s -> !Objects.equals(s.getKey(), AbilityBuilder.MANA_COST))
            .filter(s -> !Objects.equals(s.getKey(), AbilityBuilder.COOLDOWN))
            .toList()
            .isEmpty();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
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

        this.components = new ArrayList<>();
        super.baseRender(guiGraphics, mouseX, mouseY, player, centerX, centerY, mc);
        overlaySkillPoints(guiGraphics, player, 74);
        if(this.originalScale == -1){
            experienceCost(guiGraphics, mouseX - 30, mouseY - 24, 100, 1);

            this.originalScale = 0;
        }
    }

    private void overlaySkillPoints(GuiGraphics guiGraphics, LocalPlayer player, int spacer) {
        var size = 24;
        var skillPoints = CasterData.getAbilityPoints(player);
        var i1 = 20;
        var i2 = -2;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 100);
        guiGraphics.drawCenteredString(font, TextHelpers.withStyleComponent(skillPoints + "", ColourHelpers.getPerkGreen()), i1 + 30, 27 + i2, -1);
        guiGraphics.blit(SKILL_POINT, i1 - 1, 18 + i2, 0, 0, size, size, size, size);
        guiGraphics.pose().popPose();
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

    private int getResetCost(Player player){
        return Math.min((CasterData.getLevel(player)/20) * 20, 120);
    }

    private void experienceCost(GuiGraphics guiGraphics, int mouseX, int mouseY, int i, int startY) {
        var player = this.getMinecraft().player;
        if(player == null) return;
        var exp = player.experienceLevel;
        var getMaxCost = getResetCost(player);
        var expColour = exp >= getMaxCost ? 8453920 : -2070938;
        var refinementPotential = Component.literal(getResetCost(player) + "/" + player.experienceLevel);
        var offsetX = 0;
        var offsetY = -27;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0,40,100);
        boxMaker(guiGraphics, mouseX - 26 + offsetX, mouseY + offsetY, 26, 13, ColourHelpers.getBorderColour(), fadeBlack(0.6f));
        drawStringWithBackground(guiGraphics, this.font, refinementPotential, mouseX + offsetX, mouseY + 15 + offsetY, 0, expColour, true);
        guiGraphics.drawCenteredString(font, "Exp Cost", mouseX + offsetX, mouseY + 4 + offsetY, -1);
        guiGraphics.pose().popPose();
//        renderMiniXPBar(guiGraphics, mouseX - 42, mouseY+45, this.getMinecraft());
    }
}
