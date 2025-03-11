package org.jahdoo.ascension.boon;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static net.minecraft.resources.ResourceLocation.withDefaultNamespace;
import static org.jahdoo.ascension.utils.Helpers.res;

public record LevelBoon(Component label, int colour, int executeIndex, ResourceLocation icon, double value){

    public static final LevelBoon EMPTY = new LevelBoon(Component.empty(), -1, -1, res(""), 0);

    public static void saveData(LevelBoon levelBoon, CompoundTag tag, HolderLookup.Provider registries){
        tag.putString("label", Component.Serializer.toJson(levelBoon.label, registries));
        tag.putInt("colour", levelBoon.colour);
        tag.putInt("index", levelBoon.executeIndex);
        System.out.println(levelBoon.icon.getPath());
        tag.putString("icon", levelBoon.icon.getPath());
        tag.putDouble("value", levelBoon.value);
    }

    public static LevelBoon loadData(CompoundTag tag, HolderLookup.Provider registries){
        var icon = tag.getString("icon");
        var getWithContext = icon.contains("mob_effect") ?  withDefaultNamespace(icon) : res(icon);
        return new LevelBoon(
            Component.Serializer.fromJson(tag.getString("label"), registries),
            tag.getInt("colour"),
            tag.getInt("index"),
            getWithContext,
            tag.getDouble("value")
        );
    }

}