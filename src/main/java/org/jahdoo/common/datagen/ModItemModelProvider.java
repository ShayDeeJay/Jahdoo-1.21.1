package org.jahdoo.common.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.ascension.utils.Helpers;

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
        registerCoins();
    }

    private void createSimpleItemModel(DeferredHolder<Item, Item> item) {
        getWithParent(item, "item/" + item.getId().getPath());
    }

    private ModelFile modelFile(String location) {
        return new ModelFile.ExistingModelFile(Helpers.res(location), this.existingFileHelper);
    }

    private void createModel(String augment, String model) {
        withExistingParent(augment, ResourceLocation.withDefaultNamespace("item/generated"))
            .texture("layer0", Helpers.res(model));
    }

    private ItemModelBuilder getWithParent(DeferredHolder<Item, Item> item, String path) {
        return withExistingParent(item.getId().getPath(), ResourceLocation.withDefaultNamespace("item/generated"))
            .texture("layer0", Helpers.res(path));
    }

    private void registerRuneModels() {
        for (int i = 1; i < 11; i++) {
            createModel("rune" + i, "item/runes/rune" + i);
            createOverride(i, ItemReg.RUNE, "item/rune");
        }
    }

    private void registerKeyModels() {
        for (int i = 1; i < 4; i++) {
            createModel("key" + i, "item/keys/key" + i);
            createOverride(i, ItemReg.LOOT_KEY, "item/key");
        }
    }

    private void registerXpOrbModels() {
        for (int i = 1; i < 3; i++) {
            createModel("xp_orb" + i, "item/xp_orbs/xp_orb" + i);
            createOverride(i, ItemReg.EXPERIENCE_ORB, "item/xp_orb");
        }
    }

    private void registerMagnets() {
        for (int i = 1; i < 5; i++) {
            createModel("magnet" + i, "item/magnets/magnet_" + i);
            createOverride(i, ItemReg.MAGNET, "item/magnet");
        }
    }

    private void registerCoins() {
        for (int i = 1; i < 4; i++) {
            createModel("coin" + i, "item/coins/coin" + i);
            createOverride(i, ItemReg.COIN, "item/coin");
        }
    }

    private void createOverride(int runeId, DeferredHolder<Item, Item> item, String prefix) {
        getWithParent(item, prefix + "s/" + item.getId().getPath())
            .override()
            .predicate(MODEL_DATA, runeId)
            .model(modelFile(prefix + runeId))
            .end();
    }

    private void createAugmentOverride(String augment, int index) {
        var item = ItemReg.AUGMENT;
        getWithParent(item, "item/augments/" + item.getId().getPath())
            .override()
            .predicate(MODEL_DATA, index)
            .model(modelFile("item/" + augment))
            .end();
    }

    private void registerAugmentModels() {
        var augmentFiles = List.of("ice_augment", "fire_augment", "mystic_augment", "vitalis_augment", "utility_augment");
        augmentFiles.forEach(
            location -> {
                createModel(location, "item/augments/" + location);
                createAugmentOverride(location, augmentFiles.indexOf(location) + 1);
            }
        );
    }

    private void registerSimpleItems() {
        var simpleItems = List.of(
            ItemReg.NEXITE_POWDER, ItemReg.HEALTH_CONTAINER,
            ItemReg.AUGMENT_CORE, ItemReg.AUGMENT_FRAGMENT,
            ItemReg.ADVANCED_AUGMENT_CORE, ItemReg.AUGMENT_HYPER_CORE,
            ItemReg.MANA_CONTAINER, ItemReg.BOON_CONTAINER,
            ItemReg.RECALL_TOKEN, ItemReg.CHALLENGER_TICKET
        );

        simpleItems.forEach(this::createSimpleItemModel);
    }

    private void registerArmorModels() {
        var armorItems = List.of(
            ItemReg.WIZARD_HELMET, ItemReg.WIZARD_CHESTPLATE,
            ItemReg.WIZARD_LEGGINGS, ItemReg.WIZARD_BOOTS,
            ItemReg.MAGE_HELMET, ItemReg.MAGE_CHESTPLATE,
            ItemReg.MAGE_LEGGINGS, ItemReg.MAGE_BOOTS
        );

        armorItems.forEach(this::createSimpleItemModel);
    }
}
