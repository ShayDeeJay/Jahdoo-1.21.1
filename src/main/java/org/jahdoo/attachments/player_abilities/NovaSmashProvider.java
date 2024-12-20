package org.jahdoo.attachments.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

public class NovaSmashProvider implements IAttachmentSerializer<CompoundTag, NovaSmash> {

    @Override
    public NovaSmash read(IAttachmentHolder iAttachmentHolder, CompoundTag compoundTag, HolderLookup.Provider provider) {
        var playerMagicData = new NovaSmash();
        playerMagicData.loadNBTData(compoundTag, provider);
        return playerMagicData;
    }

    @Override
    public @Nullable CompoundTag write(NovaSmash NovaSmash, HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        NovaSmash.saveNBTData(tag, provider);
        return tag;
    }
}
