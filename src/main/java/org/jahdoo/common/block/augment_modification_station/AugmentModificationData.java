package org.jahdoo.common.block.augment_modification_station;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.common.components.AbilityData;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.networking.client2server.AbilityHolderC2SP;
import org.jahdoo.common.registers.AttachmentReg;

import java.util.LinkedHashMap;

import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;
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

    public static AbilityHolder updateAugmentConfig(
        String name,
        String abilityName,
        Player player
    ) {

        var data = player.getData(AttachmentReg.CASTER_DATA);
        var properties = new LinkedHashMap<>(data.getHolder(abilityName).data().abilityProperties());
        var mod = properties.get(name);
        var higherBetter = mod.isHigherBetter();
        var actualValue = doubleFormattedDouble(mod.actualValue());
        var step = doubleFormattedDouble(mod.step());
        var highestValue = doubleFormattedDouble(mod.highestValue());
        var lowestValue = doubleFormattedDouble(mod.lowestValue());
        var correctAdjustment = higherBetter ? actualValue + step : actualValue - step;

        var valueWithinRange = higherBetter && actualValue < highestValue ? correctAdjustment : !higherBetter && actualValue > lowestValue ? correctAdjustment : actualValue;
        var abilityModifier = new AbilityData.AbilityModifiers(valueWithinRange, highestValue, lowestValue, step, valueWithinRange, higherBetter);

        properties.replace(name, abilityModifier);

        var holders = new AbilityHolder(abilityName, new AbilityData(properties));

        data.updateAbility(holders);
        sendToServer(new AbilityHolderC2SP(holders));
        return holders;
    }

}
