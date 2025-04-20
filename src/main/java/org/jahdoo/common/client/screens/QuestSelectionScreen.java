package org.jahdoo.common.client.screens;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.quests.AbstractQuest;
import org.jahdoo.common.block.perk_table.PerkTableEntity;
import org.jahdoo.common.networking.client2server.AddQuestC2SP;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.QuestReg;

import java.util.Optional;

import static net.minecraft.util.FastColor.ARGB32.color;
import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponentTrans;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.SharedUI.fadeBlack;
import static org.jahdoo.common.client.screens.AbilityUnlockScreen.toComponent;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiColour;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiFade;
import static org.jahdoo.common.registers.SoundReg.SELECT;

public class QuestSelectionScreen extends Screen  {

    private int selection;
    private float fade;
    private float fadeEntryBack;
    private final BlockPos getBlockPos;

    public QuestSelectionScreen(BlockPos blockPos) {
        super(Component.empty());
        this.getBlockPos = blockPos;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    public Optional<AbstractQuest> getQuestFromBlock(){
        var level = getMinecraft().level;
        var getEntity = level.getBlockEntity(getBlockPos);

        if(getEntity instanceof PerkTableEntity perkTable)
            return QuestReg.getQuestByName(perkTable.getGetQuestId());

        return Optional.empty();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int getSize(){
        return 60;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(inButtonBounds((int) mouseX)) sharedPress();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void sharedPress() {
        var minecraft = this.getMinecraft();
        var player = minecraft.player;
        if (player == null) return;

        player.playSound(SoundReg.UNLOCK_NOTIFICATION.get(), 1F, 1F);
        getQuestFromBlock().ifPresent(x -> sendToServer(new AddQuestC2SP(x.questName())));
        minecraft.setScreen(null);
    }

    private int getPositions(){
        return this.width / 2 - getSize();
    }

    private void selectionBox(GuiGraphics guiGraphics, int xPos) {
        if(selection <= 0) return;
        if(this.getQuestFromBlock().isPresent()){
            var posColour = color((int) Math.max(0, fade), HEADER_COLOUR);
            boxMaker(guiGraphics, this.width/2 - getSize(), 0, xPos, this.height / 2, 0, posColour);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        fadeEntryBack = Math.min(0.9F, fadeEntryBack + 0.03F);

        renderBlurredBackground(partialTick);
        sectionHighlight(mouseX);
        boxMaker(guiGraphics, getPositions(), -1, getSize(), this.height, fadeBlack(0.1f), uiFade());
        selectionBox(guiGraphics, getSize());
        textSelection(guiGraphics, font);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void sectionHighlight(int mouseX) {
        if(inButtonBounds(mouseX)) {
            if(selection != 1) getMinecraft().player.playSound(SELECT.get(), 1F, 2F);
            this.fade = Math.min(fade + 10, 100);
            this.selection = 1;
            return;
        }

        this.fade = 0;
        this.selection = 0;
    }

    private boolean inButtonBounds(int mouseX) {
        var pos = getPositions();
        var spaceBy = getSize() * 2;

        return mouseX > pos - 1 && mouseX < pos + spaceBy;
    }

    private void textSelection(GuiGraphics guiGraphics, Font font) {
        if(this.fadeEntryBack <= 0.4) return;

        var getX = getPositions() + this.getSize() + 1;
        var scale = 50;

        this.getQuestFromBlock().ifPresent(
            quest -> {
                var adjustY = this.height/2 - 40;
                var space = 0;
                var body = quest.questDescription(getMinecraft().player);
                var comp = toComponent(body, "Quest", ABSORPTION_YELLOW, uiColour());

                var y2 = 40;
                guiGraphics.renderItem(new ItemStack(ItemReg.QUEST_CONTAINER.get()), getX-8, y2 - 20);
                guiGraphics.drawCenteredString(font, comp.getFirst(), getX, y2, uiColour());

                var text = withStyleComponentTrans(quest.getDisplayName(), PERK_GREEN);
                guiGraphics.drawCenteredString(font, text, getX, adjustY - 5, -1);
                guiGraphics.blit(quest.questIcon(), getX - scale/2, adjustY, 0, 0, scale, scale, scale, scale);

                for (var component : comp.subList(1, comp.size())) {
                    var y1 = adjustY + space;
                    guiGraphics.drawCenteredString(font,component, getX, y1 + 50, -1);
                    space += 12;
                }

                var y = adjustY + 80;
                guiGraphics.drawCenteredString(font, withStyleComponent("Rewards: ", OFF_WHITE), getX, y, -1);
                guiGraphics.drawCenteredString(font, withStyleComponent("+" + quest.questQuantity(getMinecraft().player) * 2 + " XP", COSMIC_PURPLE), getX, y+12, -1);

                var y1 = y + 24;
                guiGraphics.renderItem(new ItemStack(ItemReg.EXIT_KEY.get()), getX+2, y1);
                guiGraphics.renderItem(new ItemStack(ItemReg.CHALLENGER_TICKET.get()), getX-20, y1);
            }
        );

    }
}
