package org.jahdoo.common.block.augment_modification_station;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.components.AbilityData;
import org.jahdoo.common.components.AbilityHolder;

import java.util.HashMap;
import java.util.function.Consumer;

import static org.jahdoo.ascension.utils.Maths.doubleFormattedDouble;
import static org.jahdoo.common.registers.ElementReg.fromId;
import static org.jahdoo.common.registers.ElementReg.mystic;

public class AugmentModificationData {

    public static String extractName(String input) {
        if (input == null || !input.contains("|")) return "";
        return input.split("\\|")[0].trim();
    }

    public static AbilityData.AbilityModifiers getAbilityModifiers(Component component, AbilityHolder getTag) {
        return getTag.data().abilityProperties().get(extractName(component.getString()));
    }

    public static AbstractElement getAbstractElement(AugmentModificationEntity entity) {
        var slot = entity.getInteractionSlot();
        var value = slot.get(DataComponents.CUSTOM_MODEL_DATA);

        if(value != null) return fromId(value.value()).orElseThrow();

        return mystic();
    }

    public static boolean isInHitbox(int width, int height, double mouseX, double mouseY, boolean showInventory){
        var widthOffset = 100;
        var heightOffset = 115;
        var widthFrom = width - widthOffset;
        var heightFrom = height - heightOffset;
        var widthTo = width + widthOffset;
        var heightTo = height + heightOffset;
        return mouseX > widthFrom && mouseX < widthTo && mouseY > heightFrom + 50 && mouseY < heightTo - (showInventory ?  120 : 5);
    }

    public static void updateAugmentConfig(
        String name,
        AbilityData.AbilityModifiers modifiers,
        String abilityName,
        AbilityHolder holder,
        Consumer<AbilityHolder> holderExe,
        AbstractBEInventory user
    ) {
//        var newWandHolder = new WandAbilityHolder(new HashMap<>(holder.abilityProperties()));
        var newHolder = new AbilityData(new HashMap<>(holder.data().abilityProperties()));

        var higherBetter = modifiers.isHigherBetter();
        var actualValue = doubleFormattedDouble(modifiers.actualValue());
        var step = doubleFormattedDouble(modifiers.step());
        var highestValue = doubleFormattedDouble(modifiers.highestValue());
        var lowestValue = doubleFormattedDouble(modifiers.lowestValue());
        var correctAdjustment = higherBetter ? actualValue + step : actualValue - step;

        var valueWithinRange = higherBetter && actualValue < highestValue ? correctAdjustment : !higherBetter && actualValue > lowestValue ? correctAdjustment : actualValue;
        var abilityModifier = new AbilityData.AbilityModifiers(valueWithinRange, highestValue, lowestValue, step, valueWithinRange, higherBetter);

        newHolder.abilityProperties().put(name, abilityModifier);
        /* newWandHolder.abilityProperties().put(abilityName, newHolder);
        PacketDistributor.sendToServer(new SyncComponentBlockC2S(newWandHolder, user.getBlockPos()));
        holderExe.accept(newWandHolder);*/
    }

}
