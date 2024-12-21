package org.jahdoo.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.AbstractEntityProperty;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.ProjectileProperties;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

public class GenericProjectile extends ProjectileProperties implements IEntityProperties {

    private String projectileSelectionIndex;
    private DefaultEntityBehaviour getProjectile;
    private WandAbilityHolder wandAbilityHolder;
    private AbstractElement getElement;
    private String abilityId;
    public double maxDistance;
    public Vec3 blockEntityPos;

    public GenericProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public GenericProjectile(
        Player player,
        double offset,
        String projectileSelectionIndex,
        String abilityId
    ) {
        super(EntitiesRegister.GENERIC_PROJECTILE.get(), player.level());
        this.setProjectileWithOffsets(this, player, offset, 1);
        this.reapplyPosition();
        this.setOwner(player);
        this.wandAbilityHolder = player.getMainHandItem().get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
        this.projectileSelectionIndex = projectileSelectionIndex;
        this.abilityId = abilityId;
        this.getProjectile = EntityPropertyRegister.getProperty(projectileSelectionIndex);
        this.getProjectile.getGenericProjectile(this);
    }

    public GenericProjectile(
        Player player,
        double offset,
        String projectileSelectionIndex,
        String abilityId,
        AbstractElement element
    ) {
        super(EntitiesRegister.GENERIC_PROJECTILE.get(), player.level());
        this.setProjectileWithOffsets(this, player, offset, 1);
        this.reapplyPosition();
        this.setOwner(player);
        this.wandAbilityHolder = player.getMainHandItem().get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
        this.projectileSelectionIndex = projectileSelectionIndex;
        this.abilityId = abilityId;
        this.getProjectile = EntityPropertyRegister.getProperty(projectileSelectionIndex);
        this.getProjectile.getGenericProjectile(this);
        this.getElement = element;
    }

    public GenericProjectile(
        WandAbilityHolder wandAbilityHolder,
        Vec3 direction,
        Level level,
        String projectileSelectionIndex,
        String abilityId
    ) {
        super(EntitiesRegister.GENERIC_PROJECTILE.get(), level);
        this.moveTo(direction.x, direction.y, direction.z, 0, 0);
        this.blockEntityPos = direction;
        this.reapplyPosition();
        this.wandAbilityHolder = wandAbilityHolder;
        this.projectileSelectionIndex = projectileSelectionIndex;
        this.abilityId = abilityId;
        this.getProjectile = EntityPropertyRegister.getProperty(projectileSelectionIndex);
        this.getProjectile.getGenericProjectile(this);
    }

    public GenericProjectile(
        Player player,
        double offset,
        String projectileSelectionIndex,
        WandAbilityHolder wandAbilityHolder,
        double distance,
        String abilityId,
        AbstractElement element
    ) {
        super(EntitiesRegister.GENERIC_PROJECTILE.get(), player.level());
        this.setProjectileWithOffsets(this, player, offset, distance);
        this.reapplyPosition();
        this.setOwner(player);
        this.wandAbilityHolder = wandAbilityHolder;
        this.projectileSelectionIndex = projectileSelectionIndex;
        this.abilityId = abilityId;
        this.getProjectile = EntityPropertyRegister.getProperty(projectileSelectionIndex);
        this.getProjectile.getGenericProjectile(this);
        this.getElement = element;
    }

    public GenericProjectile(
        Entity owner,
        double spawnX, double spawnY, double spawnZ,
        String projectileSelectionIndex,
        WandAbilityHolder wandAbilityHolder,
        AbstractElement abstractElement,
        String abilityId
    ) {
        super(EntitiesRegister.GENERIC_PROJECTILE.get(), owner.level());
        this.moveTo(spawnX, spawnY, spawnZ, this.getYRot(), this.getXRot());
        this.reapplyPosition();
        this.setOwner(owner);
        this.wandAbilityHolder = wandAbilityHolder;
        this.getElement = abstractElement;
        this.abilityId = abilityId;
        this.projectileSelectionIndex = projectileSelectionIndex;
        this.getProjectile = EntityPropertyRegister.getProperty(projectileSelectionIndex);
        this.getProjectile.getGenericProjectile(this);
    }

    public GenericProjectile(
        Entity owner,
        double spawnX, double spawnY, double spawnZ,
        String projectileSelectionIndex,
        WandAbilityHolder wandAbilityHolder,
        String abilityId,
        AbstractElement element
    ) {
        super(EntitiesRegister.GENERIC_PROJECTILE.get(), owner.level());
        this.moveTo(spawnX, spawnY, spawnZ, this.getYRot(), this.getXRot());
        this.reapplyPosition();
        this.setOwner(owner);
        this.wandAbilityHolder = wandAbilityHolder;
        this.abilityId = abilityId;
        this.projectileSelectionIndex = projectileSelectionIndex;
        this.getProjectile = EntityPropertyRegister.getProperty(projectileSelectionIndex);
        this.getProjectile.getGenericProjectile(this);
        this.getElement = element;
    }

    public WandAbilityHolder wandAbilityHolder(){
        return this.wandAbilityHolder;
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        var entity = entityHitResult.getEntity();
        if(!(entity instanceof LivingEntity livingEntity)) return;
        if(!DefaultEntityBehaviour.canDamageEntity(livingEntity, (LivingEntity) this.getOwner())) return;
        if(this.getProjectile != null) this.getProjectile.onEntityHit(livingEntity);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        if(this.getProjectile != null){
            this.getProjectile.onBlockBlockHit(blockHitResult);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(getProjectile != null){
            this.getProjectile.onTickMethod();
            this.getProjectile.discardCondition();
        }
    }

    public void setMaxDistance(double maxDistance){
        this.maxDistance = maxDistance;
    }

    @Override
    public float getPickRadius() {
        return 0;
    }

    @Override
    public AbstractElement getElementType() {
        return this.getElement;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putString("projectileIndex", this.projectileSelectionIndex);
        pCompound.putString("abilityId", this.abilityId);
        DefaultEntityBehaviour.writeTag(this.wandAbilityHolder, this.abilityId, pCompound);
        if(this.getElement != null) pCompound.putInt("elementId", this.getElement.getTypeId());
        if(this.getProjectile != null) getProjectile.addAdditionalDetails(pCompound);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.projectileSelectionIndex = pCompound.getString("projectileIndex");
        this.abilityId = pCompound.getString("abilityId");
        this.wandAbilityHolder = DefaultEntityBehaviour.readTag(pCompound, abilityId);

        if(this.getElement == null && pCompound.getInt("elementId") > 0) {
            this.getElement = ElementRegistry.getElementByTypeId(pCompound.getInt("elementId")).getFirst();
        }

        AbstractEntityProperty abstractProjectileProperty = EntityPropertyRegister.REGISTRY.get(
            ModHelpers.res(pCompound.getString("projectileIndex"))
        );

        if(abstractProjectileProperty != null) {
            this.getProjectile = abstractProjectileProperty.getEntityProperty();
            this.getProjectile.readCompoundTag(pCompound);
            this.getProjectile.getGenericProjectile(this);
        }
    }

    @Override
    public WandAbilityHolder getwandabilityholder() {
        return this.wandAbilityHolder;
    }
}
