package org.jahdoo.common.items.ability_augment;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.common.items.BaseItem;
import org.jahdoo.common.registers.AttributeReg;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

import static org.jahdoo.common.registers.AttributeReg.replaceOrAddAttribute;

public class AugmentCrystal extends BaseItem implements ICurioItem {

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

    @Override
    public void implicitModifiers(ItemStack stack, List<Component> tooltipComponents) {
        super.implicitModifiers(stack, tooltipComponents);
        tooltipComponents.add(TextHelpers.withStyleComponent("Offhand Wands", ColourHelpers.getSympathiserOrange()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var itemStack = player.getItemInHand(usedHand);
        var bouncyFoot = AttributeReg.CHAINED_SEMTEX;
//        System.out.println(player.level());
        replaceOrAddAttribute(itemStack, bouncyFoot.getRegisteredName(), bouncyFoot, 1, EquipmentSlot.BODY, true, "bonus");
        return super.use(level, player, usedHand);
    }
}
