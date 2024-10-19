package org.jahdoo.capabilities;

import net.minecraft.nbt.CompoundTag;

public interface AbstractAttachment {

    void saveNBTData(CompoundTag nbt);

    void loadNBTData(CompoundTag nbt);

}
