package org.jahdoo.common.client.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import org.jahdoo.ascension.attachments.PlayerWallet;
import org.jahdoo.common.block.shopping_table.ShoppingTableEntity;
import org.jahdoo.common.client.SharedUI;

import java.util.Arrays;
import static org.jahdoo.ascension.attachments.PlayerWallet.getWalletCoins;
import static org.jahdoo.ascension.attachments.PlayerWallet.getWalletValue;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;

public class WalletOverlay implements LayeredDraw.Layer {

    int previousWallet;
    int currentWallet;
    int timer;
    private float fadeIn;

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        var level = minecraft.level;

        if(player == null || minecraft.options.hideGui || level == null) return;
        var wallet = getWalletValue(player);

        renderWallet(graphics, minecraft, fadeIn, deltaTracker);
        slideGui();

        deltaTracker.getGameTimeDeltaTicks();

        timer = Math.max(0, timer - 1);
        currentWallet = previousWallet;
        previousWallet = wallet;

        var screen = minecraft.screen;
        if(currentWallet != previousWallet || screen instanceof InventoryScreen || isLookingAtBlock(player, deltaTracker) != null) timer = 200;
    }


    public ShoppingTableEntity isLookingAtBlock(Player player, DeltaTracker tracker){
        var pick = player.pick(5, tracker.getRealtimeDeltaTicks(), false);
        var pickedBlock = BlockPos.containing(pick.getLocation());
        if(player.level().getBlockEntity(pickedBlock) instanceof ShoppingTableEntity e){
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

    private void renderWallet(GuiGraphics graphics, Minecraft minecraft, float fade, DeltaTracker tracker) {
        var player = minecraft.player;
        if(player == null) return;

        var size = 40;
        var spacer = 0;
        var wallet = getWalletCoins(player);
        var getX = fade - 15    ;
        var getY = -3;

        var coinsTypes = wallet.coins().reversed();
        var properties = Arrays.stream(PlayerWallet.CoinProperties.values()).toList().reversed();

        var shoppingTable = isLookingAtBlock(player, tracker);
        if(shoppingTable != null){
            System.out.println(shoppingTable.itemCosts);
        }

        for (int i = 0; i < properties.size(); i++){
            var prop = properties.get(i);
            var text = withStyleComponent(prop.getSerializedName() + ": "+ coinsTypes.get(i), prop.getTextColour());
            graphics.blit(prop.getLocation(), (int) getX, (int) (getY + spacer), 0, 0, size, size, size, size);

            graphics.drawString(minecraft.font, text, (int) (getX + 32), (int) (getY + 17 + spacer), -1, false);
            spacer += 20;
        }
    }

}
