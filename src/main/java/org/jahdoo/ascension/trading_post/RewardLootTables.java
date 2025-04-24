package org.jahdoo.ascension.trading_post;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.attachments.InstanceData;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.LocalLootBeamData;
import org.jahdoo.common.items.caster_item.CasterItem;
import org.jahdoo.common.items.magnet.Magnet;
import org.jahdoo.common.items.magnet.MagnetData;
import org.jahdoo.common.items.pendent.Pendent;
import org.jahdoo.common.items.runes.RuneItem;
import org.jahdoo.common.items.runes.rune_data.RuneHolder;
import org.jahdoo.common.items.tome.TomeOfUnity;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.shaydee.loot_beams_neoforge.data_component.DataComponentsReg;
import org.shaydee.loot_beams_neoforge.data_component.LootBeamComponent;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;
import static net.minecraft.core.registries.Registries.ENCHANTMENT;
import static net.minecraft.world.item.enchantment.Enchantments.*;
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
    
    public static final LootPoolSingletonContainer.Builder<?> INGMAS_SWORD = 
        lootTableItem(ItemReg.INGMAS_SWORD.get());

    public static final LootPoolSingletonContainer.Builder<?> GLAIVE =
        lootTableItem(ItemReg.ANCIENT_GLAIVE.get());

    public static final LootPoolSingletonContainer.Builder<?> EXIT_KEY =
        lootTableItem(ItemReg.EXIT_KEY.get());

    public static final LootPoolSingletonContainer.Builder<?> AMULET =
        lootTableItem(ItemReg.PENDENT.get());
    
    public static final LootPoolSingletonContainer.Builder<?> MAGNET =
        lootTableItem(ItemReg.MAGNET.get());

    public static final LootPoolSingletonContainer.Builder<?> CHALLENGER_TICKET =
        lootTableItem(ItemReg.CHALLENGER_TICKET.get());

    public static final LootPoolSingletonContainer.Builder<?> STARTER_PACK =
        lootTableItem(ItemReg.CARE_PACKAGE.get());

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
        int level,
        int chestRarity
    ) {
        var loot = LootTable.lootTable()
            .withPool(commonPool(level, chestRarity))
            .withPool(commonPoolSingle());

        if(chestRarity >= 1){
            loot.withPool(rarePoolSingle())
                .withPool(rarePool(level, chestRarity));
        }

        if(chestRarity >= 2){
            loot.withPool(epicPool());
        }

        if(chestRarity > 2){
            loot.withPool(legendaryPool());
        }

        return createLootParams(serverLevel, pos, loot);
    }

    public static void amuletItem(ItemStack pendent) {
        var isLegendary = getRarity() == LEGENDARY;
        var x = isLegendary ? 1 : 0;

        if(isLegendary) pendent.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(x));

        RuneHolder.createNewRuneSlots(pendent, x + 1, isLegendary ? 50 : 25);
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
        var enchantments = List.of(
            PROTECTION,
            FEATHER_FALLING,
            BLAST_PROTECTION,
            PROJECTILE_PROTECTION,
            RESPIRATION,
            AQUA_AFFINITY,
            THORNS,
            DEPTH_STRIDER,
            FROST_WALKER,
            SOUL_SPEED,
            SWIFT_SNEAK,
            SHARPNESS,
            SMITE,
            BANE_OF_ARTHROPODS,
            KNOCKBACK,
            FIRE_ASPECT,
            LOOTING,
            SWEEPING_EDGE,
            EFFICIENCY,
            SILK_TOUCH,
            UNBREAKING,
            FORTUNE,
            MENDING
        );

        var resourceKey = Helpers.listRandom(enchantments);
        var randomEnchantment = serverLevel
            .registryAccess()
            .registryOrThrow(ENCHANTMENT)
            .get(resourceKey);

        var maxLevel = randomEnchantment.getMaxLevel();
        var minLevel = randomEnchantment.getMinLevel();
        enchant(itemStack, serverLevel.registryAccess(), resourceKey, maxLevel > minLevel ? Random.nextInt(minLevel, maxLevel) : 1);
        attachLootBeam(itemStack, LocalLootBeamData.SPECIALLY_ENCHANTED_BOOK);
    }

    public static void attachItemData(
        ServerLevel serverLevel,
        JahdooRarity rarity,
        ItemStack itemStack,
        boolean isSpecial,
        JahdooRarity runeRarity
    ) {
        switch (itemStack.getItem()){
            case CasterItem ignored -> ShoppingItems.getRandomWand(rarity, itemStack);
            case TomeOfUnity ignored -> createTomeAttributes(rarity, itemStack);
            case RuneItem ignored -> generateRandomTypAttribute(itemStack, runeRarity, null);
            case ArmorItem armorItem -> enchantArmorItem(serverLevel, itemStack, armorItem, isSpecial);
            case SwordItem ignored -> enchantSword(serverLevel, itemStack, isSpecial);
            case EnchantedBookItem ignored -> enchantedBook(serverLevel, itemStack);
            case Magnet ignored -> magnetItem(rarity, itemStack);
            case Pendent ignored -> amuletItem(itemStack);
            default -> { /*IGNORE*/ }
        }
    }

    private static LootPool.Builder legendaryPool() {
        var builder = LootPool.lootPool().setRolls(exactly(1.0F));

        return builder
            .add(AUGMENT_HYPER_CORE_BUILDER.setWeight(2))
            .add(WIZARD_HELM_BUILDER.setWeight(1))
            .add(WIZARD_CHEST_BUILDER.setWeight(1))
            .add(WIZARD_LEGGINGS_BUILDER.setWeight(1))
            .add(WIZARD_BOOTS_BUILDER.setWeight(1));
    }

    private static LootPool.Builder epicPool() {
        var builder = LootPool.lootPool().setRolls(exactly(1.0F));

        return builder
            .add(GLAIVE.setWeight(10))
            .add(ADVANCED_AUGMENT_CORE_BUILDER.setWeight(5))
            .add(EXIT_KEY.setWeight(1))
            .add(TOME_OF_UNITY_BUILDER.setWeight(4))
            .add(BATTLEMAGE_HELM_BUILDER.setWeight(1))
            .add(BATTLEMAGE_CHEASTPLATE_BUILDER.setWeight(1))
            .add(BATTLEMAGE_LEGGINGS_BUILDER.setWeight(1))
            .add(BATTLEMAGE_BOOTS_BUILDER.setWeight(1));
    }

    private static LootPool.Builder rarePoolSingle() {
        var builder = LootPool.lootPool().setRolls(exactly(1.0F));

        return builder
            .add(RUNE.setWeight(15))
            .add(getRandomWand().setWeight(2))
            .add(MAGNET.setWeight(5))
            .add(NETHERITE_SWORD_BUILDER.setWeight(2))
            .add(CHALLENGER_TICKET.setWeight(1))
            .add(INGMAS_SWORD.setWeight(1));
    }

    private static LootPool.Builder rarePool(int level, int chestRarity) {
        var lootMultiplier = getLootMultiplier(level, chestRarity);
        var builder = LootPool.lootPool().setRolls(between(1.0F, lootMultiplier));

        return builder
            .add(NEXITE_BLOCK_BUILDER.setWeight(20))
            .add(BOOK_BUILDER.setWeight(15))
            .add(AUGMENT_CORE_BUILDER.setWeight(5));
    }

    private static LootPool.Builder commonPoolSingle() {
        var builder = LootPool.lootPool().setRolls(exactly(1.0F));

        return builder
            .add(IRON_SWORD_BUILDER.setWeight(20))
            .add(DIAMOND_SWORD_BUILDER.setWeight(5))
            .add(AMULET.setWeight(5))
            .add(ELYTRA_BUILDER.setWeight(1))
            .add(STARTER_PACK.setWeight(1));
    }


    private static LootPool.Builder commonPool(int level, int chestRarity) {
        var lootMultiplier = getLootMultiplier(level, chestRarity);
        var builder = LootPool.lootPool().setRolls(between(2.0F, lootMultiplier + 5));

        return builder
            .add(GOLDEN_CARROT_BUILDER.setWeight(50))
            .add(IRON_BUILDER.setWeight(35))
            .add(GOLD_BUILDER.setWeight(25))
            .add(EMERALD_BUILDER.setWeight(20))
            .add(DIAMOND_BUILDER.setWeight(10))
            .add(COIN.setWeight(10))
            .add(XP.setWeight(8))
            .add(SHULKER_SHELLS_BUILDER.setWeight(5))
            .add(NETHERITE_BUILDER.setWeight(2));

    }

    private static int getLootMultiplier(int level, int chestRarity) {
        return (((level / 5) + 1)) * (chestRarity + 1);
    }

}
