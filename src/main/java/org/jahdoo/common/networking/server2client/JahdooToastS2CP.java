package org.jahdoo.common.networking.server2client;

import net.minecraft.advancements.AdvancementType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.client.JahdooToast;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

public class JahdooToastS2CP implements CustomPacketPayload {

    public static final Type<JahdooToastS2CP> TYPE = new Type<>(JahdooHelpers.res("client_toast"));

    public static final StreamCodec<RegistryFriendlyByteBuf, JahdooToastS2CP> STREAM_CODEC =
        CustomPacketPayload.codec(JahdooToastS2CP::toBytes, JahdooToastS2CP::new);

    ResourceLocation icon;
    String header;
    String description;

    public JahdooToastS2CP(String header, String description, ResourceLocation icon) {
        this.icon = icon;
        this.header = header;
        this.description = description;
    }

    public JahdooToastS2CP(FriendlyByteBuf buf) {
        this.header = buf.readUtf();
        this.description = buf.readUtf();
        this.icon = buf.readResourceLocation();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(header);
        buf.writeUtf(description);
        buf.writeResourceLocation(icon);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof LocalPlayer localPlayer) {
                    Minecraft.getInstance().getToasts().addToast(new JahdooToast(header, description, icon, AdvancementType.CHALLENGE));
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

}
