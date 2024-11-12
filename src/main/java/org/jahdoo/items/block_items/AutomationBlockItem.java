package org.jahdoo.items.block_items;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import org.jahdoo.client.block_renderer.AutomationBlockRenderer;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.RenderUtil;
import java.util.function.Consumer;


public class AutomationBlockItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

     public AutomationBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
         SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(
            new GeoRenderProvider() {
                private AutomationBlockRenderer renderer;

                @Override
                public @NotNull BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                    if (this.renderer == null) this.renderer = new AutomationBlockRenderer();
                    return this.renderer;
                }

            }
        );
        GeoItem.super.createGeoRenderer(consumer);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    @Override
    public double getTick(Object itemStack) {
        return RenderUtil.getCurrentTick();
    }
}
