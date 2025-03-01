package org.jahdoo.ascension;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.items.augments.Augment;
import org.jahdoo.common.items.magnet.Magnet;
import org.jahdoo.common.items.magnet.MagnetData;
import org.jahdoo.common.items.runes.RuneItem;
import org.jahdoo.common.items.tome.TomeOfUnity;
import org.jahdoo.common.items.wand.WandItem;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.ItemReg;
import org.shaydee.loot_beams_neoforge.data_component.DataComponentsReg;
import org.shaydee.loot_beams_neoforge.data_component.LootBeamComponent;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;
import static net.minecraft.core.registries.Registries.ENCHANTMENT;
import static net.minecraft.world.level.storage.loot.entries.LootItem.lootTableItem;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.VAULT;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParams.ORIGIN;
import static net.minecraft.world.level.storage.loot.providers.number.ConstantValue.exactly;
import static net.minecraft.world.level.storage.loot.providers.number.UniformGenerator.between;
import static org.jahdoo.ascension.rarity.JahdooRarity.*;
import static org.jahdoo.ascension.trading_post.ShoppingArmor.enchantArmorItem;
import static org.jahdoo.ascension.trading_post.ShoppingWeapon.enchantSword;
import static org.jahdoo.ascension.utils.EnchantmentHelpers.enchant;
import static org.jahdoo.ascension.utils.EnchantmentHelpers.randomApplicableEnchantment;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.ascension.utils.Helpers.setDurability;
import static org.jahdoo.common.items.runes.rune_data.RuneHelpers.generateRandomTypAttribute;
import static org.jahdoo.common.registers.ComponentReg.JAHDOO_RARITY;

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

    public static final LootPoolSingletonContainer.Builder<?> ENCHANTED_BOTTLES_BUILDER =
        lootTableItem(Items.EXPERIENCE_BOTTLE);

    public static final LootPoolSingletonContainer.Builder<?> GOLDEN_CARROT_BUILDER =
        lootTableItem(Items.GOLDEN_CARROT);

    public static final LootPoolSingletonContainer.Builder<?> EMERALD_BUILDER =
        lootTableItem(Items.EMERALD);

    public static final LootPoolSingletonContainer.Builder<?> DIAMOND_BUILDER =
        lootTableItem(Items.DIAMOND);

    public static final LootPoolSingletonContainer.Builder<?> IRON_SWORD_BUILDER =
        lootTableItem(Items.IRON_SWORD);

    public static final LootPoolSingletonContainer.Builder<?> DIAMOND_SWORD_BUILDER =
        lootTableItem(Items.DIAMOND_SWORD);

    public static final LootPoolSingletonContainer.Builder<?> NEXITE_BLOCK_BUILDER =
        lootTableItem(BlockReg.NEXITE_BLOCK.get());

    public static final LootPoolSingletonContainer.Builder<?> AUGMENT_CORE_BUILDER =
        lootTableItem(ItemReg.AUGMENT_CORE.get());
    
    public static final LootPoolSingletonContainer.Builder<?> NETHERITE_SWORD_BUILDER = 
        lootTableItem(Items.NETHERITE_SWORD);
    
    public static final LootPoolSingletonContainer.Builder<?> ADVANCED_AUGMENT_CORE_BUILDER = 
        lootTableItem(ItemReg.ADVANCED_AUGMENT_CORE.get());
    
    public static final LootPoolSingletonContainer.Builder<?> AUGMENT_ITEM_BUILDER = 
        lootTableItem(ItemReg.AUGMENT.get());
    
    public static final LootPoolSingletonContainer.Builder<?> TOME_OF_UNITY_BUILDER =
        lootTableItem(ItemReg.TOME_OF_UNITY.get());
    
    public static final LootPoolSingletonContainer.Builder<?> AUGMENT_HYPER_CORE_BUILDER = 
        lootTableItem(ItemReg.AUGMENT_HYPER_CORE.get());
    
    public static final LootPoolSingletonContainer.Builder<?> WIZARD_HELM_BUILDER = 
        lootTableItem(ItemReg.WIZARD_HELMET.get());
    
    public static final LootPoolSingletonContainer.Builder<?> WIZARD_CHEST_BUILDER =
        lootTableItem(ItemReg.WIZARD_CHESTPLATE.get());
    
    public static final LootPoolSingletonContainer.Builder<?> WIZARD_LEGGINGS_BUILDER = 
        lootTableItem(ItemReg.WIZARD_LEGGINGS.get());
    
    public static final LootPoolSingletonContainer.Builder<?> WIZARD_BOOTS_BUILDER = 
        lootTableItem(ItemReg.WIZARD_BOOTS.get());
    
    public static final LootPoolSingletonContainer.Builder<?> BATTLEMAGE_HELM_BUILDER = 
        lootTableItem(ItemReg.MAGE_HELMET.get());
    
    public static final LootPoolSingletonContainer.Builder<?> BATTLEMAGE_CHEASTPLATE_BUILDER = 
        lootTableItem(ItemReg.MAGE_CHESTPLATE.get());
    
    public static final LootPoolSingletonContainer.Builder<?> BATTLEMAGE_LEGGINGS_BUILDER = 
        lootTableItem(ItemReg.MAGE_LEGGINGS.get());
    
    public static final LootPoolSingletonContainer.Builder<?> BATTLEMAGE_BOOTS_BUILDER = 
        lootTableItem(ItemReg.MAGE_BOOTS.get());
    
    public static final LootPoolSingletonContainer.Builder<?> ELYTRA_BUILDER = 
        lootTableItem(Items.ELYTRA);
    
    public static final LootPoolSingletonContainer.Builder<?> COIN =
        lootTableItem(ItemReg.COIN.get());
    
    public static final LootPoolSingletonContainer.Builder<?> XP = 
        lootTableItem(ItemReg.EXPERIENCE_ORB.get());
    
    public static final LootPoolSingletonContainer.Builder<?> RUNE = 
        lootTableItem(ItemReg.RUNE.get());
    
    public static final LootPoolSingletonContainer.Builder<?> BATTLEMAGE_GAUNTLET = 
        lootTableItem(ItemReg.BATTLEMAGE_GAUNTLET.get());
    
    public static final LootPoolSingletonContainer.Builder<?> INGMAS_SWORD = 
        lootTableItem(ItemReg.INGMAS_SWORD.get());
    
    public static final LootPoolSingletonContainer.Builder<?> ANCIENT_AMULET = 
        lootTableItem(ItemReg.PENDENT.get());
    
    public static final LootPoolSingletonContainer.Builder<?> MAGNET = 
        lootTableItem(ItemReg.MAGNET.get());
    
    public static ObjectArrayList<ItemStack> getCoinItems(ServerLevel serverLevel, Vec3 pos, int level) {
        var loot = LootTable.lootTable().withPool(coinLoot(level));
        
        return createLootParams(serverLevel, pos, loot);
    }

    private static void attachLootBeam(ItemStack itemStack, LootBeamComponent data) {
        var lootBeamData = DataComponentsReg.INSTANCE.getLOOT_BEAM_DATA();
        
        if(!itemStack.has(lootBeamData)) itemStack.set(lootBeamData, data);
    }

    private static LootPoolSingletonContainer.Builder<? extends LootPoolSingletonContainer.Builder<?>> getRandomWand() {
        var randomWand = ElementReg.getRandomElement().getWand();
        var wand = randomWand != null ? randomWand : ItemReg.WAND_ITEM_FROST.get();
        
        return lootTableItem(wand);
    }

    public static void attachEnchantment(ItemStack itemStack, ServerLevel serverLevel, ResourceKey<Enchantment> enchantmentKey, int minVal, int maxVal, boolean isSpecial) {
        if (Random.nextInt(isSpecial ? 5 : 20) != 0) return;
        
        enchant(itemStack, serverLevel.registryAccess(), enchantmentKey, Random.nextInt(minVal, maxVal));
        attachLootBeam(itemStack, LocalLootBeamData.ENCHANTED_VANILLA_SWORD);
    }

    private static ObjectArrayList<ItemStack> createLootParams(ServerLevel serverLevel, Vec3 pos, LootTable.Builder loot) {
        var shouldEnchant = Random.nextInt(10) == 0 ? loot.apply(randomApplicableEnchantment(serverLevel.registryAccess())) : loot;
        var lootParams = new LootParams.Builder(serverLevel).withParameter(ORIGIN, pos).create(VAULT);
        
        return shouldEnchant.build().getRandomItems(lootParams);
    }

    public static ObjectArrayList<ItemStack> getCompletionLoot(ServerLevel serverLevel, Vec3 pos, int level) {
        var loot = LootTable.lootTable().withPool(commonPool(serverLevel));
        if (Random.nextInt(Math.max(1, 10 - level)) == 0) loot.withPool(epicPool(serverLevel));
        if (Random.nextInt(Math.max(1, 100 - level)) == 0) loot.withPool(legendaryPool(serverLevel));
        
        loot.withPool(rareWeaponPool(serverLevel));
        loot.withPool(rarePool(serverLevel));
        
        return createLootParams(serverLevel, pos, loot);
    }

    public static ItemStack magnetItem(JahdooRarity getRarity, ItemStack itemStack) {
        var id = getRarity.getId() + 1;
        var origin = id * 1000;

        setDurability(itemStack, Random.nextInt(origin, origin * 3));
        itemStack.set(CUSTOM_MODEL_DATA, new CustomModelData(id - 1));
        itemStack.set(JAHDOO_RARITY, getRarity.getId());
        MagnetData.setDataByType(itemStack);
        attachLootBeam(itemStack, LocalLootBeamData.rarityLootBeam(getRarity));
        return itemStack;
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

    public static void attachItemData(ServerLevel serverLevel, JahdooRarity rarity, ItemStack itemStack, boolean isSpecial, JahdooRarity runeRarity) {
        switch (itemStack.getItem()){
            case WandItem ignored -> setGeneratedWand(rarity, itemStack);
            case TomeOfUnity ignored -> createTomeAttributes(rarity, itemStack);
            case Augment ignored -> setGeneratedAugment(itemStack, rarity);
            case RuneItem ignored -> generateRandomTypAttribute(itemStack, runeRarity);
            case ArmorItem armorItem -> enchantArmorItem(serverLevel, itemStack, armorItem, isSpecial);
            case SwordItem ignored -> enchantSword(serverLevel, itemStack, isSpecial);
            case EnchantedBookItem ignored -> enchantedBook(serverLevel, itemStack);
            case Magnet ignored -> magnetItem(rarity, itemStack);
            default -> { /*IGNORE*/ }
        }
    }

    private static LootPool.Builder coinLoot(float level) {
        var builder = LootPool.lootPool().setRolls(between(level/2, level));

        return builder
            .add(COIN.setWeight(40));

    }

    private static LootPool.Builder rarePool(ServerLevel serverLevel) {
        var builder = LootPool.lootPool().setRolls(between(1.0F, 3.0F));

        return builder
            .add(NEXITE_BLOCK_BUILDER.setWeight(20))
            .add(AUGMENT_CORE_BUILDER.setWeight(15))
            .add(BOOK_BUILDER.setWeight(5));
    }

    private static LootPool.Builder legendaryPool(ServerLevel serverLevel) {
        var builder = LootPool.lootPool().setRolls(exactly(1.0F));

        return builder
            .add(AUGMENT_HYPER_CORE_BUILDER.setWeight(6))
            .add(WIZARD_HELM_BUILDER.setWeight(1))
            .add(WIZARD_CHEST_BUILDER.setWeight(1))
            .add(WIZARD_LEGGINGS_BUILDER.setWeight(1))
            .add(WIZARD_BOOTS_BUILDER.setWeight(1));
    }

    private static LootPool.Builder epicPool(ServerLevel serverLevel) {
        var builder = LootPool.lootPool().setRolls(exactly(1.0F));

        return builder
            .add(ADVANCED_AUGMENT_CORE_BUILDER.setWeight(6))
            .add(TOME_OF_UNITY_BUILDER.setWeight(4))
            .add(BATTLEMAGE_HELM_BUILDER.setWeight(1))
            .add(BATTLEMAGE_CHEASTPLATE_BUILDER.setWeight(1))
            .add(BATTLEMAGE_LEGGINGS_BUILDER.setWeight(1))
            .add(BATTLEMAGE_BOOTS_BUILDER.setWeight(1));
    }

    private static LootPool.Builder rareWeaponPool(ServerLevel serverLevel) {
        var builder = LootPool.lootPool().setRolls(exactly(1.0F));

        return builder
            .add(RUNE.setWeight(5))
            .add(AUGMENT_ITEM_BUILDER.setWeight(5))
            .add(getRandomWand().setWeight(2))
            .add(MAGNET.setWeight(5))
            .add(IRON_SWORD_BUILDER.setWeight(20))
            .add(DIAMOND_SWORD_BUILDER.setWeight(10))
            .add(NETHERITE_SWORD_BUILDER.setWeight(2))
            .add(INGMAS_SWORD.setWeight(1));
    }

    private static LootPool.Builder commonPool(ServerLevel serverLevel) {
        var builder = LootPool.lootPool().setRolls(between(2.0F, 5.0F));

        return builder
            .add(GOLDEN_CARROT_BUILDER.setWeight(50))
            .add(IRON_BUILDER.setWeight(35))
            .add(GOLD_BUILDER.setWeight(25))
            .add(EMERALD_BUILDER.setWeight(20))
            .add(DIAMOND_BUILDER.setWeight(10))
            .add(COIN.setWeight(10))
            .add(ENCHANTED_BOTTLES_BUILDER.setWeight(8))
            .add(SHULKER_SHELLS_BUILDER.setWeight(5))
            .add(NETHERITE_BUILDER.setWeight(2))
            .add(ELYTRA_BUILDER.setWeight(1));
    }
}
