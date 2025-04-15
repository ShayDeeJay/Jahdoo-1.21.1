package org.jahdoo.common.event.event_helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.jahdoo.ascension.attachments.CasterData;
import org.jahdoo.ascension.utils.Configuration;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.client.screens.AbilityWheelScreen;
import org.jahdoo.common.items.caster_item.CasterItem;
import org.jahdoo.common.registers.AttachmentReg;

import java.util.List;
import java.util.Objects;

import static net.neoforged.neoforge.client.gui.VanillaGuiLayers.*;
import static org.jahdoo.ascension.ability.AbilityBuilder.CASTING_DISTANCE;
import static org.jahdoo.ascension.ability.abilities_combat.arcane_shift.ArcaneShiftAbility.abilityId;

public class OverlayEvent {

    private static void hideCrosshairForAbilityWheel(RenderGuiLayerEvent.Pre event) {
        if (Minecraft.getInstance().screen instanceof AbilityWheelScreen && event.getName().equals(VanillaGuiLayers.CROSSHAIR)) {
            event.setCanceled(true);
        }
    }

    public static void crosshairManager(RenderGuiLayerEvent.Pre event) {
        hideCrosshairForAbilityWheel(event);
        hideCrosshairForArcaneShift(event);
    }

    public static void simpleGui(RenderGuiLayerEvent.Pre event, LocalPlayer player) {
        if(player == null) return;
        var exceptions = List.of(
            EXPERIENCE_LEVEL, EXPERIENCE_BAR, HOTBAR,
            PLAYER_HEALTH, FOOD_LEVEL, SELECTED_ITEM_NAME,
            ARMOR_LEVEL
        );

        if(Configuration.CUSTOM_UI.get()){
            if (exceptions.contains(event.getName())) {
                event.setCanceled(true);
            }
        }
    }

    private static void hideCrosshairForArcaneShift(RenderGuiLayerEvent.Pre event) {
        var player = Minecraft.getInstance().player;
        if(player == null) return;
        var stack = Helpers.getUsedItem(player);
        var getSelectedAbility = player.getData(AttachmentReg.CASTER_DATA.get());
        if(Objects.equals(getSelectedAbility.getSelectedAbility(), abilityId.getPath().intern())){
            var pickDistance = CasterData.getSpecificValue(player, CASTING_DISTANCE);
            var pick = player.pick(pickDistance, 1, false);
            if (stack.getItem() instanceof CasterItem) {
                if (pick.getType() != HitResult.Type.MISS) {
                    if (pick instanceof BlockHitResult && event.getName().equals(VanillaGuiLayers.CROSSHAIR)) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
