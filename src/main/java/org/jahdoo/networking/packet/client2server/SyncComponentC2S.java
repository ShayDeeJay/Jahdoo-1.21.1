package org.jahdoo.networking.packet.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;

public class SyncComponentC2S implements CustomPacketPayload{
    public static final Type<SyncComponentC2S> TYPE = new Type<>(ModHelpers.res("sync_item_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncComponentC2S> STREAM_CODEC = CustomPacketPayload.codec(SyncComponentC2S::toBytes, SyncComponentC2S::new);
    private final WandAbilityHolder wandAbilityHolder;

    public SyncComponentC2S(WandAbilityHolder wandAbilityHolder) {
        this.wandAbilityHolder = wandAbilityHolder;
    }

    public SyncComponentC2S(FriendlyByteBuf buf) {
        this.wandAbilityHolder = buf.readJsonWithCodec(WandAbilityHolder.CODEC);
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeJsonWithCodec(WandAbilityHolder.CODEC, this.wandAbilityHolder);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    serverPlayer.getMainHandItem().set(WAND_ABILITY_HOLDER, this.wandAbilityHolder);
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
