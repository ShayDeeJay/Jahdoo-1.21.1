package org.jahdoo.trial_nexus.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import java.util.function.Supplier;

public class AttachmentProvider<T extends IAttachment> implements IAttachmentSerializer<CompoundTag, T> {

    private final Supplier<T> factory;

    public AttachmentProvider(Supplier<T> supplier){
        this.factory = supplier;
    }

    @Override
    public T read(IAttachmentHolder iAttachmentHolder, CompoundTag compoundTag, HolderLookup.Provider provider) {
        T attachment = factory.get();
        attachment.loadNBTData(compoundTag, provider);
        return attachment;
    }

    @Override
    public CompoundTag write(T attachment, HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        attachment.saveNBTData(tag, provider);
        return tag;
    }
}
