package org.jahdoo.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jahdoo.JahdooMod;
import org.jahdoo.trial_nexus.utils.Icons;
import top.theillusivec4.curios.api.CuriosDataProvider;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.concurrent.CompletableFuture;

public class JahdooCuriosProvider extends CuriosDataProvider {

    public JahdooCuriosProvider(
        PackOutput output,
        ExistingFileHelper fileHelper,
        CompletableFuture<HolderLookup.Provider> registries
    ) {
        super(JahdooMod.MOD_ID, output, fileHelper, registries);
    }

    @Override
    public void generate(
        HolderLookup.Provider registries,
        ExistingFileHelper fileHelper
    ) {
        this.createSlot("relic")
            .size(1)
            .dropRule(ICurio.DropRule.ALWAYS_DROP)
            .icon(Icons.CURIO_RELIC)
            .replace(true);

        this.createSlot("protector")
            .size(1)
            .dropRule(ICurio.DropRule.ALWAYS_DROP)
            .icon(Icons.CURIO_PROTECTOR)
            .replace(true);

        this.createSlot("magnet")
            .size(1)
            .dropRule(ICurio.DropRule.ALWAYS_DROP)
            .icon(Icons.CURIO_MAGNET)
            .replace(true);

        this.createSlot("shield")
            .size(1)
            .dropRule(ICurio.DropRule.ALWAYS_DROP)
            .icon(Icons.CURIO_SHIELD)
            .replace(true);

        this.createSlot("pocket_dimension")
            .size(1)
            .dropRule(ICurio.DropRule.ALWAYS_DROP)
            .icon(Icons.POCKET_DIMENSION)
            .replace(true);

        this.createEntities("player")
            .addPlayer()
            .addSlots("pocket_dimension")
            .addSlots("relic")
            .addSlots("magnet")
            .addSlots("shield")
            .addSlots("protector");
    }

}
