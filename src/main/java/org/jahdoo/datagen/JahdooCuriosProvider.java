package org.jahdoo.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jahdoo.JahdooMod;
import top.theillusivec4.curios.api.CuriosDataProvider;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.concurrent.CompletableFuture;

public class JahdooCuriosProvider extends CuriosDataProvider {

    public JahdooCuriosProvider(PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<HolderLookup.Provider> registries) {
        super(JahdooMod.MOD_ID, output, fileHelper, registries);
    }

    @Override
    public void generate(HolderLookup.Provider registries, ExistingFileHelper fileHelper) {
        this.createSlot("tome")
            .size(1)
            .dropRule(ICurio.DropRule.ALWAYS_KEEP)
            .replace(true);

        this.createSlot("glove")
            .size(1)
            .dropRule(ICurio.DropRule.ALWAYS_KEEP)
            .replace(true);

        this.createEntities("player")
            .addPlayer()
            .addSlots("tome")
            .addSlots("glove");

    }
}
