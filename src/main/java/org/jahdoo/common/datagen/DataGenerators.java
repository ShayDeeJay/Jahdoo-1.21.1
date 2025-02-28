package org.jahdoo.common.datagen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.datagen.loot.EntityTagGenerator;

@EventBusSubscriber(modid = JahdooMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {

        var generator = event.getGenerator();
        var packOutput = generator.getPackOutput();
        var existingFileHelper = event.getExistingFileHelper();
        var lookupProvider = event.getLookupProvider();
        var blockTagGenerator = generator.addProvider(event.includeServer(),new ModBlockTagGenerator(packOutput, lookupProvider, existingFileHelper));

        generator.addProvider(event.includeServer(), ModLootTableProvider.create(packOutput, lookupProvider));
        generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModItemTagGenerator(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeClient(), new EntityTagGenerator(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModWorldGenProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new ModGlobalLootModifiersProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeClient(), new RecipeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new JahdooCuriosProvider(packOutput, event.getExistingFileHelper(), lookupProvider));
        generator.addProvider(event.includeClient(), new DamageTypesProvider(packOutput, lookupProvider));

    }

}
