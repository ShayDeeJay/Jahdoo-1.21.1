package org.jahdoo.trial_nexus.rarity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;
import net.neoforged.fml.common.asm.enumextension.IndexedEnum;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jetbrains.annotations.NotNull;
import org.shaydee.shaydeeapi.Helpers;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.Arrays;
import java.util.List;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.items.runes.rune_data.RuneHelpers.getRuneData;
import static org.jahdoo.common.registers.ComponentReg.JAHDOO_RARITY;
import static org.jahdoo.trial_nexus.rarity.RarityAttributes.*;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.*;

@IndexedEnum
public enum JahdooRarity implements StringRepresentable, IExtensibleEnum {

    COMMON(0, "common", color(120, 203, 83), COMMON_ATTRIBUTES, Icons.COMMON_TAG),
    RARE(1, "rare", color(67, 164, 222), RARE_ATTRIBUTES, Icons.RARE_TAG),
    EPIC(2, "epic", color(222, 136, 255), EPIC_ATTRIBUTES, Icons.EPIC_TAG),
    LEGENDARY(3, "legendary", color(241, 194, 50), LEGENDARY_ATTRIBUTES, Icons.LEGENDARY_TAG),
    MYTHIC(4, "mythic", color(225, 92, 112), MYTHIC_ATTRIBUTES, Icons.MYTHIC_TAG),
    UNIQUE(5, "unique", ColourHelpers.getUniqueB(), MYTHIC_ATTRIBUTES, Icons.UNIQUE_TAG);

    public static final List<Pair<JahdooRarity, Integer>> BASE_RARITY_CHANCES =
        List.of(
            Pair.of(COMMON, 1),
            Pair.of(RARE, 1500),
            Pair.of(EPIC, 4500),
            Pair.of(LEGENDARY, 5500),
            Pair.of(MYTHIC, 6000)
        );

    private final int id;
    private final String name;
    private final int color;
    private final RarityAttributes attributes;
    private final ResourceLocation tag;

    JahdooRarity(
        int id,
        String name,
        int color,
        RarityAttributes attributes,
        ResourceLocation tag
    ) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.attributes = attributes;
        this.tag = tag;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }

    private static int getRandom() {
        return Random.nextInt(1, 6020);
    }

    public int getId() {
        return this.id;
    }

    public int getColour() {
        return this.color;
    }

    public RarityAttributes getAttributes() {
        return this.attributes;
    }

    public ResourceLocation getTag() {
        return this.tag;
    }

    public static List<JahdooRarity> getAllRarities() {
        return Arrays.stream(JahdooRarity.values()).toList();
    }

    public static JahdooRarity getAllRarities(int index) {
        return Arrays.stream(JahdooRarity.values()).toList().get(index);
    }

    public static Component attachRarityTooltip(ItemStack wandItem, Level level) {
        var getRarityId = wandItem.get(JAHDOO_RARITY);
        if(getRarityId != null && level != null) {
            var getRarity = JahdooRarity.getAllRarities().get(Math.clamp(getRarityId, 0, 5));
            return JahdooRarity.addRarityTooltip(getRarity, level);
        }
        return null;
    }

    public static JahdooRarity getRarityFromItem(ItemStack wandItem) {
        var integer = wandItem.get(JAHDOO_RARITY);
        if(integer == null) return JahdooRarity.COMMON;

        return getAllRarities(integer);
    }

    public static JahdooRarity getReverseRarity() {
        var filteredList = BASE_RARITY_CHANCES
            .stream()
            .filter(rarity -> rarity.getSecond() >= getRandom())
            .toList();
        return Helpers.listRandom(filteredList).getFirst();
    }

    public static JahdooRarity getRarity() {
        var filteredList = BASE_RARITY_CHANCES
            .stream()
            .filter(rarity -> rarity.getSecond() <= getRandom())
            .toList();
        return Helpers.listRandom(filteredList).getFirst();
    }

    public static JahdooRarity getRarity(List<Pair<JahdooRarity, Integer>> rarities) {
        var filteredList = rarities
            .stream()
            .filter(jahdooRarity -> jahdooRarity.getSecond() <= getRandom())
            .toList();
        return Helpers.listRandom(filteredList).getFirst();
    }

    public static Component addRarityTooltip(JahdooRarity rarity, Level level){
        if(level == null) return Component.empty();

        var id = "rarity.jahdoo.current_rarity";
        var colour = getColorTransition(ColourHelpers.getUniqueA(), ColourHelpers.getUniqueB(), (int) level.getGameTime(), 50);
        var getCorrectColour = rarity.id == 5 ? colour : rarity.getColour();
        var sibling = TextHelpers.withStyleComponent(rarity.getSerializedName(), getCorrectColour);

        return TextHelpers.withStyleComponentTrans(id, ColourHelpers.getSubHeaderColour()).copy().append(sibling);
    }

    public static Component attachRuneTierTooltip(ItemStack wandItem) {
        var data = getRuneData(wandItem);
        var getRarity = JahdooRarity.getAllRarities().get(Math.clamp(data.tier(), 0, 5));
        var getTier = romanNumeralConverter(getRarity.id);
        return TextHelpers.withStyleComponent("Tier ", ColourHelpers.getSubHeaderColour())
            .copy()
            .append(TextHelpers.withStyleComponent(getTier, getRarity.getColour()));
    }

    public static @NotNull String romanNumeralConverter(int number) {
        return NUMERALS.get(number);
    }

    public static final List<String> NUMERALS = List.of(
        "I",   // index 0
        "II",  // index 1
        "III", // index 2
        "IV",  // index 3
        "V",   // index 4
        "VI",  // index 5
        "VII", // index 6
        "VIII",// index 7
        "IX",  // index 8
        "X",   // index 9
        "XI"   // index 10
    );

    public static void debugRarity(Player player){
        if(!player.level().isClientSide){
            playDebugMessage(player, "NEW ROLL");
            playDebugMessage(player, "---------------------------------");

            var common = 0;
            var uncommon = 0;
            var epic = 0;
            var legendary = 0;
            var ethereal =  0;

            for (int i = 2000; i > 0; i--) {
                var rarity = JahdooRarity.getRarity(
                    List.of(
                        Pair.of(COMMON, 1),
                        Pair.of(RARE, 1500),
                        Pair.of(EPIC, 5000),
                        Pair.of(LEGENDARY, 5800),
                        Pair.of(MYTHIC, 6000)
                    )
                );

                switch (rarity){
                    case COMMON -> common++;
                    case RARE -> uncommon++;
                    case EPIC -> epic++;
                    case LEGENDARY -> legendary++;
                    case MYTHIC -> ethereal++;
                }
            }

            playDebugMessage(player, "Common " + common);
            playDebugMessage(player, "Un-Common " + uncommon);
            playDebugMessage(player, "Epic " + epic);
            playDebugMessage(player, "Legendary " + legendary);
            playDebugMessage(player, "Eternal " + ethereal);
            playDebugMessage(player, "---------------------------------");
        }
    }
}
