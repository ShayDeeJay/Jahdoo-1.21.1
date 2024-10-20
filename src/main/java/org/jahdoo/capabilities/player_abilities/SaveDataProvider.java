package org.jahdoo.capabilities.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

public class SaveDataProvider implements IAttachmentSerializer<CompoundTag, SaveData> {

    @Override
    public SaveData read(IAttachmentHolder iAttachmentHolder, CompoundTag compoundTag, HolderLookup.Provider provider) {
        var playerMagicData = new SaveData();
        playerMagicData.loadNBTData(compoundTag, provider);
        return playerMagicData;
    }

    @Override
    public @Nullable CompoundTag write(SaveData NovaSmash, HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        NovaSmash.saveNBTData(tag, provider);
        return tag;
    }
}
