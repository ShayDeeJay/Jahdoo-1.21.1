package org.jahdoo.attachments.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

public class DimensionalRecallProvider implements IAttachmentSerializer<CompoundTag, DimensionalRecall> {

    @Override
    public DimensionalRecall read(IAttachmentHolder iAttachmentHolder, CompoundTag compoundTag, HolderLookup.Provider provider) {
        var playerMagicData = new DimensionalRecall();
        playerMagicData.loadNBTData(compoundTag,provider );
        return playerMagicData;
    }

    @Override
    public @Nullable CompoundTag write(DimensionalRecall dimensionalRecall, HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        dimensionalRecall.saveNBTData(tag, provider);
        return tag;
    }
}
