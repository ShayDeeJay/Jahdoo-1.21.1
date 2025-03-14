package org.jahdoo.ascension.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public interface IAttachment {

    void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider);

    void loadNBTData(CompoundTag nbt,HolderLookup.Provider provider);

}
