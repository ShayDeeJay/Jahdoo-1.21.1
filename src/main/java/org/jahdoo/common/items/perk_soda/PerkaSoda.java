package org.jahdoo.common.items.perk_soda;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.PlayerBoonReg;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.Helpers;

import java.util.List;
import java.util.UUID;

public class PerkaSoda extends Item implements JahdooItem {

    public PerkaSoda() {
        super(new Properties());
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 24;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.empty();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var customModelData = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        if(customModelData != null){
            var getId = stack.get(ComponentReg.ID);
            var getNameById = PlayerBoonReg.getFromId(getId);
            this.standAloneModifiersWithLabel(stack, tooltipComponents, context, getNameById.getLabel(), ColourStore.HEADER_COLOUR, getNameById.colour(), 2, 20, false);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        var getBoons = getDrinkBoon(stack);
        var value = getBoons.modifier().amount();
        var attribute = getBoons.attribute();
        addTrailNexusAttribute((Player) livingEntity, value, attribute);
        stack.shrink(1);
        return super.finishUsingItem(stack, level, livingEntity);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var item = player.getItemInHand(usedHand);
        if(!(level instanceof ServerLevel serverLevel)) return InteractionResultHolder.fail(item);

        addPerk(item);

        Helpers.getSoundWithPositionV(serverLevel, player.position(), SoundReg.CAN_OPEN.get(), 1, 1.6F);
        return ItemUtils.startUsingInstantly(level, player, usedHand);
    }

    private static void addTrailNexusAttribute(Player livingEntity, double value, Holder<Attribute> attribute) {
        Helpers.addTransientAttribute(livingEntity, value, "boon" + attribute.value().getDescriptionId() + UUID.randomUUID(), attribute);
    }

    private static ItemAttributeModifiers.Entry getDrinkBoon(ItemStack stack) {
        return stack.getAttributeModifiers()
            .modifiers()
            .stream()
            .filter(s -> s.modifier().id().getPath().contains("boon"))
            .toList()
            .getFirst();
    }

    public static void addPerk(ItemStack item) {
        var customModelData = DataComponents.CUSTOM_MODEL_DATA;
        if(!item.has(customModelData)){
            var manaPool = PlayerBoonReg.randomBoon();
            var rarity = JahdooRarity.getRarity();
            var value = manaPool.getValue(rarity);
            item.set(customModelData, new CustomModelData(manaPool.getTextureId()));

            var boon = new AttributeModifier(Helpers.res("boon"), value, AttributeModifier.Operation.ADD_VALUE);
            var x = ItemAttributeModifiers.builder().add(manaPool.attributeHolder(), boon, EquipmentSlotGroup.ARMOR);
            item.set(DataComponents.ATTRIBUTE_MODIFIERS, x.build());
            item.set(ComponentReg.ID, manaPool.id());
        }
    }

}
