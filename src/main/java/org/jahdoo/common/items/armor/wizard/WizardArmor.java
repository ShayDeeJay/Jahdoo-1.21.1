package org.jahdoo.common.items.armor.wizard;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.items.armor.BaseArmor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

import static org.jahdoo.common.items.runes.rune_data.RuneHolder.*;
import static org.jahdoo.common.registers.ArmorMaterialReg.*;
import static org.jahdoo.common.registers.ComponentReg.*;

public class WizardArmor extends BaseArmor implements GeoItem, JahdooItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public WizardArmor(Type type) {
        super(WIZARD, type, new Properties()
              .durability(37)
              .component(RUNE_HOLDER.get(), makeRuneSlots(1, 100))
        );
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack);
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return super.getEquipmentSlot();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller",0, state -> PlayState.STOP));
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(
              new GeoRenderProvider() {
                  private WizardArmorRenderer renderer;

                  @Override
                  public @Nullable <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
                      if (this.renderer == null) this.renderer = new WizardArmorRenderer();
                      return this.renderer;
                  }
              }
        );
    }
}
