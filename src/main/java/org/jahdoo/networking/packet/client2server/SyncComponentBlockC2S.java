package org.jahdoo.networking.packet.client2server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;

public class SyncComponentBlockC2S implements CustomPacketPayload{
    public static final Type<SyncComponentBlockC2S> TYPE = new Type<>(ModHelpers.res("sync_item_block_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncComponentBlockC2S> STREAM_CODEC = CustomPacketPayload.codec(SyncComponentBlockC2S::toBytes, SyncComponentBlockC2S::new);
    private WandAbilityHolder wandAbilityHolder;
    private BlockPos blockPos;

    public SyncComponentBlockC2S(WandAbilityHolder wandAbilityHolder, BlockPos blockPos) {
        this.wandAbilityHolder = wandAbilityHolder;
        this.blockPos = blockPos;
    }

    public SyncComponentBlockC2S(FriendlyByteBuf buf) {
        this.wandAbilityHolder = buf.readJsonWithCodec(WandAbilityHolder.CODEC);
        this.blockPos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeJsonWithCodec(WandAbilityHolder.CODEC, this.wandAbilityHolder);
        bug.writeBlockPos(this.blockPos);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player().level() instanceof ServerLevel serverLevel){
                    var entity = serverLevel.getBlockEntity(this.blockPos);
                    sendTagsToSlot(entity, this.wandAbilityHolder);
                }
            }
        );
        return true;
    }

    public static void sendTagsToSlot(BlockEntity entity, WandAbilityHolder wandAbilityHolder) {
        if(entity instanceof ModularChaosCubeEntity entity1){
            var handler = entity1.inputItemHandler;
            var augment = handler.getStackInSlot(0).copy();
            augment.set(WAND_ABILITY_HOLDER, wandAbilityHolder);
            handler.setStackInSlot(0, augment);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
