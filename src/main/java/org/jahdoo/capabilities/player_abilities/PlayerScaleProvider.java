package org.jahdoo.capabilities.player_abilities;

import com.google.common.collect.HashMultimap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jahdoo.capabilities.AbstractAttachment;
import org.jahdoo.utils.GeneralHelpers;
import org.jetbrains.annotations.Nullable;

import static org.jahdoo.registers.AttachmentRegister.BOUNCY_FOOT;

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

