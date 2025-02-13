package org.jahdoo.items.magnet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import static org.jahdoo.registers.DataComponentRegistry.MAGNET_DATA;

public record MagnetData(
        boolean active,
        int range,
        double strength
) {
    public static final MagnetData DEFAULT = new MagnetData(false, 3, 0.2);

    public static MagnetData simpleMagnet() {
        return new MagnetData(false, 5, 0.5);
    }

    public static MagnetData greaterMagnet() {
        return new MagnetData(false, 7, 0.8);
    }

    public static MagnetData perfectMagnet() {
        return new MagnetData(false, 10, 1.5);
    }

    public static MagnetData ancientMagnet() {
        return new MagnetData(false, 15, 2);
    }

    public static void setDataByType(ItemStack stack){
        var type = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        if(type != null) {
            var id = type.value();
            var dataType = switch (id) {
                case 1 -> simpleMagnet();
                case 2 -> greaterMagnet();
                case 3 -> perfectMagnet();
                case 4, 5 -> ancientMagnet();
                default -> DEFAULT;
            };
            stack.set(MAGNET_DATA, dataType);
        }
    }

    public MagnetData setActive(boolean active) {
        return new MagnetData(active, this.range, this.strength);
    }

    public MagnetData setRange(int range) {
        return new MagnetData(this.active, range, this.strength);
    }

    public MagnetData setStrength(double strength) {
        return new MagnetData(this.active, this.range, strength);
    }

    public static MagnetData getMagnetData(ItemStack itemStack) {
        return itemStack.has(MAGNET_DATA) ? itemStack.get(MAGNET_DATA) : DEFAULT;
    }

    public static void updateActive(ItemStack itemStack, boolean active) {
        itemStack.update(MAGNET_DATA, DEFAULT, data -> data.setActive(active));
    }

    public static MagnetData deserialise(RegistryFriendlyByteBuf friendlyByteBuf) {
        return new MagnetData(
            friendlyByteBuf.readBoolean(),
            friendlyByteBuf.readInt(),
            friendlyByteBuf.readDouble()
        );
    }

    public void serialise(RegistryFriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBoolean(active);
        friendlyByteBuf.writeInt(range);
        friendlyByteBuf.writeDouble(strength);
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, MagnetData> STREAM_CODEC = StreamCodec.ofMember(
        MagnetData::serialise,
        MagnetData::deserialise
    );

    public static final Codec<MagnetData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.BOOL.fieldOf("active").forGetter(MagnetData::active),
            Codec.INT.fieldOf("range").forGetter(MagnetData::range),
            Codec.DOUBLE.fieldOf("strength").forGetter(MagnetData::strength)
        ).apply(instance, MagnetData::new)
    );

}