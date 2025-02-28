package org.jahdoo.common.entities.generic_projectile;

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
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.ability.AbstractEntityProperty;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.ability.ProjectileProperties;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.entities.IEntityProperties;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.EntityDataReg;
import org.jahdoo.common.registers.EntityReg;
import org.jahdoo.ascension.utils.Helpers;
import org.jetbrains.annotations.NotNull;

public class GenericProjectile extends ProjectileProperties implements IEntityProperties {

    private String projectileSelectionIndex;
    private DefaultEntityBehaviour getProjectile;
    private WandAbilityHolder wandAbilityHolder;
    private AbstractElement getElement;
    private String abilityId;
    public double maxDistance;
    public Vec3 blockEntityPos;

    public GenericProjectile(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    public GenericProjectile(
        Player player,
        double offset,
        String projectileSelectionIndex,
        String abilityId
    ) {
        super(EntityReg.GENERIC_PROJECTILE.get(), player.level());
        this.setProjectileWithOffsets(this, player, offset, 1);
        this.reapplyPosition();
        this.setOwner(player);
        this.wandAbilityHolder = WandAbilityHolder.getHolderFromWand(player);
        this.projectileSelectionIndex = projectileSelectionIndex;
        this.abilityId = abilityId;
        this.getProjectile = EntityDataReg.getProperty(projectileSelectionIndex);
        this.getProjectile.getGenericProjectile(this);
    }

    public GenericProjectile(
        Player player,
        double offset,
        String index,
        String abilityId,
        AbstractElement element
    ) {
        super(EntityReg.GENERIC_PROJECTILE.get(), player.level());
        this.setProjectileWithOffsets(this, player, offset, 1);
        this.reapplyPosition();
        this.setOwner(player);
        this.wandAbilityHolder = WandAbilityHolder.getHolderFromWand(player);
        this.projectileSelectionIndex = index;
        this.abilityId = abilityId;
        this.getProjectile = EntityDataReg.getProperty(index);
        this.getProjectile.getGenericProjectile(this);
        this.getElement = element;
    }

    public GenericProjectile(
        WandAbilityHolder wandAbilityHolder,
        Vec3 direction,
        Level level,
        String index,
        String abilityId
    ) {
        super(EntityReg.GENERIC_PROJECTILE.get(), level);
        this.moveTo(direction.x, direction.y, direction.z, 0, 0);
        this.reapplyPosition();
        this.blockEntityPos = direction;
        this.wandAbilityHolder = wandAbilityHolder;
        this.projectileSelectionIndex = index;
        this.abilityId = abilityId;
        this.getProjectile = EntityDataReg.getProperty(index);
        this.getProjectile.getGenericProjectile(this);
    }

    public GenericProjectile(
        Entity owner,
        double spawnX,
        double spawnY,
        double spawnZ,
        String index,
        WandAbilityHolder wandAbilityHolder,
        AbstractElement abstractElement,
        String abilityId
    ) {
        super(EntityReg.GENERIC_PROJECTILE.get(), owner.level());
        this.moveTo(spawnX, spawnY, spawnZ, this.getYRot(), this.getXRot());
        this.reapplyPosition();
        this.setOwner(owner);
        this.wandAbilityHolder = wandAbilityHolder;
        this.getElement = abstractElement;
        this.abilityId = abilityId;
        this.projectileSelectionIndex = index;
        this.getProjectile = EntityDataReg.getProperty(index);
        this.getProjectile.getGenericProjectile(this);
    }

    public WandAbilityHolder wandAbilityHolder(){
        return this.wandAbilityHolder;
    }

    @Override
    public WandAbilityHolder getwandabilityholder() {
        return this.wandAbilityHolder;
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

    @Override
    protected void onHitEntity(@NotNull EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        var entity = entityHitResult.getEntity();
        if(!(entity instanceof LivingEntity livingEntity)) return;
        if(!DefaultEntityBehaviour.canDamageEntity(livingEntity, (LivingEntity) this.getOwner())) return;
        if(this.getProjectile != null) this.getProjectile.onEntityHit(livingEntity);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("projectileIndex", this.projectileSelectionIndex);
        tag.putString("abilityId", this.abilityId);
        DefaultEntityBehaviour.writeTag(this.wandAbilityHolder, this.abilityId, tag);
        if(this.getElement != null) tag.putInt("elementId", this.getElement.id());
        if(this.getProjectile != null) getProjectile.addAdditionalDetails(tag);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.projectileSelectionIndex = tag.getString("projectileIndex");
        this.abilityId = tag.getString("abilityId");
        this.wandAbilityHolder = DefaultEntityBehaviour.readTag(tag, abilityId);

        if(this.getElement == null && tag.getInt("elementId") > 0) {
            ElementReg.fromId(tag.getInt("elementId")).ifPresent(
                element -> this.getElement = element
            );
        }

        AbstractEntityProperty abstractProjectileProperty = EntityDataReg.REGISTRY.get(
            Helpers.res(tag.getString("projectileIndex"))
        );

        if(abstractProjectileProperty != null) {
            this.getProjectile = abstractProjectileProperty.getEntityProperty();
            this.getProjectile.readCompoundTag(tag);
            this.getProjectile.getGenericProjectile(this);
        }
    }
}
