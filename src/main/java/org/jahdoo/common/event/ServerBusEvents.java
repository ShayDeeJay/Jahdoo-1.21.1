package org.jahdoo.common.event;

import net.minecraft.core.Direction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.datagen.*;
import org.jahdoo.common.datagen.loot.BiomeProvider;
import org.jahdoo.common.datagen.loot.EntityTagGenerator;
import org.jahdoo.common.entities.custom_entities.CustomSkeleton;
import org.jahdoo.common.entities.custom_entities.CustomVillager;
import org.jahdoo.common.entities.custom_entities.CustomZombie;
import org.jahdoo.common.entities.ancient_golem.AncientGolem;
import org.jahdoo.common.entities.decoy.Decoy;
import org.jahdoo.common.entities.inferno_creeper.InfernoCreeper;
import org.jahdoo.common.entities.void_spider.VoidSpider;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.common.registers.EntityReg;

import static net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK;
import static org.jahdoo.common.registers.BlockEntityReg.*;

@EventBusSubscriber(modid = JahdooMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ServerBusEvents {

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
        generator.addProvider(event.includeClient(), new BiomeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new ModAdvancementProvider(packOutput, lookupProvider, event.getExistingFileHelper()));

    }

    @SubscribeEvent
    public static void attachAttribute(EntityAttributeModificationEvent event){
        AttributeReg.attachAttribute(event);
    }

    @SubscribeEvent
    public static void attachAttribute(EntityAttributeCreationEvent event){
        event.put(EntityReg.ETERNAL_WIZARD.get(), CustomSkeleton.createAttributes().build());
        event.put(EntityReg.DECOY.get(), Decoy.createMobAttributes().build());
        event.put(EntityReg.CUSTOM_ZOMBIE.get(), CustomZombie.createMobAttributes().build());
        event.put(EntityReg.CUSTOM_SKELETON.get(), CustomSkeleton.createMobAttributes().build());
        event.put(EntityReg.CUSTOM_VILLAGER.get(), CustomVillager.createMobAttributes().build());
        event.put(EntityReg.ANCIENT_GOLEM.get(), AncientGolem.createAttributes().build());
        event.put(EntityReg.INFERNO_CREEPER.get(), InfernoCreeper.createAttributes().build());
        event.put(EntityReg.VOID_SPIDER.get(), VoidSpider.createMain().build());
        event.put(EntityReg.VOID_SPIDER_SPAWN.get(), VoidSpider.createBaby().build());
        event.put(EntityReg.SAFE.get(), VoidSpider.createBaby().build());
        event.put(EntityReg.EXPLOSIVE_BARREL.get(), VoidSpider.createBaby().build());
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(BLOCK, TANK_BE.get(), (blockEntity, side) -> blockEntity.getInputItemHandler());

        event.registerBlockEntity(BLOCK, MODULAR_CHAOS_CUBE_BE.get(), (blockEntity, side) -> blockEntity.getInputItemHandler());

        event.registerBlockEntity(
            BLOCK, CREATOR_BE.get(),(blockEntity, side) -> {
                if (side == Direction.DOWN) return blockEntity.getOutputItemHandler();
                return blockEntity.getInputItemHandler();
            }
        );

        event.registerBlockEntity(
              BLOCK, INFUSER_BE.get(), (blockEntity, side) -> {
                  if (side == Direction.UP) return blockEntity.getInputItemHandler();
                  return blockEntity.getOutputItemHandler();
              }
        );
    }

}
