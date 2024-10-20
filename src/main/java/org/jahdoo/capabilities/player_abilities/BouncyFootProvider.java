package org.jahdoo.capabilities.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

public class BouncyFootProvider implements IAttachmentSerializer<CompoundTag, BouncyFoot> {

    @Override
    public BouncyFoot read(IAttachmentHolder iAttachmentHolder, CompoundTag compoundTag, HolderLookup.Provider provider) {
        var playerMagicData = new BouncyFoot();
        playerMagicData.loadNBTData(compoundTag, provider);
        return playerMagicData;
    }

    @Override
    public @Nullable CompoundTag write(BouncyFoot bouncyFoot, HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        bouncyFoot.saveNBTData(tag, provider);
        return tag;
    }
}
