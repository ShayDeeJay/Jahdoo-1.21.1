package org.jahdoo.common.networking.client2server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.components.AbilityHolder;

import static org.jahdoo.common.registers.ComponentReg.ABILITY_HOLDER;

public class SyncComponentBlockC2S implements CustomPacketPayload{
    public static final Type<SyncComponentBlockC2S> TYPE = new Type<>(Helpers.res("sync_item_block_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncComponentBlockC2S> STREAM_CODEC = CustomPacketPayload.codec(SyncComponentBlockC2S::toBytes, SyncComponentBlockC2S::new);
    private AbilityHolder abilityHolder;
    private BlockPos blockPos;

    public SyncComponentBlockC2S(AbilityHolder wandAbilityHolder, BlockPos blockPos) {
        this.abilityHolder = wandAbilityHolder;
        this.blockPos = blockPos;
    }

    public SyncComponentBlockC2S(FriendlyByteBuf buf) {
        this.abilityHolder = buf.readJsonWithCodec(AbilityHolder.CODEC);
        this.blockPos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeJsonWithCodec(AbilityHolder.CODEC, this.abilityHolder);
        bug.writeBlockPos(this.blockPos);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player().level() instanceof ServerLevel serverLevel){
                    var entity = serverLevel.getBlockEntity(this.blockPos);
                    sendTagsToSlot(entity, this.abilityHolder);
                }
            }
        );
        return true;
    }

    public static void sendTagsToSlot(BlockEntity entity, AbilityHolder wandAbilityHolder) {
        if(entity instanceof AbstractBEInventory entity1){
            var handler = entity1.inputItemHandler;
            var augment = handler.getStackInSlot(0).copy();
            augment.set(ABILITY_HOLDER, wandAbilityHolder);
            handler.setStackInSlot(0, augment);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
