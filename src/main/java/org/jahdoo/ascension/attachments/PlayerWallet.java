package org.jahdoo.ascension.attachments;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.ItemReg;

import java.util.Arrays;
import java.util.List;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;
import static org.jahdoo.ascension.utils.ColourStore.*;

public class PlayerWallet implements IAttachment {

    public static final String WALLET_TOTAL = "wallet_total";
    int wallet;

    public void addBronze(int multi) { wallet += multi; }
    public void addSilver(int multi) { wallet += (100 * multi); }
    public void addGold(int multi) { wallet += (10000 * multi); }
    public void addPlatinum(int multi) { wallet += (1000000 * multi); }
    public void setWallet(int newWallet) { wallet = newWallet; }
    public int getWallet() { return wallet; }

    public static void updateWallet(Player player, CurrencyConverter converter){
        var getWallet = player.getData(AttachmentReg.PLAYER_WALLET);
        getWallet.setWallet(CurrencyConverter.convertToWallet(converter));
    }

    public static void updateWallet(Player player, int newWallet){
        var getWallet = player.getData(AttachmentReg.PLAYER_WALLET);
        getWallet.setWallet(newWallet);
    }

    public static CurrencyConverter getWalletCoins(Player player){
        var getWallet = player.getData(AttachmentReg.PLAYER_WALLET);
        return CurrencyConverter.convertToCoins(getWallet.wallet);
    }

    public static int getWalletValue(Player player){
        return player.getData(AttachmentReg.PLAYER_WALLET).wallet;
    }

    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putInt(WALLET_TOTAL, wallet);
    }

    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        this.wallet = nbt.getInt(WALLET_TOTAL);
    }

    public record CurrencyConverter(int platinum, int gold, int silver, int bronze) {

        public static final CurrencyConverter EMPTY = new CurrencyConverter(0, 0, 0, 0);

        public static CurrencyConverter loadData(CompoundTag tag){
            return new CurrencyConverter(tag.getInt("p"), tag.getInt("g"), tag.getInt("s"), tag.getInt("b"));
        }

        public static void saveData(CompoundTag compoundTag, CurrencyConverter currency){
            compoundTag.putInt("b", currency.bronze());
            compoundTag.putInt("s", currency.silver());
            compoundTag.putInt("g", currency.gold());
            compoundTag.putInt("p", currency.platinum());
        }

        public static Pair<CoinProperties, Integer> getCoin(CurrencyConverter converter){
            var index = 0;
            for (var coin : converter.coins()) {
                if(coin > 0){
                    var getProp = Arrays.stream(CoinProperties.values()).toList().get(index);
                    return Pair.of(getProp, coin);
                }
                index++;
            }
            return Pair.of(CoinProperties.BRONZE, 0);
        }

        public List<Integer> coins(){
            return List.of(bronze, silver, gold, platinum);
        }

        public static CurrencyConverter setPlatinumCost(int platinum){
            return new CurrencyConverter(platinum, 0, 0, 0);
        }

        public static CurrencyConverter setGoldCost(int gold){
            return new CurrencyConverter(0, gold, 0, 0);
        }

        public static CurrencyConverter setSilverCost(int silver){
            return new CurrencyConverter(0, 0, silver, 0);
        }

        public static CurrencyConverter setBronzeCost(int bronze){
            return new CurrencyConverter(0, 0, 0, bronze);
        }

        public static void purchase(CurrencyConverter converter, Player player) {
            var wallet = PlayerWallet.getWalletValue(player);
            var walletAfterPurchase = wallet - convertToWallet(converter);
            updateWallet(player, walletAfterPurchase);
        }

        public static boolean checkAndPurchase(CurrencyConverter converter, Player player) {
            var wallet = PlayerWallet.getWalletValue(player);
            if (canPurchase(converter, wallet)) {
                var walletAfterPurchase = wallet - convertToWallet(converter);
                updateWallet(player, walletAfterPurchase);
                return true;
            }
            return false;
        }

        public static boolean canPurchase(CurrencyConverter converter, int wallet) {
            return wallet >= convertToWallet(converter);
        }

        public static CurrencyConverter convertToCoins(int wallet) {
            int platinumConversion = 1000000;
            int goldConversion = 10000;
            int silverConversion = 100;

            int platinum = wallet / platinumConversion;
            wallet %= platinumConversion;

            int gold = wallet / goldConversion;
            wallet %= goldConversion;

            int silver = wallet / silverConversion;
            wallet %= silverConversion;

            int bronze = wallet;

            return new CurrencyConverter(platinum, gold, silver, bronze);
        }

        public static int convertToWallet(CurrencyConverter converter) {
            return converter.platinum() * 1000000
                + converter.gold() * 10000
                + converter.silver() * 100
                + converter.bronze();
        }

        public static ItemStack getItemStack(CurrencyConverter converter){
            var type = CurrencyConverter.getCoin(converter).getFirst().ordinal();
            var itemStack = new ItemStack(ItemReg.COIN);
            if(type == 0) return itemStack;

            itemStack.set(CUSTOM_MODEL_DATA, new CustomModelData(type));
            return itemStack;
        }

        @Override
        public String toString() {
            return String.format(
                "Platinum = %d\nGold = %d\nSilver = %d\nBronze = %d",
                platinum, gold, silver, bronze
            );
        }
    }

    public enum CoinProperties implements StringRepresentable, IExtensibleEnum {
        BRONZE(Icons.BRONZE_COIN, "Bronze", BRONZE_COIN),
        SILVER(Icons.SILVER_COIN, "Silver", SILVER_COIN),
        GOLD(Icons.GOLD_COIN, "Gold", GOLD_COIN),
        PLATINUM(Icons.PLATINUM_COIN, "Platinum", PLATINUM_COIN);

        private final ResourceLocation location;
        private final String name;
        private final int textColour;

        CoinProperties(ResourceLocation location, String name, int textColour) {
            this.location = location;
            this.name = name;
            this.textColour = textColour;
        }

        public int getTextColour() {
            return textColour;
        }

        public ResourceLocation getLocation() {
            return location;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
