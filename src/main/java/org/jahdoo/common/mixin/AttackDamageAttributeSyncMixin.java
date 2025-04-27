package org.jahdoo.common.mixin;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Attributes.class)
public abstract class AttackDamageAttributeSyncMixin {


    @Redirect(
        method = "<clinit>",
        at = @At(
            value = "NEW",
            target = "(Ljava/lang/String;DDD)Lnet/minecraft/world/entity/ai/attributes/RangedAttribute;"
        )
    )
    private static RangedAttribute modifyAttributes(
        String description,
        double defaultValue,
        double min,
        double max
    ) {
        RangedAttribute attribute = new RangedAttribute(description, defaultValue, min, max);

        attribute = switch (description) {
            case "attribute.name.generic.attack_damage" -> {
                System.out.println("IM HERE: DAMAGE");
                yield new RangedAttribute(description, defaultValue, min, 2048.0);
            }
            case "attribute.name.generic.armor" -> {
                System.out.println("IM HERE: ARMOR");
                yield new RangedAttribute(description, defaultValue, min, 500.0);
            }
            case "attribute.name.generic.armor_toughness" -> {
                System.out.println("IM HERE: TOUGHNESS");
                yield new RangedAttribute(description, defaultValue, min, 300.0);

                // Add more cases if needed
            }
            default -> attribute;
        };

        System.out.println(attribute);
        attribute = (RangedAttribute) attribute.setSyncable(true);
        return attribute;
    }

}