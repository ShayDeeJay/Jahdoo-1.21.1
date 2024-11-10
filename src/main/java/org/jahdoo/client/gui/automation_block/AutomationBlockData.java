package org.jahdoo.client.gui.automation_block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.block.automation_block.AutomationBlockEntity;
import org.jahdoo.networking.packet.client2server.UpdateDirectionC2SPacket;
import org.jahdoo.registers.AttachmentRegister;

import java.util.List;

public class AutomationBlockData {

    // Toggle the power state
    public static void togglePower(AutomationBlockEntity entity) {
        var currentPos = entity.getData(AttachmentRegister.POS);
        var currentPower = entity.getData(AttachmentRegister.BOOL);

        var element = entity.direction().stream()
            .filter(pair -> pair.getSecond().equals(currentPos))
            .findFirst();

        if (element.isPresent()) {
            // No need to pass PacketDistributor, directly use the static reference
            PacketDistributor.sendToServer(new UpdateDirectionC2SPacket(
                entity.getBlockPos(), element.get().getFirst(), !currentPower));
            entity.setData(AttachmentRegister.POS, currentPos);
            entity.setData(AttachmentRegister.BOOL, !currentPower);
        }
    }

    // Move to the next or previous position
    public static void movePosition(List<Pair<String, BlockPos>> pos, boolean forward, AutomationBlockEntity entity) {
        var currentPos = entity.getData(AttachmentRegister.POS);
        var currentPower = entity.getData(AttachmentRegister.BOOL);

        var element = pos.stream()
            .filter(pair -> pair.getSecond().equals(currentPos))
            .findFirst();

        if (element.isPresent()) {
            int currentIndex = pos.indexOf(element.get());
            int nextIndex = forward ? (currentIndex + 1) % pos.size()
                : (currentIndex - 1 + pos.size()) % pos.size();

            var nextPos = pos.get(nextIndex);
            entity.setDirection(nextPos.getFirst());
            // No need to pass PacketDistributor, directly use the static reference
            PacketDistributor.sendToServer(new UpdateDirectionC2SPacket(
                entity.getBlockPos(), nextPos.getFirst(), currentPower));
            entity.setData(AttachmentRegister.POS, nextPos.getSecond());
            entity.setData(AttachmentRegister.BOOL, currentPower);
        }
    }
}