package org.jahdoo.common.client.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.common.block.shopping_table.ShoppingTableEntity;

import java.util.Arrays;
import java.util.Objects;

import static net.minecraft.network.chat.Component.*;
import static org.jahdoo.ascension.attachments.PlayerWallet.*;
import static org.jahdoo.ascension.attachments.PlayerWallet.CurrencyConverter.*;
import static org.jahdoo.ascension.attachments.PlayerWallet.getWalletCoins;
import static org.jahdoo.ascension.attachments.PlayerWallet.getWalletValue;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;

public class WalletOverlay implements LayeredDraw.Layer {

    private int previousWallet;
    private int timer;
    private float fadeIn;

    public static int canPurchase(CurrencyConverter wallet, ShoppingTableEntity shoppingTable) {
        var currentWallet = convertToWallet(wallet);
        var shoppingWallet = convertToWallet(shoppingTable.itemCosts);
        return currentWallet - shoppingWallet;
    }

    public ShoppingTableEntity isLookingAtBlock(Player player, DeltaTracker tracker){
        var pick = player.pick(3, tracker.getRealtimeDeltaTicks(), false);
        var pickedBlock = BlockPos.containing(pick.getLocation());
        var level = player.level();
        if(level.getBlockEntity(pickedBlock) instanceof ShoppingTableEntity e){
            return e;
        }
        if(level.getBlockEntity(pickedBlock.below(1)) instanceof ShoppingTableEntity e){
            return e;
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

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        var minecraft = Minecraft.getInstance();
        var level = minecraft.level;
        var currentWallet = previousWallet;
        var screen = minecraft.screen;
        var player = minecraft.player;

        if(player == null || minecraft.options.hideGui || level == null) return;
        var shoppingTable = isLookingAtBlock(player, deltaTracker);

        renderWallet(graphics, minecraft, fadeIn, deltaTracker);
        slideGui();
        deltaTracker.getGameTimeDeltaTicks();
        timer = Math.max(0, timer - 1);
        previousWallet = getWalletValue(player);
        if(currentWallet != previousWallet || screen instanceof InventoryScreen) timer = 200;
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
        return name.copy().append(withStyleComponent(prop2 + (isDeductible ? "↓" : ""), isDeductible ? ColourStore.NEGATIVE_RED : prop.getTextColour()));
    }

    private void renderWallet(GuiGraphics graphics, Minecraft minecraft, float fade, DeltaTracker tracker) {
        var player = minecraft.player;
        if(player == null) return;

        var size = 40;
        var spacer = 0;
        var wallet = getWalletCoins(player);
        var getX = fade - 15    ;
        var getY = -3;

        var coinsTypes = wallet.coins().reversed();
        var properties = Arrays.stream(CoinProperties.values()).toList().reversed();
        var shoppingTable = isLookingAtBlock(player, tracker);

        if(shoppingTable != null){
            Component comp;

            if (canPurchase(wallet, shoppingTable) <= 0) {
                comp = withStyleComponent("Can't Purchase!", ColourStore.NEGATIVE_RED);
            } else {
                comp = withStyleComponent("Can Purchase!", ColourStore.MAGNET_RANGE_GREEN);
            }

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
