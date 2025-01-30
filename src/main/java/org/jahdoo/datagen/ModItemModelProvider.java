package org.jahdoo.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jahdoo.JahdooMod;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.ModHelpers;

import java.util.List;

public class ModItemModelProvider extends ItemModelProvider {

    private static final ResourceLocation MODEL_DATA = ResourceLocation.withDefaultNamespace("custom_model_data");

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, JahdooMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        registerAugmentModels();
        registerRuneModels();
        registerKeyModels();
        registerXpOrbModels();
        registerSimpleItems();
        registerArmorModels();
    }

    private void registerAugmentModels() {
        List<String> augmentFiles = List.of(
            "fire_augment",
            "ice_augment",
            "lightning_augment",
            "mystic_augment",
            "vitalis_augment",
            "utility_augment"
        );
        augmentFiles.forEach(this::createAugmentModel);
        augmentFiles.forEach(this::createAugmentOverride);
    }

    private void registerRuneModels() {
        for (int i = 1; i < 12; i++) {
            createRuneModel(i);
            createRuneOverride(i);
        }
    }

    private void registerKeyModels() {
        for (int i = 1; i < 4; i++) {
            createKeyModel(i);
            createKeyOverride(i);
        }
    }

    private void registerXpOrbModels() {
        for (int i = 1; i < 3; i++) {
            createXpOrbModel(i);
            createXpOrbOverride(i);
        }
    }

    private void registerSimpleItems() {
        List<DeferredHolder<Item, Item>> simpleItems = List.of(
                ItemsRegister.NEXITE_POWDER, ItemsRegister.HEALTH_CONTAINER,
                ItemsRegister.AUGMENT_CORE, ItemsRegister.AUGMENT_FRAGMENT,
                ItemsRegister.ADVANCED_AUGMENT_CORE, ItemsRegister.AUGMENT_HYPER_CORE,
                ItemsRegister.BRONZE_COIN, ItemsRegister.SILVER_COIN,
                ItemsRegister.GOLD_COIN, ItemsRegister.PLATINUM_COIN
        );

        simpleItems.forEach(this::createSimpleItemModel);
    }

    private void registerArmorModels() {
        List<DeferredHolder<Item, Item>> armorItems = List.of(
                ItemsRegister.WIZARD_HELMET, ItemsRegister.WIZARD_CHESTPLATE,
                ItemsRegister.WIZARD_LEGGINGS, ItemsRegister.WIZARD_BOOTS,
                ItemsRegister.MAGE_HELMET, ItemsRegister.MAGE_CHESTPLATE,
                ItemsRegister.MAGE_LEGGINGS, ItemsRegister.MAGE_BOOTS
        );

        armorItems.forEach(this::createSimpleItemModel);
    }

    private void createAugmentModel(String augment) {
        withExistingParent(augment, ResourceLocation.withDefaultNamespace("item/generated"))
                .texture("layer0", ModHelpers.res("item/augments/" + augment));
    }

    private void createAugmentOverride(String augment) {
        simpleAugmentItem(ItemsRegister.AUGMENT_ITEM)
                .override()
                .predicate(MODEL_DATA, List.of("fire_augment", "ice_augment", "lightning_augment",
                        "mystic_augment", "vitalis_augment", "utility_augment").indexOf(augment) + 1)
                .model(modelFile("item/" + augment))
                .end();
    }

    private void createRuneModel(int runeId) {
        withExistingParent("rune" + runeId, ResourceLocation.withDefaultNamespace("item/generated"))
                .texture("layer0", ModHelpers.res("item/runes/rune" + runeId));
    }

    private void createRuneOverride(int runeId) {
        upgradeRunes(ItemsRegister.RUNE)
                .override()
                .predicate(MODEL_DATA, runeId)
                .model(modelFile("item/rune" + runeId))
                .end();
    }

    private void createKeyModel(int keyId) {
        withExistingParent("key" + keyId, ResourceLocation.withDefaultNamespace("item/generated"))
                .texture("layer0", ModHelpers.res("item/keys/key" + keyId));
    }

    private void createKeyOverride(int keyId) {
        keys(ItemsRegister.LOOT_KEY)
                .override()
                .predicate(MODEL_DATA, keyId)
                .model(modelFile("item/key" + keyId))
                .end();
    }

    private void createXpOrbModel(int orbId) {
        withExistingParent("xp_orb" + orbId, ResourceLocation.withDefaultNamespace("item/generated"))
                .texture("layer0", ModHelpers.res("item/xp_orbs/xp_orb" + orbId));
    }

    private void createXpOrbOverride(int orbId) {
        xp(ItemsRegister.EXPERIENCE_ORB)
                .override()
                .predicate(MODEL_DATA, orbId)
                .model(modelFile("item/xp_orb" + orbId))
                .end();
    }

    private void createSimpleItemModel(DeferredHolder<Item, Item> item) {
        getWithParent(item, "item/" + item.getId().getPath());
    }

    private ItemModelBuilder getWithParent(DeferredHolder<Item, Item> item, String path) {
        return withExistingParent(item.getId().getPath(), ResourceLocation.parse("item/generated"))
                .texture("layer0", ModHelpers.res(path));
    }

    private ItemModelBuilder simpleAugmentItem(DeferredHolder<Item, Item> item) {
        return withExistingParent(item.getId().getPath(), ResourceLocation.withDefaultNamespace("item/generated"))
                .texture("layer0", ModHelpers.res("item/augments/" + item.getId().getPath()));
    }

    private ItemModelBuilder upgradeRunes(DeferredHolder<Item, Item> item) {
        return withExistingParent(item.getId().getPath(), ResourceLocation.withDefaultNamespace("item/generated"))
                .texture("layer0", ModHelpers.res("item/runes/" + item.getId().getPath()));
    }

    private ItemModelBuilder keys(DeferredHolder<Item, Item> item) {
        return withExistingParent(item.getId().getPath(), ResourceLocation.withDefaultNamespace("item/generated"))
                .texture("layer0", ModHelpers.res("item/keys/" + item.getId().getPath()));
    }

    private ItemModelBuilder xp(DeferredHolder<Item, Item> item) {
        return withExistingParent(item.getId().getPath(), ResourceLocation.withDefaultNamespace("item/generated"))
                .texture("layer0", ModHelpers.res("item/xp_orbs/" + item.getId().getPath()));
    }

    private ModelFile modelFile(String location) {
        return new ModelFile.ExistingModelFile(ModHelpers.res(location), this.existingFileHelper);
    }
}
