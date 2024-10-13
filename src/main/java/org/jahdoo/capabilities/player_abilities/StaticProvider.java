package org.jahdoo.capabilities.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

public class StaticProvider implements IAttachmentSerializer<CompoundTag, Static> {

    @Override
    public Static read(IAttachmentHolder iAttachmentHolder, CompoundTag compoundTag, HolderLookup.Provider provider) {
        var playerMagicData = new Static();
        playerMagicData.loadNBTData(compoundTag);
        return playerMagicData;
    }

    @Override
    public @Nullable CompoundTag write(Static Static, HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        Static.saveNBTData(tag);
        return tag;
    }
}
