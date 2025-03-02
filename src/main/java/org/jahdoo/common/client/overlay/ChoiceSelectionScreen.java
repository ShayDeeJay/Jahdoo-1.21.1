package org.jahdoo.common.client.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.boon.Boon;
import org.jahdoo.ascension.boon.BoonSelection;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.ascension.utils.ColourStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.client.Minecraft.*;
import static net.minecraft.network.chat.Component.*;
import static net.minecraft.util.FastColor.*;
import static net.minecraft.util.FastColor.ARGB32.*;
import static org.jahdoo.ascension.boon.BoonSelection.*;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.common.client.SharedUI.*;
import static org.jahdoo.common.registers.ItemReg.*;

public class ChoiceSelectionScreen extends Screen  {

    private int selection;
    private float fade;
    private float fadeEntryBack;
    private int selectionOffset;
    private final List<Boon> boons = new ArrayList<>();

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    public ChoiceSelectionScreen() {
        super(literal("Choice Selection Screen"));

        for (var i = 0; i < 3; i++){
            boons.add(Helpers.listRandom(addAttribute(getInstance().player)));
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int getSize(){
        return 70;
    }

    private void doOnFirst(int first){
        sharedPress();
        boons.get(1).execute().run();
    }

    private void doOnSecond(int second){
        sharedPress();
        boons.getFirst().execute().run();
    }

    private void doOnThird(int third){
        sharedPress();
        boons.get(2).execute().run();
    }

    private void increaseAlpha(int offset) {
        if(fade < 120) fade += 10;
        this.selectionOffset = offset;
    }

    private void selectionBox(GuiGraphics guiGraphics, int xPos, int color, int colour2) {
        var border = getFadedColourBackground(0f);
        boxMaker(guiGraphics, selectionOffset, 0, xPos, this.height/2, border, color);
//        boxMaker(guiGraphics, selectionOffset, this.height/2, xPos, this.height/4, border, colour2);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        sectionHighlight((int) mouseX, this::doOnFirst, this::doOnSecond, this::doOnThird);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void playSound(int num) {
        if(this.selection != num){
            getMinecraft().player.playSound(SoundReg.SELECT.get(), 1F, 2F);
        }
    }

    private void selectionSections(GuiGraphics guiGraphics) {
        var edges = getFadedColourBackground(0.1f);
        var centre = getFadedColourBackground(fadeEntryBack);

        for (var position : getPositions()) {
            boxMaker(guiGraphics, position, -1, getSize(), this.height, edges, centre);
        }
    }

    private void sharedPress() {
        var minecraft = this.getMinecraft();
        var player = minecraft.player;

        if (player == null) return;

        player.playSound(SoundEvents.VAULT_OPEN_SHUTTER, 1F, 1F);
        minecraft.setScreen(null);
    }

    private List<Integer> getPositions(){
        var posFirst = this.width / 2 - getSize();
        var spacer = getSize() * 2 + 20;
        var posSecond = posFirst - spacer;
        var posThird = posFirst + spacer;

        return List.of(posFirst, posSecond, posThird);
    }

    private void textSelection(GuiGraphics guiGraphics, ArrayList<Component> tooltip, int i, Font font) {
        if(this.fadeEntryBack <= 0.4) return;
        var index = 0;

        for (var position : getPositions()) {
            var spacer = 0;
            var x = position + 71;
            var y = Math.min((i - (float) tooltip.size() / 2) + spacer, fadeEntryBack * 1200) + 4;
            var boon = boons.get(index);
            var label = boon.label();
            var splitText = font.getSplitter().splitLines(label, 120, Style.EMPTY);
            var height = 0;

            for (var splitLine : splitText) {
                centeredStringNoShadow(guiGraphics, font, withStyleComponent(splitLine.getString(), boon.colour()), x, (int) y + height - (splitText.size() * 5), -1, true);
                height += 10;
            }

            index++;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        var itemStack = new ItemStack(WAND_ITEM_FROST.get());
        var tooltip = new ArrayList<>(getTooltipFromItem(getMinecraft(), itemStack));
        var fadedColourBackground = color((int) Math.max(0, fade), MAGNET_RANGE_GREEN);
        var fadedColourBackgroundA = color((int) Math.max(0, fade), MAGNET_STRENGTH_RED);
        var i = this.height / 2;

        this.renderBlurredBackground(partialTick);
        this.fadeEntryBack = Math.min(0.6F, this.fadeEntryBack + 0.03F);

        sectionHighlight(mouseX, this::increaseAlpha, this::increaseAlpha, this::increaseAlpha);
        selectionSections(guiGraphics);
        selectionBox(guiGraphics, getSize(), fadedColourBackground, fadedColourBackgroundA);
        textSelection(guiGraphics, tooltip, i, font);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void sectionHighlight(int mouseX, Consumer<Integer> doOn0, Consumer<Integer> doOn1, Consumer<Integer> doOn2) {
        var pos = getPositions();
        var pos1 = pos.getFirst();
        var pos2 = pos.get(1);
        var pos3 = pos.get(2);
        var spaceBy = getSize() * 2;

        if(mouseX > pos2 - 1 &&  mouseX < pos2 + spaceBy) {
            playSound(1);
            doOn0.accept(pos2);
            this.selection = 1;
        } else if (mouseX > pos1 - 1  &&  mouseX < pos1 + spaceBy) {
            playSound(2);
            doOn1.accept(pos1);
            this.selection = 2;
        } else if (mouseX > pos3 - 1 &&  mouseX < pos3 + spaceBy) {
            playSound(3);
            doOn2.accept(pos3);
            this.selection = 3;
        } else {
            if(fade > 0) fade -= 10;
            this.selection = 0;
        }
    }

}
