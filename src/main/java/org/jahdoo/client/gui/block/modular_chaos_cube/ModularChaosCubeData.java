package org.jahdoo.client.gui.block.modular_chaos_cube;

import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.attachments.player_abilities.ModularChaosCubeProperties;
import org.jahdoo.networking.packet.client2server.ModularChaosCubeC2SPacket;
import static org.jahdoo.registers.AttachmentRegister.MODULAR_CHAOS_CUBE;
public class ModularChaosCubeData {

    // Toggle the power state
    public static void togglePower(ModularChaosCubeEntity entity) {
        var autoBlock = entity.getData(MODULAR_CHAOS_CUBE);
        var switched = autoBlock.updateActive(!autoBlock.active());
        PacketDistributor.sendToServer(new ModularChaosCubeC2SPacket(entity.getBlockPos(), switched));
        entity.setData(MODULAR_CHAOS_CUBE, switched);
        entity.setChanged();
    }

    public static void toggleChained(ModularChaosCubeEntity entity) {
        var autoBlock = entity.getData(MODULAR_CHAOS_CUBE);
        var switched = autoBlock.updateChained(!autoBlock.chained());
        PacketDistributor.sendToServer(new ModularChaosCubeC2SPacket(entity.getBlockPos(), switched));
        entity.setData(MODULAR_CHAOS_CUBE, switched);
        entity.setChanged();
    }

    public static void selectDirection(ModularChaosCubeEntity entity, ModularChaosCubeProperties newDirection) {
        PacketDistributor.sendToServer(new ModularChaosCubeC2SPacket(entity.getBlockPos(), newDirection));
        entity.setData(MODULAR_CHAOS_CUBE, newDirection);
        entity.setChanged();
    }
}