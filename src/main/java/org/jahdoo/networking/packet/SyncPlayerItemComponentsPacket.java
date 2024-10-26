package org.jahdoo.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.utils.GeneralHelpers;

import static org.jahdoo.registers.AttachmentRegister.MAGE_FLIGHT;
import static org.jahdoo.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;

public class SyncPlayerItemComponentsPacket implements CustomPacketPayload{
    public static final Type<SyncPlayerItemComponentsPacket> TYPE = new Type<>(GeneralHelpers.modResourceLocation("sync_item_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPlayerItemComponentsPacket> STREAM_CODEC = CustomPacketPayload.codec(SyncPlayerItemComponentsPacket::toBytes, SyncPlayerItemComponentsPacket::new);
    private WandAbilityHolder wandAbilityHolder;

    public SyncPlayerItemComponentsPacket(WandAbilityHolder wandAbilityHolder) {
        this.wandAbilityHolder = wandAbilityHolder;
    }

    public SyncPlayerItemComponentsPacket(FriendlyByteBuf buf) {
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
