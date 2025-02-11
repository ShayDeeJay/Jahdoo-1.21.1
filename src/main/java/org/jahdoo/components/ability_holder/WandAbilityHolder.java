package org.jahdoo.components.ability_holder;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.utils.ModHelpers;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.jahdoo.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;

public record WandAbilityHolder(Map<String, AbilityHolder> abilityProperties) {

    public static final WandAbilityHolder DEFAULT = new WandAbilityHolder(new LinkedHashMap<>());

    public static final StreamCodec<FriendlyByteBuf, WandAbilityHolder> STREAM_CODEC = StreamCodec.ofMember(
        WandAbilityHolder::serialise,
        WandAbilityHolder::deserialise
    );

    public static WandAbilityHolder getHolder(ItemStack itemStack){
        var getHolder = itemStack.get(WAND_ABILITY_HOLDER);
        if(getHolder != null) return getHolder;
        return DEFAULT;
    }

    private void serialise(FriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeMap(abilityProperties, ByteBufCodecs.STRING_UTF8, AbilityHolder.STREAM_CODEC);
    }

    private static WandAbilityHolder deserialise(FriendlyByteBuf friendlyByteBuf){
        return new WandAbilityHolder(
            friendlyByteBuf.readMap(Maps::newLinkedHashMapWithExpectedSize, ByteBufCodecs.STRING_UTF8, AbilityHolder.STREAM_CODEC)
        );
    }

    public static final Codec<WandAbilityHolder> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, AbilityHolder.CODEC)
                .fieldOf("wand_ability_properties")
                .forGetter(WandAbilityHolder::abilityProperties)
        ).apply(instance, WandAbilityHolder::new)
    );

    public static WandAbilityHolder getHolderFromWand(Player player){
        var component = WAND_ABILITY_HOLDER.get();
        return ModHelpers.getUsedItem(player).get(component);
    }
}
