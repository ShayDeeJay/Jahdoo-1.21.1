package org.jahdoo.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public interface AbstractAttachment {

    void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider);

    void loadNBTData(CompoundTag nbt,HolderLookup.Provider provider);

}
