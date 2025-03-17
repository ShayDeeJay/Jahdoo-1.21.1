package org.jahdoo.common.client.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.ascension.attachments.InstanceData;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.common.block.shopping_table.ShoppingTableEntity;
import org.jahdoo.common.registers.AttachmentReg;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.mojang.datafixers.util.Pair.of;
import static net.minecraft.network.chat.Component.empty;
import static org.jahdoo.ascension.attachments.PlayerWallet.*;
import static org.jahdoo.ascension.attachments.PlayerWallet.CurrencyConverter.convertToCoins;
import static org.jahdoo.ascension.attachments.PlayerWallet.CurrencyConverter.convertToWallet;
import static org.jahdoo.ascension.utils.ColourStore.MAGNET_RANGE_GREEN;
import static org.jahdoo.ascension.utils.ColourStore.NEGATIVE_RED;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
import static org.jahdoo.common.client.Icons.*;

public class WalletOverlay implements LayeredDraw.Layer {

    private int previousWallet;
    private InstanceData instanceData;
    private int timer;
    private float fadeIn;

    private int timer2;
    private float fadeIn2;


    public static int canPurchase(CurrencyConverter wallet, ShoppingTableEntity shoppingTable) {
        var currentWallet = convertToWallet(wallet);
        var shoppingWallet = convertToWallet(shoppingTable.itemCosts);
        return currentWallet - shoppingWallet;
    }

    public ShoppingTableEntity isLookingAtBlock(Player player){
        var pick = player.pick(player.blockInteractionRange(), 1F, false);

        if(pick instanceof BlockHitResult result){
            var level = player.level();
            var pos = result.getBlockPos();
            var below = pos.below(1);
            var barrierPos = level.getBlockEntity(below);

            if(level.getBlockEntity(pos) instanceof ShoppingTableEntity e){
                return e;
            }
            if(level.getBlockState(pos).is(Blocks.BARRIER) && barrierPos instanceof ShoppingTableEntity e){
                return e;
            }
        }

        return null;
    }

    private void slideGui() {
        var maxFadeIn = 10.0F;
        var minFadeIn = -130.0F;

        if (timer > 0) {
            var distanceToMax = maxFadeIn - this.fadeIn;
            var easeFactor = 0.1F;
            var fadeAmount = distanceToMax * easeFactor;
            this.fadeIn = Math.min(this.fadeIn + fadeAmount, maxFadeIn);
        } else {
            var distanceFromMin = this.fadeIn - minFadeIn;
            var easeFactor = 0.1F;
            var fadeAmount = distanceFromMin * easeFactor;
            this.fadeIn = Math.max(this.fadeIn - fadeAmount, minFadeIn);
        }
    }

    private void slideGuiStats() {
        var maxFadeIn = 10.0F;
        var minFadeIn = -130.0F;

        if (timer2 > 0) {
            var distanceToMax = maxFadeIn - this.fadeIn2;
            var easeFactor = 0.1F;
            var fadeAmount = distanceToMax * easeFactor;
            this.fadeIn2 = Math.min(this.fadeIn2 + fadeAmount, maxFadeIn);
        } else {
            var distanceFromMin = this.fadeIn2 - minFadeIn;
            var easeFactor = 0.1F;
            var fadeAmount = distanceFromMin * easeFactor;
            this.fadeIn2 = Math.max(this.fadeIn2 - fadeAmount, minFadeIn);
        }
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        var minecraft = Minecraft.getInstance();
        var level = minecraft.level;
        var currentWallet = previousWallet;
        var currentData = instanceData;
        var screen = minecraft.screen;
        var player = minecraft.player;

        if(player == null || minecraft.options.hideGui || level == null) return;
        var shoppingTable = isLookingAtBlock(player);

        renderWallet(graphics, minecraft, fadeIn);
        slideGui();
        slideGuiStats();
        deltaTracker.getGameTimeDeltaTicks();

        if(minecraft.player.level().getDescription().getString().contains("trial")){
            levelData(graphics, level, minecraft, currentData, screen);
        }


        timer = Math.max(0, timer - 1);
        previousWallet = getWalletValue(player);
        if(currentWallet != previousWallet) timer = 200;
        if(screen instanceof InventoryScreen) {
            timer = 30;
            timer2 = 30;
        }
        if(shoppingTable != null && shoppingTable.canPurchase()) timer = 50;
    }

    private void levelData(GuiGraphics graphics, ClientLevel level, Minecraft minecraft, InstanceData currentData, Screen screen) {
        var data = level.getData(AttachmentReg.INSTANCE_DATA);
        var spacer = 0;
        var offsetX = -14 + fadeIn2;
        var offsetY = 24;

        graphics.pose().pushPose();
        var sizeB = 1.8F;
        graphics.pose().scale(sizeB, sizeB, sizeB);
        graphics.drawString(minecraft.font, withStyleComponent("Current Run", ColourStore.COSMIC_PURPLE), (int) (10 + offsetX), 33 + offsetY, -1, true);
        graphics.pose().popPose();

        var getComps = List.of(
            of(appendStat("Rooms Completed: ", data.getClearedRooms()), UPGRADE),
            of(appendStat("Horde Killed: ", data.getHorde()), HORDE),
            of(appendStat("Skeletons Killed: ", data.getSkeleton()), SKELETON),
            of(appendStat("Wizards Killed: ", data.getEternalWizard()), ETERNAL_WIZARD),
            of(appendStat("Spiders Killed: ", data.getVoidSpider()), VOID_SPIDER)
        );

        for (var getComp : getComps) {
            var size = 18;
            graphics.blit(getComp.getSecond(), (int) (10 + offsetX), 94 + spacer + offsetY, 0, 0, size, size, size, size);
            graphics.drawString(minecraft.font, getComp.getFirst(), (int) (30 + offsetX), 100 + spacer + offsetY, -1, true);
            spacer += 18;
        }

        timer2 = Math.max(0, timer2 - 1);
        instanceData = data;

        if(currentData != instanceData) timer2 = 400;
        if(screen instanceof InventoryScreen) {
            timer = 30;
            timer2 = 30;
        }
    }

    private static @NotNull MutableComponent appendStat(String prefix, int value) {
        var aetherBlue = ColourStore.PENDENT_NAME;
        return withStyleComponent(prefix, aetherBlue).copy().append(withStyleComponent("" + value, MAGNET_RANGE_GREEN));
    }

    private static Component displayDifference(
        CurrencyConverter wallet,
        ShoppingTableEntity shoppingTable,
        int index,
        Integer coin,
        CoinProperties prop
    ) {
        if(shoppingTable == null) return empty();

        var validAmount = canPurchase(wallet, shoppingTable);
        if(validAmount <= 0) return empty();

        var newWallet = convertToCoins(validAmount).coins().reversed();
        var prop2 = newWallet.get(index);
        var name = withStyleComponent(prop.getSerializedName() + ": ", prop.getTextColour());
        var isDeductible = !Objects.equals(coin, prop2);

        return name.copy().append(withStyleComponent(prop2 + (isDeductible ? "↓" : ""), isDeductible ? NEGATIVE_RED : prop.getTextColour()));
    }

    private void renderWallet(GuiGraphics graphics, Minecraft minecraft, float fade) {
        var player = minecraft.player;
        if(player == null) return;

        var size = 40;
        var spacer = 0;
        var wallet = getWalletCoins(player);
        var getX = fade - 15    ;
        var getY = -3;

        var coinsTypes = wallet.coins().reversed();
        var properties = Arrays.stream(CoinProperties.values()).toList().reversed();
        var shoppingTable = isLookingAtBlock(player);

        if(shoppingTable != null){
            var cantPurchase = canPurchase(wallet, shoppingTable) <= 0;
            var purchaseText = cantPurchase ? "Can't" : "Can";
            var purchaseColour = cantPurchase ? NEGATIVE_RED : MAGNET_RANGE_GREEN;
            var comp = withStyleComponent(purchaseText + " Purchase!", purchaseColour);

            graphics.drawString(minecraft.font, comp, (int) (getX + 16), getY + 95 + spacer, -1, false);
        }

        for (int i = 0; i < properties.size(); i++){
            var prop = properties.get(i);
            var coin = coinsTypes.get(i);
            var text = withStyleComponent(prop.getSerializedName() + ": "+ coin, prop.getTextColour());
            var priceDifference = displayDifference(wallet, shoppingTable, i, coin, prop);
            var getTextType = !priceDifference.equals(empty()) ? priceDifference : text;

            graphics.blit(prop.getLocation(), (int) getX, getY + spacer, 0, 0, size, size, size, size);
            graphics.drawString(minecraft.font, getTextType, (int) (getX + 32), getY + 17 + spacer, -1, false);

            spacer += 20;
        }
    }

}
