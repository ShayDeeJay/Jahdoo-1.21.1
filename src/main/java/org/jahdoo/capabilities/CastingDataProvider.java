package org.jahdoo.capabilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

public class CastingDataProvider implements IAttachmentSerializer<CompoundTag, CastingData> {

    @Override
    public CastingData read(IAttachmentHolder iAttachmentHolder, CompoundTag compoundTag, HolderLookup.Provider provider) {
        var playerMagicData = new CastingData();
        playerMagicData.loadNBTData(compoundTag);
        return playerMagicData;
    }

    @Override
    public @Nullable CompoundTag write(CastingData playerMagicSystem, HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        playerMagicSystem.saveNBTData(tag);
        return tag;
    }
}
