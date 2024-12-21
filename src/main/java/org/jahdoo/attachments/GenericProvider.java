package org.jahdoo.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class GenericProvider<T extends AbstractAttachment> implements IAttachmentSerializer<CompoundTag, T> {

    private final Supplier<T> factory;

    public GenericProvider(Supplier<T> supplier){
        this.factory = supplier;
    }

    @Override
    public T read(IAttachmentHolder iAttachmentHolder, CompoundTag compoundTag, HolderLookup.Provider provider) {
        T attachment = factory.get();
        attachment.loadNBTData(compoundTag, provider);
        return attachment;
    }

    @Override
    public @Nullable CompoundTag write(T attachment, HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        attachment.saveNBTData(tag, provider);
        return tag;
    }
}
