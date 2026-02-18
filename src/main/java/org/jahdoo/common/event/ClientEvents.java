package org.jahdoo.common.event;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import org.jahdoo.JahdooMod;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import static net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import static org.jahdoo.common.event.event_helpers.EventHelpers.*;
import static org.jahdoo.common.event.event_helpers.KeyBindHelper.quickSelectBehaviour;
import static org.jahdoo.common.event.event_helpers.KeyBindHelper.toggleLockAbility;
import static org.jahdoo.common.event.event_helpers.OverlayEvent.crosshairManager;
import static org.jahdoo.common.event.event_helpers.OverlayEvent.simpleGui;
import static org.jahdoo.common.event.event_helpers.RenderEventHelper.*;

@EventBusSubscriber(modid = JahdooMod.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void entityRenderer(RenderLivingEvent.Pre event) {
        var poseStack = event.getPoseStack();
        var entity = event.getEntity();
        var instance = Minecraft.getInstance();

        if(!entity.isAlive()) return;
        renderHealthBar(event, entity, instance, poseStack);
        mysticEffectClient(event);
        renderChampionVisual(event);
    }

    @SubscribeEvent
    public static void overlayEventPre(RenderGuiLayerEvent.Pre event) {
        var instance = Minecraft.getInstance();
        var player = instance.player;

        crosshairManager(event);
        simpleGui(event, player);
    }

    @SubscribeEvent
    public static void tooltipEvent(RenderTooltipEvent.GatherComponents e){
        var current = e.getTooltipElements();
        var itemStack = e.getItemStack();
        var item = itemStack.getItem();
        var instance = Minecraft.getInstance();

        renderMoreInfoTooltip(itemStack, instance, current);
        specialisedJahdooItemTooltip(item, current);
        renderOverEnchantedToolTip(itemStack, instance, current);
        renderRuneSockets(itemStack, current);
    }


    @SubscribeEvent
    public static void playerRenderer(RenderLevelStageEvent event) {
        if (event.getStage() != Stage.AFTER_BLOCK_ENTITIES) return;

        var player = (Player) event.getCamera().getEntity();
        var stack = JahdooHelpers.getUsedItem(player);

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
        mapCustomKeys(instance, player);
    }

}


