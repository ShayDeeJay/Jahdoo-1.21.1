package org.jahdoo.common.event;

import net.minecraft.core.Direction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.entities.CustomSkeleton;
import org.jahdoo.common.entities.CustomZombie;
import org.jahdoo.common.entities.ancient_golem.AncientGolem;
import org.jahdoo.common.entities.decoy.Decoy;
import org.jahdoo.common.entities.void_spider.VoidSpider;
import org.jahdoo.common.registers.AttributesRegister;
import org.jahdoo.common.registers.BlockEntitiesRegister;
import org.jahdoo.common.registers.EntitiesRegister;

import static net.neoforged.neoforge.capabilities.Capabilities.*;
import static net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.*;
import static org.jahdoo.common.registers.BlockEntitiesRegister.*;

@EventBusSubscriber(modid = JahdooMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ServerBusEvents {

    @SubscribeEvent
    public static void attachAttribute(EntityAttributeModificationEvent event){
        AttributesRegister.attachAttribute(event);
    }

    @SubscribeEvent
    public static void attachAttribute(EntityAttributeCreationEvent event){
        event.put(EntitiesRegister.ETERNAL_WIZARD.get(), CustomSkeleton.createAttributes().build());
        event.put(EntitiesRegister.DECOY.get(), Decoy.createMobAttributes().build());
        event.put(EntitiesRegister.CUSTOM_ZOMBIE.get(), CustomZombie.createMobAttributes().build());
        event.put(EntitiesRegister.CUSTOM_SKELETON.get(), CustomSkeleton.createMobAttributes().build());
        event.put(EntitiesRegister.ANCIENT_GOLEM.get(), AncientGolem.createAttributes().build());
        event.put(EntitiesRegister.VOID_SPIDER.get(), VoidSpider.createMain().build());
        event.put(EntitiesRegister.VOID_SPIDER_SPAWN.get(), VoidSpider.createBaby().build());
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(BLOCK, TANK_BE.get(), (blockEntity, side) -> blockEntity.inputItemHandler);

        event.registerBlockEntity(BLOCK, MODULAR_CHAOS_CUBE_BE.get(), (blockEntity, side) -> blockEntity.inputItemHandler);

        event.registerBlockEntity(
              BLOCK, INFUSER_BE.get(), (blockEntity, side) -> {
                  if (side == Direction.UP) return blockEntity.inputItemHandler;
                  return blockEntity.outputItemHandler;
              }
        );
    }

}
