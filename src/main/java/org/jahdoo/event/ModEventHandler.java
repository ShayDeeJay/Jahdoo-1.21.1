package org.jahdoo.event;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerLifecycleEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.jahdoo.JahdooMod;
import org.jahdoo.entities.AncientGolem;
import org.jahdoo.entities.CustomZombie;
import org.jahdoo.entities.Decoy;
import org.jahdoo.entities.EternalWizard;
import org.jahdoo.registers.AttributesRegister;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.EntitiesRegister;

@EventBusSubscriber(modid = JahdooMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventHandler {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK, BlockEntitiesRegister.TANK_BE.get(), (blockEntity, side) -> blockEntity.inputItemHandler
        );

        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK, BlockEntitiesRegister.MODULAR_CHAOS_CUBE_BE.get(), (blockEntity, side) -> blockEntity.inputItemHandler
        );

        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK, BlockEntitiesRegister.INFUSER_BE.get(), (blockEntity, side) -> {
                if (side == Direction.UP) return blockEntity.inputItemHandler;
                return blockEntity.outputItemHandler;
            }
        );
    }

    @SubscribeEvent
    public static void attachAttribute(EntityAttributeModificationEvent event){
        AttributesRegister.attachAttribute(event);
    }


    @SubscribeEvent
    public static void attachAttribute(EntityAttributeCreationEvent event){
        event.put(EntitiesRegister.ETERNAL_WIZARD.get(), EternalWizard.createAttributes().build());
        event.put(EntitiesRegister.DECOY.get(), Decoy.createMobAttributes().build());
        event.put(EntitiesRegister.CUSTOM_ZOMBIE.get(), CustomZombie.createMobAttributes().build());
        event.put(EntitiesRegister.ANCIENT_GOLEM.get(), AncientGolem.createAttributes().build());
    }
}
