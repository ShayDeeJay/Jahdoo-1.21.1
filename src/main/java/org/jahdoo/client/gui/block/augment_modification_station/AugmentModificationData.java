package org.jahdoo.client.gui.block.augment_modification_station;

import net.minecraft.core.component.DataComponents;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.block.AbstractTankUser;
import org.jahdoo.block.augment_modification_station.AugmentModificationEntity;
import org.jahdoo.components.AbilityHolder;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.networking.packet.client2server.SyncComponentBlockC2S;
import org.jahdoo.registers.ElementRegistry;

import java.util.HashMap;
import java.util.function.Consumer;

public class AugmentModificationData {
    public static void updateAugmentConfig(String e, AbilityHolder.AbilityModifiers v, double i, String abilityName, WandAbilityHolder holder, Consumer<WandAbilityHolder> holderExe, AbstractTankUser user) {
        var newWandHolder = new WandAbilityHolder(new HashMap<>(holder.abilityProperties()));
        var newHolder = new AbilityHolder(new HashMap<>(holder.abilityProperties().get(abilityName).abilityProperties()));
        var correctAdjustment = v.isHigherBetter() ? v.actualValue() + v.step() : v.actualValue() - v.step();
        var valueWithinRange = v.isHigherBetter() && v.actualValue() < v.highestValue() ? correctAdjustment : !v.isHigherBetter() && v.actualValue() > v.lowestValue() ? correctAdjustment : v.actualValue();
        var abilityModifier = new AbilityHolder.AbilityModifiers(valueWithinRange, v.highestValue(), v.lowestValue(), v.step(), i, v.isHigherBetter());
        newHolder.abilityProperties().put(e, abilityModifier);
        newWandHolder.abilityProperties().put(abilityName, newHolder);
        PacketDistributor.sendToServer(new SyncComponentBlockC2S(newWandHolder, user.getBlockPos()));
        holderExe.accept(newWandHolder);
    }

    public static AbstractElement getAbstractElement(AugmentModificationEntity entity) {
        int value = entity.getInteractionSlot().get(DataComponents.CUSTOM_MODEL_DATA).value();
        return ElementRegistry.getElementByTypeId(value).getFirst();
    }
}
