package org.jahdoo.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import org.jahdoo.common.registers.ComponentReg;

import static org.jahdoo.common.registers.ItemReg.*;

public record CoreData(int required, int filled) {

    private void serialise(FriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeInt(required);
        friendlyByteBuf.writeInt(filled);
    }

    private static CoreData deserialise(FriendlyByteBuf byteBuf){
        return new CoreData(byteBuf.readInt(), byteBuf.readInt());
    }

    public static final StreamCodec<FriendlyByteBuf, CoreData> STREAM_CODEC = StreamCodec.ofMember(
        CoreData::serialise,
        CoreData::deserialise
    );

    public static Item getChargedVersion(ItemStack itemStack){
        if(itemStack.is(AUGMENT_CORE)) return CHARGED_AUGMENT_CORE.get();
        if(itemStack.is(ADVANCED_AUGMENT_CORE)) return CHARGED_ADVANCED_AUGMENT_CORE.get();
        if(itemStack.is(AUGMENT_HYPER_CORE)) return CHARGED_AUGMENT_HYPER_CORE.get();

        return ItemStack.EMPTY.getItem();
    }

    public static boolean isFull(ItemStack itemStack){
        var data = itemStack.get(ComponentReg.CORE_DATA);
        if(data != null){
            return data.required == data.filled;
        }
        return false;
    }

    public static void increment(ItemStack itemStack, int fillAmount){
        var data = itemStack.get(ComponentReg.CORE_DATA);
        if(data != null){
            var required = data.required();
            var newData = new CoreData(required, Math.min(data.filled()+fillAmount, required));
            itemStack.set(ComponentReg.CORE_DATA, newData);
            if(data.filled + 1 >= required){
                itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));
            }
        }
    }

    public static ItemStack incrementCore(ItemStack itemStack, int fillAmount){
        var data = itemStack.get(ComponentReg.CORE_DATA);
        if(data != null){
            var required = data.required();
            var newData = new CoreData(required, Math.min(data.filled()+fillAmount, required));
            itemStack.set(ComponentReg.CORE_DATA, newData);
            if(data.filled + 1 >= required){
                return new ItemStack(getChargedVersion(itemStack));
            }
        }
        return itemStack;
    }

    public static void setFilled(ItemStack itemStack){
        var data = itemStack.get(ComponentReg.CORE_DATA);
        if(data != null){
            var required = data.required();
            var newData = new CoreData(required, required);
            itemStack.set(ComponentReg.CORE_DATA, newData);
//            itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));
        }
    }



    public static int getRequired(ItemStack itemStack){
        var data = itemStack.get(ComponentReg.CORE_DATA);
        if(data != null){
            return data.required();
        }
        return -1;
    }

    public static int getFilled(ItemStack itemStack){
        var data = itemStack.get(ComponentReg.CORE_DATA);
        if(data != null){
            return data.filled();
        }
        return -1;
    }

    public static final Codec<CoreData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.INT.fieldOf("required").forGetter(CoreData::required),
            Codec.INT.fieldOf("filled").forGetter(CoreData::filled)
        ).apply(instance, CoreData::new)
    );

}