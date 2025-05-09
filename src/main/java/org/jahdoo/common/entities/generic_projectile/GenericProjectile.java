package org.jahdoo.common.entities.generic_projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.ability.AbstractEntityProperty;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.ability.ProjectileProperties;
import org.jahdoo.ascension.attachments.CasterData;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.power_up_station.PowerUpStationEntity;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.entities.IEntityProperties;
import org.jahdoo.common.registers.EntityReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.EntityDataReg;
import org.jetbrains.annotations.NotNull;

import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.ascension.utils.PositionFinders.getOuterRingOfRadiusRandom;
import static org.jahdoo.common.particle.ParticleHandlers.*;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE;

public class GenericProjectile extends ProjectileProperties implements IEntityProperties {

    public static final String POWER_UP_KEY = "power_up_value";
    private String projectileSelectionIndex;
    private DefaultEntityBehaviour getProjectile;
    private AbilityHolder abilityHolder;
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
        setProjectileWithOffsets(this, player, offset, 1);
        this.reapplyPosition();
        this.setOwner(player);
        this.abilityHolder = CasterData.entityHolderWithSelected(player);
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
        setProjectileWithOffsets(this, player, offset, 1);
        this.reapplyPosition();
        this.setOwner(player);
        this.abilityHolder = CasterData.entityHolder(player, abilityId);
        this.projectileSelectionIndex = index;
        this.abilityId = abilityId;
        this.getProjectile = EntityDataReg.getProperty(index);
        this.getProjectile.getGenericProjectile(this);
        this.getElement = element;
    }

    public GenericProjectile(
        AbilityHolder holder,
        Vec3 direction,
        Level level,
        String index,
        String abilityId
    ) {
        super(EntityReg.GENERIC_PROJECTILE.get(), level);
        this.moveTo(direction.x, direction.y, direction.z, 0, 0);
        this.reapplyPosition();
        this.blockEntityPos = direction;
        this.abilityHolder = holder;
        this.projectileSelectionIndex = index;
        this.abilityId = abilityId;
        this.getProjectile = EntityDataReg.getProperty(index);
        this.getProjectile.getGenericProjectile(this);
    }

    public GenericProjectile(
        Vec3 direction,
        Level level
    ) {
        super(EntityReg.GENERIC_PROJECTILE.get(), level);
        this.moveTo(direction.x, direction.y, direction.z, 0, 0);
        this.reapplyPosition();
        this.blockEntityPos = direction;
    }


    public GenericProjectile(
        Entity owner,
        double spawnX,
        double spawnY,
        double spawnZ,
        String index,
        AbilityHolder wandAbilityHolder,
        AbstractElement abstractElement,
        String abilityId
    ) {
        super(EntityReg.GENERIC_PROJECTILE.get(), owner.level());
        this.moveTo(spawnX, spawnY, spawnZ, this.getYRot(), this.getXRot());
        this.reapplyPosition();
        this.setOwner(owner);
        this.abilityHolder = wandAbilityHolder;
        this.getElement = abstractElement;
        this.abilityId = abilityId;
        this.projectileSelectionIndex = index;
        this.getProjectile = EntityDataReg.getProperty(index);
        this.getProjectile.getGenericProjectile(this);
    }

    @Override
    public AbilityHolder getAbilityHolder() {
        return this.abilityHolder;
    }

    public void setMaxDistance(double maxDistance){
        this.maxDistance = maxDistance;
    }

    public String getAbilityId(){
        return abilityId;
    }

    @Override
    public float getPickRadius() {
        return 0;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public AbstractElement getElementType() {
        return this.getElement;
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        var pos = blockHitResult.getBlockPos();

        if(this.getProjectile != null){
            this.getProjectile.onBlockBlockHit(blockHitResult);
        } else {
            if(level() instanceof ServerLevel){
                if (this.level().getBlockEntity(pos) instanceof PowerUpStationEntity entity) {
                    var item = entity.inputItemHandler.getStackInSlot(0);
                    if(item.isEmpty() || !CoreData.isFull(item)){
                        var powerUpValue = this.getPersistentData().getInt(POWER_UP_KEY);
                        CoreData.increment(item, powerUpValue);
                        var filled = CoreData.getFilled(item);
                        var needed = CoreData.getRequired(item);

                        var percent = (float) filled / needed;
                        percent = (float) Math.min(1.0, Math.max(0.0, percent));
                        var volume = (float) (1.0 + percent);

                        entity.updateBlock();
                        getOuterRingOfRadiusRandom(pos.getCenter().subtract(0, 0.5, 0), 0.6, 20, this::particleSetter);
                        Helpers.getSoundWithPositionV(this.level(), pos.getCenter(), SoundReg.VITALITY_ABILITY.get(), 1, 1.8F);
                        Helpers.getSoundWithPositionV(this.level(), pos.getCenter(), SoundReg.LOOTBOX_OPEN.get(), 1, volume);
                    }
                    this.discard();
                }
            }
        }
    }

    private void particleSetter(Vec3 positions) {
        var primary = ColourStore.RATING_4_YELLOW;
        sendParticles(
            level(), getNonBakedParticles(primary, primary, 20, 2F), positions.offsetRandom(RandomSource.create(), 0.2f),
            0, 0, Random.nextDouble(0.02,0.2),0,1
        );
    }

    @Override
    public void tick() {
        super.tick();
        if(getProjectile != null){
            this.getProjectile.onTickMethod();
            this.getProjectile.discardCondition();
        } else {
            if(this.tickCount > 1){
                var primary = ColourStore.RATING_4_YELLOW;
                playParticles3(genericParticle(SOFT_PARTICLE, 2, 1F, primary, primary), this, 10, 0);
            }
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
        if(projectileSelectionIndex != null) tag.putString("projectileIndex", this.projectileSelectionIndex);
        if(abilityId != null) tag.putString("abilityId", this.abilityId);
        AbilityHolder.writeTag(this.abilityHolder, tag);
        if(this.getElement != null) tag.putInt("elementId", this.getElement.id());
        if(this.getProjectile != null) getProjectile.addAdditionalDetails(tag);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.projectileSelectionIndex = tag.getString("projectileIndex");
        this.abilityId = tag.getString("abilityId");
        this.abilityHolder = AbilityHolder.readTag(tag, abilityId);

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
