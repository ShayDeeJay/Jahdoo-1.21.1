package org.jahdoo.common.items.block_items;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.level.block.Block;
import org.jahdoo.common.block.dissembler.DisassemblerBlockRenderer;
import org.jahdoo.common.items.BaseJahdooBlockItem;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

import java.util.function.Consumer;

public class DisassemblerBlockItem extends BaseJahdooBlockItem implements GeoItem {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public DisassemblerBlockItem(Block pBlock) {
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
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(
              new GeoRenderProvider() {
                  private DisassemblerBlockRenderer renderer;

                  @Override
                  public @NotNull BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                      if (this.renderer == null) this.renderer = new DisassemblerBlockRenderer();
                      return this.renderer;
                  }

              }
        );
        GeoItem.super.createGeoRenderer(consumer);
    }

}
