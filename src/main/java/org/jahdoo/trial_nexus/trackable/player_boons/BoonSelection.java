package org.jahdoo.trial_nexus.trackable.player_boons;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import static java.util.Arrays.stream;
import static net.minecraft.resources.ResourceLocation.withDefaultNamespace;

public class BoonSelection {

    public static ResourceLocation iconFromEffect(Holder<MobEffect> effect){
        var name = stream(effect.value().getDescriptionId().split("\\.")).toList().getLast();
        return withDefaultNamespace("textures/mob_effect/" + name + ".png");
    }

}
