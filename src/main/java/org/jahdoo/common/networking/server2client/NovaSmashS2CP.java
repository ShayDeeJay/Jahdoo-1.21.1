package org.jahdoo.common.networking.server2client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.ascension.utils.Helpers;

import static org.jahdoo.common.registers.AttachmentReg.NOVA_SMASH;

public class NovaSmashS2CP implements CustomPacketPayload {

    public static final Type<NovaSmashS2CP> TYPE = new Type<>(Helpers.res("nova_smash_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NovaSmashS2CP> STREAM_CODEC = CustomPacketPayload.codec(NovaSmashS2CP::toBytes, NovaSmashS2CP::new);

    int highestDelta;
    boolean canSmash;
    float damage;

    public NovaSmashS2CP(int highestDelta, boolean canSmash, float damage) {
        this.canSmash = canSmash;
        this.highestDelta = highestDelta;
        this.damage = damage;
    }

    public NovaSmashS2CP(FriendlyByteBuf buf) {
        this.highestDelta = buf.readInt();
        this.canSmash = buf.readBoolean();
        this.damage = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.highestDelta);
        buf.writeBoolean(this.canSmash);
        buf.writeFloat(this.damage);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork( () -> {
                if(ctx.player() instanceof LocalPlayer localPlayer){
                    var novaSmash = localPlayer.getData(NOVA_SMASH);
                    novaSmash.setCanSmash(this.canSmash);
                    novaSmash.setHighestDelta(this.highestDelta);
                    novaSmash.setGetDamage(this.damage);
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
