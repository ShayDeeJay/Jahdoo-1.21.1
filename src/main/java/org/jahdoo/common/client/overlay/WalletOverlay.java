package org.jahdoo.common.client.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.common.block.shopping_table.ShoppingTableEntity;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.client.screens.StatScreen;

import java.util.Arrays;
import java.util.Objects;

import static net.minecraft.network.chat.Component.empty;
import static org.jahdoo.ascension.attachments.PlayerWallet.*;
import static org.jahdoo.ascension.attachments.PlayerWallet.CurrencyConverter.convertToCoins;
import static org.jahdoo.ascension.attachments.PlayerWallet.CurrencyConverter.convertToWallet;
import static org.jahdoo.ascension.utils.ColourStore.MAGNET_RANGE_GREEN;
import static org.jahdoo.ascension.utils.ColourStore.NEGATIVE_RED;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
import static org.jahdoo.common.client.screens.StatScreen.*;

public class WalletOverlay implements LayeredDraw.Layer {
    private int previousWallet;
    private int timer;
    private float fadeIn;

    public static int canPurchase(CurrencyConverter wallet, ShoppingTableEntity shoppingTable) {
        var currentWallet = convertToWallet(wallet);
        var shoppingWallet = convertToWallet(shoppingTable.itemCosts);
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
        var screen = minecraft.screen;
        var player = minecraft.player;

        if(player == null || minecraft.options.hideGui || level == null) return;
        var shoppingTable = isLookingAtBlock(player);

        renderWallet(graphics, minecraft, fadeIn, 0 , 0, true);
        slideGui();

        timer = Math.max(0, timer - 1);
        previousWallet = getWalletValue(player);

        if(currentWallet != previousWallet) timer = 200;
        if(screen instanceof StatScreen) { timer = 0; }
        if(shoppingTable != null && shoppingTable.canPurchase()) timer = 50;
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

    public static void renderWallet(
        GuiGraphics graphics,
        Minecraft minecraft,
        float fade,
        double adjustX,
        double adjustY,
        boolean hideBackground
    ) {
        var player = minecraft.player;
        if(player == null) return;

        var size = 34;
        var spacer = 0;
        var wallet = getWalletCoins(player);
        int getX = (int) (fade - 15 + adjustX);
        int getY = (int) (-3 + adjustY);

        var coinsTypes = wallet.coins().reversed();
        var properties = Arrays.stream(CoinProperties.values()).toList().reversed();
        var shoppingTable = isLookingAtBlock(player);

        if(shoppingTable != null){
            var cantPurchase = canPurchase(wallet, shoppingTable) <= 0;
            var purchaseText = cantPurchase ? "Can't" : "Can";
            var purchaseColour = cantPurchase ? NEGATIVE_RED : MAGNET_RANGE_GREEN;
            var comp = withStyleComponent(purchaseText + " Purchase!", purchaseColour);

            graphics.drawString(minecraft.font, comp, getX + 16, getY + 95 + spacer, -1, false);
        }

        if(!hideBackground){
            SharedUI.boxMaker(graphics, getX + 6, getY - 10, 48, 46, 0, fadeBackground, fadeBackground);
            graphics.drawString(minecraft.font, withStyleComponent("Wallet", ColourStore.SUB_HEADER_COLOUR), getX + 16, getY + spacer - 2, -1, false);
        }

        for (int i = 0; i < properties.size(); i++){
            var prop = properties.get(i);
            var coin = coinsTypes.get(i);
            var text = withStyleComponent(prop.getSerializedName() + ": "+ coin, prop.getTextColour());
            var priceDifference = displayDifference(wallet, shoppingTable, i, coin, prop);
            var getTextType = !priceDifference.equals(empty()) ? priceDifference : text;

            graphics.blit(prop.getLocation(), getX + 2, getY + spacer + 2, 0, 0, size, size, size, size);
            graphics.drawString(minecraft.font, getTextType, getX + 28, getY + 15 + spacer, -1, false);

            spacer += 16;
        }
    }

}
