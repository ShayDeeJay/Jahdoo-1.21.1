package org.jahdoo.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import org.jahdoo.JahdooMod;
import org.jahdoo.client.KeyBinding;
import org.jahdoo.client.RenderHelpers;
import org.jahdoo.client.SharedUI;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.event.event_helpers.WandAbilitySelector;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2ic;

import java.util.List;
import java.util.Optional;

import static org.jahdoo.event.event_helpers.KeyBindHelper.*;
import static org.jahdoo.event.event_helpers.OverlayEvent.crosshairManager;
import static org.jahdoo.event.event_helpers.OverlayEvent.simpleGui;
import static org.jahdoo.event.event_helpers.RenderEventHelper.*;

@EventBusSubscriber(modid = JahdooMod.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft instance = Minecraft.getInstance();
        Player player = instance.player;
        QuickSelectBehaviour(player, instance);
        toggleLockAbility(player);

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
    public static void entityRenderer(RenderLivingEvent.Pre event) {
        var entity = event.getEntity();
        var effect = EffectsRegister.ARCANE_EFFECT;
        var putEffect = entity.getEffect(effect);
        if(putEffect != null){
            var height = entity.getBbHeight() / 2;
            var tick = entity.tickCount;
            var anim = (tick + event.getPartialTick());
            var pos = event.getPoseStack();
            pos.rotateAround(Axis.XN.rotationDegrees(anim), 0, height, 0);
            pos.rotateAround(Axis.YN.rotationDegrees(anim), 0, height, 0);
            pos.rotateAround(Axis.ZN.rotationDegrees(anim), 0, height, 0);
            if(putEffect.getDuration() == 0) entity.removeEffect(effect);
        }


        var player = Minecraft.getInstance().player;
        if(player != null && getEntityPlayerIsLookingAt(player, 30) == entity){

            var pose = event.getPoseStack();
            var buffer = event.getMultiBufferSource();
            var light = 255;

            pose.pushPose();
            pose.translate(0, 0, 0);
//            pose.rotateAround(Axis.YP.rotationDegrees(90), 0, 0, 0);
//            pose.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
            pose.rotateAround(Axis.YP.rotationDegrees(entity.tickCount + event.getPartialTick()), 0, 0, 0);
//        pose.rotateAround(Axis.ZN.rotationDegrees(90), 0,0,0);
//        pose.rotateAround(Axis.XN.rotationDegrees(90), 0,0,0);

//        pose.rotateAround(Axis.XP.rotationDegrees(entity.yBodyRotO + 90), 0,0,0);

            var width = Math.max(entity.getBbWidth() - 0.3f, 1);
            var scale = Math.sin((entity.tickCount + event.getPartialTick()) / 15.0F) * 0.1F + width;


            System.out.println(scale);
            drawSlash(pose.last(), buffer, light, (float) scale, ModHelpers.res("textures/entity/magic_circle_2.png"));
            pose.popPose();
        }
    }

    public static Entity getEntityPlayerIsLookingAt(Player player, double maxDistance) {
        var eyePosition = player.getEyePosition(1.0F);
        var lookVector = player.getViewVector(1.0F).scale(maxDistance);

        var endPoint = eyePosition.add(lookVector);

        var searchBox = player.getBoundingBox().expandTowards(lookVector).inflate(1.0D);
        var entities = player.level().getEntities(player, searchBox, entity -> entity.isPickable());

        Entity closestEntity = null;
        var closestDistance = maxDistance;

        for (Entity entity : entities) {
            var entityBox = entity.getBoundingBox().inflate(0.3D);
            var hit = entityBox.clip(eyePosition, endPoint);

            if (hit.isPresent()) {
                var distance = eyePosition.distanceTo(hit.get());

                if (distance < closestDistance) {
                    closestEntity = entity;
                    closestDistance = distance;
                }
            }
        }

        return closestEntity;
    }

    public static void drawSlash(PoseStack.Pose pose, MultiBufferSource bufferSource, int light, float width, ResourceLocation resourceLocation) {
        Matrix4f poseMatrix = pose.pose();

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(resourceLocation));
        float halfWidth = width;
        //old color: 125, 0, 10
        float y = 0.01f;
        var player = Minecraft.getInstance().player;
        if(player != null){
            var wand = player.getMainHandItem();
            if(wand.getItem() instanceof WandItem){
                var element = ElementRegistry.getElementByWandType(wand.getItem());
                if(!element.isEmpty()){
                    var elementColour = element.getFirst().particleColourPrimary();
                    consumer.addVertex(poseMatrix, -halfWidth, y, -halfWidth).setColor(elementColour).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 1f, 0f);
                    consumer.addVertex(poseMatrix, halfWidth, y, -halfWidth).setColor(elementColour).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 1f, 0f);
                    consumer.addVertex(poseMatrix, halfWidth, y, halfWidth).setColor(elementColour).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 1f, 0f);
                    consumer.addVertex(poseMatrix, -halfWidth, y, halfWidth).setColor(elementColour).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 1f, 0f);
                }
            }
        }

    }

    @SubscribeEvent
    public static void tooltipEvent(RenderTooltipEvent.Pre event){
//        var graphics = event.getGraphics();
//        var player = Minecraft.getInstance().player;
//        var i = 0;
//        var components = event.getComponents();
//        var j = components.size() == 1 ? -2 : 0;
//
//        for (ClientTooltipComponent clienttooltipcomponent : components) {
//            int k = clienttooltipcomponent.getWidth(event.getFont());
//            if (k > i) {
//                i = k;
//            }
//
//            j += clienttooltipcomponent.getHeight();
//        }

//        int i2 = i;
//        int j2 = j;
//        var position = event.getTooltipPositioner().positionTooltip(event.getScreenWidth(), event.getScreenHeight(), event.getX(), event.getY(), i2, j2);
//        System.out.println(event.);
//        SharedUI.renderItem(graphics, position.x(), position.y(), player, event.getItemStack(), Minecraft.getInstance().getTimer().getRealtimeDeltaTicks(), 20);
    }

    @SubscribeEvent
    public static void PlayerRenderer(RenderLevelStageEvent event) {
        var player = (Player) event.getCamera().getEntity();
        var stack = player.getMainHandItem();
        renderUtilityOverlay(event, player, stack);
        renderTeleportLocationOverlay(event, player, stack);
        lockNearbyTarget(event);
    }

    @SubscribeEvent
    public static void overlayEvent(RenderGuiLayerEvent.Pre event) {
        var player = Minecraft.getInstance().player;
        crosshairManager(event);
        simpleGui(event, player);
    }
}


