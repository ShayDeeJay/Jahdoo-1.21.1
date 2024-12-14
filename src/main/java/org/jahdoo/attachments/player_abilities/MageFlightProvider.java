package org.jahdoo.attachments.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

public class MageFlightProvider implements IAttachmentSerializer<CompoundTag, MageFlight> {

    @Override
    public MageFlight read(IAttachmentHolder iAttachmentHolder, CompoundTag compoundTag, HolderLookup.Provider provider) {
        var playerMagicData = new MageFlight();
        playerMagicData.loadNBTData(compoundTag, provider);
        return playerMagicData;
    }

    @Override
    public @Nullable CompoundTag write(MageFlight mageFlight, HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        mageFlight.saveNBTData(tag, provider);
        return tag;
    }
}
