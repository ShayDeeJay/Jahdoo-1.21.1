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
        registerMagnets();
    }

    private void registerAugmentModels() {
        var augmentFiles = List.of("fire_augment", "ice_augment", "lightning_augment", "mystic_augment", "vitalis_augment", "utility_augment");
        augmentFiles.forEach(
            location -> {
                createModel(location, "item/augments/" + location);
                createAugmentOverride(location, augmentFiles.indexOf(location) + 1);
            }
        );
//        augmentFiles.forEach(this::createAugmentOverride);
    }

    private void registerRuneModels() {
        for (int i = 1; i < 12; i++) {
            createModel("rune" + i, "item/runes/rune" + i);
            createOverride(i, ItemsRegister.RUNE, "item/rune");
        }
    }

    private void registerKeyModels() {
        for (int i = 1; i < 4; i++) {
            createModel("key" + i, "item/keys/key" + i);
            createOverride(i, ItemsRegister.LOOT_KEY, "item/key");
        }
    }

    private void registerXpOrbModels() {
        for (int i = 1; i < 3; i++) {
            createModel("xp_orb" + i, "item/xp_orbs/xp_orb" + i);
            createOverride(i, ItemsRegister.EXPERIENCE_ORB, "item/xp_orb");
        }
    }

    private void registerMagnets() {
        for (int i = 1; i < 12; i++) {
            createModel("magnet" + i, "item/magnets/magnet_" + i);
            createOverride(i, ItemsRegister.MAGNET, "item/magnet");
        }
    }

    private void registerSimpleItems() {
        var simpleItems = List.of(
            ItemsRegister.NEXITE_POWDER, ItemsRegister.HEALTH_CONTAINER,
            ItemsRegister.AUGMENT_CORE, ItemsRegister.AUGMENT_FRAGMENT,
            ItemsRegister.ADVANCED_AUGMENT_CORE, ItemsRegister.AUGMENT_HYPER_CORE,
            ItemsRegister.BRONZE_COIN, ItemsRegister.SILVER_COIN,
            ItemsRegister.GOLD_COIN, ItemsRegister.PLATINUM_COIN
        );

        simpleItems.forEach(this::createSimpleItemModel);
    }

    private void registerArmorModels() {
        var armorItems = List.of(
            ItemsRegister.WIZARD_HELMET, ItemsRegister.WIZARD_CHESTPLATE,
            ItemsRegister.WIZARD_LEGGINGS, ItemsRegister.WIZARD_BOOTS,
            ItemsRegister.MAGE_HELMET, ItemsRegister.MAGE_CHESTPLATE,
            ItemsRegister.MAGE_LEGGINGS, ItemsRegister.MAGE_BOOTS
        );

        armorItems.forEach(this::createSimpleItemModel);
    }

    private void createAugmentOverride(String augment, int index) {
        var item = ItemsRegister.AUGMENT_ITEM;
        getWithParent(item, "item/augments/" + item.getId().getPath())
                .override()
                .predicate(MODEL_DATA, index)
                .model(modelFile("item/" + augment))
                .end();
    }

    private void createOverride(int runeId, DeferredHolder<Item, Item> item, String prefix) {
        getWithParent(item, prefix + "s/" + item.getId().getPath())
                .override()
                .predicate(MODEL_DATA, runeId)
                .model(modelFile(prefix + runeId))
                .end();
    }

    private void createModel(String augment, String model) {
        withExistingParent(augment, ResourceLocation.withDefaultNamespace("item/generated"))
                .texture("layer0", ModHelpers.res(model));
    }

    private void createSimpleItemModel(DeferredHolder<Item, Item> item) {
        getWithParent(item, "item/" + item.getId().getPath());
    }

    private ItemModelBuilder getWithParent(DeferredHolder<Item, Item> item, String path) {
        return withExistingParent(item.getId().getPath(), ResourceLocation.withDefaultNamespace("item/generated"))
                .texture("layer0", ModHelpers.res(path));
    }

    private ModelFile modelFile(String location) {
        return new ModelFile.ExistingModelFile(ModHelpers.res(location), this.existingFileHelper);
    }
}
