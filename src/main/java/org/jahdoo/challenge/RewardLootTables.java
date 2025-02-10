package org.jahdoo.challenge;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.items.Magnet;
import org.jahdoo.items.Pendent;
import org.jahdoo.items.TomeOfUnity;
import org.jahdoo.items.augments.Augment;
import org.jahdoo.items.runes.RuneItem;
import org.jahdoo.items.runes.rune_data.RuneHolder;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shaydee.loot_beams_neoforge.data_component.DataComponentsReg;
import org.shaydee.loot_beams_neoforge.data_component.LootBeamComponent;

import static net.minecraft.world.entity.EquipmentSlot.*;
import static net.minecraft.world.item.enchantment.Enchantments.*;
import static org.jahdoo.ability.rarity.JahdooRarity.*;
import static org.jahdoo.challenge.EnchantmentHelpers.*;
import static org.jahdoo.challenge.trading_post.ShoppingArmor.enchantArmorItem;
import static org.jahdoo.challenge.trading_post.ShoppingWeapon.enchantSword;
import static org.jahdoo.items.runes.rune_data.RuneData.RuneHelpers.*;
import static org.jahdoo.registers.DataComponentRegistry.JAHDOO_RARITY;
import static org.jahdoo.utils.ModHelpers.Random;

public class RewardLootTables {
    public static final LootPoolSingletonContainer.Builder<?> BOOK_BUILDER = LootItem.lootTableItem(Items.ENCHANTED_BOOK);
    public static final LootPoolSingletonContainer.Builder<?> GOLD_BUILDER = LootItem.lootTableItem(Items.GOLD_INGOT);
    public static final LootPoolSingletonContainer.Builder<?> NETHERITE_BUILDER = LootItem.lootTableItem(Items.NETHERITE_SCRAP);
    public static final LootPoolSingletonContainer.Builder<?> IRON_BUILDER = LootItem.lootTableItem(Items.IRON_INGOT);
    public static final LootPoolSingletonContainer.Builder<?> SHULKER_SHELLS_BUILDER = LootItem.lootTableItem(Items.SHULKER_SHELL);
    public static final LootPoolSingletonContainer.Builder<?> ENCHANTED_BOTTLES_BUILDER = LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE);
    public static final LootPoolSingletonContainer.Builder<?> GOLDEN_CARROT_BUILDER = LootItem.lootTableItem(Items.GOLDEN_CARROT);
    public static final LootPoolSingletonContainer.Builder<?> EMERALD_BUILDER = LootItem.lootTableItem(Items.EMERALD);
    public static final LootPoolSingletonContainer.Builder<?> DIAMOND_BUILDER = LootItem.lootTableItem(Items.DIAMOND);
    public static final LootPoolSingletonContainer.Builder<?> IRON_SWORD_BUILDER = LootItem.lootTableItem(Items.IRON_SWORD);
    public static final LootPoolSingletonContainer.Builder<?> DIAMOND_SWORD_BUILDER = LootItem.lootTableItem(Items.DIAMOND_SWORD);
    public static final LootPoolSingletonContainer.Builder<?> NEXITE_BLOCK_BUILDER = LootItem.lootTableItem(BlocksRegister.NEXITE_BLOCK.get());
    public static final LootPoolSingletonContainer.Builder<?> AUGMENT_CORE_BUILDER = LootItem.lootTableItem(ItemsRegister.AUGMENT_CORE.get());
    public static final LootPoolSingletonContainer.Builder<?> NETHERITE_SWORD_BUILDER = LootItem.lootTableItem(Items.NETHERITE_SWORD);
    public static final LootPoolSingletonContainer.Builder<?> ADVANCED_AUGMENT_CORE_BUILDER = LootItem.lootTableItem(ItemsRegister.ADVANCED_AUGMENT_CORE.get());
    public static final LootPoolSingletonContainer.Builder<?> AUGMENT_ITEM_BUILDER = LootItem.lootTableItem(ItemsRegister.AUGMENT_ITEM.get());
    public static final LootPoolSingletonContainer.Builder<?> TOME_OF_UNITY_BUILDER = LootItem.lootTableItem(ItemsRegister.TOME_OF_UNITY.get());
    public static final LootPoolSingletonContainer.Builder<?> AUGMENT_HYPER_CORE_BUILDER = LootItem.lootTableItem(ItemsRegister.AUGMENT_HYPER_CORE.get());
    public static final LootPoolSingletonContainer.Builder<?> WIZARD_HELM_BUILDER = LootItem.lootTableItem(ItemsRegister.WIZARD_HELMET.get());
    public static final LootPoolSingletonContainer.Builder<?> WIZARD_CHEST_BUILDER = LootItem.lootTableItem(ItemsRegister.WIZARD_CHESTPLATE.get());
    public static final LootPoolSingletonContainer.Builder<?> WIZARD_LEGGINGS_BUILDER = LootItem.lootTableItem(ItemsRegister.WIZARD_LEGGINGS.get());
    public static final LootPoolSingletonContainer.Builder<?> WIZARD_BOOTS_BUILDER = LootItem.lootTableItem(ItemsRegister.WIZARD_BOOTS.get());
    public static final LootPoolSingletonContainer.Builder<?> BATTLEMAGE_HELM_BUILDER = LootItem.lootTableItem(ItemsRegister.MAGE_HELMET.get());
    public static final LootPoolSingletonContainer.Builder<?> BATTLEMAGE_CHEASTPLATE_BUILDER = LootItem.lootTableItem(ItemsRegister.MAGE_CHESTPLATE.get());
    public static final LootPoolSingletonContainer.Builder<?> BATTLEMAGE_LEGGINGS_BUILDER = LootItem.lootTableItem(ItemsRegister.MAGE_LEGGINGS.get());
    public static final LootPoolSingletonContainer.Builder<?> BATTLEMAGE_BOOTS_BUILDER = LootItem.lootTableItem(ItemsRegister.MAGE_BOOTS.get());

    public static final LootPoolSingletonContainer.Builder<?> ELYTRA_BUILDER = LootItem.lootTableItem(Items.ELYTRA);
    public static final LootPoolSingletonContainer.Builder<?> GOLD_COIN = LootItem.lootTableItem(ItemsRegister.GOLD_COIN.get());
    public static final LootPoolSingletonContainer.Builder<?> SILVER_COIN = LootItem.lootTableItem(ItemsRegister.SILVER_COIN.get());
    public static final LootPoolSingletonContainer.Builder<?> BRONZE_COIN = LootItem.lootTableItem(ItemsRegister.BRONZE_COIN.get());
    public static final LootPoolSingletonContainer.Builder<?> XP = LootItem.lootTableItem(ItemsRegister.EXPERIENCE_ORB.get());
    public static final LootPoolSingletonContainer.Builder<?> RUNE = LootItem.lootTableItem(ItemsRegister.RUNE.get());
    public static final LootPoolSingletonContainer.Builder<?> BATTLEMAGE_GAUNTLET = LootItem.lootTableItem(ItemsRegister.BATTLEMAGE_GAUNTLET.get());
    public static final LootPoolSingletonContainer.Builder<?> INGMAS_SWORD = LootItem.lootTableItem(ItemsRegister.INGMAS_SWORD.get());
    public static final LootPoolSingletonContainer.Builder<?> ANCIENT_AMULET = LootItem.lootTableItem(ItemsRegister.PENDENT.get());
    public static final LootPoolSingletonContainer.Builder<?> MAGNET = LootItem.lootTableItem(ItemsRegister.MAGNET.get());


    public static ObjectArrayList<ItemStack> getCompletionLoot(ServerLevel serverLevel, Vec3 pos, int level) {
        var loot = LootTable.lootTable().withPool(commonPool(serverLevel));
        if (Random.nextInt(Math.max(1, 10 - level)) == 0) loot.withPool(epicPool(serverLevel));
        if (Random.nextInt(Math.max(1, 100 - level)) == 0) loot.withPool(legendaryPool(serverLevel));
        loot.withPool(rareWeaponPool(serverLevel));

        loot.withPool(rarePool(serverLevel));
        return createLootParams(serverLevel, pos, loot);
    }

    private static LootPoolSingletonContainer.@NotNull Builder<? extends LootPoolSingletonContainer.Builder<?>> getRandomWand() {
        var randomWand = ElementRegistry.getRandomElement().getWand();
        var wand = randomWand != null ? randomWand : ItemsRegister.WAND_ITEM_FROST.get();
        return LootItem.lootTableItem(wand);
    }

    public static ObjectArrayList<ItemStack> getCoinItems(ServerLevel serverLevel, Vec3 pos, int level) {
        var loot = LootTable.lootTable().withPool(coinLoot(level));
        return createLootParams(serverLevel, pos, loot);
    }

    private static @NotNull ObjectArrayList<ItemStack> createLootParams(ServerLevel serverLevel, Vec3 pos, LootTable.Builder loot) {
        var param = LootContextParams.ORIGIN;
        var vault = LootContextParamSets.VAULT;
        var shouldEnchant = Random.nextInt(10) == 0 ? loot.apply(randomApplicableEnchantment(serverLevel.registryAccess())) : loot;
        var lootParams = new LootParams.Builder(serverLevel).withParameter(param, pos).create(vault);
        return shouldEnchant.build().getRandomItems(lootParams);
    }

    private static LootPool.@NotNull Builder coinLoot(float level) {
        var builder = LootPool.lootPool().setRolls(UniformGenerator.between(level/2, level));
        return builder
                .add(BRONZE_COIN.setWeight(40))
                .add(SILVER_COIN.setWeight(5))
                .add(GOLD_COIN.setWeight(1));
    }

    private static LootPool.@NotNull Builder legendaryPool(ServerLevel serverLevel) {
        var builder = LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F));
        return builder
            .add(AUGMENT_HYPER_CORE_BUILDER.setWeight(6))
            .add(WIZARD_HELM_BUILDER.setWeight(1))
            .add(WIZARD_CHEST_BUILDER.setWeight(1))
            .add(WIZARD_LEGGINGS_BUILDER.setWeight(1))
            .add(WIZARD_BOOTS_BUILDER.setWeight(1))
            .add(GOLD_COIN.setWeight(1));
    }

    private static LootPool.@NotNull Builder epicPool(ServerLevel serverLevel) {
        var builder = LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F));
        return builder
            .add(ADVANCED_AUGMENT_CORE_BUILDER.setWeight(6))
            .add(TOME_OF_UNITY_BUILDER.setWeight(4))
            .add(BATTLEMAGE_HELM_BUILDER.setWeight(1))
            .add(BATTLEMAGE_CHEASTPLATE_BUILDER.setWeight(1))
            .add(BATTLEMAGE_LEGGINGS_BUILDER.setWeight(1))
            .add(BATTLEMAGE_BOOTS_BUILDER.setWeight(1));
    }

    private static LootPool.@NotNull Builder rarePool(ServerLevel serverLevel) {
        var builder = LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 3.0F));
        return builder
            .add(NEXITE_BLOCK_BUILDER.setWeight(20))
            .add(AUGMENT_CORE_BUILDER.setWeight(15))
            .add(BOOK_BUILDER.setWeight(5));
    }

    private static LootPool.@NotNull Builder rareWeaponPool(ServerLevel serverLevel) {
        var builder = LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F));
        return builder
            .add(RUNE.setWeight(5))
            .add(AUGMENT_ITEM_BUILDER.setWeight(5))
            .add(SILVER_COIN.setWeight(1))
            .add(getRandomWand().setWeight(2))
            .add(MAGNET.setWeight(5))
            .add(IRON_SWORD_BUILDER.setWeight(20))
            .add(DIAMOND_SWORD_BUILDER.setWeight(10))
            .add(NETHERITE_SWORD_BUILDER.setWeight(2))
            .add(INGMAS_SWORD.setWeight(1));
    }

    private static LootPool.@NotNull Builder commonPool(ServerLevel serverLevel) {
        var builder = LootPool.lootPool().setRolls(UniformGenerator.between(2.0F, 5.0F));
        return builder
            .add(GOLDEN_CARROT_BUILDER.setWeight(50))
            .add(IRON_BUILDER.setWeight(35))
            .add(GOLD_BUILDER.setWeight(25))
            .add(EMERALD_BUILDER.setWeight(20))
            .add(DIAMOND_BUILDER.setWeight(10))
            .add(BRONZE_COIN.setWeight(10))
            .add(ENCHANTED_BOTTLES_BUILDER.setWeight(8))
            .add(SHULKER_SHELLS_BUILDER.setWeight(5))
            .add(NETHERITE_BUILDER.setWeight(2))
            .add(ELYTRA_BUILDER.setWeight(1));
    }


    public static void attachItemData(ServerLevel serverLevel, JahdooRarity rarity, ItemStack itemStack, boolean isSpecial, @Nullable JahdooRarity runeRarity) {
        var item = itemStack.getItem();
        switch (item){
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

    public static @NotNull ItemStack magnetItem(JahdooRarity getRarity, ItemStack itemStack) {
        var id = getRarity.getId() + 1;
        var origin = id * 1000;
        ModHelpers.setDurability(itemStack, Random.nextInt(origin, origin * 3));
        itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(id - 1));
        itemStack.set(JAHDOO_RARITY, getRarity.getId());
        attachLootBeam(itemStack, LocalLootBeamData.rarityLootBeam(getRarity));
        return itemStack;
    }

    private static void enchantedBook(ServerLevel serverLevel, ItemStack itemStack){
        serverLevel.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getRandom(RandomSource.create()).ifPresent(
            key -> {
                var value = key.getDelegate().value();
                var maxLevel = value.getMaxLevel();
                var minLevel = value.getMinLevel();
                enchant(itemStack, serverLevel.registryAccess(), key.getKey(), maxLevel > minLevel ? Random.nextInt(minLevel, maxLevel) : 1);
                attachLootBeam(itemStack, LocalLootBeamData.SPECIALLY_ENCHANTED_BOOK);
            }
        );
    }

    private static void attachLootBeam(ItemStack itemStack, LootBeamComponent data) {
        var lootBeamData = DataComponentsReg.INSTANCE.getLOOT_BEAM_DATA();
        if(!itemStack.has(lootBeamData)) itemStack.set(lootBeamData, data);
    }


    public static void attachEnchantment(ItemStack itemStack, ServerLevel serverLevel, ResourceKey<Enchantment> enchantmentKey, int minVal, int maxVal, boolean isSpecial) {
        if (Random.nextInt(isSpecial ? 5 : 20) != 0) return;
        enchant(itemStack, serverLevel.registryAccess(), enchantmentKey, Random.nextInt(minVal, maxVal));
        attachLootBeam(itemStack, LocalLootBeamData.ENCHANTED_VANILLA_SWORD);
    }

}
