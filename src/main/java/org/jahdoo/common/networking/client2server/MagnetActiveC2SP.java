package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.items.magnet.MagnetData;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import top.theillusivec4.curios.api.CuriosApi;

public class MagnetActiveC2SP implements CustomPacketPayload {
    public static final Type<MagnetActiveC2SP> TYPE = new Type<>(JahdooHelpers.res("active_magnet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MagnetActiveC2SP> STREAM_CODEC =
            CustomPacketPayload.codec(MagnetActiveC2SP::toBytes, MagnetActiveC2SP::new);

    public MagnetActiveC2SP() {}

    public MagnetActiveC2SP(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                var player = ctx.player();
                var curio = CuriosApi.getCuriosInventory(player);
                if(curio.isPresent()){
                    var curios = curio.get().findCurios(ItemReg.MAGNET.get());
                    if(!curios.isEmpty()){
                        var magnet = curios.getFirst().stack();
                        var magnetData = magnet.get(ComponentReg.MAGNET_DATA);
                        if(magnetData != null){
                            MagnetData.updateActive(magnet, !magnetData.active());
                            var active = JahdooHelpers.withStyleComponent("Active", ColourStore.MAGNET_RANGE_GREEN);
                            var deactivate = JahdooHelpers.withStyleComponent("Deactivated", ColourStore.MAGNET_STRENGTH_RED);
                            player.displayClientMessage(!magnetData.active() ? active : deactivate, true);
                            if(player instanceof ServerPlayer serverPlayer){
                                JahdooHelpers.sendClientSound(serverPlayer, SoundReg.SELECT.get(), 1, 1);
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
