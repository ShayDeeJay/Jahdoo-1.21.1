package org.jahdoo.event;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.jahdoo.JahdooMod;
import org.jahdoo.client.gui.ability_and_utility_menus.AbilityWheelMenu;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.client.KeyBinding;

import java.util.List;

import static net.neoforged.neoforge.client.gui.VanillaGuiLayers.*;

public class ModEvents {

    @EventBusSubscriber(modid = JahdooMod.MOD_ID, value = Dist.CLIENT)
    public static class ClientEvents {

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Player player = Minecraft.getInstance().player;

            if(player != null && player.getMainHandItem().getItem() instanceof WandItem){
//                var minecraft = Minecraft.getInstance();
                if (KeyBinding.QUICK_SELECT.isDown()) Minecraft.getInstance().setScreen(new AbilityWheelMenu());
            }

            if(KeyBinding.WAND_SLOT_1A.consumeClick()) WandAbilitySelector.selectWandSlot(1);
            if(KeyBinding.WAND_SLOT_2A.consumeClick()) WandAbilitySelector.selectWandSlot(2);
            if(KeyBinding.WAND_SLOT_3A.consumeClick()) WandAbilitySelector.selectWandSlot(3);
            if(KeyBinding.WAND_SLOT_4A.consumeClick()) WandAbilitySelector.selectWandSlot(4);
            if(KeyBinding.WAND_SLOT_5A.consumeClick()) WandAbilitySelector.selectWandSlot(5);
            if(KeyBinding.WAND_SLOT_6A.consumeClick()) WandAbilitySelector.selectWandSlot(6);
            if(KeyBinding.WAND_SLOT_7A.consumeClick()) WandAbilitySelector.selectWandSlot(7);
            if(KeyBinding.WAND_SLOT_8A.consumeClick()) WandAbilitySelector.selectWandSlot(8);
            if(KeyBinding.WAND_SLOT_9A.consumeClick()) WandAbilitySelector.selectWandSlot(9);
            if(KeyBinding.WAND_SLOT_10A.consumeClick()) WandAbilitySelector.selectWandSlot(10);

        }

        @SubscribeEvent
        public static void PlayerRenderer(RenderLevelStageEvent event) {
//            var player = (Player) event.getCamera().getEntity();
//
//            var target = getEntityInRange(player, 15, 25);
//
//            if (target == null || !player.hasLineOfSight(target)) return;
//            if (!(player.getMainHandItem().getItem() instanceof WandItem)) return;
//
//
//            target.addEffect(new MobEffectInstance(MobEffects.GLOWING.getDelegate(), 20, 1, false, false), target);
//
//            var targetPos = target.position().add(0, target.getBbHeight() - 0.2, 0);
//
//            double deltaX = targetPos.x - player.getX();
//            double deltaY = targetPos.y - (player.getY() + player.getEyeHeight());
//            double deltaZ = targetPos.z - player.getZ();
//
//            float desiredYaw = (float) (Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90);
//            float desiredPitch = (float) -Math.toDegrees(Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ)));
//
//            float currentYaw = player.getYRot() % 360;
//            if (currentYaw > 180) currentYaw -= 360;
//            if (currentYaw < -180) currentYaw += 360;
//
//            desiredYaw = desiredYaw % 360;
//            if (desiredYaw > 180) desiredYaw -= 360;
//            if (desiredYaw < -180) desiredYaw += 360;
//
//            float yawDifference = desiredYaw - currentYaw;
//            if (yawDifference > 180) yawDifference -= 360;
//            if (yawDifference < -180) yawDifference += 360;
//
//
//
//            float smoothFactor = 0.013f; // Adjust for smoother/faster transitions
//            player.setYRot(currentYaw + yawDifference * smoothFactor);
//            player.setXRot(player.getXRot() + (desiredPitch - player.getXRot()) * smoothFactor);
        }

        public static LivingEntity getEntityInRange(Player player, double maxDistance, float maxAngle) {
            var playerPosition = player.position();
            var playerDirection = player.getLookAngle(); // Direction the player is looking

            LivingEntity nearestEntity = player.level().getNearestEntity(
                Mob.class,
                TargetingConditions.DEFAULT,
                player,
                playerPosition.x, playerPosition.y, playerPosition.z,
                new AABB(BlockPos.containing(playerPosition)).inflate(maxDistance, 4, maxDistance)
            );

            if (nearestEntity == null) return null;
            var targetDirection = nearestEntity.position().subtract(playerPosition).normalize();

            double angleToTarget = Math.toDegrees(Math.acos(playerDirection.dot(targetDirection)));

            if (angleToTarget <= maxAngle) {
                return nearestEntity;
            } else {
                return null;
            }
        }

        @SubscribeEvent
        public static void overlayEvent(RenderGuiLayerEvent.Pre event) {
            var player = Minecraft.getInstance().player;
            if (Minecraft.getInstance().screen instanceof AbilityWheelMenu && event.getName().equals(VanillaGuiLayers.CROSSHAIR)) {
                event.setCanceled(true);
            }

            if(player.getMainHandItem().getItem() instanceof WandItem){
                List<ResourceLocation> exceptions = List.of(
                    EXPERIENCE_LEVEL,
                    EXPERIENCE_BAR,
                    HOTBAR,
                    PLAYER_HEALTH,
                    FOOD_LEVEL
//                    CROSSHAIR
                );

//                if(exceptions.contains(event.getName())){
//                    event.setCanceled(true);
//                }
            }
        }

    }

}


