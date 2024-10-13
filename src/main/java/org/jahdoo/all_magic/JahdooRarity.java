package org.jahdoo.all_magic;

import net.minecraft.network.chat.Style;
import net.minecraft.util.FastColor;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;
import net.neoforged.fml.common.asm.enumextension.IndexedEnum;
import net.neoforged.fml.common.asm.enumextension.NetworkedEnum;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.particle.ParticleStore.rgbToInt;

@NetworkedEnum(NetworkedEnum.NetworkCheck.BIDIRECTIONAL)
@IndexedEnum
public enum JahdooRarity implements StringRepresentable, IExtensibleEnum {
    COMMON(0, "Common", color(255,11,176,16), 1),
    UNCOMMON(1, "Un-common", color(255,67, 164, 222), 300),
    EPIC(2, "Epic", color(255,222, 136, 255), 800),
    LEGENDARY(3, "Legendary", color(255,225, 199, 107), 1500),
    ETERNAL(4, "Eternal", color(255,218, 71, 71), 3000);


    private static final List<JahdooRarity> getAllRarities = List.of(
        COMMON,
        UNCOMMON,
        EPIC,
        LEGENDARY,
        ETERNAL
    );

    private final int id;
    private final String name;
    private final int color;
    private final UnaryOperator<Style> styleModifier;
    private final int chanceRange;

    JahdooRarity(int id, String name, int color, int chanceRange) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.chanceRange = chanceRange;
        this.styleModifier = (style) -> style.withColor(color);
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }

    public int getId(){
        return this.id;
    }

    public int getColour(){
        return this.color;
    }

    public UnaryOperator<Style> getStyleModifier() {
        return this.styleModifier;
    }

    public static List<JahdooRarity> getAllRarities(){
        return getAllRarities;
    }

    public static JahdooRarity getRarity(){
        var getRandom = GeneralHelpers.Random.nextInt(1, 3400);
        var filteredList = new ArrayList<>(getAllRarities.stream().filter(jahdooRarity -> jahdooRarity.chanceRange <= getRandom).toList());
        return GeneralHelpers.getRandomListElement(filteredList);
    }

    public static AbstractAbility getAbilityWithRarity(){
        var list = AbilityRegister.getMatchingRarity(JahdooRarity.getRarity());
        return list.get(GeneralHelpers.Random.nextInt(0, list.size()));
    }


    //Debug using use on item
    public static void debugRarity(Player player){
        if(!player.level().isClientSide){
            GeneralHelpers.playDebugMessage(player, "NEW ROLL");
            GeneralHelpers.playDebugMessage(player, "---------------------------------");

            var common = new AtomicInteger();
            var uncommon = new AtomicInteger();
            var epic = new AtomicInteger();
            var legendary = new AtomicInteger();
            var ethereal = new AtomicInteger();

            for (int i = 500; i > 0; i--) {
                var rarity = JahdooRarity.getRarity();

                if (rarity == JahdooRarity.COMMON) {
                    common.set(common.get() + 1);
                } else if (rarity == JahdooRarity.UNCOMMON) {
                    uncommon.set(uncommon.get() + 1);
                } else if (rarity == JahdooRarity.EPIC) {
                    epic.set(epic.get() + 1);
                } else if (rarity == JahdooRarity.LEGENDARY) {
                    legendary.set(legendary.get() + 1);
                } else if (rarity == JahdooRarity.ETERNAL) {
                    ethereal.set(ethereal.get() + 1);
                }
            }

            GeneralHelpers.playDebugMessage(player, "Common " + common.get());
            GeneralHelpers.playDebugMessage(player, "Un-Common " + uncommon.get());
            GeneralHelpers.playDebugMessage(player, "Epic " + epic.get());
            GeneralHelpers.playDebugMessage(player, "Legendary " + legendary.get());
            GeneralHelpers.playDebugMessage(player, "Eternal " + ethereal.get());
            GeneralHelpers.playDebugMessage(player, "  ");
        }
    }
}
