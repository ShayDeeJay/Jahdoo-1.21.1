package org.jahdoo.common.client.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.common.block.shopping_table.ShoppingTableEntity;
import org.jahdoo.common.client.SharedUI;
import org.jetbrains.annotations.Nullable;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.Arrays;
import java.util.Objects;

import static net.minecraft.network.chat.Component.empty;
import static org.jahdoo.common.client.screens.StatScreen.fadeBackground;
import static org.jahdoo.trial_nexus.attachments.PlayerWallet.*;
import static org.jahdoo.trial_nexus.attachments.PlayerWallet.CurrencyConverter.convertToCoins;
import static org.jahdoo.trial_nexus.attachments.PlayerWallet.CurrencyConverter.convertToWallet;

public class WalletOverlay implements LayeredDraw.Layer {
    private int previousWallet;
    private int timer;
    private float fadeIn;

    public static int canPurchase(CurrencyConverter wallet, CurrencyConverter currencyConverter) {
        var currentWallet = convertToWallet(wallet);
        var shoppingWallet = convertToWallet(currencyConverter);
        return currentWallet - shoppingWallet;
    }

    public static ShoppingTableEntity isLookingAtBlock(Player player){
        var pick = player.pick(player.blockInteractionRange(), 1F, false);

        if(pick instanceof BlockHitResult result){
            var level = player.level();
            var pos = result.getBlockPos();
            var below = pos.below(1);
            var belowBarrier = level.getBlockEntity(below);

            if(level.getBlockEntity(pos) instanceof ShoppingTableEntity e){
                return e;
            }
            if(level.getBlockState(pos).is(Blocks.BARRIER) && belowBarrier instanceof ShoppingTableEntity e){
                return e;
            }
        }

        return null;
    }

    private void slideGui() {
        var maxFadeIn = 10.0F;
        var minFadeIn = -130.0F;
        var easeFactor = 0.1F;

        if (timer > 0) {
            var distanceToMax = maxFadeIn - this.fadeIn;
            var fadeAmount = distanceToMax * easeFactor;
            this.fadeIn = Math.min(this.fadeIn + fadeAmount, maxFadeIn);
        } else {
            var distanceFromMin = this.fadeIn - minFadeIn;
            var fadeAmount = distanceFromMin * easeFactor;
            this.fadeIn = Math.max(this.fadeIn - fadeAmount, minFadeIn);
        }
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        var minecraft = Minecraft.getInstance();
        var level = minecraft.level;
        var currentWallet = previousWallet;
        var player = minecraft.player;

        if(player == null || minecraft.options.hideGui || level == null) return;
        var shoppingTable = isLookingAtBlock(player);

//        timer = 10;
        renderWallet(graphics, minecraft, fadeIn, 0 , 0, true, true, false, null);

        slideGui();

        timer = Math.max(0, timer - 1);
        previousWallet = getWalletValue(player);

        if(currentWallet != previousWallet) timer = 200;
        if(minecraft.screen != null) { timer = 0; }
        if(shoppingTable != null && shoppingTable.canPurchase()) timer = 50;
    }

    private static Component displayDifference(
        CurrencyConverter wallet,
        @Nullable CurrencyConverter purchasePrice,
        int index,
        Integer coin,
        CoinProperties prop
    ) {
        if(purchasePrice == null) return empty();
        var validAmount = canPurchase(wallet, purchasePrice);

        if(validAmount <= 0) return empty();

        var newWallet = convertToCoins(validAmount).coins().reversed();
        var prop2 = newWallet.get(index);
        var name = TextHelpers.withStyleComponent(/*prop.getSerializedName() + ": "*/"", prop.getTextColour());
        var isDeductible = !Objects.equals(coin, prop2);

        return name.copy().append(TextHelpers.withStyleComponent(prop2 + (isDeductible ? "↓" : ""), isDeductible ? ColourHelpers.getNegativeRed() : prop.getTextColour()));
    }

    public static void renderWallet(
        GuiGraphics graphics,
        Minecraft minecraft,
        float fade,
        double adjustX,
        double adjustY,
        boolean hideBackground,
        boolean isHorizontal,
        boolean showText,
        @Nullable CurrencyConverter converter
    ) {
        var player = minecraft.player;
        if(player == null) return;

        var size = 24;
        var spacer = 0;
        var wallet = getWalletCoins(player);
        int getX = (int) (fade - 15 + adjustX);
        int getY = (int) (-3 + adjustY + 20);

        var coinsTypes = wallet.coins().reversed();
        var properties = Arrays.stream(CoinProperties.values()).toList().reversed();
        var shoppingTable = isLookingAtBlock(player);

        if(shoppingTable != null || converter != null){
            var currencyConverter = shoppingTable != null ? shoppingTable.itemCosts : converter;
            var cantPurchase = canPurchase(wallet, currencyConverter) <= 0;
            if(cantPurchase){
                var comp = TextHelpers.withStyleComponent("Insufficient Funds!", ColourHelpers.getNegativeRed());
                graphics.drawString(minecraft.font, comp, getX + 15, getY + 84 + spacer, -1, false);
            }
        }

        if(!hideBackground){
            var x = getX + 6;
            var y = getY - 10;
            var startX = x + (isHorizontal ? 4 : -2);
            var startY = y + (isHorizontal ? 28 : 8);

            SharedUI.boxMaker(graphics, startX, startY, isHorizontal ? 60 : (showText ? 48 : 26), isHorizontal ? 10 : 42, 0, fadeBackground);
            var wallet1 = TextHelpers.withStyleComponent("Wallet", ColourHelpers.getSubHeaderColour());
            graphics.drawString(minecraft.font, wallet1, getX + 14, getY + spacer + 8, -1, false);
        }

        for (int i = 0; i < properties.size(); i++){
            var prop = properties.get(i);
            var coin = coinsTypes.get(i);
            var s = prop.getSerializedName() + ": ";
            var coin1 = (showText ? s : "") + coin.intValue();
            var text = TextHelpers.withStyleComponent(coin1, prop.getTextColour());
            var priceDifference = displayDifference(wallet, shoppingTable != null ? shoppingTable.itemCosts : converter, i, coin, prop);
            var getTextType = !priceDifference.equals(empty()) ? priceDifference : text;
            var x = getX + 6 + (isHorizontal ? spacer : 0);
            var y = getY + 12 + (!isHorizontal ? spacer : 0);

            graphics.blit(prop.getLocation(), x, y, 0, 0, size, size, size, size);
            graphics.drawString(minecraft.font, getTextType, x + 19, y + 10, -1, false);

            var length = coin1.length();

            var horizontalSpacing = 20 + (length * 5);
            var verticalSpacing = 14;
            spacer += (isHorizontal ? horizontalSpacing : verticalSpacing);
        }

    }

}
