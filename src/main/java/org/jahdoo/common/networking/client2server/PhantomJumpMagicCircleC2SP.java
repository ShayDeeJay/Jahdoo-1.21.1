package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

public class PhantomJumpMagicCircleC2SP implements CustomPacketPayload {

    public static final Type<PhantomJumpMagicCircleC2SP> TYPE =
        new Type<>(JahdooHelpers.res("magic_circle_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PhantomJumpMagicCircleC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(
            PhantomJumpMagicCircleC2SP::toBytes,
            PhantomJumpMagicCircleC2SP::new
        );

    private final int elementId;

    public PhantomJumpMagicCircleC2SP(int elementId) {
        this.elementId = elementId;
    }

    public PhantomJumpMagicCircleC2SP(FriendlyByteBuf buf) {
        this.elementId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.elementId);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;
            if (!(player.level() instanceof ServerLevel serverLevel)) return;

            var cloud = new AoeCloud(
                serverLevel,
                player,
                player.getBbWidth() - 0.2F,
                20
            );

            serverLevel.addFreshEntity(cloud);

            cloud.setEntityType("jump_circle");
            cloud.setElementId(this.elementId);
            cloud.moveTo(player.position());

            player.walkAnimation.setSpeed(0);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}