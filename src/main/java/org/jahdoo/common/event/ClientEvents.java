package org.jahdoo.common.event;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import org.jahdoo.JahdooMod;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.client.OverlayBlockTooltip;
import org.jahdoo.common.client.RuneTooltipRenderer;
import org.jahdoo.common.client.screens.StatScreen;

import static net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import static net.neoforged.neoforge.client.event.RenderLivingEvent.Pre;
import static org.jahdoo.common.client.KeyBinding.*;
import static org.jahdoo.common.event.event_helpers.EventHelpers.mysticEffectClient;
import static org.jahdoo.common.event.event_helpers.KeyBindHelper.quickSelectBehaviour;
import static org.jahdoo.common.event.event_helpers.KeyBindHelper.toggleLockAbility;
import static org.jahdoo.common.event.event_helpers.OverlayEvent.crosshairManager;
import static org.jahdoo.common.event.event_helpers.OverlayEvent.simpleGui;
import static org.jahdoo.common.event.event_helpers.RenderEventHelper.*;
import static org.jahdoo.common.event.event_helpers.WandAbilitySelector.selectWandSlot;
import static org.jahdoo.common.items.wand.WandItemHelper.getAllSlots;

@EventBusSubscriber(modid = JahdooMod.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void entityRenderer(Pre event) {
        mysticEffectClient(event);
    }

    @SubscribeEvent
    public static void overlayEventPre(RenderGuiLayerEvent.Pre event) {
        var instance = Minecraft.getInstance();
        var player = instance.player;

        crosshairManager(event);
        simpleGui(event, player);
    }

    @SubscribeEvent
    public static void overlayEventPost(RenderGuiLayerEvent.Post event) {
        OverlayBlockTooltip.overlayEvent(event);
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
    public static void playerRenderer(RenderLevelStageEvent event) {
        if (event.getStage() != Stage.AFTER_BLOCK_ENTITIES) return;

        var player = (Player) event.getCamera().getEntity();
        var stack = Helpers.getUsedItem(player);

        renderUtilityOverlay(event, player, stack);
        renderTeleportLocationOverlay(event, player, stack);
        renderAbilityOverlay(event,stack, player);
        lockNearbyTarget(event);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        var instance = Minecraft.getInstance();
        var player = instance.player;
        if(player == null) return;

        quickSelectBehaviour(player, instance);
        toggleLockAbility(player);

        if(WAND_SLOT_1A.consumeClick()) selectWandSlot(1);
        if(WAND_SLOT_2A.consumeClick()) selectWandSlot(2);
        if(WAND_SLOT_3A.consumeClick()) selectWandSlot(3);
        if(WAND_SLOT_4A.consumeClick()) selectWandSlot(4);
        if(WAND_SLOT_5A.consumeClick()) selectWandSlot(5);
        if(WAND_SLOT_6A.consumeClick()) selectWandSlot(6);
        if(WAND_SLOT_7A.consumeClick()) selectWandSlot(7);
        if(WAND_SLOT_8A.consumeClick()) selectWandSlot(8);
        if(WAND_SLOT_9A.consumeClick()) selectWandSlot(9);
        if(WAND_SLOT_10A.consumeClick()) selectWandSlot(10);
        if(STAT_SCREEN.consumeClick()) {
            instance.setScreen(new StatScreen());
            Helpers.syncAbilities();
        }
    }

}


