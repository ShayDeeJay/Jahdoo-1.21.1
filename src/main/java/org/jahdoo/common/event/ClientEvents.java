package org.jahdoo.common.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.items.caster_item.CastHelper;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.mod.EntityEffectReg;
import org.jahdoo.trial_nexus.attachments.effects.AbstractEntityEffect;
import org.jahdoo.trial_nexus.attachments.player_abilities.Blink;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import static java.lang.Math.min;
import static net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import static org.jahdoo.common.client.RenderHelpers.drawTexture;
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

        if(entity instanceof Player) {
            event.setCanceled(Blink.isActive(entity));
            event.getEntity().walkAnimation.setSpeed(entity.getData(AttachmentReg.MAGE_FLIGHT).isFlying ? 0.05f : event.getEntity().walkAnimation.speed());
        }

        if(!entity.isAlive()) return;
        renderHealthBar(event, entity, instance, poseStack);
        mysticEffectClient(event);
        renderChampionVisual(event);

        for (var hasEffect : EntityEffectReg.getHasEffects(entity)) {
            getMagicCircle(entity, event.getPartialTick(), poseStack, event.getMultiBufferSource(), hasEffect);
            return;
        }
    }

    private static void getMagicCircle(Entity entity, float partialTicks, PoseStack pose, MultiBufferSource bufferSource, AbstractEntityEffect effect) {
        pose.pushPose();
        pose.rotateAround(Axis.YN.rotationDegrees((entity.tickCount + partialTicks) * 6), 0, 0, 0);
        var colour = effect.getElement().partColourB();

        drawTexture(
            pose.last(),
            bufferSource,
            255,
            min(entity.getBbWidth(), Minecraft.getInstance().level.getGameTime() + partialTicks),
            effect.icon(), colour
        );

        drawTexture(
            pose.last(),
            bufferSource,
            255,
            min(entity.getBbWidth() + 1f, Minecraft.getInstance().level.getGameTime() + partialTicks),
            JahdooHelpers.res("textures/entity/shield.png"), colour
        );

        drawTexture(
            pose.last(),
            bufferSource,
            255,
            min(entity.getBbWidth() + 1, Minecraft.getInstance().level.getGameTime() + partialTicks),
            JahdooHelpers.res("textures/entity/" + "a" + ".png"), colour
        );

        pose.popPose();
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

        specialisedJahdooItemTooltip(item, current);
        renderOverEnchantedToolTip(itemStack, instance, current);
        renderRuneSockets(itemStack, current);
    }

    @SubscribeEvent
    public static void playerRenderer(RenderLevelStageEvent event) {
        if (event.getStage() != Stage.AFTER_BLOCK_ENTITIES) return;

        var player = (Player) event.getCamera().getEntity();
        var stack = CastHelper.hasValidCasterItem(player);

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


