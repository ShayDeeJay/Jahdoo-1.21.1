package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.utils.Helpers;

import static org.jahdoo.common.registers.AttachmentReg.PLAYER_WALLET;

public class WalletSyncC2SP implements CustomPacketPayload {
    public static final Type<WalletSyncC2SP> TYPE = new Type<>(Helpers.res("sync_server_wallet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WalletSyncC2SP> STREAM_CODEC = CustomPacketPayload.codec(WalletSyncC2SP::toBytes, WalletSyncC2SP::new);

    private final int wallet;

    public WalletSyncC2SP(int wallet) {
        this.wallet = wallet;
    }

    public WalletSyncC2SP(FriendlyByteBuf buf) {
        this.wallet = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeInt(this.wallet);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    if(ctx.player() instanceof ServerPlayer serverPlayer) {
                        var getWallet = serverPlayer.getData(PLAYER_WALLET);
                        getWallet.setWallet(wallet);
                    }
                }
            }
        );
        return true;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
