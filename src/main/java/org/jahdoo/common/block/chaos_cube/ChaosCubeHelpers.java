package org.jahdoo.common.block.chaos_cube;

import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.common.networking.client2server.ChaosCubeC2SP;
import org.jahdoo.trial_nexus.attachments.ChaosCubeData;

import static org.jahdoo.common.registers.AttachmentReg.MODULAR_CHAOS_CUBE;
public class ChaosCubeHelpers {

    // Toggle the power state
    public static void selectDirection(ChaosCubeEntity entity, ChaosCubeData newDirection) {
        PacketDistributor.sendToServer(new ChaosCubeC2SP(entity.getBlockPos(), newDirection));
        entity.setData(MODULAR_CHAOS_CUBE, newDirection);
        entity.setChanged();
    }

    public static void toggleChained(ChaosCubeEntity entity) {
        var autoBlock = entity.getData(MODULAR_CHAOS_CUBE);
        var switched = autoBlock.updateChained(!autoBlock.chained());
        PacketDistributor.sendToServer(new ChaosCubeC2SP(entity.getBlockPos(), switched));
        entity.setData(MODULAR_CHAOS_CUBE, switched);
        entity.setChanged();
    }

    public static void togglePower(ChaosCubeEntity entity) {
        var autoBlock = entity.getData(MODULAR_CHAOS_CUBE);
        var switched = autoBlock.updateActive(!autoBlock.active());
        PacketDistributor.sendToServer(new ChaosCubeC2SP(entity.getBlockPos(), switched));
        entity.setData(MODULAR_CHAOS_CUBE, switched);
        entity.setChanged();
    }

}