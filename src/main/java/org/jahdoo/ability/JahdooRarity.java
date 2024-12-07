package org.jahdoo.ability;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;
import net.neoforged.fml.common.asm.enumextension.IndexedEnum;
import org.jahdoo.items.augments.Augment;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.items.augments.AugmentItemHelper.setAbilityToAugment;
import static org.jahdoo.utils.ModHelpers.withStyleComponent;
import static org.jahdoo.utils.ModHelpers.withStyleComponentTrans;

@IndexedEnum
public enum JahdooRarity implements StringRepresentable, IExtensibleEnum {
    COMMON(0, "Common", color(255,11,176,16), 1),
    UNCOMMON(1, "Uncommon", color(255,67, 164, 222), 300),
    EPIC(2, "Epic", color(255,222, 136, 255), 800),
    LEGENDARY(3, "Legendary", color(255,225, 199, 107), 1500),
    ETERNAL(4, "Eternal", color(255,218, 71, 71), 3000);

    private final int id;
    private final String name;
    private final int color;
    private final UnaryOperator<Style> styleModifier;
    private final int chanceRange;
    private static final List<JahdooRarity> getAllRarities = List.of(COMMON, UNCOMMON, EPIC, LEGENDARY, ETERNAL);

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
        var getRandom = ModHelpers.Random.nextInt(1, 3400);
        var filteredList = new ArrayList<>(getAllRarities.stream().filter(jahdooRarity -> jahdooRarity.chanceRange <= getRandom).toList());
        return ModHelpers.getRandomListElement(filteredList);
    }

    public static AbilityRegistrar getAbilityWithRarity(){
        var list = AbilityRegister.getMatchingRarity(JahdooRarity.getRarity());
        return list.get(ModHelpers.Random.nextInt(0, list.size()));
    }

    public static ItemStack getAbilityAugment(JahdooRarity...jahdooRarities){
        var allRarities = Arrays.stream(jahdooRarities).toList();
        var list = AbilityRegister.getMatchingRarity(allRarities.get(ModHelpers.Random.nextInt(0, allRarities.size())));
        var ability = list.get(ModHelpers.Random.nextInt(0, list.size()));
        var emptyStack = new ItemStack(ItemsRegister.AUGMENT_ITEM.get());
        ability.setModifiers(emptyStack);
        var wandAbilityHolder = emptyStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
        setAbilityToAugment(emptyStack, ability, wandAbilityHolder);
        return emptyStack;
    }

    public static Component addRarityTooltip(JahdooRarity rarity){
        return withStyleComponentTrans("rarity.jahdoo.current_rarity", -9013642).copy().append(withStyleComponent(rarity.getSerializedName(), rarity.getColour()));
    }

    //Debug using use on item
    public static void debugRarity(Player player){
        if(!player.level().isClientSide){
            ModHelpers.playDebugMessage(player, "NEW ROLL");
            ModHelpers.playDebugMessage(player, "---------------------------------");

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

            ModHelpers.playDebugMessage(player, "Common " + common.get());
            ModHelpers.playDebugMessage(player, "Un-Common " + uncommon.get());
            ModHelpers.playDebugMessage(player, "Epic " + epic.get());
            ModHelpers.playDebugMessage(player, "Legendary " + legendary.get());
            ModHelpers.playDebugMessage(player, "Eternal " + ethereal.get());
            ModHelpers.playDebugMessage(player, "  ");
        }
    }
}
