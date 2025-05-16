package org.jahdoo.common.networking.server2client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.utils.Helpers;

import static org.jahdoo.common.registers.AttachmentReg.PLAYER_WALLET_DATA;

public class WalletSyncS2CP implements CustomPacketPayload {
    public static final Type<WalletSyncS2CP> TYPE = new Type<>(Helpers.res("sync_client_wallet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WalletSyncS2CP> STREAM_CODEC = CustomPacketPayload.codec(WalletSyncS2CP::toBytes, WalletSyncS2CP::new);

    private final int wallet;

    public WalletSyncS2CP(int wallet) {
        this.wallet = wallet;
    }

    public WalletSyncS2CP(FriendlyByteBuf buf) {
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
                    if(ctx.player() instanceof LocalPlayer localPlayer) {
                        localPlayer.getData(PLAYER_WALLET_DATA).setWallet(wallet);
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
