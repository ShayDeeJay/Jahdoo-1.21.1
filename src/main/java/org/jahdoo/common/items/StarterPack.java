package org.jahdoo.common.items;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jahdoo.trial_nexus.mobs.MobItemHandler;
import org.jahdoo.trial_nexus.utils.Helpers;

import java.util.List;

import static org.jahdoo.trial_nexus.utils.ColourStore.*;

public class StarterPack extends Item {

    public static final List<Item> IRON = List.of(Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.IRON_SWORD);
    public static final List<Item> DIAMOND = List.of(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS, Items.DIAMOND_SWORD);
    public static final List<Item> NETHERITE = List.of(Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS, Items.NETHERITE_SWORD);

    public StarterPack() { super(new Properties()); }

    @Override
    public Component getName(ItemStack stack) {
        var data = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        var getType = data == null ? Pair.of("Iron ", SILVER_COIN) :
                 data.value() == 1 ? Pair.of("Diamond ", DIAMOND_BOX) :
                                     Pair.of("Netherite ", NETHERITE_BOX) ;

        return Helpers.withStyleComponent(getType.getFirst() + "Starter Pack", getType.getSecond());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var itemInHand = player.getItemInHand(usedHand);
        var data = itemInHand.get(DataComponents.CUSTOM_MODEL_DATA);
        var getType = data == null ? IRON : data.value() == 1 ? DIAMOND : NETHERITE ;
        player.playSound(SoundEvents.TRIAL_SPAWNER_OPEN_SHUTTER , 1, 2F);
        player.playSound(SoundEvents.ARMOR_EQUIP_IRON.value(), 1, 1.6F);

        if(level instanceof ServerLevel serverLevel){
            var items = MobItemHandler.addArmorWithElement(
                player,
                serverLevel,
                getType.getFirst(),
                getType.get(1),
                getType.get(2),
                getType.get(3),
                getType.get(4)
            );

            for (var item : items) Helpers.throwOrAddItem(player, item);
            itemInHand.shrink(1);
        }
        return InteractionResultHolder.fail(itemInHand);
    }

}
