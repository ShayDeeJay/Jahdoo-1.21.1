package org.jahdoo.common.items.block_items;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import org.jahdoo.common.block.infuser.InfuserBlockRenderer;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.RenderUtil;

import java.util.function.Consumer;

public class InfuserBlockItem extends BlockItem implements GeoItem {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public InfuserBlockItem(Block pBlock) {
        super(pBlock, new Properties());
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object itemStack) {
        return RenderUtil.getCurrentTick();
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(
              new GeoRenderProvider() {
                  private InfuserBlockRenderer renderer;

                  @Override
                  public @NotNull BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                      if (this.renderer == null) this.renderer = new InfuserBlockRenderer();
                      return this.renderer;
                  }

              }
        );
        GeoItem.super.createGeoRenderer(consumer);
    }

}
