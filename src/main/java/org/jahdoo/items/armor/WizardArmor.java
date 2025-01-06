package org.jahdoo.items.armor;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jahdoo.client.armor_renderer.WizardArmorRenderer;
import org.jahdoo.components.rune_data.RuneHolder;
import org.jahdoo.event.event_helpers.EventHelpers;
import org.jahdoo.items.JahdooItem;
import org.jahdoo.registers.DataComponentRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class WizardArmor extends ArmorItem implements GeoItem, JahdooItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public WizardArmor(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, getComponent());
    }

    private static @NotNull Properties getComponent() {
        return new Properties()
            .durability(37)
            .component(DataComponentRegistry.RUNE_HOLDER.get(), RuneHolder.makeRuneSlots(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(
            new GeoRenderProvider() {
                private WizardArmorRenderer renderer;

                @Override
                public @Nullable <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
                    if (this.renderer == null)
                        this.renderer = new WizardArmorRenderer();

                    return this.renderer;
                }
            }
        );
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return super.getEquipmentSlot();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
            new AnimationController<>(this, "controller",0, state -> PlayState.STOP)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
