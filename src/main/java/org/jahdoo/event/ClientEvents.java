package org.jahdoo.event;

import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import org.jahdoo.JahdooMod;
import org.jahdoo.client.KeyBinding;
import org.jahdoo.event.event_helpers.WandAbilitySelector;
import org.jahdoo.registers.EffectsRegister;

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
        if(entity.hasEffect(effect)){
            var height = entity.getBbHeight() / 2;
            var tick = entity.tickCount;
            var anim = (tick + event.getPartialTick());
            var pos = event.getPoseStack();
            pos.rotateAround(Axis.XN.rotationDegrees(anim), 0, height, 0);
            pos.rotateAround(Axis.YN.rotationDegrees(anim), 0, height, 0);
            pos.rotateAround(Axis.ZN.rotationDegrees(anim), 0, height, 0);
            if(entity.getEffect(effect).getDuration() == 0) entity.removeEffect(effect);
        }
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


