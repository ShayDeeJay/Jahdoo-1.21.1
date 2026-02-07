package org.jahdoo.trial_nexus.boon.player_boons;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import java.util.Collections;
import java.util.List;

public record Boon(List<Component> label, int colour, Runnable execute, ResourceLocation icon){
    public static final Boon EMPTY = new Boon(Collections.emptyList(), -1, () -> {}, JahdooHelpers.res(""));
}
