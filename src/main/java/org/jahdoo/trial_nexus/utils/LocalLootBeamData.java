package org.jahdoo.trial_nexus.utils;

import net.minecraft.world.item.ItemStack;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.shaydee.loot_beams_neoforge.components.DataComponentsReg;
import org.shaydee.loot_beams_neoforge.components.LootBeamComponent;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

import java.awt.*;

import static org.jahdoo.trial_nexus.rarity.JahdooRarity.*;

public class LocalLootBeamData {

    public static final LootBeamComponent SPECIALLY_ENCHANTED_BOOK = lootBeamWithColour(Color.YELLOW.getRGB());
    public static final LootBeamComponent ENCHANTED_VANILLA_SWORD = lootBeamWithColour(Color.PINK.getRGB());
    public static final LootBeamComponent COIN = coinLootBeamWithColour(Color.WHITE.getRGB());

    private static final LootBeamComponent COMMON_ITEM = rarityLootBeam(COMMON);
    private static final LootBeamComponent RARE_ITEM = rarityLootBeam(RARE);
    private static final LootBeamComponent EPIC_ITEM = rarityLootBeam(EPIC);
    private static final LootBeamComponent LEGENDARY_ITEM = rarityLootBeam(LEGENDARY);
    private static final LootBeamComponent MYTHIC_ITEM = rarityLootBeam(MYTHIC);
    private static final LootBeamComponent UNIQUE_ITEM = uniqueLootBeam(UNIQUE);

    public static LootBeamComponent coinLootBeamWithColour(int colour){
        var beamHeight = 0.45F;
        var beamRadius = 0.2F;
        var shadowRadius = 0f;
        var renderDistance = 250.0;
        return new LootBeamComponent(colour, colour, beamHeight, 0.8F,  1F,  false, beamRadius, 0.4F, shadowRadius, false, renderDistance,  false, 20, 0.1, shadowRadius, false);
    }

    public static LootBeamComponent rarityLootBeam(JahdooRarity rarity){
        var beamHeight = 0.5F + ((float) rarity.getId() / 4);
        var beamRadius = 0.35F + ((float) rarity.getId() / 10);
        var shadowRadius = 0.4f + ((float) rarity.getId() / 10);
        var renderDistance = 48.0 + (rarity.getId() * 20);
        return new LootBeamComponent(rarity.getColour(), rarity.getColour(),beamHeight, 0.8F,  1F,  true, beamRadius, 0.4F, shadowRadius, true, renderDistance, false, 20, 0.1, shadowRadius, false);
    }

    public static LootBeamComponent lootBeamWithColour(int colour){
        var beamHeight = 0.75F;
        var beamRadius = 0.45F;
        var shadowRadius = 0.3f;
        var renderDistance = 64.0;
        return new LootBeamComponent(colour, colour, beamHeight, 0.8F,  1F,  true, beamRadius, 0.4F, shadowRadius, true, renderDistance,  false, 20, 0.1, shadowRadius, false);
    }

    public static LootBeamComponent uniqueLootBeam(JahdooRarity rarity){
        var beamHeight = 0.5F + ((float) rarity.getId() / 4);
        var beamRadius = 0.35F + ((float) rarity.getId() / 10);
        var shadowRadius = 0.4f + ((float) rarity.getId() / 10);
        var renderDistance = 250;
        return new LootBeamComponent(ColourHelpers.getUniqueB(), ColourHelpers.getUniqueA(), beamHeight, 0.8F,  1F, true, beamRadius, 0.4F, shadowRadius, true, renderDistance,  true, 5, 0.25, 1.5, true);
    }

    public static LootBeamComponent soulItemBeam(){
        var rarity = UNIQUE;
        var beamHeight = 0.6F + ((float) rarity.getId() / 4);
        var beamRadius = 0.5F + ((float) rarity.getId() / 10);
        var renderDistance = 250;
        return new LootBeamComponent(ColourHelpers.getUniqueA(), ColourHelpers.getPerkGreen(), beamHeight, 0.8F,  1F, true, beamRadius, 0.4F, 0, true, renderDistance,  true, 5, 0.25, 1.5, true);
    }

    public static void attachCoinSackLootBeam(ItemStack stack){
        var beamHeight = 0.5F;
        var beamRadius = 0.35F;
        var shadowRadius = 0.4f;
        var renderDistance = 48;
        var lootBeamComponent = new LootBeamComponent(ColourHelpers.getGoldCoin(), ColourHelpers.getSilverCoin(), beamHeight, 0.8F, 1F, true, beamRadius, 0.4F, shadowRadius, false, renderDistance, true, 5, 0.25, 1.5, true);
        LootBeamComponent.toLootBeamComponent(lootBeamComponent, stack);
    }

    public static void attachLootBeamComponent(ItemStack itemStack, JahdooRarity rarity) {
        var component =  switch (rarity.getId()){
            case 1 -> RARE_ITEM;
            case 2 -> EPIC_ITEM;
            case 3 -> LEGENDARY_ITEM;
            case 4 -> MYTHIC_ITEM;
            case 5 -> UNIQUE_ITEM;
            default -> COMMON_ITEM;
        };
        itemStack.set(DataComponentsReg.getLOOT_BEAM_DATA(), component);
    }

}
