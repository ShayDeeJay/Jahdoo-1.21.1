package org.jahdoo.common.event.event_helpers;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.common.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.common.networking.packet.client2server.ModularChaosCubeC2SPacket;

import static com.mojang.blaze3d.platform.InputConstants.*;
import static net.minecraft.client.Minecraft.*;
import static org.jahdoo.ascension.attachments.player_abilities.ModularChaosCubeProperties.getRelativePosition;
import static org.jahdoo.ascension.attachments.player_abilities.ModularChaosCubeProperties.updateAll;
import static org.jahdoo.common.registers.AttachmentReg.MODULAR_CHAOS_CUBE;

public class CopyPasteEvent {

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

    public static void copyPasteBlockProperties(Player player) {
        var pick = player.pick(5, 1, false);

        if(pick.getType() == HitResult.Type.MISS) return;
        if(!(pick instanceof BlockHitResult blockHitResult)) return;

        var be = player.level().getBlockEntity(blockHitResult.getBlockPos());

        if(!(be instanceof ModularChaosCubeEntity modEntity)) return;
        if(!(player instanceof LocalPlayer)) return;

        var window = getInstance().getWindow().getWindow();
        var keyDownCtrl = isKeyDown(window, KEY_LCONTROL);
        var keyDownC = isKeyDown(window, KEY_C);
        var keyDownV = isKeyDown(window, KEY_V);

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
    }

}
