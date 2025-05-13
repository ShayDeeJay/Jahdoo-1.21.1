package org.jahdoo.common.registers;

import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.trial_nexus.utils.Helpers;

import java.util.EnumMap;
import java.util.List;

import static net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_DIAMOND;

public class ArmorMaterialReg {

    private static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, JahdooMod.MOD_ID);

    public static void register(IEventBus eventBus) {
        ARMOR_MATERIALS.register(eventBus);
    }

    public static DeferredHolder<ArmorMaterial, ArmorMaterial> MAGE =
        register("mage_armor", makeArmorMap(2, 5, 6, 2, 5), 9, 0F, 0F);

    public static DeferredHolder<ArmorMaterial, ArmorMaterial> WIZARD =
        register("wizard_armor", makeArmorMap(3, 6, 8, 3, 11), 10, 2F, 0F);

    public static DeferredHolder<ArmorMaterial, ArmorMaterial> BATTLEMAGE =
        register("battlemage_armor", makeArmorMap(3, 6, 8, 3, 15), 15, 3F, 0F);

    public static DeferredHolder<ArmorMaterial, ArmorMaterial> KNIGHT_KING =
        register("knight_king_armor", makeArmorMap(4, 8, 14, 4, 20), 20, 5F, 0F);

    public static DeferredHolder<ArmorMaterial, ArmorMaterial> ANCIENT_GOLEM_ARMOR =
        register("ancient_golem_armor", makeArmorMap(4, 8, 14, 4, 20), 20, 5F, 0F);

    private static DeferredHolder<ArmorMaterial, ArmorMaterial> register(
        String name,
        EnumMap<ArmorItem.Type, Integer> defense,
        int enchantmentValue,
        float toughness,
        float knockBack
    ) {
        var list = List.of(new ArmorMaterial.Layer(Helpers.res(name)));
        var material = new ArmorMaterial(defense, enchantmentValue, ARMOR_EQUIP_DIAMOND, Ingredient::of, list, toughness, knockBack);

        return ARMOR_MATERIALS.register(name, ()-> material);
    }

    static public EnumMap<ArmorItem.Type, Integer> makeArmorMap(int boots, int leggings, int chestplate, int helmet, int body) {
        return Util.make(
            new EnumMap<>(ArmorItem.Type.class),
            (enumMap) -> {
                enumMap.put(ArmorItem.Type.BOOTS, boots);
                enumMap.put(ArmorItem.Type.LEGGINGS, leggings);
                enumMap.put(ArmorItem.Type.CHESTPLATE, chestplate);
                enumMap.put(ArmorItem.Type.HELMET, helmet);
                enumMap.put(ArmorItem.Type.BODY, body);
            }
        );
    }

}
