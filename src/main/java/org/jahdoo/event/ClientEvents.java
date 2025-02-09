package org.jahdoo.event;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import org.jahdoo.JahdooMod;
import org.jahdoo.block.shopping_table.ShoppingTableBlock;
import org.jahdoo.block.shopping_table.ShoppingTableEntity;
import org.jahdoo.client.KeyBinding;
import org.jahdoo.client.OverlayBlockTooltip;
import org.jahdoo.client.RuneTooltipRenderer;
import org.jahdoo.entities.living.VoidSpider;
import org.jahdoo.event.event_helpers.WandAbilitySelector;

import static org.jahdoo.event.event_helpers.EventHelpers.mysticEffectClient;
import static org.jahdoo.event.event_helpers.KeyBindHelper.quickSelectBehaviour;
import static org.jahdoo.event.event_helpers.KeyBindHelper.toggleLockAbility;
import static org.jahdoo.event.event_helpers.OverlayEvent.crosshairManager;
import static org.jahdoo.event.event_helpers.OverlayEvent.simpleGui;
import static org.jahdoo.event.event_helpers.RenderEventHelper.*;
import static org.jahdoo.items.wand.WandItemHelper.getAllSlots;

@EventBusSubscriber(modid = JahdooMod.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        var instance = Minecraft.getInstance();
        var player = instance.player;

        quickSelectBehaviour(player, instance);
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
        mysticEffectClient(event);
//         else {
//            var player = Minecraft.getInstance().player;
//            if(player != null /*&& getEntityPlayerIsLookingAt(player, 30) == entity*/){
//                var pose = event.getPoseStack();
//                var buffer = event.getMultiBufferSource();
//                var scale = Math.sin((entity.tickCount + event.getPartialTick()) / 5.0F) * 0.08F + 0.4;
//                pose.pushPose();
//                pose.translate(0, entity.getBbHeight() + 0.5, 0);
//                pose.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
//                pose.rotateAround(Axis.XN.rotationDegrees(90), 0,0,0);
////                pose.rotateAround(Axis.YP.rotationDegrees(90), 0, 0, 0);
////                pose.rotateAround(Axis.YP.rotationDegrees(entity.tickCount + event.getPartialTick()), 0, 0, 0);
////                pose.rotateAround(Axis.ZN.rotationDegrees((float) scale), 0,0,0);
////                pose.rotateAround(Axis.XP.rotationDegrees(entity.yBodyRotO + 10), 0,0,0);
////                pose.scale(0,0,1);
//                drawTexture(pose.last(), buffer, 255, 1, ModHelpers.res("textures/entity/shield.png"), FastColor.ARGB32.color(155, -1));
//                pose.popPose();
//            }
//        }
    }

    @SubscribeEvent
    public static void tooltipEvent(RenderTooltipEvent.GatherComponents e){
        var current = e.getTooltipElements();
        var itemStack = e.getItemStack();
        var allSlots = getAllSlots(itemStack);
        if(allSlots.isEmpty()) return;
        var runeSockets = new RuneTooltipRenderer.RuneComponent(itemStack, allSlots);

        current.add(current.size(), Either.right(runeSockets));
    }

    @SubscribeEvent
    public static void PlayerRenderer(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) return;
        var player = (Player) event.getCamera().getEntity();
        var stack = player.getItemInHand(player.getUsedItemHand());

        renderUtilityOverlay(event, player, stack);
        renderTeleportLocationOverlay(event, player, stack);
        lockNearbyTarget(event);
        renderAbilityOverlay(event,stack, player);
    }

    @SubscribeEvent
    public static void overlayEvent(RenderGuiLayerEvent.Pre event) {
        var instance = Minecraft.getInstance();
        var player = instance.player;
        crosshairManager(event);
        simpleGui(event, player);
        OverlayBlockTooltip.overlayEvent(event);
    }
}


