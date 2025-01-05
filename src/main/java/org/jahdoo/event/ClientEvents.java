package org.jahdoo.event;

import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import org.jahdoo.JahdooMod;
import org.jahdoo.client.KeyBinding;
import org.jahdoo.client.RuneTooltipRenderer;
import org.jahdoo.event.event_helpers.WandAbilitySelector;
import org.jahdoo.registers.*;

import static org.jahdoo.event.event_helpers.EventHelpers.getEntityPlayerIsLookingAt;
import static org.jahdoo.event.event_helpers.KeyBindHelper.*;
import static org.jahdoo.event.event_helpers.OverlayEvent.crosshairManager;
import static org.jahdoo.event.event_helpers.OverlayEvent.simpleGui;
import static org.jahdoo.event.event_helpers.RenderEventHelper.*;
import static org.jahdoo.items.wand.WandItemHelper.getAllSlots;

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
        var effect = EffectsRegister.MYSTIC_EFFECT;
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
        } else {

            var player = Minecraft.getInstance().player;
            if(player != null && getEntityPlayerIsLookingAt(player, 30) == entity){

                var pose = event.getPoseStack();
                var buffer = event.getMultiBufferSource();
                var scale = Math.sin((entity.tickCount + event.getPartialTick()) / 5.0F) * 0.08F + 0.4;
                pose.pushPose();
                pose.translate(0, 0, 0);
//          pose.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
//          pose.rotateAround(Axis.XN.rotationDegrees(90), 0,0,0);
//          pose.rotateAround(Axis.YP.rotationDegrees(90), 0, 0, 0);
            pose.rotateAround(Axis.YP.rotationDegrees(entity.tickCount + event.getPartialTick()), 0, 0, 0);
//            pose.rotateAround(Axis.ZN.rotationDegrees((float) scale), 0,0,0);
//            pose.rotateAround(Axis.XP.rotationDegrees(entity.yBodyRotO + 90), 0,0,0);

//            drawSlash(pose.last(), buffer, FULL_BRIGHT, (float) scale + 0.4f, ModHelpers.res("textures/gui/gui_button_power_off.png"));
//            drawSlash(pose.last(), buffer, FULL_BRIGHT, entity.getBbWidth() + (float) scale - 0.4f, ModHelpers.res("textures/entity/magic_circle_2.png"));
//            drawSlash(pose.last(), buffer, FULL_BRIGHT, entity.getBbWidth() + (float) scale + 0.4f, ModHelpers.res("textures/entity/shield.png"));
                pose.popPose();
            }
        }
    }

    @SubscribeEvent
    public static void tooltipEvent(RenderTooltipEvent.GatherComponents e){
        var current = e.getTooltipElements();
        var allSlots = getAllSlots(e.getItemStack());
        if(allSlots.isEmpty()) return;
        var runeSockets = new RuneTooltipRenderer.RuneComponent(e.getItemStack(), allSlots);
        e.getTooltipElements().add(current.size(), Either.right(runeSockets));
    }

    @SubscribeEvent
    public static void PlayerRenderer(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) return;

        var player = (Player) event.getCamera().getEntity();
        var stack = player.getMainHandItem();

        renderUtilityOverlay(event, player, stack);
        renderTeleportLocationOverlay(event, player, stack);
        lockNearbyTarget(event);
        renderAbilityOverlay(event,stack, player);
    }

    @SubscribeEvent
    public static void overlayEvent(RenderGuiLayerEvent.Pre event) {
        var player = Minecraft.getInstance().player;
        crosshairManager(event);
        simpleGui(event, player);
    }
}


