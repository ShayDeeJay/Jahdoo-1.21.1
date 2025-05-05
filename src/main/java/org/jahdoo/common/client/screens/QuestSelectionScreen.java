package org.jahdoo.common.client.screens;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.ascension.attachments.CasterData;
import org.jahdoo.ascension.attachments.PlayerWallet;
import org.jahdoo.ascension.quests.AbstractQuest;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.perk_table.PerkTableEntity;
import org.jahdoo.common.client.overlay.WalletOverlay;
import org.jahdoo.common.items.CoinSack;
import org.jahdoo.common.networking.client2server.AddQuestC2SP;
import org.jahdoo.common.networking.client2server.PerkTableSyncC2SP;
import org.jahdoo.common.networking.client2server.WalletSyncC2SP;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.QuestReg;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI;
import static net.minecraft.util.FastColor.ARGB32.color;
import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;
import static org.jahdoo.ascension.attachments.PlayerWallet.CurrencyConverter;
import static org.jahdoo.ascension.attachments.PlayerWallet.CurrencyConverter.convertToCoins;
import static org.jahdoo.ascension.attachments.PlayerWallet.getWalletValue;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponentTrans;
import static org.jahdoo.common.client.Icons.*;
import static org.jahdoo.common.client.SharedUI.boxMaker;
import static org.jahdoo.common.client.SharedUI.fadeBlack;
import static org.jahdoo.common.client.button.ToggleComponent.menuButtonSound;
import static org.jahdoo.common.client.screens.AbilityUnlockScreen.toComponent;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiColour;
import static org.jahdoo.common.client.screens.AbstractPanableScreen.uiFade;
import static org.jahdoo.common.registers.SoundReg.SELECT;

public class QuestSelectionScreen extends Screen  {

    private int selection;
    private float fade;
    private float fadeEntryBack;
    private final BlockPos getBlockPos;
    private List<Component> getToolTip = new ArrayList<>();
    private CurrencyConverter converter;

    public QuestSelectionScreen(BlockPos blockPos) {
        super(Component.empty());
        this.getBlockPos = blockPos;
    }

    @Override
    protected void init() {
        super.init();
        var sharedX = width / 2 - 88;
        var sharedY = this.height / 2 - 20;
        this.addRenderableWidget(
            menuButtonSound(
                sharedX, sharedY, (Button) -> onClick(1),
                REFRESH, 30, false, 0, new WidgetSprites(GUI_BUTTON, GUI_BUTTON), true, this::onHover,
                forUI(SoundReg.UPGRADE_MODIFIER.get(), 0.8F, 0.6F)
            )
        );

        this.addRenderableWidget(
            menuButtonSound(
                sharedX, sharedY + 24, (Button) -> onClick(2),
                CLOSE, 30, false, 0, new WidgetSprites(GUI_BUTTON, GUI_BUTTON), true, ()->{},
                SimpleSoundInstance.forUI(SELECT.get(), 1F, 1F)
            )
        );
    }

    private void onHover() {
        var roll = "Re-Roll";
        var table = getPerkTable();

        getToolTip.add(Helpers.withStyleComponent(roll, uiColour()));
        if(table.isPresent()){
            var getTable = table.get();
            var counter = getTable.getReRollCounter();
            var playerLevel = CasterData.getLevel(getMinecraft().player);

            if(counter == 0){
                getToolTip.add(Helpers.withStyleComponent("Free", uiColour()));
                this.converter = new CurrencyConverter(0,0,0,0);
            } else {
                var wallet = playerLevel * (counter * counter + 2);
                this.converter = convertToCoins(wallet);
                CoinSack.coinToolTip(getToolTip, wallet);
            }
        }
    }

    public boolean canPurchase(){
        if(this.converter == null) return false;
        return CurrencyConverter.canPurchase(this.converter, PlayerWallet.getWalletValue(getMinecraft().player));
    }

    private void onClick(int type) {
        var player = getMinecraft().player;
        if(type == 1) {
            if(canPurchase() & player != null){
                getPerkTable().ifPresent(PerkTableEntity::reRollQuest);
                PacketDistributor.sendToServer(new WalletSyncC2SP(getWalletValue(player)));
                CurrencyConverter.checkAndPurchase(this.converter, player);
            }
            return;
        }

        getMinecraft().setScreen(null);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int getSize(){
        return 60;
    }

    private int getPositions(){
        return this.width / 2 - getSize();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(inButtonBounds((int) mouseX)) sharedPress();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean inButtonBounds(int mouseX) {
        var pos = getPositions();
        var spaceBy = getSize() * 2;

        return mouseX > pos - 1 && mouseX < pos + spaceBy;
    }

    public Optional<PerkTableEntity> getPerkTable(){
        var level = getMinecraft().level;
        var getEntity = level.getBlockEntity(getBlockPos);

        if(getEntity instanceof PerkTableEntity perkTable){
            return Optional.of(perkTable);
        }

       return Optional.empty();
    }

    public Optional<AbstractQuest> getQuestFromBlock(){
        var table = getPerkTable();

        if(table.isPresent()){
            return QuestReg.getQuestByName(table.get().getGetQuestId());
        }

        return Optional.empty();
    }

    private void selectionBox(GuiGraphics guiGraphics, int xPos) {
        if(selection <= 0) return;
        if(this.getQuestFromBlock().isPresent()){
            var posColour = color((int) Math.max(0, fade), uiColour());
            boxMaker(guiGraphics, this.width/2 - getSize(), 0, xPos, this.height / 2, posColour,0,0);
        }
    }

    private void sectionHighlight(int mouseX) {
        if(inButtonBounds(mouseX)) {
            if(selection != 1) getMinecraft().player.playSound(SELECT.get(), 1F, 1F);
            this.fade = Math.min(fade + 10, 100);
            this.selection = 1;
            return;
        }

        this.fade = 0;
        this.selection = 0;
    }

    private void sharedPress() {
        var minecraft = this.getMinecraft();
        var player = minecraft.player;
        if (player == null) return;

        player.playSound(SoundReg.UNLOCK_NOTIFICATION.get(), 1F, 1F);
        getQuestFromBlock().ifPresent(x -> sendToServer(new AddQuestC2SP(x.questName())));
        PacketDistributor.sendToServer(new PerkTableSyncC2SP(getMinecraft().player.getUUID(), getBlockPos));
        minecraft.setScreen(null);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        fadeEntryBack = Math.min(0.9F, fadeEntryBack + 0.03F);

        renderBlurredBackground(partialTick);
        WalletOverlay.renderWallet(guiGraphics, getMinecraft(), 1, 10, 0, true, this.converter);
        sectionHighlight(mouseX);
        boxMaker(guiGraphics, getPositions(), -1, getSize(), this.height, fadeBlack(0.1f), uiFade());
        selectionBox(guiGraphics, getSize());
        textSelection(guiGraphics, font);
        guiGraphics.renderTooltip(font, this.getToolTip, Optional.empty(), mouseX, mouseY);

        this.converter = null;
        this.getToolTip = new ArrayList<>();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
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
                var comp = toComponent(body, "Optional Quest", ABSORPTION_YELLOW, uiColour());

                var y2 = this.height/4;
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
                guiGraphics.drawCenteredString(font, withStyleComponent("+" + quest.questXp(getMinecraft().player) + " XP", COSMIC_PURPLE), getX, y+12, -1);

                var y1 = y + 24;
                var spaceBy = 0;
                var itemStacks = quest.questRewards();
                for (var questReward : itemStacks) {
                    guiGraphics.renderItem(questReward, getX + spaceBy - 10 - itemStacks.size() * 5, y1);
                    spaceBy += 20;
                }
            }
        );

    }
}
