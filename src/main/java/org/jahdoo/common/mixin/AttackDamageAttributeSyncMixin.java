package org.jahdoo.common.mixin;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Attributes.class)
public abstract class AttackDamageAttributeSyncMixin {

    @Redirect(method = "<clinit>", at = @At(value = "NEW", target = "(Ljava/lang/String;DDD)Lnet/minecraft/world/entity/ai/attributes/RangedAttribute;"))
    private static RangedAttribute change(String description, double pdefaultvalue, double min, double max) {
        var attribute = new RangedAttribute(description, pdefaultvalue, min, max);
        var isAttackDamage = "attribute.name.generic.attack_damage".equals(description);

        if (isAttackDamage) attribute = (RangedAttribute) attribute.setSyncable(true);
        return attribute;
    }
}