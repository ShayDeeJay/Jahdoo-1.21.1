package org.jahdoo.common.datagen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jahdoo.JahdooMod;

import java.util.concurrent.CompletableFuture;

import static org.jahdoo.ascension.utils.ModTags.Entities.*;
import static org.jahdoo.common.registers.EntityReg.*;

public class EntityTagGenerator extends EntityTypeTagsProvider {

    public EntityTagGenerator(
        PackOutput output,
        CompletableFuture<HolderLookup.Provider> provider,
        ExistingFileHelper existingFileHelper
    ) {
        super(output, provider, JahdooMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(IGNORE_ENTITY)
            .add(GENERIC_PROJECTILE.get());

        this.tag(HORDE_MOBS)
            .add(CUSTOM_ZOMBIE.get())
            .add(EntityType.HUSK)
            .add(EntityType.VINDICATOR)
            .add(EntityType.ZOMBIFIED_PIGLIN);

    }

}
