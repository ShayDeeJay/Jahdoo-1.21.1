package org.jahdoo.ascension.boon.player_boons;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.ascension.utils.Helpers;

import java.util.Collections;
import java.util.List;

public record Boon(List<Component> label, int colour, Runnable execute, ResourceLocation icon){
    public static final Boon EMPTY = new Boon(Collections.emptyList(), -1, () -> {}, Helpers.res(""));
}
