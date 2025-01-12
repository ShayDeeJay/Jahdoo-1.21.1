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

    private static final ResourceLocation modelData = ResourceLocation.withDefaultNamespace("custom_model_data");

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, JahdooMod.MOD_ID, existingFileHelper);
    }

    public ModelFile modelFile(String location) {
        return new ModelFile.ExistingModelFile(ModHelpers.res(location),this.existingFileHelper);
    }

    @Override
    protected void registerModels() {
        List<String> augmentFiles = List.of(
            "fire_augment",
            "ice_augment",
            "lightning_augment",
            "mystic_augment",
            "vitalis_augment",
            "utility_augment"
        );

        augmentFiles.forEach(this::simpleAugmentItemModel);
        for(int i = 1; i < 12; i++) simpleRunesItemModel(i);

        simpleItemOther(ItemsRegister.NEXITE_POWDER);
        simpleItemOther(ItemsRegister.HEALTH_CONTAINER);
        simpleItemOther(ItemsRegister.AUGMENT_CORE);
        simpleItemOther(ItemsRegister.AUGMENT_FRAGMENT);
        simpleItemOther(ItemsRegister.ADVANCED_AUGMENT_CORE);
        simpleItemOther(ItemsRegister.AUGMENT_HYPER_CORE);
        simpleItemOther(ItemsRegister.BRONZE_COIN);
        simpleItemOther(ItemsRegister.SILVER_COIN);
        simpleItemOther(ItemsRegister.GOLD_COIN);

//        simpleItemOther(ItemsRegister.POWER_GEM);

        simpleItemOther(ItemsRegister.WIZARD_HELMET);
        simpleItemOther(ItemsRegister.WIZARD_CHESTPLATE);
        simpleItemOther(ItemsRegister.WIZARD_LEGGINGS);
        simpleItemOther(ItemsRegister.WIZARD_BOOTS);

        simpleItemOther(ItemsRegister.MAGE_HELMET);
        simpleItemOther(ItemsRegister.MAGE_CHESTPLATE);
        simpleItemOther(ItemsRegister.MAGE_LEGGINGS);
        simpleItemOther(ItemsRegister.MAGE_BOOTS);

        augmentFiles.forEach( overrider ->
            simpleAugmentItem(ItemsRegister.AUGMENT_ITEM)
                .override()
                .predicate(modelData, augmentFiles.indexOf(overrider) + 1)
                .model(modelFile("item/"+overrider))
                .end()
        );


        for(int i = 1; i < 12; i++){
            upgradeRunes(ItemsRegister.RUNE)
                .override()
                .predicate(modelData, i)
                .model(modelFile("item/"+i))
                .end();
        }
    }

    private ItemModelBuilder getWithParent(DeferredHolder<Item, Item> item, String path){
        return withExistingParent(
            item.getId().getPath(),
            ResourceLocation.parse("item/generated")
        ).texture("layer0", ModHelpers.res(path));
    }

    private void simpleItemOther(DeferredHolder<Item, Item> item) {
        getWithParent(item, "item/" + item.getId().getPath());
    }

    private void simpleAugmentItemModel(String item) {
        withExistingParent(item,
            ResourceLocation.withDefaultNamespace("item/generated"))
            .texture("layer0", ModHelpers.res("item/augments/" + item));
    }

    private ItemModelBuilder simpleAugmentItem(DeferredHolder<Item, Item> item) {
       return withExistingParent(item.getId().getPath(),
           ResourceLocation.withDefaultNamespace("item/generated"))
           .texture("layer0", ModHelpers.res("item/augments/" + item.getId().getPath())
       );
    }

    private void simpleRunesItemModel(int item) {
        withExistingParent(String.valueOf(item),
            ResourceLocation.withDefaultNamespace("item/generated"))
            .texture("layer0", ModHelpers.res("item/runes/rune" + item));
    }

    private ItemModelBuilder upgradeRunes(DeferredHolder<Item, Item> item) {
        return withExistingParent(item.getId().getPath(),
            ResourceLocation.withDefaultNamespace("item/generated"))
            .texture("layer0", ModHelpers.res("item/runes/" + item.getId().getPath()));
    }


}
