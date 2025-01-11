package org.jahdoo.challenge;

import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.shaydee.loot_beams_neoforge.data_component.DataComponentsReg;
import org.shaydee.loot_beams_neoforge.data_component.LootBeamComponent;

import java.awt.*;

import static org.jahdoo.ability.rarity.JahdooRarity.*;

public class LocalLootBeamData {

    public static LootBeamComponent rarityLootBeam(JahdooRarity rarity){
        var beamHeight = 0.6F + ((float) rarity.getId() / 4);
        var beamRadius = 0.55F + ((float) rarity.getId() / 10);
        var shadowRadius = 0.2f + ((float) rarity.getId() / 10);
        var renderDistance = 48.0 + (rarity.getId() * 20);
        return new LootBeamComponent(rarity.getColour(), beamHeight, 0.8F, 0F, 0.2F, true, true, true, beamRadius, 0.4F, shadowRadius, true, renderDistance);
    }

    public static LootBeamComponent lootBeamWithColour(int colour){
        var beamHeight = 0.75F;
        var beamRadius = 0.40F;
        var shadowRadius = 0.2f;
        var renderDistance = 64.0;
        return new LootBeamComponent(colour, beamHeight, 0.8F, 0F, 0.2F, true, true, true, beamRadius, 0.4F, shadowRadius, true, renderDistance);
    }


    public static void attachComponent (ItemStack itemStack, JahdooRarity rarity) {
        System.out.println(rarity.getSerializedName());
        var component =  switch (rarity.getId()){
            case 1 -> RARE_ITEM;
            case 2 -> EPIC_ITEM;
            case 3 -> LEGENDARY_ITEM;
            case 4 -> ETERNAL_ITEM;
            default -> COMMON_ITEM;
        };
        itemStack.set(DataComponentsReg.INSTANCE.getLOOT_BEAM_DATA(), component);
    }

    public static LootBeamComponent SPECIALLY_ENCHANTED_BOOK = lootBeamWithColour(Color.YELLOW.getRGB());
    public static LootBeamComponent COMMON_ITEM = rarityLootBeam(COMMON);
    public static LootBeamComponent RARE_ITEM = rarityLootBeam(RARE);
    public static LootBeamComponent EPIC_ITEM = rarityLootBeam(EPIC);
    public static LootBeamComponent LEGENDARY_ITEM = rarityLootBeam(LEGENDARY);
    public static LootBeamComponent ETERNAL_ITEM = rarityLootBeam(ETERNAL);
}
