package org.jahdoo.common.items.runes.rune_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record RuneData(
    int elementId,
    String name,
    String description,
    int colour,
    int rarityId,
    int tier
){
    public static final String SUFFIX = "Rune";
    public static final String DEFAULT_NAME = "Unidentified";
    public static final int NO_ELEMENT = -1;
    public static final int NO_VALUE = -1;
    public static final String NO_DESCRIPTION = "";
    public static final RuneData DEFAULT = new RuneData(NO_ELEMENT, DEFAULT_NAME, "", -1, 0, 0);

    public RuneData insertNewName(String name){
        return new RuneData(this.elementId, name, this.description, this.colour, this.rarityId, this.tier);
    }

    public RuneData insertNewDescription(String description){
        return new RuneData(this.elementId, this.name, description, this.colour, this.rarityId, this.tier);
    }

    public RuneData insertNewElement(int elementId){
        return new RuneData(elementId, this.name, this.description, this.colour, this.rarityId, this.tier);
    }

    public RuneData insertNewRarity(int rarityId){
        return new RuneData(this.elementId, this.name, this.description, this.colour, rarityId, this.tier);
    }

    public RuneData insertNewColour(int colour){
        return new RuneData(this.elementId, this.name, this.description, colour, this.rarityId, this.tier);
    }

    public RuneData insertNewTier(int tier){
        return new RuneData(this.elementId, this.name, this.description, colour, this.rarityId, tier);
    }

    public int getTypeColourSecondary(){
        return colour;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, RuneData> STREAM_CODEC = StreamCodec.ofMember(
        RuneData::serialise,
        RuneData::deserialise
    );

    public static RuneData deserialise(RegistryFriendlyByteBuf friendlyByteBuf){
        return new RuneData(
            friendlyByteBuf.readInt(),
            friendlyByteBuf.readUtf(),
            friendlyByteBuf.readUtf(),
            friendlyByteBuf.readInt(),
            friendlyByteBuf.readInt(),
            friendlyByteBuf.readInt()
        );
    }

    public void serialise(RegistryFriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeInt(this.elementId);
        friendlyByteBuf.writeUtf(this.name);
        friendlyByteBuf.writeUtf(this.description);
        friendlyByteBuf.writeInt(this.colour);
        friendlyByteBuf.writeInt(this.rarityId);
        friendlyByteBuf.writeInt(this.tier);
    }

    public static final Codec<RuneData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.INT.fieldOf("element_id").forGetter(RuneData::elementId),
            Codec.STRING.fieldOf("name").forGetter(RuneData::name),
            Codec.STRING.fieldOf("description").forGetter(RuneData::description),
            Codec.INT.fieldOf("colour").forGetter(RuneData::colour),
            Codec.INT.fieldOf("rarity_id").forGetter(RuneData::rarityId),
            Codec.INT.fieldOf("tier").forGetter(RuneData::tier)
            ).apply(instance, RuneData::new)
    );

}
