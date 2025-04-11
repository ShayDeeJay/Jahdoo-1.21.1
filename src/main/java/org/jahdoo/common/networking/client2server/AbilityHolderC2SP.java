package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.networking.server2client.AbilityHolderS2CP;
import org.jahdoo.common.registers.AttachmentReg;

public class AbilityHolderC2SP implements CustomPacketPayload {

    public static final Type<AbilityHolderC2SP> TYPE = new Type<>(Helpers.res("ability_holder"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityHolderC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(AbilityHolderC2SP::toBytes, AbilityHolderC2SP::new);

    private final AbilityHolder abilityHolder;
    private final int upgradeCost;

    public AbilityHolderC2SP(AbilityHolder abilityHolder, int upgradeCost) {
        this.abilityHolder = abilityHolder;
        this.upgradeCost = upgradeCost;
    }

    public AbilityHolderC2SP(FriendlyByteBuf buf) {
        this.abilityHolder = buf.readJsonWithCodec(AbilityHolder.CODEC);
        this.upgradeCost = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(AbilityHolder.CODEC, this.abilityHolder);
        buf.writeInt(upgradeCost);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    var casterData = serverPlayer.getData(AttachmentReg.CASTER_DATA);
                    if(abilityHolder != AbilityHolder.DEFAULT){
                        casterData.updateAbility(abilityHolder);
                        casterData.decrementAbilityPoints(upgradeCost);
                    }
                    PacketDistributor.sendToPlayer(serverPlayer, new AbilityHolderS2CP(casterData.getUnlockedAbilities()));
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
