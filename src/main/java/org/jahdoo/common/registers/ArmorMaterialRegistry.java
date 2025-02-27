package org.jahdoo.common.registers;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.ascension.utils.Helpers;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

import static net.minecraft.sounds.SoundEvents.*;
import static net.minecraft.world.item.crafting.Ingredient.*;

public class ArmorMaterialRegistry {

    private static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, JahdooMod.MOD_ID);

    public static void register(IEventBus eventBus) {
        ARMOR_MATERIALS.register(eventBus);
    }

    public static DeferredHolder<ArmorMaterial, ArmorMaterial> WIZARD =
        register("wizard_armor", makeArmorMap(3, 8, 6, 3));

    public static DeferredHolder<ArmorMaterial, ArmorMaterial> MAGE =
        register("mage_armor", makeArmorMap(3, 8, 6, 3));

    private static DeferredHolder<ArmorMaterial, ArmorMaterial> register(
        String name,
        EnumMap<ArmorItem.Type, Integer> defense
    ) {
        var list = List.of(new ArmorMaterial.Layer(Helpers.res(name)));
        var material = new ArmorMaterial(defense, 15, ARMOR_EQUIP_DIAMOND, () -> of(Tags.Items.INGOTS_IRON), list, 3F, 0F);

        return ARMOR_MATERIALS.register(name, ()-> material);
    }

    static public EnumMap<ArmorItem.Type, Integer> makeArmorMap(int helmet, int chestplate, int leggings, int boots) {
        return Util.make(
            new EnumMap<>(ArmorItem.Type.class),
            (enumMap) -> {
                enumMap.put(ArmorItem.Type.BOOTS, boots);
                enumMap.put(ArmorItem.Type.LEGGINGS, leggings);
                enumMap.put(ArmorItem.Type.CHESTPLATE, chestplate);
                enumMap.put(ArmorItem.Type.HELMET, helmet);
            }
        );
    }

}
