package org.jahdoo.common.items.ability_augment;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import org.jahdoo.common.items.caster_item.CasterItem;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;

public class AugmentCrystal extends CasterItem implements ICurioItem {

    public AugmentCrystal() {
        super(
            new Properties()
                .durability(300)
                .stacksTo(1)
        );
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @Override
    public Component getName(ItemStack stack) {
        return TextHelpers.withStyleComponent(super.getName(stack).getString(), ColourHelpers.getGoldCoin());
    }

//    @Override
//    public void implicitModifiers(ItemStack stack, List<Component> tooltipComponents) {
//        super.implicitModifiers(stack, tooltipComponents);
//        tooltipComponents.add(TextHelpers.withStyleComponent("Offhand Wands", ColourHelpers.getSympathiserOrange()));
//    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var itemStack = player.getItemInHand(usedHand);
//        var bouncyFoot = AttributeReg.CHAINED_SEMTEX;

        itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(Random.nextInt(1, 5)));

//        replaceOrAddAttribute(itemStack, bouncyFoot.getRegisteredName(), bouncyFoot, 1, EquipmentSlot.BODY, true, "bonus");
        return super.use(level, player, usedHand);
    }
}
