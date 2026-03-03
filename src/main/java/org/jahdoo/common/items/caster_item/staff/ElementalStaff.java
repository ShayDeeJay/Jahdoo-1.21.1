package org.jahdoo.common.items.caster_item.staff;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jahdoo.common.items.caster_item.BaseMagicWeapon;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class ElementalStaff extends BaseMagicWeapon implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public String location;

    public ElementalStaff() {
        super(elementalStaffProperties());
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(
            new GeoRenderProvider() {
                private ElementalStaffItemRenderer renderer;
                @Override
                public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                    if (this.renderer == null) this.renderer = new ElementalStaffItemRenderer();
                    return this.renderer;
                }
            }
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public static Properties elementalStaffProperties(){
        var properties = new Properties();
        int attackDamage = 1;
        float attackSpeed = -2.6F;
        var modifier = new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage + Tiers.NETHERITE.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE);
        var modifier1 = new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE);
        var modifier2 = new AttributeModifier(JahdooHelpers.res("reach"), 1, AttributeModifier.Operation.ADD_VALUE);
        var mainHand = EquipmentSlotGroup.MAINHAND;
        var attributes = ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE, modifier, mainHand)
            .add(Attributes.ATTACK_SPEED, modifier1, mainHand)
            .add(Attributes.ENTITY_INTERACTION_RANGE, modifier2, mainHand)
            .build();
        properties.attributes(attributes);
        return properties;
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
//        controllers.add(new AnimationController<>(this, 0, state -> state.setAndContinue(IDLE_ANIMATION)));
//        controllers.add(new AnimationController<>(this, "Activation", 0, state -> PlayState.CONTINUE)
//            .triggerableAnim(SINGLE_CAST_ID, SINGLE_CAST)
//            .triggerableAnim(CANT_CAST_ID, CANT_CAST)
//            .triggerableAnim(HOLD_CAST_ID, HOLD_CAST)
//            .triggerableAnim(ROTATION_CAST_ID, ROTATION_CAST)
//        );
    }

}
