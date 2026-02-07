package org.jahdoo.common.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.trial_nexus.tasks.RookieAssassin;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends AdvancementProvider {
    public ModAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new RookieAssassinAdvancement()));
    }

    public static class RookieAssassinAdvancement implements AdvancementGenerator{
        @Override
        public void generate(HolderLookup.Provider provider, Consumer<AdvancementHolder> consumer, ExistingFileHelper existingFileHelper) {
            var rookie = new RookieAssassin();
            Advancement.Builder.advancement()
                .display(new ItemStack(Items.ROTTEN_FLESH), JahdooHelpers.withStyleComponent(rookie.taskName(), ColourStore.RATING_4_YELLOW), JahdooHelpers.withStyleComponent(rookie.taskDescription(), ColourStore.GOLD_COIN), null, AdvancementType.CHALLENGE, true, true, false)
                .addCriterion("rookie_assassin", InventoryChangeTrigger.TriggerInstance.hasItems(ItemReg.BOON_CONTAINER.get()))
                .save(consumer, "rookie_assassin");
        }
    }

}
