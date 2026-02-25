package org.jahdoo.trial_nexus.boon.StatEntry;

import net.minecraft.resources.ResourceLocation;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

public abstract class AbstractStatEntry {

    public String getLabel(){
        return TextHelpers.nameToId(id());
    };

    public abstract String id();

    public abstract int colour();

    public abstract ResourceLocation icon();

    public abstract StatCategory category();

}