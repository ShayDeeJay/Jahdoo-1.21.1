package org.jahdoo.common.networking.client2server;

import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import java.util.UUID;

public class AttributeC2SP implements CustomPacketPayload {

    public static final Type<AttributeC2SP> TYPE = new Type<>(JahdooHelpers.res("attribute_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AttributeC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(AttributeC2SP::toBytes, AttributeC2SP::new);

    private final Holder<Attribute> attribute;
    private final Double value;

    public AttributeC2SP(
        Holder<Attribute> attribute,
        Double value
    ) {
        this.attribute = attribute;
        this.value = value;
    }

    public AttributeC2SP(FriendlyByteBuf buf) {
        this.attribute = buf.readJsonWithCodec(Attribute.CODEC);
        this.value = buf.readDouble();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(Attribute.CODEC, this.attribute);
        buf.writeDouble(this.value);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    JahdooHelpers.addTransientAttribute(serverPlayer, value, "boon" + attribute.value().getDescriptionId() + UUID.randomUUID(), attribute);
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

}
