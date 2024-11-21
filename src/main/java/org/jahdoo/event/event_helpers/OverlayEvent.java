package org.jahdoo.event.event_helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.jahdoo.ability.all_abilities.abilities.ArcaneShiftAbility;
import org.jahdoo.client.gui.ability_and_utility_menus.AbilityWheelMenu;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.utils.ModHelpers;

import java.util.List;
import java.util.Objects;

import static net.neoforged.neoforge.client.gui.VanillaGuiLayers.*;
import static net.neoforged.neoforge.client.gui.VanillaGuiLayers.CROSSHAIR;
import static org.jahdoo.ability.AbilityBuilder.CASTING_DISTANCE;
import static org.jahdoo.registers.DataComponentRegistry.WAND_DATA;

public class OverlayEvent {
    public static void crosshairManager(RenderGuiLayerEvent.Pre event) {
        hideCrosshairForAbilityWheel(event);
        hideCrosshairForArcaneShift(event);
    }

    private static void hideCrosshairForAbilityWheel(RenderGuiLayerEvent.Pre event) {
        if (Minecraft.getInstance().screen instanceof AbilityWheelMenu && event.getName().equals(VanillaGuiLayers.CROSSHAIR)) {
            event.setCanceled(true);
        }
    }

    private static void hideCrosshairForArcaneShift(RenderGuiLayerEvent.Pre event) {
        var player = Minecraft.getInstance().player;
        if(player == null) return;
        var stack = player.getMainHandItem();
        var getSelectedAbility = stack.get(WAND_DATA);
        if(getSelectedAbility == null) return;
        if(Objects.equals(getSelectedAbility.selectedAbility(), ArcaneShiftAbility.abilityId.getPath().intern())){
            double pickDistance = ModHelpers.getTag(player, CASTING_DISTANCE, getSelectedAbility.selectedAbility());
            var pick = player.pick(pickDistance, 1, false);
            if (stack.getItem() instanceof WandItem) {
                if (pick.getType() != HitResult.Type.MISS) {
                    if (pick instanceof BlockHitResult && event.getName().equals(VanillaGuiLayers.CROSSHAIR)) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    public static void simpleGui(RenderGuiLayerEvent.Pre event, LocalPlayer player) {
        if(player == null) return;
        if(player.getMainHandItem().getItem() instanceof WandItem){
            java.util.List<ResourceLocation> exceptions = List.of(
                EXPERIENCE_LEVEL,
                EXPERIENCE_BAR,
                HOTBAR,
                PLAYER_HEALTH,
                FOOD_LEVEL,
                CROSSHAIR
            );

            if(exceptions.contains(event.getName())){
                event.setCanceled(true);
            }
        }
    }

}
