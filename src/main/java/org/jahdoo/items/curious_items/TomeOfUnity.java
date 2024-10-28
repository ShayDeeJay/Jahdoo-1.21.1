package org.jahdoo.items.curious_items;

import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jahdoo.registers.AttributesRegister;
import org.jahdoo.utils.GeneralHelpers;
import top.theillusivec4.curios.Curios;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosTooltip;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;

public class TomeOfUnity extends Item implements ICurioItem {

    public TomeOfUnity() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        var player = slotContext.entity();
        extracted(player, 5);
    }


    private static void extracted(LivingEntity player, int num) {
        List<ItemEntity> items = player.level().getEntitiesOfClass(
            ItemEntity.class,
            player.getBoundingBox().inflate(num * 3),
            entity -> true
        );

        List<ExperienceOrb> experienceOrbs = player.level().getEntitiesOfClass(
            ExperienceOrb.class,
            player.getBoundingBox().inflate(num * 3),
            entity -> true
        );

        for (ItemEntity item : items) {
            GeneralHelpers.entityMover(player, item, (double) num / 4, 0.2);
        }

        for (ExperienceOrb experience : experienceOrbs) {
            GeneralHelpers.entityMover(player, experience, (double) num / 4, 0.2);
        }
    }
}
