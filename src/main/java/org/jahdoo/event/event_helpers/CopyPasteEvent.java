package org.jahdoo.event.event_helpers;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.client.gui.ability_and_utility_menus.AbilityWheelMenu;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.networking.packet.client2server.ModularChaosCubeC2SPacket;

import java.util.List;

import static net.neoforged.neoforge.client.gui.VanillaGuiLayers.*;
import static net.neoforged.neoforge.client.gui.VanillaGuiLayers.CROSSHAIR;
import static org.jahdoo.capabilities.player_abilities.ModularChaosCubeProperties.getRelativePosition;
import static org.jahdoo.capabilities.player_abilities.ModularChaosCubeProperties.updateAll;
import static org.jahdoo.registers.AttachmentRegister.MODULAR_CHAOS_CUBE;

public class CopyPasteEvent {

    public static void copyPasteBlockProperties(Player player) {
        var pick = player.pick(5, 1, false);
        if(pick.getType() != HitResult.Type.MISS){
            if(pick instanceof BlockHitResult blockHitResult){
                var be = player.level().getBlockEntity(blockHitResult.getBlockPos());
                if(be instanceof ModularChaosCubeEntity modEntity){
                    if(player instanceof LocalPlayer){
                        long window = Minecraft.getInstance().getWindow().getWindow();
                        var keyDownCtrl = InputConstants.isKeyDown(window, InputConstants.KEY_LCONTROL);
                        var keyDownC = InputConstants.isKeyDown(window, InputConstants.KEY_C);
                        var keyDownV = InputConstants.isKeyDown(window, InputConstants.KEY_V);
                        if(keyDownC && keyDownCtrl) {
                            if(modEntity.hasData(MODULAR_CHAOS_CUBE)){
                                var data = modEntity.getData(MODULAR_CHAOS_CUBE);
                                player.setData(MODULAR_CHAOS_CUBE, data);
                                player.displayClientMessage(Component.literal("Copied!"), true);
                            } else {
                                player.displayClientMessage(Component.literal("No data to copy!"), true);
                            }
                        };
                        if(keyDownV && keyDownCtrl) {
                            if(player.hasData(MODULAR_CHAOS_CUBE)){
                                convertSavedData(modEntity, player);
                                player.displayClientMessage(Component.literal("Pasted!"), true);
                            } else {
                                player.displayClientMessage(Component.literal("Nothing to paste!"), true);
                            }
                        };
                    };
                }
            }
        }
    }

    private static void convertSavedData(ModularChaosCubeEntity modEntity, Player player) {
        var chaosCubeProperties = player.getData(MODULAR_CHAOS_CUBE);
        var action = chaosCubeProperties.getDirection(chaosCubeProperties.action());
        var input = chaosCubeProperties.getDirection(chaosCubeProperties.input());
        var output = chaosCubeProperties.getDirection(chaosCubeProperties.output());

        var actionNew = getRelativePosition(action, modEntity.getBlockPos());
        var inputNew = getRelativePosition(input, modEntity.getBlockPos());
        var outputNew = getRelativePosition(output, modEntity.getBlockPos());
        var update = updateAll(actionNew, inputNew, outputNew, chaosCubeProperties.active(), chaosCubeProperties.speed(), modEntity.getBlockPos(), chaosCubeProperties.chained());
        PacketDistributor.sendToServer(new ModularChaosCubeC2SPacket(modEntity.getBlockPos(), update));
        modEntity.setData(MODULAR_CHAOS_CUBE, update);
        modEntity.setChanged();
    }

}
