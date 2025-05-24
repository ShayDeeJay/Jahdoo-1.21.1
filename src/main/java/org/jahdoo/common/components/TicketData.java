package org.jahdoo.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.registers.ComponentReg;

import java.util.HashMap;
import java.util.Map;

public record TicketData(
    Map<String, Double> values
) {

    public static void addNewEntry(ItemStack itemStack, String tag, double value){
        var getFromStack = itemStack.get(ComponentReg.TICKET_DATA);
        var newMap = new HashMap<>(getFromStack != null ? getFromStack.values() : new HashMap<>());
        newMap.put(tag, value);
        itemStack.set(ComponentReg.TICKET_DATA, new TicketData(newMap));
    }

    public double get(String key) {
        return values.getOrDefault(key, 0.0);
    }

    private void serialise(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeMap(values, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeDouble);
    }

    private static TicketData deserialise(FriendlyByteBuf byteBuf){
        return new TicketData(byteBuf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readDouble));
    }

    public static final StreamCodec<FriendlyByteBuf, TicketData> STREAM_CODEC = StreamCodec.ofMember(
        TicketData::serialise,
        TicketData::deserialise
    );

    public static final Codec<TicketData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("Data").forGetter(run -> run.values)
        ).apply(instance, TicketData::new)
    );

}
