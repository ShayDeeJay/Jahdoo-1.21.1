package org.jahdoo.common.client.screens;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.boon.player_boons.Boon;
import org.jahdoo.common.registers.SoundReg;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ascension.boon.player_boons.BoonSelection.getNegativeBoon;
import static org.jahdoo.ascension.boon.player_boons.BoonSelection.getPositiveBoon;
import static org.jahdoo.ascension.utils.ColourStore.MAGNET_RANGE_GREEN;
import static org.jahdoo.ascension.utils.ColourStore.MAGNET_STRENGTH_RED;
import static org.jahdoo.common.client.SharedUI.*;
import static org.jahdoo.common.registers.ItemReg.WAND_ITEM_FROST;

public class BoonSelectionScreen extends Screen  {

    private int selection;
    private float fade;
    private float fadeEntryBack;
    private int selectionOffset;
    private final List<Boon> boonsPositive = new ArrayList<>();
    private final List<Boon> boonsNegative = new ArrayList<>();

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    public BoonSelectionScreen() {
        super(literal("Choice Selection Screen"));

        for (var i = 0; i < 3; i++){
            boonsPositive.add(getPositiveBoon());
            boonsNegative.add(getNegativeBoon());
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
        boonsPositive.getFirst().execute().run();
        boonsNegative.getFirst().execute().run();
    }

    private void doOnSecond(int second){
        sharedPress();
        boonsPositive.get(1).execute().run();
        boonsNegative.get(1).execute().run();
    }

    private void doOnThird(int third){
        sharedPress();
        boonsPositive.get(2).execute().run();
        boonsNegative.get(2).execute().run();
    }

    private void increaseAlpha(int offset) {
        if(fade < 150) fade += 10;
        this.selectionOffset = offset;
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
        var edges = fadeBlack(0.1f);
        var centre = fadeBlack(fadeEntryBack);

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

    private void selectionBox(GuiGraphics guiGraphics, int xPos) {
        if(selection <= 0) return;
        var border = fadeBlack(0f);
        var index = this.selection - 1;
        var hasNegativeBoon = boonsNegative.get(index) != Boon.EMPTY;
        var hasPositiveBoon = boonsPositive.get(index) != Boon.EMPTY;

        if(hasPositiveBoon){
            var posColour = color((int) Math.max(0, fade), MAGNET_RANGE_GREEN);
            boxMaker(guiGraphics, selectionOffset, 0, xPos, this.height / (hasNegativeBoon ? 4 : 2), border, posColour);
        }

        if (hasNegativeBoon) {
            var negColour = color((int) Math.max(0, fade), MAGNET_STRENGTH_RED);
            boxMaker(guiGraphics, selectionOffset, this.height, xPos, -this.height / (hasPositiveBoon ? 4 : 2), border, negColour);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        var itemStack = new ItemStack(WAND_ITEM_FROST.get());
        var tooltip = new ArrayList<>(getTooltipFromItem(getMinecraft(), itemStack));
        var i = this.height / 2;

        renderBlurredBackground(partialTick);
        fadeEntryBack = Math.min(0.9F, fadeEntryBack + 0.03F);

        sectionHighlight(mouseX, this::increaseAlpha, this::increaseAlpha, this::increaseAlpha);
        selectionSections(guiGraphics);
        selectionBox(guiGraphics, getSize());

        textSelection(guiGraphics, tooltip, i, font);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void sectionHighlight(
        int mouseX,
        Consumer<Integer> doOn0,
        Consumer<Integer> doOn1,
        Consumer<Integer> doOn2
    ) {
        var pos = getPositions();
        var pos1 = pos.getFirst();
        var pos2 = pos.get(1);
        var pos3 = pos.get(2);
        var spaceBy = getSize() * 2;

        if(mouseX > pos1 - 1 &&  mouseX < pos1 + spaceBy) {
            playSound(1);
            doOn0.accept(pos1);
            this.selection = 1;
        } else if (mouseX > pos2 - 1  &&  mouseX < pos2 + spaceBy) {
            playSound(2);
            doOn1.accept(pos2);
            this.selection = 2;
        } else if (mouseX > pos3 - 1 &&  mouseX < pos3 + spaceBy) {
            playSound(3);
            doOn2.accept(pos3);
            this.selection = 3;
        } else {
            if(fade > 0) fade -= 10;
            this.selection = 0;
            this.selectionOffset = -1000;
        }
    }

    private void textSelection(GuiGraphics guiGraphics, ArrayList<Component> tooltip, int i, Font font) {
        if(this.fadeEntryBack <= 0.4) return;
        var index = 0;

        for (var position : getPositions()) {
            var boon = boonsPositive.get(index);
            var boonNeg = boonsNegative.get(index);

            var labelPositive = boon.label();
            var labelNegative = boonNeg.label();

            var hasNegativeBoon = boonNeg != Boon.EMPTY;
            var hasPositiveBoon = boon != Boon.EMPTY;

            var x = position + 71;
            var y = Math.min((i - (float) tooltip.size() / 2) , fadeEntryBack * 1200) -44;
            var height = 0;
            var isHovered = this.selectionOffset == position;
            var scale = 20;

            if(hasPositiveBoon){
                var adjustHeight = this.height / (hasNegativeBoon ? 4 : 1);
                var adjustY = hasNegativeBoon ? 0 : 70;

                boxMaker(guiGraphics, x - 71, -1, 70, adjustHeight + 1, MAGNET_RANGE_GREEN, 0);
                guiGraphics.blit(boon.icon(), position + 60, (int) y - 55 + adjustY, 0, 0, scale, scale, scale, scale);
                for (var component : labelPositive) {
                    centeredStringNoShadow(guiGraphics, font, component, x, (int) y + height - (labelPositive.size() * 5) + adjustY, -1, isHovered);
                    height += 10;
                }
            }

            height = 0;

            if(hasNegativeBoon){
                var adjustHeight = hasPositiveBoon ? (int) y + 47 : -1;
                var adjustY = hasPositiveBoon ? 0 : -70;

                boxMaker(guiGraphics, x - 71, adjustHeight, 70, this.height, MAGNET_STRENGTH_RED, 0);
                guiGraphics.blit(boonNeg.icon(), position + 60, (int) y + (79) + adjustY, 0, 0, scale, scale, scale, scale);
                for(var component : labelNegative){
                    centeredStringNoShadow(guiGraphics, font, component, x, (int) y + height - (labelPositive.size() * 5) + 134 + adjustY, -1, isHovered);
                    height += 10;
                }
            }

            index++;
        }
    }
}
