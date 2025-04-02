package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;

import static org.jahdoo.common.registers.ComponentReg.ABILITY_HOLDER;

public class SyncComponentC2S implements CustomPacketPayload{
    public static final Type<SyncComponentC2S> TYPE = new Type<>(Helpers.res("sync_item_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncComponentC2S> STREAM_CODEC = CustomPacketPayload.codec(SyncComponentC2S::toBytes, SyncComponentC2S::new);
    private final AbilityHolder abilityHolder;

    public SyncComponentC2S(AbilityHolder wandAbilityHolder) {
        this.abilityHolder = wandAbilityHolder;
    }

    public SyncComponentC2S(FriendlyByteBuf buf) {
        this.abilityHolder = buf.readJsonWithCodec(AbilityHolder.CODEC);
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeJsonWithCodec(AbilityHolder.CODEC, this.abilityHolder);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    serverPlayer.getItemInHand(serverPlayer.getUsedItemHand()).set(ABILITY_HOLDER, this.abilityHolder);
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
