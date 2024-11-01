package org.jahdoo.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.ProjectileProperties;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;

import static org.jahdoo.entities.ProjectileAnimations.*;

public class ElementProjectile extends ProjectileProperties implements GeoEntity {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Integer> ADDITIONAL_PREDICATE = SynchedEntityData.defineId(ElementProjectile.class, EntityDataSerializers.INT);

    private boolean showTrailParticles;
    private boolean setAdditionalRestriction;

    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;

    WandAbilityHolder wandAbilityHolder;
    DefaultEntityBehaviour getProjectile;
    public String selectedAbility;
    boolean isChildObject;
    String abilityId;

    public int predicateType() {
        return this.entityData.get(ADDITIONAL_PREDICATE);
    }

    public void setPredicate(int getSelectedAbility) {
        this.entityData.set(ADDITIONAL_PREDICATE, getSelectedAbility);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(ADDITIONAL_PREDICATE, 0);
    }

    public ElementProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ElementProjectile(
        EntityType<? extends Projectile> entityType,
        LivingEntity owner,
        String selectedAbility,
        double spacing,
        String abilityId
    ) {
        super(entityType, owner.level());
        this.setProjectileWithOffsets(this, owner, spacing, 1);
        this.reapplyPosition();
        this.setOwner(owner);
        this.wandAbilityHolder = owner.getMainHandItem().get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
        this.selectedAbility = selectedAbility;
        this.abilityId = abilityId;
        this.getProjectile = EntityPropertyRegister.getProperty(selectedAbility);
        this.getProjectile.getElementProjectile(this);
    }

    public ElementProjectile(
        EntityType<? extends Projectile> entityType,
        LivingEntity owner,
        String selectedAbility,
        WandAbilityHolder wandAbilityHolder,
        String abilityId
    ) {
        super(entityType, owner.level());
        this.setProjectileWithOffsets(this, owner, 0, 1);
        this.reapplyPosition();
        this.setOwner(owner);
        this.wandAbilityHolder = wandAbilityHolder;
        this.getProjectile =  EntityPropertyRegister.getProperty(selectedAbility);
        this.selectedAbility = selectedAbility;
        this.abilityId = abilityId;
        this.getProjectile.getElementProjectile(this);
    }

    public ElementProjectile(
        EntityType<? extends Projectile> entityType,
        LivingEntity owner,
        double x, double y, double z,
        String selection,
        WandAbilityHolder wandAbilityHolder,
        String abilityId
    ) {
        super(entityType, owner.level());
        this.moveTo(x, y, z, 0, 0);
        this.reapplyPosition();
        this.setOwner(owner);
        this.wandAbilityHolder = wandAbilityHolder;
        this.selectedAbility = selection;
        this.abilityId = abilityId;
        this.getProjectile = EntityPropertyRegister.getProperty(selection);
        this.getProjectile.getElementProjectile(this);
        this.isChildObject = true;
    }

    public DefaultEntityBehaviour getCurrentProjectile(){
        return EntityPropertyRegister.getProperty(selectedAbility);
    }

    public WandAbilityHolder wandAbilityHolder(){
        return this.wandAbilityHolder;
    }

    @Override
    public void lerpTo(double pX, double pY, double pZ, float pYRot, float pXRot, int pSteps) {
        this.lerpX = pX;
        this.lerpY = pY;
        this.lerpZ = pZ;
        this.lerpYRot = pYRot;
        this.lerpXRot = pXRot;
        this.lerpSteps = 5;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if(!(level() instanceof ServerLevel serverLevel)) return;
        if(!(entity instanceof LivingEntity livingEntity) ) return;
        if(entity == this.getOwner() || entity instanceof EternalWizard) return;
        if (getProjectile == null) return;

        ParticleHandlers.particleBurst(serverLevel, entityHitResult.getLocation(), 5, getProjectile.getElementType().getParticleGroup().baked());
        getProjectile.onEntityHit(livingEntity);
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        if(!(level() instanceof ServerLevel serverLevel)) return;
        if (getProjectile == null) return;

        getProjectile.onBlockBlockHit(blockHitResult);
        if (!blockHitResult.isInside()) {
            var splashParticles = new BakedParticleOptions(this.getElementType().getTypeId(),5,2f, false);
            ParticleHandlers.particleBurst(serverLevel, blockHitResult.getLocation(), 10, splashParticles);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if(getProjectile != null){
            getProjectile.onTickMethod();
            getProjectile.discardCondition();
        }

        if(this.level().isClientSide){
            if (this.lerpSteps > 0) {
                double d = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
                double e = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
                double y = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
                double g = Mth.wrapDegrees(this.lerpYRot - (double) this.getYRot());
                this.setYRot(this.getYRot() + (float) g / (float) this.lerpSteps);
                this.setXRot(this.getXRot() + (float) (this.lerpXRot - (double) this.getXRot()) / (float) this.lerpSteps);
                --this.lerpSteps;
                this.setPos(d, e, y);
                this.setRot(this.getYRot(), this.getXRot());
            }
        }

        if(this.showTrailParticles){
            ParticleHandlers.EntityProjectileParticles(this, tickCount, 0.30f, this.getElementType());
        }

        if (this.getOwner() != null && distanceTo(this.getOwner()) > 70f) this.discard();
    }

    public void setShowTrailParticles(boolean setShowTrailParticles){
        this.showTrailParticles = setShowTrailParticles;
    }

    public void setIsChildObject(boolean parentObject){
        this.isChildObject = parentObject;
    }


    public void setAdditionalRestrictionBound(boolean additionalRestrictionBound){
        this.setAdditionalRestriction = additionalRestrictionBound;
    }

    public boolean getAdditionalRestriction(){
        return this.setAdditionalRestriction;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putString("ability", this.selectedAbility);
        pCompound.putString("abilityId", this.abilityId);
        DefaultEntityBehaviour.writeTag(this.wandAbilityHolder, this.abilityId, pCompound);
        getProjectile.addAdditionalDetails(pCompound);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.selectedAbility = pCompound.getString("ability");
        this.abilityId = pCompound.getString("abilityId");
        this.wandAbilityHolder = DefaultEntityBehaviour.readTag(pCompound, this.abilityId);
        if(this.getProjectile == null && !selectedAbility.isEmpty()){
            this.getProjectile = EntityPropertyRegister.REGISTRY
                .get(ModHelpers.modResourceLocation(selectedAbility))
                .getEntityProperty();
            this.getProjectile.readCompoundTag(pCompound);
            this.getProjectile.getElementProjectile(this);
        }
    }

    @Override
    public AbstractElement getElementType() {
        return this.getCurrentProjectile().getElementType();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
            new AnimationController<>(this,
                state -> switch (this.animationType()) {
                    case 1 -> state.setAndContinue(FIREBALL);
                    case 2 -> state.setAndContinue(FIREBALL_EXPLODE);
                    case 3 -> state.setAndContinue(ICE_SPIKES);
                    case 4 -> state.setAndContinue(QUANTUM_EXPANSION);
                    case 5 -> state.setAndContinue(QUANTUM_COMBUSTION);
                    case 6 -> state.setAndContinue(SEMTEX);
                    case 7 -> state.setAndContinue(ORB);
                    case 8 -> {
                        state.setControllerSpeed(0.8f);
                        yield state.setAndContinue(ORB_END);
                    }
                    case 9 -> state.setAndContinue(BOLTZ);
                    default -> state.setAndContinue(IDLE);
                }
            )
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
