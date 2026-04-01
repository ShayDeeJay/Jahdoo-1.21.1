package org.jahdoo.common.items.caster_item.elemental_wand;

import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.items.caster_item.CasterItem;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

import static org.jahdoo.common.items.caster_item.CasterItemHelper.wandItemRenderer;
import static org.jahdoo.common.items.caster_item.ItemAnimations.*;

public class ElementalWand extends CasterItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public String location;
    public String type = "";

    public static final String UNIQUE_WAND= "unique";
    public static final String ETERNAL_WAND= "eternal";
    public static final String COMMON_WAND= "common";

    public ElementalWand(String location, String type) {
        super(wandProperties());
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
        this.location = location;
        if(type != null) this.type = type;
    }

    public static @NotNull String isBasic(String type) {
        if(type.isEmpty()) return type;

        return type + "_";
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        wandItemRenderer(consumer);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, 0, state -> state.setAndContinue(IDLE_ANIMATION)));
        controllers.add(new AnimationController<>(this, "Activation", 0, state -> PlayState.CONTINUE)
            .triggerableAnim(SINGLE_CAST_ID, SINGLE_CAST)
            .triggerableAnim(CANT_CAST_ID, CANT_CAST)
            .triggerableAnim(HOLD_CAST_ID, HOLD_CAST)
            .triggerableAnim(ROTATION_CAST_ID, ROTATION_CAST)
        );
    }

    @Override
    public String descriptionId(ItemStack stack) {
        return "description.item.jahdoo.elemental_wands";
    }
}
