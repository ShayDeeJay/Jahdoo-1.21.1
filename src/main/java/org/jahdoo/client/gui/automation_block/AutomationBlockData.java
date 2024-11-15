package org.jahdoo.client.gui.automation_block;

import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.block.automation_block.AutomationBlockEntity;
import org.jahdoo.capabilities.player_abilities.AutoBlock;
import org.jahdoo.networking.packet.client2server.AutoBlockC2SPacket;

import java.util.List;

import static org.jahdoo.client.gui.automation_block.AutomationBlockScreen.isContainerAccessor;
import static org.jahdoo.registers.AttachmentRegister.AUTO_BLOCK;

public class AutomationBlockData {

    // Toggle the power state
    public static void togglePower(AutomationBlockEntity entity) {
        var autoBlock = entity.getData(AUTO_BLOCK);
        var switched = autoBlock.updateActive(!autoBlock.active());
        PacketDistributor.sendToServer(new AutoBlockC2SPacket(entity.getBlockPos(), switched));
        entity.setData(AUTO_BLOCK, switched);
    }

    public static void selectDirection(AutomationBlockEntity entity, AutoBlock newDirection) {
        PacketDistributor.sendToServer(new AutoBlockC2SPacket(entity.getBlockPos(), newDirection));
        entity.setData(AUTO_BLOCK, newDirection);
        entity.setChanged();
    }
}