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
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.common.registers.ItemReg;

import java.util.List;

public class ModItemModelProvider extends ItemModelProvider {

    private static final ResourceLocation MODEL_DATA = ResourceLocation.withDefaultNamespace("custom_model_data");

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, JahdooMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        registerRuneModels();
        registerKeyModels();
        registerXpOrbModels();
        registerSimpleItems();
        registerArmorModels();
        registerMagnets();
        registerCoins();
        registerCarePackages();
        registerRecoveryReceipts();
        registerAugmentCoreFilled();
        registerAdvancedAugmentCoreFilled();
        registerAugmentHyperCoreFilled();
        registerTickets();
        registerPerkaSoda();
        registerStamps();
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
        for (int i = 1; i < 7; i++) {
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

    private void registerCarePackages() {
        for (int i = 1; i < 3; i++) {
            createModel("starter_pack" + i, "item/starter_packs/starter_pack" + i);
            createOverride(i, ItemReg.CARE_PACKAGE, "item/starter_pack");
        }
    }

    private void registerRecoveryReceipts() {
        for (int i = 1; i < 4; i++) {
            createModel("recovery_receipt" + i, "item/recovery_receipts/recovery_receipt" + i);
            createOverride(i, ItemReg.RECOVERY_RECEIPT, "item/recovery_receipt");
        }
    }

    private void registerTickets() {
        for (int i = 1; i < 6; i++) {
            createModel("trial_ticket" + i, "item/trial_tickets/trial_ticket" + i);
            createOverride(i, ItemReg.TRIAL_TICKET, "item/trial_ticket");
        }
    }

    private void registerPerkaSoda() {
        for (int i = 1; i < 5; i++) {
            createModel("perka_soda" + i, "item/perka_sodas/perka_soda" + i);
            createOverride(i, ItemReg.PERKA_SODA, "item/perka_soda");
        }
    }

    private void registerStamps() {
        for (int i = 1; i < 11; i++) {
            createModel("stamp" + i, "item/stamps/stamp" + i);
            createOverride(i, ItemReg.STAMP, "item/stamp");
        }
    }

    private void registerAugmentHyperCoreFilled() {
        createModel("augment_hyper_core_filled", "item/augment_hyper_core_filled");
        var item = ItemReg.AUGMENT_HYPER_CORE;
        var prefix = "item/augment_hyper_core";
        getWithParent(item, prefix)
            .override()
            .predicate(MODEL_DATA, 1)
            .model(modelFile(prefix+"_filled"))
            .end();
    }

    private void registerAdvancedAugmentCoreFilled() {
        createModel("advanced_augment_core_filled", "item/advanced_augment_core_filled");
        var item = ItemReg.ADVANCED_AUGMENT_CORE;
        var prefix = "item/advanced_augment_core";
        getWithParent(item, prefix)
            .override()
            .predicate(MODEL_DATA, 1)
            .model(modelFile(prefix+"_filled"))
            .end();
    }

    private void registerAugmentCoreFilled() {
        createModel("augment_core_filled", "item/augment_core_filled");
        var item = ItemReg.AUGMENT_CORE;
        var prefix = "item/augment_core";
        getWithParent(item, prefix)
            .override()
            .predicate(MODEL_DATA, 1)
            .model(modelFile(prefix+"_filled"))
            .end();
    }

    private void createOverride(int runeId, DeferredHolder<Item, Item> item, String prefix) {
        getWithParent(item, prefix + "s/" + item.getId().getPath())
            .override()
            .predicate(MODEL_DATA, runeId)
            .model(modelFile(prefix + runeId))
            .end();
    }

    private void registerSimpleItems() {
        var simpleItems = List.of(
            ItemReg.NEXITE_POWDER, ItemReg.ESSENCE_FRAGMENT, ItemReg.INFERNO_AUGMENT,
            ItemReg.FROST_AUGMENT, ItemReg.MYSTIC_AUGMENT, ItemReg.VITALITY_AUGMENT,
            ItemReg.SKILL_POINT, ItemReg.EXIT_KEY, ItemReg.COIN_SACK,
            ItemReg.STONE_OF_REGRET, ItemReg.ROSE_QUARTZ, ItemReg.ENCHANTED_DIAMOND
        );

        simpleItems.forEach(this::createSimpleItemModel);
    }

    private void registerArmorModels() {
        var armorItems = List.of(
            ItemReg.WIZARD_HELMET, ItemReg.WIZARD_CHESTPLATE,
            ItemReg.WIZARD_LEGGINGS, ItemReg.WIZARD_BOOTS,
            ItemReg.MAGE_HELMET, ItemReg.MAGE_CHESTPLATE,
            ItemReg.MAGE_LEGGINGS, ItemReg.MAGE_BOOTS,
            ItemReg.BATTLEMAGE_HELMET, ItemReg.BATTLEMAGE_CHESTPLATE,
            ItemReg.BATTLEMAGE_LEGGINGS, ItemReg.BATTLEMAGE_BOOTS,
            ItemReg.KNIGHT_KING_HELMET, ItemReg.KNIGHT_KING_CHESTPLATE,
            ItemReg.KNIGHT_KING_LEGGINGS, ItemReg.KNIGHT_KING_BOOTS,
            ItemReg.ANCIENT_GOLEM_HELMET, ItemReg.ANCIENT_GOLEM_CHESTPLATE,
            ItemReg.ANCIENT_GOLEM_LEGGINGS, ItemReg.ANCIENT_GOLEM_BOOTS
        );

        armorItems.forEach(this::createSimpleItemModel);
    }
}
