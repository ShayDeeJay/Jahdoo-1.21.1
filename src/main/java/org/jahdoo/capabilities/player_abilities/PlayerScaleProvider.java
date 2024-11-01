package org.jahdoo.capabilities.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

public class PlayerScaleProvider implements IAttachmentSerializer<CompoundTag, PlayerScale> {

    @Override
    public PlayerScale read(IAttachmentHolder iAttachmentHolder, CompoundTag compoundTag, HolderLookup.Provider provider) {
        var playerScale = iAttachmentHolder instanceof ServerPlayer serverPlayer ? new PlayerScale(serverPlayer) : new PlayerScale();
        playerScale.loadNBTData(compoundTag, provider);
        return playerScale;
    }

    @Override
    public @Nullable CompoundTag write(PlayerScale playerScale, HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        playerScale.saveNBTData(tag, provider);
        return tag;
    }
}

