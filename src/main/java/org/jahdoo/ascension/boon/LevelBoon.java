package org.jahdoo.ascension.boon;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jahdoo.ascension.utils.Helpers;

import java.util.Collections;
import java.util.function.Consumer;

public record LevelBoon(Component label, int colour, int executeIndex, ResourceLocation icon){
    public static final LevelBoon EMPTY = new LevelBoon(Component.empty(), -1, -1, Helpers.res(""));

    public static void saveData(LevelBoon levelBoon, CompoundTag tag, HolderLookup.Provider registries){
        tag.putString("label", Component.Serializer.toJson(levelBoon.label, registries));
        tag.putInt("colour", levelBoon.colour);
        tag.putInt("index", levelBoon.executeIndex);
        System.out.println(levelBoon.icon.getPath());
        tag.putString("icon", levelBoon.icon.getPath());
    }

    public static LevelBoon loadData(CompoundTag tag, HolderLookup.Provider registries){
        return new LevelBoon(
            Component.Serializer.fromJson(tag.getString("label"), registries),
            tag.getInt("colour"),
            tag.getInt("index"),
            ResourceLocation.withDefaultNamespace(tag.getString("icon"))
        );
    }

}