package org.jahdoo.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import org.jahdoo.JahdooMod;
import org.jahdoo.block.shopping_table.ShoppingTableBlock;
import org.jahdoo.block.shopping_table.ShoppingTableEntity;
import org.jahdoo.client.KeyBinding;
import org.jahdoo.client.RuneTooltipRenderer;
import org.jahdoo.event.event_helpers.WandAbilitySelector;
import org.jahdoo.registers.*;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;
import org.joml.Matrix4f;

import static org.jahdoo.client.RenderHelpers.drawTexture;
import static org.jahdoo.event.event_helpers.EventHelpers.getEntityPlayerIsLookingAt;
import static org.jahdoo.event.event_helpers.EventHelpers.mysticEffectClient;
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
        var allSlots = getAllSlots(e.getItemStack());
        if(allSlots.isEmpty()) return;
        var runeSockets = new RuneTooltipRenderer.RuneComponent(e.getItemStack(), allSlots);

        e.getTooltipElements().add(current.size(), Either.right(runeSockets));

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


        if(player != null){
            var x = player.pick(3, event.getPartialTick().getGameTimeDeltaTicks(), false);
            var entity = player.level().getBlockEntity(BlockPos.containing(x.getLocation()));

            if(entity instanceof ShoppingTableEntity tableEntity){
                var guiGraphics = event.getGuiGraphics();
                var width = guiGraphics.guiWidth()/2;
                var height = guiGraphics.guiHeight()/2;
                var itemStack = tableEntity.getItem().getStackInSlot(0);
                var tooltipHeight = Screen.getTooltipFromItem(instance, itemStack);
                var getState = tableEntity.getBlockState().getValue(ShoppingTableBlock.TEXTURE);
                if(instance.screen == null){
                    if (tooltipHeight.size() > 1 && getState != 3) {
                        guiGraphics.renderTooltip(instance.font, itemStack, width + 60, height - (tooltipHeight.size() * 3));
                    }
                }
            }
        }
    }
}


