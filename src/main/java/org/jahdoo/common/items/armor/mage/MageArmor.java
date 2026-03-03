package org.jahdoo.common.items.armor.mage;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.items.armor.BaseArmor;
import org.jahdoo.common.registers.ArmorMaterialReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shaydee.shaydeeapi.helpers.TextHelpers;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class MageArmor extends BaseArmor implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MageArmor(Type type) {
        super(ArmorMaterialReg.MAGE, type, getComponent());
    }

    private static @NotNull Properties getComponent() {
        return new Properties().durability(28);
    }

    @Override
    public Component getName(ItemStack stack) {
        return TextHelpers.withStyleComponent(super.getName(stack).getString(), ElementReg.vitality().textColourB());
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller",0, state -> PlayState.STOP));
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(
              new GeoRenderProvider() {
                  private MageArmorRenderer renderer;

                  @Override
                  public @Nullable <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
                      if (this.renderer == null)
                          this.renderer = new MageArmorRenderer();

                      return this.renderer;
                  }
              }
        );
    }
}
