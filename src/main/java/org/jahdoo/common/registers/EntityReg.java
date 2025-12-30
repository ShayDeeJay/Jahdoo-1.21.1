package org.jahdoo.common.registers;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.entities.custom_entities.CustomSkeleton;
import org.jahdoo.common.entities.custom_entities.CustomVillager;
import org.jahdoo.common.entities.custom_entities.CustomZombie;
import org.jahdoo.common.entities.ancient_golem.AncientGolem;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.entities.burning_skull.BurningSkull;
import org.jahdoo.common.entities.decoy.Decoy;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.entities.eternal_wizard.EternalWizard;
import org.jahdoo.common.entities.explosive_barrel.ExplosiveBarrel;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.entities.ice_spear.IceSpear;
import org.jahdoo.common.entities.inferno_creeper.InfernoCreeper;
import org.jahdoo.common.entities.safe.Safe;
import org.jahdoo.common.entities.void_spider.VoidSpider;

import static net.minecraft.world.entity.EntityType.*;
import static net.minecraft.world.entity.MobCategory.*;

public class EntityReg {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(Registries.ENTITY_TYPE, JahdooMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<ElementProjectile>> FROST_ELEMENT_PROJECTILE =
        registerElementProjectile("frost_element_projectile");

    public static final DeferredHolder<EntityType<?>, EntityType<ElementProjectile>> INFERNO_ELEMENT_PROJECTILE =
        registerElementProjectile("inferno_element_projectile");

    public static final DeferredHolder<EntityType<?>, EntityType<ElementProjectile>> MYSTIC_ELEMENT_PROJECTILE =
        registerElementProjectile("mystic_element_projectile");

    public static final DeferredHolder<EntityType<?>, EntityType<ElementProjectile>> VITALITY_ELEMENT_PROJECTILE =
        registerElementProjectile("vitality_element_projectile");

    public static final DeferredHolder<EntityType<?>, EntityType<GenericProjectile>> GENERIC_PROJECTILE =
        regEntity("generic_projectile", Builder.<GenericProjectile>of(GenericProjectile::new, MISC)
            .sized(0.2f, 0.2f)
            .updateInterval(1)
        );

    public static final DeferredHolder<EntityType<?>, EntityType<EternalWizard>> ETERNAL_WIZARD =
        regEntity("eternal_wizard", Builder.<EternalWizard>of(EternalWizard::new, MONSTER)
            .sized(0.6F, 1.99F)
            .clientTrackingRange(10)
        );

    public static final DeferredHolder<EntityType<?>, EntityType<Decoy>> DECOY =
        regEntity("decoy", Builder.<Decoy>of(Decoy::new, MONSTER)
            .sized(0.6F, 1.95F)
            .clientTrackingRange(10)
        );

    public static final DeferredHolder<EntityType<?>, EntityType<AncientGolem>> ANCIENT_GOLEM =
        regEntity("ancient_golem", Builder.<AncientGolem>of(AncientGolem::new, MONSTER)
            .sized(1.4F, 2.7F)
            .clientTrackingRange(10)
        );

    public static final DeferredHolder<EntityType<?>, EntityType<CustomZombie>> CUSTOM_ZOMBIE =
        regEntity("jahdoo_zombie", Builder.<CustomZombie>of(CustomZombie::new, MONSTER)
            .sized(0.6F, 1.95F)
            .clientTrackingRange(10)
        );

    public static final DeferredHolder<EntityType<?>, EntityType<BurningSkull>> FLAMING_SKULL =
        regEntity("flaming_skull", Builder.<BurningSkull>of(BurningSkull::new, MISC)
            .sized(0.8f, 0.8f)
            .updateInterval(1)
        );

    public static final DeferredHolder<EntityType<?>, EntityType<AoeCloud>> CUSTOM_AOE_CLOUD =
        regEntity("generic_aoe", Builder.<AoeCloud>of(AoeCloud::new, MISC)
            .sized(6.0F, 0.5F)
            .updateInterval(1)
            .clientTrackingRange(10)
        );

    public static final DeferredHolder<EntityType<?>, EntityType<VoidSpider>> VOID_SPIDER =
        regEntity("void_spider", Builder.<VoidSpider>of(VoidSpider::new, MONSTER)
            .sized(1.4F, 0.9F)
            .passengerAttachments(0.765F)
            .clientTrackingRange(8)
        );

    public static final DeferredHolder<EntityType<?>, EntityType<InfernoCreeper>> INFERNO_CREEPER =
        regEntity("inferno_creeper", Builder.<InfernoCreeper>of(InfernoCreeper::new, MONSTER)
            .sized(0.6F, 1.7F)
            .clientTrackingRange(8)
        );

    public static final DeferredHolder<EntityType<?>, EntityType<VoidSpider>> VOID_SPIDER_SPAWN =
        regEntity("void_spider_spawn", Builder.<VoidSpider>of(VoidSpider::new, MONSTER)
            .sized(1.4F, 0.9F)
            .passengerAttachments(0.765F)
            .clientTrackingRange(8)
        );

    public static final DeferredHolder<EntityType<?>, EntityType<CustomVillager>> CUSTOM_VILLAGER =
        regEntity("jahdoo_villager", Builder.<CustomVillager>of(CustomVillager::new, MONSTER)
            .sized(0.6F, 1.95F)
            .eyeHeight(1.62F)
            .clientTrackingRange(10)
        );

    public static final DeferredHolder<EntityType<?>, EntityType<CustomSkeleton>> CUSTOM_SKELETON =
        regEntity("jahdoo_skeleton", Builder.<CustomSkeleton>of(CustomSkeleton::new, MISC)
            .sized(0.6F, 1.99F)
            .eyeHeight(1.74F)
            .ridingOffset(-0.7F)
            .clientTrackingRange(10)
        );

    public static final DeferredHolder<EntityType<?>, EntityType<IceSpear>> ICE_SPEAR =
        regEntity("ice_spear", Builder.<IceSpear>of(IceSpear::new, MISC)
            .sized(0.5F, 0.5F)
            .eyeHeight(0.13F)
            .clientTrackingRange(10)
        );

    public static DeferredHolder<EntityType<?>, EntityType<ElementProjectile>> registerElementProjectile(String name){
        return regEntity(name, Builder.<ElementProjectile>of(ElementProjectile::new, MISC)
            .sized(0.4f, 0.4f)
            .updateInterval(1)
        );
    }

    public static final DeferredHolder<EntityType<?>, EntityType<Safe>> SAFE =
        regEntity("safe", Builder.<Safe>of(Safe::new, MISC)
            .sized(0.8f, 0.9f)
            .updateInterval(1)
        );

    public static final DeferredHolder<EntityType<?>, EntityType<ExplosiveBarrel>> EXPLOSIVE_BARREL =
        regEntity("explosive_barrel", Builder.<ExplosiveBarrel>of(ExplosiveBarrel::new, MISC)
            .sized(0.8f, 1.2f)
            .updateInterval(1)
        );


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

    public static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> regEntity(String name, EntityType.Builder<T> supplier) {
      return ENTITY_TYPES.register(name, () -> supplier.build(name));
    }
}