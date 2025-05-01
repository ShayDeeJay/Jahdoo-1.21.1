package org.jahdoo.ascension.rarity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;
import net.neoforged.fml.common.asm.enumextension.IndexedEnum;
import org.jahdoo.ascension.utils.ColourStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ascension.rarity.RarityAttributes.*;
import static org.jahdoo.ascension.utils.ColourStore.SUB_HEADER_COLOUR;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.common.items.runes.rune_data.RuneHelpers.getRuneData;
import static org.jahdoo.common.registers.ComponentReg.JAHDOO_RARITY;

@IndexedEnum
public enum JahdooRarity implements StringRepresentable, IExtensibleEnum {

    COMMON(0, "Common", color(120, 203, 83), COMMON_ATTRIBUTES),
    RARE(1, "Rare", color(67, 164, 222), RARE_ATTRIBUTES),
    EPIC(2, "Epic", color(222, 136, 255), EPIC_ATTRIBUTES),
    LEGENDARY(3, "Legendary", color(241, 194, 50), LEGENDARY_ATTRIBUTES),
    ETERNAL(4, "Eternal", color(225,92,199), ETERNAL_ATTRIBUTES),
    UNIQUE(5, "Unique", color(104, 243, 252), ETERNAL_ATTRIBUTES);

    public static final List<Pair<JahdooRarity, Integer>> BASE_RARITY_CHANCES = List.of(
        Pair.of(COMMON, 1),
        Pair.of(RARE, 1500),
        Pair.of(EPIC, 4500),
        Pair.of(LEGENDARY, 5500),
        Pair.of(ETERNAL, 6000)
    );

    private final int id;
    private final String name;
    private final int color;
    private final RarityAttributes attributes;

    JahdooRarity(int id, String name, int color, RarityAttributes attributes) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.attributes = attributes;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
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

    public static List<JahdooRarity> getAllRarities() {
        return Arrays.stream(JahdooRarity.values()).toList();
    }

    public static JahdooRarity getAllRarities(int index) {
        return Arrays.stream(JahdooRarity.values()).toList().get(index);
    }

    private static JahdooRarity getJahdooRarity(@Nullable JahdooRarity rarity) {
        var correctRarity = rarity == null ? JahdooRarity.getRarity() : rarity;
        return correctRarity == JahdooRarity.UNIQUE ? JahdooRarity.EPIC : correctRarity;
    }

    public static Component attachRarityTooltip(ItemStack wandItem, Level level) {
        var getRarityId = wandItem.get(JAHDOO_RARITY);
        if(getRarityId != null && level != null) {
            var getRarity = JahdooRarity.getAllRarities().get(Math.clamp(getRarityId, 0, 5));
            return JahdooRarity.addRarityTooltip(getRarity, level);
        }
        return null;
    }

    public static JahdooRarity getRarity() {
        var getRandom = Random.nextInt(1, 6020);
        var filteredList = new ArrayList<>(
            BASE_RARITY_CHANCES
                .stream()
                .filter(rarity -> rarity.getSecond() <= getRandom)
                .filter(rarity -> rarity.getFirst().id != 5)
                .toList()
        );
        return getJahdooRarity(listRandom(filteredList).getFirst());
    }

    public static JahdooRarity getRarity(@Nullable List<Pair<JahdooRarity, Integer>> rarities) {
        var getRandom = Random.nextInt(1, 6020);
        var correctRarity = rarities == null ? BASE_RARITY_CHANCES : rarities;
        var filteredList = correctRarity
            .stream()
            .filter(jahdooRarity -> jahdooRarity.getSecond() <= getRandom)
            .filter(jahdooRarity -> jahdooRarity.getFirst().id != 5)
            .toList();
        System.out.println(filteredList);
        return listRandom(filteredList).getFirst();
    }

    public static Component addRarityTooltip(JahdooRarity rarity, Level level){
        if(level == null) return Component.empty();
        var id = "rarity.jahdoo.current_rarity";
        var colour = getColorTransition(ColourStore.UNIQUE_A, ColourStore.UNIQUE_B, (int) level.getGameTime(), 50);
        var getCorrectColour = rarity.id == 5 ? colour : rarity.getColour();
        var sibling = withStyleComponent(rarity.getSerializedName(), getCorrectColour);

        return withStyleComponentTrans(id, SUB_HEADER_COLOUR).copy().append(sibling);
    }

    public static Component attachRuneTierTooltip(ItemStack wandItem) {
        var data = getRuneData(wandItem);
        var getRarity = JahdooRarity.getAllRarities().get(Math.clamp(data.tier(), 0, 5));
        var getTier = romanNumeralConverter(getRarity.id);
        return withStyleComponent("Tier ", SUB_HEADER_COLOUR).copy().append(withStyleComponent(getTier, getRarity.getColour()));
    }

    public static @NotNull String romanNumeralConverter(int number) {
        return switch (number) {
            case 1 -> "II";
            case 2 -> "III";
            case 3 -> "IV";
            case 4 -> "V";
            case 5 -> "VI";
            default -> "I";
        };
    }

    //Debug using use on item
    public static void debugRarity(Player player){
        if(!player.level().isClientSide){
            playDebugMessage(player, "NEW ROLL");
            playDebugMessage(player, "---------------------------------");

            var common = new AtomicInteger();
            var uncommon = new AtomicInteger();
            var epic = new AtomicInteger();
            var legendary = new AtomicInteger();
            var ethereal = new AtomicInteger();

            for (int i = 2000; i > 0; i--) {
                var rarity = JahdooRarity.getRarity();

                if (rarity == JahdooRarity.COMMON) {
                    common.set(common.get() + 1);
                } else if (rarity == JahdooRarity.RARE) {
                    uncommon.set(uncommon.get() + 1);
                } else if (rarity == JahdooRarity.EPIC) {
                    epic.set(epic.get() + 1);
                } else if (rarity == JahdooRarity.LEGENDARY) {
                    legendary.set(legendary.get() + 1);
                } else if (rarity == JahdooRarity.ETERNAL) {
                    ethereal.set(ethereal.get() + 1);
                }
            }

            playDebugMessage(player, "Common " + common.get());
            playDebugMessage(player, "Un-Common " + uncommon.get());
            playDebugMessage(player, "Epic " + epic.get());
            playDebugMessage(player, "Legendary " + legendary.get());
            playDebugMessage(player, "Eternal " + ethereal.get());
            playDebugMessage(player, "  ");
        }
    }
}
