package org.jahdoo.networking.packet.client2server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.block.automation_block.AutomationBlockEntity;
import org.jahdoo.components.DataComponentHelper;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.AttachmentRegister;
import org.jahdoo.utils.ModHelpers;

public class UpdateDirectionC2SPacket implements CustomPacketPayload {
    public static final Type<UpdateDirectionC2SPacket> TYPE = new Type<>(ModHelpers.modResourceLocation("directions"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateDirectionC2SPacket> STREAM_CODEC = CustomPacketPayload.codec(UpdateDirectionC2SPacket::toBytes, UpdateDirectionC2SPacket::new);

    BlockPos blockPos;
    String direction;
    boolean powered;

    public UpdateDirectionC2SPacket(BlockPos blockPos, String direction, boolean powered) {
        this.blockPos = blockPos;
        this.direction = direction;
        this.powered = powered;
    }

    public UpdateDirectionC2SPacket(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.direction = buf.readUtf();
        this.powered = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeUtf(direction);
        buf.writeBoolean(powered);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork( () -> {
                if(ctx.player().level() instanceof ServerLevel serverLevel){
                    var bEntity = serverLevel.getBlockEntity(blockPos);
                    if(bEntity instanceof AutomationBlockEntity entity){
                        entity.setDirection(direction);
                        entity.setData(AttachmentRegister.POS, entity.direction);
                        entity.setData(AttachmentRegister.BOOL, powered);
                    }
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
