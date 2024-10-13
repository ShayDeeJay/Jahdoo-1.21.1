package org.jahdoo.registers;


import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.items.augments.Augment;
import org.jahdoo.items.heart_container.HealthContainer;
import org.jahdoo.items.infuser_block_item.InfuserBlockItem;
import org.jahdoo.items.wand.WandItem;

public class ItemsRegister {

    public static MobEffect effects;

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, JahdooMod.MOD_ID);

    public static final DeferredHolder<Item, Item> AUGMENT_ITEM =
        ITEMS.register("unidentified_augment", () -> new Augment(new Item.Properties()));

    public static final DeferredHolder<Item, Item> JIDE_POWDER =
        ITEMS.register("jide_powder", () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> AUGMENT_CORE =
        ITEMS.register("augment_core", () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> INFUSER_ITEM =
        ITEMS.register("infuser", () -> new InfuserBlockItem(BlocksRegister.INFUSER.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> WAND_ITEM_MYSTIC =
        ITEMS.register("wand_mystic", () -> new WandItem("wand_mystic"));

    public static final DeferredHolder<Item, Item> WAND_ITEM_FROST =
        ITEMS.register("wand_frost", () -> new WandItem("wand_frost"));

    public static final DeferredHolder<Item, Item> WAND_ITEM_INFERNO =
        ITEMS.register("wand_inferno", () -> new WandItem("wand_inferno"));

    public static final DeferredHolder<Item, Item> WAND_ITEM_LIGHTNING =
        ITEMS.register("wand_lightning", () -> new WandItem("wand_lightning"));

    public static final DeferredHolder<Item, Item> WAND_ITEM_VITALITY =
        ITEMS.register("wand_vitality", () -> new WandItem("wand_vitality"));

    public static final DeferredHolder<Item, Item> HEALTH_CONTAINER =
        ITEMS.register("health_container", () -> new HealthContainer(new Item.Properties()));

    public static final DeferredHolder<Item, Item> AUGMENT_FRAGMENT =
        ITEMS.register("augment_fragment", () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
