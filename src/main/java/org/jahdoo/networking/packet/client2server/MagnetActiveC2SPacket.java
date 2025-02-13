package org.jahdoo.networking.packet.client2server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.block.AbstractBEInventory;
import org.jahdoo.items.magnet.MagnetData;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;
import top.theillusivec4.curios.api.CuriosApi;

public class MagnetActiveC2SPacket implements CustomPacketPayload {
    public static final Type<MagnetActiveC2SPacket> TYPE = new Type<>(ModHelpers.res("active_magnet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MagnetActiveC2SPacket> STREAM_CODEC =
            CustomPacketPayload.codec(MagnetActiveC2SPacket::toBytes, MagnetActiveC2SPacket::new);

    public MagnetActiveC2SPacket() {}

    public MagnetActiveC2SPacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                var player = ctx.player();
                var curio = CuriosApi.getCuriosInventory(player);
                if(curio.isPresent()){
                    var curios = curio.get().findCurios(ItemsRegister.MAGNET.get());
                    if(!curios.isEmpty()){
                        var magnet = curios.getFirst().stack();
                        var magnetData = magnet.get(DataComponentRegistry.MAGNET_DATA);
                        if(magnetData != null){
                            MagnetData.updateActive(magnet, !magnetData.active());
                            var active = ModHelpers.withStyleComponent("Active", ColourStore.MAGNET_RANGE_GREEN);
                            var deactivate = ModHelpers.withStyleComponent("Deactivated", ColourStore.MAGNET_STRENGTH_RED);
                            player.displayClientMessage(!magnetData.active() ? active : deactivate, true);
                            if(player instanceof ServerPlayer serverPlayer){
                                ModHelpers.sendClientSound(serverPlayer, SoundRegister.SELECT.get(), 1, 1);
                            }
                        }
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
