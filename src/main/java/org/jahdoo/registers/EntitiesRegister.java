package org.jahdoo.registers;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.Arrow;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.entities.*;
import org.jahdoo.entities.living.*;

public class EntitiesRegister {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, JahdooMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<ElementProjectile>> FROST_ELEMENT_PROJECTILE =
        ENTITY_TYPES.register(
            "frost_element_projectile", () -> EntityType.Builder.<ElementProjectile>of(ElementProjectile::new, MobCategory.MISC)
                .sized(0.4f, 0.4f)
                .updateInterval(1)
                .build("frost_element_projectile")
        );

    public static final DeferredHolder<EntityType<?>, EntityType<ElementProjectile>> INFERNO_ELEMENT_PROJECTILE =
        ENTITY_TYPES.register(
            "inferno_element_projectile", () -> EntityType.Builder.<ElementProjectile>of(ElementProjectile::new, MobCategory.MISC)
                .sized(0.4f, 0.4f)
                .updateInterval(1)
                .build("inferno_element_projectile")
        );

    public static final DeferredHolder<EntityType<?>, EntityType<ElementProjectile>> MYSTIC_ELEMENT_PROJECTILE =
        ENTITY_TYPES.register(
            "mystic_element_projectile", () -> EntityType.Builder.<ElementProjectile>of(ElementProjectile::new, MobCategory.MISC)
                .sized(0.4f, 0.4f)
                .updateInterval(1)
                .build("mystic_element_projectile")
        );

    public static final DeferredHolder<EntityType<?>, EntityType<ElementProjectile>> LIGHTNING_ELEMENT_PROJECTILE =
        ENTITY_TYPES.register(
            "lightning_element_projectile", () -> EntityType.Builder.<ElementProjectile>of(ElementProjectile::new, MobCategory.MISC)
                .sized(0.4f, 0.4f)
                .updateInterval(1)
                .build("lightning_element_projectile")
        );

    public static final DeferredHolder<EntityType<?>, EntityType<ElementProjectile>> VITALITY_ELEMENT_PROJECTILE =
        ENTITY_TYPES.register(
            "vitality_element_projectile", () -> EntityType.Builder.<ElementProjectile>of(ElementProjectile::new, MobCategory.MISC)
                .sized(0.4f, 0.4f)
                .updateInterval(1)
                .build("vitality_element_projectile")
        );

    public static final DeferredHolder<EntityType<?>, EntityType<GenericProjectile>> GENERIC_PROJECTILE =
        ENTITY_TYPES.register(
            "generic_projectile", () -> EntityType.Builder.<GenericProjectile>of(GenericProjectile::new, MobCategory.MISC)
                .sized(0.2f, 0.2f)
                .updateInterval(1)
                .build("generic_projectile")
        );

    public static final DeferredHolder<EntityType<?>, EntityType<AoeCloud>> CUSTOM_AOE_CLOUD =
        ENTITY_TYPES.register(
            "generic_aoe", () -> EntityType.Builder.<AoeCloud>of(AoeCloud::new, MobCategory.MISC)
                .sized(6.0F, 0.5F)
                .updateInterval(1)
                .clientTrackingRange(10)
                .build("generic_aoe")
        );


    public static final DeferredHolder<EntityType<?>, EntityType<EternalWizard>> ETERNAL_WIZARD =
        ENTITY_TYPES.register(
            "eternal_wizard", () -> EntityType.Builder.<EternalWizard>of(EternalWizard::new, MobCategory.MONSTER)
                .sized(0.6F, 1.99F)
                .clientTrackingRange(10)
                .build("eternal_wizard")
        );

    public static final DeferredHolder<EntityType<?>, EntityType<Decoy>> DECOY =
        ENTITY_TYPES.register(
            "decoy", () -> EntityType.Builder.<Decoy>of(Decoy::new, MobCategory.MONSTER)
                .sized(0.6F, 1.95F)
                .clientTrackingRange(10)
                .build("decoy")
        );

    public static final DeferredHolder<EntityType<?>, EntityType<AncientGolem>> ANCIENT_GOLEM =
        ENTITY_TYPES.register(
            "ancient_golem", () -> EntityType.Builder.<AncientGolem>of(AncientGolem::new, MobCategory.MONSTER)
                .sized(1.4F, 2.7F)
                .clientTrackingRange(10)
                .build("ancient_golem")
        );

    public static final DeferredHolder<EntityType<?>, EntityType<CustomZombie>> CUSTOM_ZOMBIE =
        ENTITY_TYPES.register(
            "jahdoo_zombie", () -> EntityType.Builder.<CustomZombie>of(CustomZombie::new, MobCategory.MONSTER)
                .sized(0.6F, 1.95F)
                .clientTrackingRange(10)
                .build("zombie")
        );

    public static final DeferredHolder<EntityType<?>, EntityType<CustomSkeleton>> CUSTOM_SKELETON =
        ENTITY_TYPES.register(
            "jahdoo_skeleton", () -> EntityType.Builder.<CustomSkeleton>of(CustomSkeleton::new, MobCategory.MONSTER)
                .sized(0.6F, 1.99F)
                .eyeHeight(1.74F)
                .ridingOffset(-0.7F)
                .clientTrackingRange(10)
                .build("skeleton")
        );

    public static final DeferredHolder<EntityType<?>, EntityType<FlamingSkull>> FLAMING_SKULL =
        ENTITY_TYPES.register(
            "flaming_skull", () -> EntityType.Builder.<FlamingSkull>of(FlamingSkull::new, MobCategory.MISC)
                .sized(0.8f, 0.8f)
                .updateInterval(1)
                .build("flaming_skull")
        );





    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}