package org.jahdoo.trial_nexus.loot;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.level_manager.InstanceDifficulty;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.trading_post.ShoppingArmor;
import org.jahdoo.trial_nexus.trading_post.ShoppingItems;
import org.jahdoo.trial_nexus.utils.LocalLootBeamData;
import org.jahdoo.trial_nexus.utils.Maths;
import org.jahdoo.common.items.caster_item.CasterItem;
import org.jahdoo.common.items.gauntlet.BattlemageGauntlet;
import org.jahdoo.common.items.magnet.Magnet;
import org.jahdoo.common.items.runes.RuneItem;
import org.jahdoo.common.items.shields.JahdooShieldItem;
import org.jahdoo.common.items.tome.TomeOfUnity;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.shaydee.loot_beams_neoforge.data_component.DataComponentsReg;
import org.shaydee.loot_beams_neoforge.data_component.LootBeamComponent;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.core.registries.Registries.ENCHANTMENT;
import static net.minecraft.world.level.storage.loot.entries.LootItem.lootTableItem;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.VAULT;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParams.ORIGIN;
import static net.minecraft.world.level.storage.loot.providers.number.ConstantValue.exactly;
import static net.minecraft.world.level.storage.loot.providers.number.UniformGenerator.between;
import static org.jahdoo.trial_nexus.trading_post.ShoppingItems.*;
import static org.jahdoo.trial_nexus.utils.EnchantmentHelpers.enchant;
import static org.jahdoo.trial_nexus.utils.EnchantmentHelpers.randomApplicableEnchantment;
import static org.jahdoo.trial_nexus.utils.Helpers.Random;
import static org.jahdoo.common.items.runes.rune_data.RuneHelpers.generateRandomTypAttribute;

public class RewardLootTables {

    public static final LootPoolSingletonContainer.Builder<?> BOOK_BUILDER =
        lootTableItem(Items.ENCHANTED_BOOK);

    public static final LootPoolSingletonContainer.Builder<?> GOLD_BUILDER =
        lootTableItem(Items.GOLD_INGOT);

    public static final LootPoolSingletonContainer.Builder<?> NETHERITE_BUILDER =
        lootTableItem(Items.NETHERITE_SCRAP);

    public static final LootPoolSingletonContainer.Builder<?> IRON_BUILDER =
        lootTableItem(Items.IRON_INGOT);

    public static final LootPoolSingletonContainer.Builder<?> SHULKER_SHELLS_BUILDER =
        lootTableItem(Items.SHULKER_SHELL);

    public static final LootPoolSingletonContainer.Builder<?> GOLDEN_CARROT_BUILDER =
        lootTableItem(Items.GOLDEN_CARROT);

    public static final LootPoolSingletonContainer.Builder<?> EMERALD_BUILDER =
        lootTableItem(Items.EMERALD);

    public static final LootPoolSingletonContainer.Builder<?> REDSTONE =
        lootTableItem(Items.REDSTONE);

    public static final LootPoolSingletonContainer.Builder<?> LAPIS =
        lootTableItem(Items.LAPIS_LAZULI);

    public static final LootPoolSingletonContainer.Builder<?> QUARTZ =
        lootTableItem(Items.QUARTZ);

    public static final LootPoolSingletonContainer.Builder<?> SLIME =
        lootTableItem(Items.SLIME_BALL);

    public static final LootPoolSingletonContainer.Builder<?> NETHERITE_INGOT =
        lootTableItem(Items.NETHERITE_INGOT);

    public static final LootPoolSingletonContainer.Builder<?> ENDER_PEARL =
        lootTableItem(Items.ENDER_PEARL);

    public static final LootPoolSingletonContainer.Builder<?> DIAMOND_BUILDER =
        lootTableItem(Items.DIAMOND);

    public static final LootPoolSingletonContainer.Builder<?> DIAMOND_SWORD_BUILDER =
        lootTableItem(Items.DIAMOND_SWORD);

    public static final LootPoolSingletonContainer.Builder<?> NEXITE_BLOCK_BUILDER =
        lootTableItem(BlockReg.NEXITE_BLOCK.get());

    public static final LootPoolSingletonContainer.Builder<?> AUGMENT_CORE_BUILDER =
        lootTableItem(ItemReg.AUGMENT_CORE.get());

    public static final LootPoolSingletonContainer.Builder<?> ESSENCE_FRAGMENT =
        lootTableItem(ItemReg.ESSENCE_FRAGMENT.get());
    
    public static final LootPoolSingletonContainer.Builder<?> NETHERITE_SWORD_BUILDER = 
        lootTableItem(Items.NETHERITE_SWORD);
    
    public static final LootPoolSingletonContainer.Builder<?> ADVANCED_AUGMENT_CORE_BUILDER = 
        lootTableItem(ItemReg.ADVANCED_AUGMENT_CORE.get());
    
    public static final LootPoolSingletonContainer.Builder<?> TOME_OF_UNITY_BUILDER =
        lootTableItem(ItemReg.TOME_OF_UNITY.get());
    
    public static final LootPoolSingletonContainer.Builder<?> AUGMENT_HYPER_CORE_BUILDER = 
        lootTableItem(ItemReg.AUGMENT_HYPER_CORE.get());


    public static final LootPoolSingletonContainer.Builder<?> KNIGHT_KING_HELM_BUILDER =
        lootTableItem(ItemReg.KNIGHT_KING_HELMET.get());

    public static final LootPoolSingletonContainer.Builder<?> KNIGHT_KING_CHEST_BUILDER =
        lootTableItem(ItemReg.KNIGHT_KING_CHESTPLATE.get());

    public static final LootPoolSingletonContainer.Builder<?> KNIGHT_KING_LEGGINGS_BUILDER =
        lootTableItem(ItemReg.KNIGHT_KING_LEGGINGS.get());

    public static final LootPoolSingletonContainer.Builder<?> KNIGHT_KING_BOOTS_BUILDER =
        lootTableItem(ItemReg.KNIGHT_KING_BOOTS.get());


    public static final LootPoolSingletonContainer.Builder<?> WIZARD_HELM_BUILDER = 
        lootTableItem(ItemReg.WIZARD_HELMET.get());
    
    public static final LootPoolSingletonContainer.Builder<?> WIZARD_CHEST_BUILDER =
        lootTableItem(ItemReg.WIZARD_CHESTPLATE.get());
    
    public static final LootPoolSingletonContainer.Builder<?> WIZARD_LEGGINGS_BUILDER = 
        lootTableItem(ItemReg.WIZARD_LEGGINGS.get());
    
    public static final LootPoolSingletonContainer.Builder<?> WIZARD_BOOTS_BUILDER = 
        lootTableItem(ItemReg.WIZARD_BOOTS.get());


    public static final LootPoolSingletonContainer.Builder<?> BATTLEMAGE_HELM_BUILDER = 
        lootTableItem(ItemReg.BATTLEMAGE_HELMET.get());
    
    public static final LootPoolSingletonContainer.Builder<?> BATTLEMAGE_CHEASTPLATE_BUILDER = 
        lootTableItem(ItemReg.BATTLEMAGE_CHESTPLATE.get());
    
    public static final LootPoolSingletonContainer.Builder<?> BATTLEMAGE_LEGGINGS_BUILDER = 
        lootTableItem(ItemReg.BATTLEMAGE_LEGGINGS.get());
    
    public static final LootPoolSingletonContainer.Builder<?> BATTLEMAGE_BOOTS_BUILDER = 
        lootTableItem(ItemReg.BATTLEMAGE_BOOTS.get());


    public static final LootPoolSingletonContainer.Builder<?> MAGE_HELM_BUILDER =
        lootTableItem(ItemReg.MAGE_HELMET.get());

    public static final LootPoolSingletonContainer.Builder<?> MAGE_CHEST_BUILDER =
        lootTableItem(ItemReg.MAGE_CHESTPLATE.get());

    public static final LootPoolSingletonContainer.Builder<?> MAGE_LEGGINGS_BUILDER =
        lootTableItem(ItemReg.MAGE_LEGGINGS.get());

    public static final LootPoolSingletonContainer.Builder<?> MAGE_BOOTS_BUILDER =
        lootTableItem(ItemReg.MAGE_BOOTS.get());


    public static final LootPoolSingletonContainer.Builder<?> ELYTRA_BUILDER = 
        lootTableItem(Items.ELYTRA);
    
    public static final LootPoolSingletonContainer.Builder<?> COIN =
        lootTableItem(ItemReg.COIN.get());
    
    public static final LootPoolSingletonContainer.Builder<?> XP = 
        lootTableItem(ItemReg.EXPERIENCE_ORB.get());
    
    public static final LootPoolSingletonContainer.Builder<?> RUNE = 
        lootTableItem(ItemReg.RUNE.get());
    
    public static final LootPoolSingletonContainer.Builder<?> INGMAS_SWORD = 
        lootTableItem(ItemReg.INGMAS_SWORD.get());

    public static final LootPoolSingletonContainer.Builder<?> GLAIVE =
        lootTableItem(ItemReg.ANCIENT_GLAIVE.get());

    public static final LootPoolSingletonContainer.Builder<?> EXIT_KEY =
        lootTableItem(ItemReg.EXIT_KEY.get());
    
    public static final LootPoolSingletonContainer.Builder<?> MAGNET =
        lootTableItem(ItemReg.MAGNET.get());

    public static final LootPoolSingletonContainer.Builder<?> CHALLENGER_TICKET =
        lootTableItem(ItemReg.TRIAL_TICKET.get());

    public static final LootPoolSingletonContainer.Builder<?> STARTER_PACK =
        lootTableItem(ItemReg.CARE_PACKAGE.get());

    public static final LootPoolSingletonContainer.Builder<?> BASIC_SHIELD =
        lootTableItem(ItemReg.BASIC_SHIELD.get());

    public static final LootPoolSingletonContainer.Builder<?> UNDEAD_PROTECTOR_SHIELD =
        lootTableItem(ItemReg.UNDEAD_PROTECTOR_SHIELD.get());

    public static final LootPoolSingletonContainer.Builder<?> GAUNTLET =
        lootTableItem(ItemReg.BATTLEMAGE_GAUNTLET.get());

    public static final LootPoolSingletonContainer.Builder<?> ELEMENTAL_SWORD =
        lootTableItem(ItemReg.ELEMENTAL_SWORD.get());


    public static List<ItemStack> getCoinItems(InstanceData data) {
        var lootCoins = new ArrayList<ItemStack>();

        for (int i = 0; i < data.getBronzeCoin(); i++){
            lootCoins.add(new ItemStack(ItemReg.COIN));
        }

        for (int i = 0; i < data.getSilverCoin(); i++){
            var stack = new ItemStack(ItemReg.COIN);
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));
            lootCoins.add(stack);
        }

        for (int i = 0; i < data.getGoldCoin(); i++){
            var stack = new ItemStack(ItemReg.COIN);
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(2));
            lootCoins.add(stack);
        }

        return lootCoins;
    }

    private static void attachLootBeam(ItemStack itemStack, LootBeamComponent data) {
        var lootBeamData = DataComponentsReg.INSTANCE.getLOOT_BEAM_DATA();
        
        if(!itemStack.has(lootBeamData)) itemStack.set(lootBeamData, data);
    }

    private static LootPoolSingletonContainer.Builder<? extends LootPoolSingletonContainer.Builder<?>> getRandomWand() {
        var randomWand = ElementReg.random().getWand();
        var wand = randomWand != null ? randomWand : ItemReg.WAND_ITEM_FROST.get();
        
        return lootTableItem(wand);
    }

    public static void attachEnchantmentWithChance(ItemStack itemStack, ServerLevel serverLevel, ResourceKey<Enchantment> enchantmentKey, int minVal, int maxVal, boolean isSpecial) {
        if (Random.nextInt(isSpecial ? 5 : 20) != 0) return;

        enchant(itemStack, serverLevel.registryAccess(), enchantmentKey, Random.nextInt(minVal, maxVal));
        attachLootBeam(itemStack, LocalLootBeamData.ENCHANTED_VANILLA_SWORD);
    }

    public static void attachEnchantment(ItemStack itemStack, ServerLevel serverLevel, ResourceKey<Enchantment> enchantmentKey, int minVal, int maxVal) {
        enchant(itemStack, serverLevel.registryAccess(), enchantmentKey, Random.nextInt(minVal, maxVal));
        attachLootBeam(itemStack, LocalLootBeamData.ENCHANTED_VANILLA_SWORD);
    }

    private static ObjectArrayList<ItemStack> createLootParams(ServerLevel serverLevel, Vec3 pos, LootTable.Builder loot) {
        var shouldEnchant = Random.nextInt(10) == 0 ? loot.apply(randomApplicableEnchantment(serverLevel.registryAccess())) : loot;
        var lootParams = new LootParams.Builder(serverLevel).withParameter(ORIGIN, pos).create(VAULT);
        
        return shouldEnchant.build().getRandomItems(lootParams);
    }

    public static ObjectArrayList<ItemStack> getCompletionLoot(
        ServerLevel serverLevel,
        Vec3 pos,
        String difficulty,
        int chestRarity
    ) {
        var loot = LootTable.lootTable();
        var getDifficulty = InstanceDifficulty.getFromName(difficulty);

        loot.withPool(multiPoolBuilder(getDifficulty, chestRarity));
        loot.withPool(singlePoolBuilder(getDifficulty, chestRarity));

        return createLootParams(serverLevel, pos, loot);
    }

    private static void enchantedBook(ServerLevel serverLevel, ItemStack itemStack){
        serverLevel
            .registryAccess()
            .registryOrThrow(ENCHANTMENT)
            .getRandom(RandomSource.create())
            .ifPresent(
                key -> {
                    var value = key.getDelegate().value();
                    var maxLevel = value.getMaxLevel();
                    var minLevel = value.getMinLevel();

                    enchant(itemStack, serverLevel.registryAccess(), key.getKey(), maxLevel > minLevel ? Random.nextInt(minLevel, maxLevel) : 1);
                    attachLootBeam(itemStack, LocalLootBeamData.SPECIALLY_ENCHANTED_BOOK);
                }
            );
    }

    public static void attachItemData(
        ServerLevel serverLevel,
        ItemStack itemStack,
        JahdooRarity runeRarity,
        int chestRarity
    ) {
        var raritiesByChestRarity = getRaritiesByChestRarity(chestRarity);
        switch (itemStack.getItem()){
            case CasterItem ignored -> ShoppingItems.getRandomWand(raritiesByChestRarity, itemStack);
            case TomeOfUnity ignored -> createTomeAttributes(itemStack, raritiesByChestRarity);
            case RuneItem ignored -> generateRandomTypAttribute(itemStack, runeRarity, raritiesByChestRarity);
            case ArmorItem ignored -> ShoppingArmor.attachCustomArmorData(serverLevel, itemStack, raritiesByChestRarity);
            case SwordItem ignored -> ShoppingItems.attachWeaponData(itemStack, raritiesByChestRarity, serverLevel);
            case EnchantedBookItem ignored -> enchantedBook(serverLevel, itemStack);
            case Magnet ignored -> magnetItem(itemStack, raritiesByChestRarity);
            case JahdooShieldItem ignored -> basicShieldWithRarity(itemStack, raritiesByChestRarity);
            case BattlemageGauntlet ignore -> getGauntletWithRarity(itemStack, raritiesByChestRarity);
            default -> { /*IGNORE*/ }
        }
    }

    private static LootPool.Builder multiPoolBuilder(InstanceDifficulty difficulty, int chestRarity) {
        var lootMultiplier = getLootMultiplier(difficulty, chestRarity);
        var min = 3F;
        var builder = LootPool.lootPool().setRolls(between(min, min + lootMultiplier));
        var newRarity = chestRarity+1;

        if(newRarity >= 1){
            //Common
            builder.add(GOLDEN_CARROT_BUILDER.setWeight(50));
            builder.add(IRON_BUILDER.setWeight(35));
            builder.add(REDSTONE.setWeight(30));
            builder.add(LAPIS.setWeight(30));
            builder.add(GOLD_BUILDER.setWeight(25));
            builder.add(ENDER_PEARL.setWeight(20));
            builder.add(EMERALD_BUILDER.setWeight(15));
            builder.add(DIAMOND_BUILDER.setWeight(10));
            builder.add(SLIME.setWeight(10));
            builder.add(QUARTZ.setWeight(10));
            builder.add(COIN.setWeight(10));
            builder.add(XP.setWeight(8));
            builder.add(NETHERITE_BUILDER.setWeight(5));
            builder.add(SHULKER_SHELLS_BUILDER.setWeight(5));
            builder.add(NETHERITE_INGOT.setWeight(2));
        }

        if(newRarity >= 2){
            //Rare
            builder.add(NEXITE_BLOCK_BUILDER.setWeight(20));
            builder.add(BOOK_BUILDER.setWeight(15));
        }

        return builder;
    }

    private static LootPool.Builder singlePoolBuilder(InstanceDifficulty difficulty, int chestRarity) {
        var builder = LootPool.lootPool().setRolls(exactly(1.0F));
        var newRarity = chestRarity+1;

        if(newRarity >= 1){
            //Common
            builder.add(DIAMOND_SWORD_BUILDER.setWeight(5));
            builder.add(ELYTRA_BUILDER.setWeight(1));
            builder.add(NETHERITE_SWORD_BUILDER.setWeight(1));
            builder.add(STARTER_PACK.setWeight(1));
        }

        if(newRarity >=2){
            //Rare
            builder.add(ESSENCE_FRAGMENT.setWeight(5));

            if(Maths.percentageChance(calculateChance(1, difficulty, newRarity))){
                builder.add(AUGMENT_CORE_BUILDER.setWeight((int) calculateChance(1, difficulty, newRarity)));
            }

            if(Maths.percentageChance(calculateChance(20, difficulty, newRarity))){
                builder.add(RUNE.setWeight((int) calculateChance(15, difficulty, newRarity)));
                builder.add(MAGNET.setWeight((int) calculateChance(10, difficulty, newRarity)));
                builder.add(getRandomWand().setWeight((int) calculateChance(5, difficulty, newRarity)));
            }
        }

        if(newRarity >= 3){
            //Legendary
            if(Maths.percentageChance(calculateChance(1, difficulty, newRarity))){
                builder.add(ADVANCED_AUGMENT_CORE_BUILDER.setWeight((int) calculateChance(1, difficulty, newRarity)));
            }
            if(Maths.percentageChance(calculateChance(1, difficulty, newRarity))){
                builder.add(CHALLENGER_TICKET.setWeight((int) calculateChance(1, difficulty, newRarity)));
                builder.add(EXIT_KEY.setWeight((int) calculateChance(1, difficulty, newRarity)));
            }
        }

        if(newRarity == 4){
            //Eternal
            if(Maths.percentageChance(calculateChance(1, difficulty, newRarity))){
                builder.add(AUGMENT_HYPER_CORE_BUILDER.setWeight((int) calculateChance(1, difficulty, newRarity)));
            }
        }

        if(Maths.percentageChance(calculateChance(15, difficulty, newRarity))){
            builder.add(TOME_OF_UNITY_BUILDER.setWeight((int) calculateChance(4, difficulty, newRarity)));
            builder.add(INGMAS_SWORD.setWeight((int) calculateChance(3, difficulty, newRarity)));
            addMageArmor(builder, newRarity);
        }

        if(Maths.percentageChance(calculateChance(8, difficulty, newRarity))){
            builder.add(GLAIVE.setWeight((int) calculateChance(2, difficulty, newRarity)));
            builder.add(BASIC_SHIELD.setWeight((int) calculateChance(4, difficulty, newRarity)));
        }

        if(Maths.percentageChance(calculateChance(5, difficulty, newRarity))){
//            builder.add(UNDEAD_PROTECTOR_SHIELD.setWeight(2));
            builder.add(ELEMENTAL_SWORD.setWeight((int) calculateChance(1, difficulty, newRarity)));
            builder.add(GAUNTLET.setWeight((int) calculateChance(1, difficulty, newRarity)));
            addBattlemageArmor(builder, newRarity);
        }

        if(Maths.percentageChance(calculateChance(1.5, difficulty, newRarity))){
            addWizardArmor(builder, newRarity);
        }

        if(Maths.percentageChance(calculateChance(0.5, difficulty, newRarity))){
            addKnightKingArmor(builder, newRarity);
        }

        return builder;
    }

    public static void addMageArmor(LootPool.Builder builder, int chestRarity){
        builder.add(MAGE_HELM_BUILDER.setWeight(chestRarity));
        builder.add(MAGE_CHEST_BUILDER.setWeight(chestRarity));
        builder.add(MAGE_LEGGINGS_BUILDER.setWeight(chestRarity));
        builder.add(MAGE_BOOTS_BUILDER.setWeight(chestRarity));
    }

    public static void addKnightKingArmor(LootPool.Builder builder, int chestRarity){
        builder.add(KNIGHT_KING_HELM_BUILDER.setWeight(chestRarity));
        builder.add(KNIGHT_KING_CHEST_BUILDER.setWeight(chestRarity));
        builder.add(KNIGHT_KING_LEGGINGS_BUILDER.setWeight(chestRarity));
        builder.add(KNIGHT_KING_BOOTS_BUILDER.setWeight(chestRarity));
    }

    public static void addWizardArmor(LootPool.Builder builder, int chestRarity){
        builder.add(WIZARD_HELM_BUILDER.setWeight(chestRarity));
        builder.add(WIZARD_CHEST_BUILDER.setWeight(chestRarity));
        builder.add(WIZARD_LEGGINGS_BUILDER.setWeight(chestRarity));
        builder.add(WIZARD_BOOTS_BUILDER.setWeight(chestRarity));
    }

    public static void addBattlemageArmor(LootPool.Builder builder, int chestRarity){
        builder.add(BATTLEMAGE_HELM_BUILDER.setWeight(chestRarity));
        builder.add(BATTLEMAGE_CHEASTPLATE_BUILDER.setWeight(chestRarity));
        builder.add(BATTLEMAGE_LEGGINGS_BUILDER.setWeight(chestRarity));
        builder.add(BATTLEMAGE_BOOTS_BUILDER.setWeight(chestRarity));
    }

    private static int getLootMultiplier(InstanceDifficulty difficulty, int chestRarity) {
        return ((difficulty.expMultiplier() + 1)) * (chestRarity + 1);
    }

    public static double calculateChance(double baseChance, InstanceDifficulty diff, int chestRarity) {
        return baseChance * diff.expMultiplier() + chestRarity;
    }
}
