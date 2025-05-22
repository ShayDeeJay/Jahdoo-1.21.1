package org.jahdoo.common.event;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.client.OverlayBlockTooltip;
import org.jahdoo.common.client.screens.AbilityUnlockScreen;
import org.jahdoo.common.client.screens.RunScreen;
import org.jahdoo.common.client.screens.StatScreen;
import org.jahdoo.common.client.tooltip_renderer.RuneTooltipRenderer;
import org.jahdoo.common.entities.ITamableEntity;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.trial_nexus.utils.Helpers;

import java.util.Optional;
import java.util.UUID;

import static net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import static net.neoforged.neoforge.client.event.RenderLivingEvent.Pre;
import static org.jahdoo.common.client.KeyBinding.*;
import static org.jahdoo.common.event.event_helpers.EventHelpers.mysticEffectClient;
import static org.jahdoo.common.event.event_helpers.EventHelpers.selectAbilitySlot;
import static org.jahdoo.common.event.event_helpers.KeyBindHelper.quickSelectBehaviour;
import static org.jahdoo.common.event.event_helpers.KeyBindHelper.toggleLockAbility;
import static org.jahdoo.common.event.event_helpers.OverlayEvent.crosshairManager;
import static org.jahdoo.common.event.event_helpers.OverlayEvent.simpleGui;
import static org.jahdoo.common.event.event_helpers.RenderEventHelper.*;
import static org.jahdoo.common.items.caster_item.CasterItemHelper.getAllSlots;

@EventBusSubscriber(modid = JahdooMod.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void entityRenderer(Pre event) {
        var poseStack = event.getPoseStack();
        var entity = event.getEntity();
        var instance = Minecraft.getInstance();
        if(!entity.isAlive()) return;
        renderHealthBar(event, entity, instance, poseStack);
        mysticEffectClient(event);
        renderChampionVisual(event);
    }

    public static ResourceLocation getHealthHolderIcon(Entity entity, Player player) {
        if (entity instanceof ITamableEntity tamable) {
            Optional<UUID> ownerUUID = tamable.getOwnerUUIDOptional();
            if (ownerUUID.isPresent() && ownerUUID.get().equals(player.getUUID())) {
                return Icons.HEALTH_HOLDER_ALLIED;
            }
        }
        return Icons.HEALTH_HOLDER;
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
        var item = itemStack.getItem();

        if(item instanceof JahdooItem){
            var iterator = current.iterator();
            while (iterator.hasNext()) {
                var tooltipElement = iterator.next();
                var left = tooltipElement.left();
                if (left.isPresent()) {
                    var formattedText = left.get();
                    var string = formattedText.toString();
                    var neoforge = string.contains("neoforge");
                    var whenOn = string.contains("item.modifiers");
                    var enchantment = string.contains("enchantment");
                    var mcComponent = string.contains("literal{ }[style={color=dark_green}");
                    var modNamePre = string.contains("creative_tab.jahdoo_tab");
                    var curio = string.contains("curios.modifiers");
                    var empty = formattedText.getString().isEmpty();
                    if (empty || neoforge || whenOn || enchantment || mcComponent || modNamePre || curio) {
                        iterator.remove();
                    }
                }
            }
        }
//
//        if(itemStack.has(ComponentReg.JAHDOO_RARITY)){
//            var runeSockets = new RarityTooltipRenderer.RarityTag(itemStack, current);
//            current.addFirst(Either.right(runeSockets));
//        }

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

        if(WAND_SLOT_1A.consumeClick()) selectAbilitySlot(1);
        if(WAND_SLOT_2A.consumeClick()) selectAbilitySlot(2);
        if(WAND_SLOT_3A.consumeClick()) selectAbilitySlot(3);
        if(WAND_SLOT_4A.consumeClick()) selectAbilitySlot(4);
        if(WAND_SLOT_5A.consumeClick()) selectAbilitySlot(5);
        if(WAND_SLOT_6A.consumeClick()) selectAbilitySlot(6);
        if(WAND_SLOT_7A.consumeClick()) selectAbilitySlot(7);
        if(WAND_SLOT_8A.consumeClick()) selectAbilitySlot(8);
        if(WAND_SLOT_9A.consumeClick()) selectAbilitySlot(9);
        if(WAND_SLOT_10A.consumeClick()) selectAbilitySlot(10);
        if(STAT_SCREEN.consumeClick()) {
            instance.setScreen(new StatScreen());
        }
        if(ABILITY_SCREEN.consumeClick()) {
            instance.setScreen(new AbilityUnlockScreen());
        }
        if(RUN_SCREEN.consumeClick()) {
            instance.setScreen(new RunScreen());
        }
    }

}


