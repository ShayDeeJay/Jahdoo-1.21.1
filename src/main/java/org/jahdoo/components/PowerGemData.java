package org.jahdoo.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.registers.DataComponentRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record PowerGemData(
    String name,
    String description,
    int colour,
    int rarityId
){
    public static final String SUFFIX = "Power Gem";
    public static final String DEFAULT_NAME = "Generic " + SUFFIX;
    public static final PowerGemData DEFAULT = new PowerGemData(DEFAULT_NAME, "Has no power", -1, 0);

    public static List<String> populatedList(int initValue){
        var list = new ArrayList<String>();
        for(int i =0; i < initValue; i++) list.add("empty" + i);
        return list;
    }

    public PowerGemData insertNewName(String name){
        return new PowerGemData(name, this.description, this.rarityId, this.colour);
    }

    public PowerGemData insertNewDescription(String description){
        return new PowerGemData(this.name, description, this.rarityId, this.colour);
    }

    public PowerGemData insertNewRarity(int rarityId){
        return new PowerGemData(this.name, this.description, rarityId, this.colour);
    }

    public PowerGemData insertNewColour(int colour){
        return new PowerGemData(this.name, this.description, this.rarityId, colour);
    }

    public static String getName(ItemStack itemStack){
        var data = itemStack.get(DataComponentRegistry.POWER_GEM_DATA.get());
        if(data != null && !Objects.equals(data.name(), DEFAULT_NAME)) return data.name();
        return DEFAULT_NAME;
    }

    public static String getDescription(ItemStack itemStack){
        var data = itemStack.get(DataComponentRegistry.POWER_GEM_DATA.get());
        if(data != null) return data.description();
        return "";
    }

    public void serialise(RegistryFriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeUtf(this.name);
        friendlyByteBuf.writeUtf(this.description);
        friendlyByteBuf.writeInt(this.rarityId);
        friendlyByteBuf.writeInt(this.colour);
    }

    public static PowerGemData deserialise(RegistryFriendlyByteBuf friendlyByteBuf){
        return new PowerGemData(
            friendlyByteBuf.readUtf(),
            friendlyByteBuf.readUtf(),
            friendlyByteBuf.readInt(),
            friendlyByteBuf.readInt()
        );
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, PowerGemData> STREAM_CODEC = StreamCodec.ofMember(
        PowerGemData::serialise,
        PowerGemData::deserialise
    );

    public static final Codec<PowerGemData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(PowerGemData::name),
            Codec.STRING.fieldOf("description").forGetter(PowerGemData::description),
            Codec.INT.fieldOf("colour").forGetter(PowerGemData::rarityId),
            Codec.INT.fieldOf("rarity_id").forGetter(PowerGemData::rarityId)
        ).apply(instance, PowerGemData::new)
    );
}
