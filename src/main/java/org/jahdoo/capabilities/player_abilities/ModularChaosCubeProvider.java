package org.jahdoo.capabilities.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

public class ModularChaosCubeProvider implements IAttachmentSerializer<CompoundTag, ModularChaosCubeProperties> {

    @Override
    public ModularChaosCubeProperties read(IAttachmentHolder iAttachmentHolder, CompoundTag compoundTag, HolderLookup.Provider provider) {
        var playerMagicData = new ModularChaosCubeProperties();
        playerMagicData.loadNBTData(compoundTag, provider);
        return playerMagicData;
    }

    @Override
    public @Nullable CompoundTag write(ModularChaosCubeProperties autoBlock, HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        autoBlock.saveNBTData(tag, provider);
        return tag;
    }
}
