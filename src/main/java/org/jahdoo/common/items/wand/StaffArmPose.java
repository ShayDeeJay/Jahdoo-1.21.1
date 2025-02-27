package org.jahdoo.common.items.wand;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.registers.ItemsRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.jahdoo.common.client.StaffArmPose.STAFF_ARM_POSE;
import static org.jahdoo.common.registers.ItemsRegister.*;

@EventBusSubscriber(modid = JahdooMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class StaffArmPose {

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Nullable
            @Override
            public HumanoidModel.ArmPose getArmPose(
                @NotNull LivingEntity entityLiving,
                @NotNull InteractionHand hand,
                @NotNull ItemStack itemStack
            ) {
                return STAFF_ARM_POSE.getValue();
            }
        }, ITEMS.getEntries()
            .stream()
            .filter(item -> item.get() instanceof WandItem)
            .map(holder -> (Item) holder.get())
            .toArray(Item[]::new));
    }

}
