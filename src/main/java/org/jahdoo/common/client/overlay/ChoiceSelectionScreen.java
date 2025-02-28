package org.jahdoo.common.client.overlay;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.ascension.utils.ColourStore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.jahdoo.common.client.SharedUI.*;

public class ChoiceSelectionScreen extends Screen  {

    private float fade;
    private float fadeEntryBack;
    private int selectionOffset;

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    public ChoiceSelectionScreen() {
        super(Component.literal("Choice Selection Screen"));
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
    }

    private void doOnSecond(int second){
        sharedPress();
    }

    private void doOnThird(int third){
        sharedPress();
    }

    private void increaseAlpha(int offset) {
        if(fade < 120) fade += 10;
        this.selectionOffset = offset;
    }

    private void selectionBox(GuiGraphics guiGraphics, int xPos, int color) {
        var border = getFadedColourBackground(0f);
        boxMaker(guiGraphics, selectionOffset, 0, xPos, this.height, border, color);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        sectionHighlight((int) mouseX, this::doOnFirst, this::doOnSecond, this::doOnThird);
        return super.mouseClicked(mouseX, mouseY, button);
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
        player.playSound(SoundReg.SELECT.get(),2,1);
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

        for (var position : getPositions()) {
            var spacer = 0;
            for (var component : tooltip) {
                var x = position + 71;
                var y = Math.min((i - (float) tooltip.size() / 2) + spacer - 15, fadeEntryBack * 1200);
                centeredStringNoShadow(guiGraphics, font, component, x, (int) y, -1, false);
                spacer += 10;
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        var font = getMinecraft().font;
        var itemStack = new ItemStack(ItemReg.WAND_ITEM_FROST.get());
        var tooltip = new ArrayList<>(getTooltipFromItem(getMinecraft(), itemStack));
        var fadedColourBackground = FastColor.ARGB32.color((int) Math.max(0, fade), ColourStore.SYMPATHISER_ORANGE);
        var i = this.height / 2;

        this.renderBlurredBackground(partialTick);
        this.fadeEntryBack = Math.min(0.6F, this.fadeEntryBack + 0.03F);

        sectionHighlight(mouseX, this::increaseAlpha, this::increaseAlpha, this::increaseAlpha);
        selectionSections(guiGraphics);
        selectionBox(guiGraphics, getSize(), fadedColourBackground);
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
            doOn0.accept(pos2);
            getMinecraft().player.playSound(SoundReg.SELECT.get());
        } else if (mouseX > pos1 - 1  &&  mouseX < pos1 + spaceBy) {
            doOn1.accept(pos1);
        } else if (mouseX > pos3 - 1 &&  mouseX < pos3 + spaceBy) {
            doOn2.accept(pos3);
        } else {
            if(fade > 0) fade -= 10;
        }
    }

}
