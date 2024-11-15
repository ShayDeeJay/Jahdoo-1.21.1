package org.jahdoo.capabilities.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

public class AutoBlockProvider implements IAttachmentSerializer<CompoundTag, AutoBlock> {

    @Override
    public AutoBlock read(IAttachmentHolder iAttachmentHolder, CompoundTag compoundTag, HolderLookup.Provider provider) {
        var playerMagicData = new AutoBlock();
        playerMagicData.loadNBTData(compoundTag, provider);
        return playerMagicData;
    }

    @Override
    public @Nullable CompoundTag write(AutoBlock autoBlock, HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        autoBlock.saveNBTData(tag, provider);
        return tag;
    }
}
