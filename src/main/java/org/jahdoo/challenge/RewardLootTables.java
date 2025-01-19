package org.jahdoo.challenge;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.items.TomeOfUnity;
import org.jahdoo.items.augments.Augment;
import org.jahdoo.items.runes.RuneItem;
import org.jahdoo.items.runes.rune_data.RuneData;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shaydee.loot_beams_neoforge.data_component.DataComponentsReg;

import static org.jahdoo.challenge.EnchantmentHelpers.*;
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
    public static final LootPoolSingletonContainer.Builder<?> RUNE = LootItem.lootTableItem(ItemsRegister.RUNE.get());

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

    public static ItemStack getEliteItem(ServerLevel serverLevel, Vec3 pos) {
        var loot = LootTable.lootTable().withPool(poolForEliteShopping());
        var lootParams = ModHelpers.getRandomListElement(createLootParams(serverLevel, pos, loot));
        var rarity = JahdooRarity.ETERNAL;
        attachItemData(serverLevel, rarity, lootParams, true, rarity);
        return lootParams;
    }

    private static LootPool.@NotNull Builder poolForEliteShopping() {
        var builder = LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F));
        return builder
            .add(RUNE.setWeight(5))
            .add(AUGMENT_ITEM_BUILDER.setWeight(5))
            .add(TOME_OF_UNITY_BUILDER.setWeight(4))
            .add(getRandomWand().setWeight(2))
            .add(WIZARD_HELM_BUILDER.setWeight(1))
            .add(WIZARD_CHEST_BUILDER.setWeight(1))
            .add(WIZARD_LEGGINGS_BUILDER.setWeight(1))
            .add(WIZARD_BOOTS_BUILDER.setWeight(1))
            .add(BATTLEMAGE_HELM_BUILDER.setWeight(1))
            .add(BATTLEMAGE_CHEASTPLATE_BUILDER.setWeight(1))
            .add(BATTLEMAGE_LEGGINGS_BUILDER.setWeight(1))
            .add(BATTLEMAGE_BOOTS_BUILDER.setWeight(1));
    }

    private static @NotNull ObjectArrayList<ItemStack> createLootParams(ServerLevel serverLevel, Vec3 pos, LootTable.Builder loot) {
        var param = LootContextParams.ORIGIN;
        var vault = LootContextParamSets.VAULT;
        var shouldEnchant = Random.nextInt(10) == 0 ? loot.apply(randomApplicableEnchantment(serverLevel.registryAccess())) : loot;
        var lootParams = new LootParams.Builder(serverLevel).withParameter(param, pos).create(vault);
        return shouldEnchant.build().getRandomItems(lootParams);
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
            .add(IRON_SWORD_BUILDER.setWeight(20))
            .add(DIAMOND_SWORD_BUILDER.setWeight(10))
            .add(NETHERITE_SWORD_BUILDER.setWeight(2));
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


    public static void attachItemData(ServerLevel serverLevel, JahdooRarity rarity, ItemStack itemStack, boolean isGuaranteeEnchantment, @Nullable JahdooRarity runeRarity) {
        var item = itemStack.getItem();
        switch (item){
            case WandItem ignored -> JahdooRarity.setGeneratedWand(rarity, itemStack);
            case TomeOfUnity ignored -> JahdooRarity.createTomeAttributes(rarity, itemStack);
            case Augment ignored -> JahdooRarity.setGeneratedAugment(itemStack, rarity);
            case RuneItem ignored -> RuneData.RuneHelpers.generateRandomTypAttribute(itemStack, runeRarity);
            case ArmorItem armorItem -> enchantArmorItem(serverLevel, itemStack, armorItem, isGuaranteeEnchantment);
            case SwordItem ignored -> enchantSword(serverLevel, itemStack, isGuaranteeEnchantment);
            case EnchantedBookItem ignored -> enchantedBook(serverLevel, itemStack);
            default -> { /*IGNORE*/ }
        }
    }

    private static void enchantedBook(ServerLevel serverLevel, ItemStack itemStack){
        serverLevel.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getRandom(RandomSource.create()).ifPresent(
            key -> {
                var value = key.getDelegate().value();
                var maxLevel = value.getMaxLevel();
                var minLevel = value.getMinLevel();
                enchant(itemStack, serverLevel.registryAccess(), key.getKey(), maxLevel > minLevel ? Random.nextInt(minLevel, maxLevel) : 1);
                var lootBeamData = DataComponentsReg.INSTANCE.getLOOT_BEAM_DATA();
                if(!itemStack.has(lootBeamData)) itemStack.set(lootBeamData, LocalLootBeamData.SPECIALLY_ENCHANTED_BOOK);
            }
        );
    }

    private static void enchantSword(ServerLevel serverLevel, ItemStack itemStack, boolean isGuaranteed) {
        if(Random.nextInt(50) != 0) return;

        attachEnchantment(itemStack, serverLevel, Enchantments.SHARPNESS, 6, 11, isGuaranteed);
        attachEnchantment(itemStack, serverLevel, Enchantments.SWEEPING_EDGE, 4, 8, isGuaranteed);
        attachEnchantment(itemStack, serverLevel, Enchantments.LOOTING, 4, 8, isGuaranteed);
        attachEnchantment(itemStack, serverLevel, Enchantments.UNBREAKING, 4, 8, isGuaranteed);
    }

    private static void enchantArmorItem(ServerLevel serverLevel, ItemStack itemStack, ArmorItem armorItem, boolean isGuaranteed) {
        if(!isGuaranteed){
            if (Random.nextInt(50) != 0) return;
        }

        attachEnchantment(itemStack, serverLevel, Enchantments.BLAST_PROTECTION, 5, 10, isGuaranteed);
        attachEnchantment(itemStack, serverLevel, Enchantments.PROJECTILE_PROTECTION, 5, 10, isGuaranteed);
        attachEnchantment(itemStack, serverLevel, Enchantments.PROTECTION, 5, 10, isGuaranteed);
        attachEnchantment(itemStack, serverLevel, Enchantments.UNBREAKING, 4, 9, isGuaranteed);

        if(armorItem.getEquipmentSlot() == EquipmentSlot.FEET){
            attachEnchantment(itemStack, serverLevel, Enchantments.SOUL_SPEED, 4, 9, isGuaranteed);
            attachEnchantment(itemStack, serverLevel, Enchantments.DEPTH_STRIDER, 4, 9, isGuaranteed);
            attachEnchantment(itemStack, serverLevel, Enchantments.FEATHER_FALLING, 5, 10, isGuaranteed);
        }

        if(armorItem.getEquipmentSlot() == EquipmentSlot.LEGS){
            attachEnchantment(itemStack, serverLevel, Enchantments.SWIFT_SNEAK, 4, 9, isGuaranteed);
        }

        if(armorItem.getEquipmentSlot() == EquipmentSlot.HEAD){
            attachEnchantment(itemStack, serverLevel, Enchantments.RESPIRATION, 4, 9, isGuaranteed);
        }

    }

    private static void attachEnchantment(ItemStack itemStack, ServerLevel serverLevel, ResourceKey<Enchantment> enchantmentKey, int minVal, int maxVal, boolean isGuaranteed) {
        if(!isGuaranteed){
            if (Random.nextInt(20) != 0) return;
        }

        enchant(itemStack, serverLevel.registryAccess(), enchantmentKey, Random.nextInt(minVal, maxVal));
        var lootBeamData = DataComponentsReg.INSTANCE.getLOOT_BEAM_DATA();
        if(!itemStack.has(lootBeamData)) itemStack.set(lootBeamData, LocalLootBeamData.ENCHANTED_VANILLA_SWORD);
    }

}
