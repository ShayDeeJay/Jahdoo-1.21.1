package org.jahdoo.ascension.ability.abilities_combat.ancient_golem;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.PositionFinders;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.ancient_golem.AncientGolem;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.ItemReg;

import java.util.UUID;

import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.ascension.utils.PositionFinders.getOuterRingOfRadiusList;
import static org.jahdoo.common.particle.ParticleHandlers.*;
import static org.jahdoo.common.registers.AttributeReg.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.common.registers.AttributeReg.VITALITY_MAGIC_DAMAGE_MULTIPLIER;

public class SummonAncientGolem extends DefaultEntityBehaviour {

    private static final ResourceLocation abilityId = Helpers.res("summon_ancient_golem_property");
    private AncientGolem ancientGolem;
    private UUID uuid;
    private double increaseRate = 0.5;
    private double effectDuration;
    private double effectStrength;
    private double effectChance;
    private double lifeTime;
    private double height;
    private double damage;
    private int position;

    @Override
    public void getAoeCloud(AoeCloud aoeCloud) {
        super.getAoeCloud(aoeCloud);
        var player = this.cloud.getOwner();
        var damage = this.getTag(DAMAGE);
        this.effectDuration = getTag(EFFECT_DURATION);
        this.effectStrength = getTag(EFFECT_STRENGTH);
        this.effectChance = getTag(EFFECT_CHANCE);
        this.lifeTime = getTag(LIFETIME);
        if(player != null){
            this.damage = Helpers.attributeModifierCalculator(
                player, (float) damage, true,
                MAGIC_DAMAGE_MULTIPLIER,
                VITALITY_MAGIC_DAMAGE_MULTIPLIER
            );
        }
    }


    @Override
    public AbilityHolder getAbilityHolder() {
        return this.cloud.getAbilityHolder();
    }

    @Override
    public String abilityId() {
        return SummonAncientGolemAbility.abilityId.getPath().intern();
    }

    @Override
    public AbstractElement getElementType() {
        return ElementReg.vitality();
    }

    private Level level(){
        return this.cloud.level();
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new SummonAncientGolem();
    }

    private void setSpawnParticles(Level level){
        var bakedParticle = bakedParticle(getElementType().id(), 20, 3f, false);
        PositionFinders.innerRadiusRandom(cloud.position(), this.ancientGolem.getBbWidth(), 5).forEach(
            positions -> sendParticles(level, bakedParticle, positions, 1, 0, 1,0,0.05)
        );
    }

    public void clientDiggingParticles(LivingEntity livingEntity, Level level) {
        var randomsource = livingEntity.getRandom();
        var blockstate = livingEntity.getBlockStateOn();
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            for (int i = 0; i < 15; ++i) {
                var d0 = livingEntity.getX() + (double) Mth.randomBetween(randomsource, -0.5F, 0.5F);
                var d1 = livingEntity.getY();
                var d2 = livingEntity.getZ() + (double) Mth.randomBetween(randomsource, -0.5F, 0.5F);
                sendParticles(level, new BlockParticleOption(ParticleTypes.BLOCK, blockstate), new Vec3(d0, d1, d2), 2, 0, 0.5,0,0.5);
            }
        }
    }

    @Override
    public void onTickMethod() {
        if(cloud.level() instanceof ServerLevel serverLevel){
            if(ancientGolem == null && uuid != null && serverLevel.getEntity(uuid) instanceof AncientGolem ancientGolems) {
                this.ancientGolem = ancientGolems;
            }
        }
        if(this.ancientGolem != null) this.clientDiggingParticles(this.ancientGolem, level());
        this.spawnAnimation();
        this.spawnEternalWizard();
        this.setSpawnParticles(level());
        this.setOuterRingPulses(level());
    }

    private void spawnAnimation(){
        if(ancientGolem == null) return;

        if (ancientGolem.position().y < cloud.position().y + 0.5) {
            if (ancientGolem.isNoAi()) {
                ancientGolem.moveTo(ancientGolem.position().add(0, increaseRate, 0));
            }
        }

        if (cloud.tickCount > 18) {
            if (ancientGolem.isInvulnerable()) ancientGolem.setInvulnerable(false);
            if (ancientGolem.isNoAi()) ancientGolem.setNoAi(false);
            cloud.discard();
        }
    }

    private void setOuterRingPulses(Level level){
        var positions = getOuterRingOfRadiusList(cloud.position(), this.ancientGolem.getBbWidth(), 20);
        var particleOptions = genericParticle(ParticleStore.MAGIC_PARTICLE, this.getElementType(), 10, 0.1f, true);
        if(this.height < 1) this.height += 0.05; else this.height = 0;

        if(position < positions.size()){
            for(double i = 0;  i < 3; i += 1){
                var gottenPosition = positions.get(position).add(0, this.height + i,0);
                sendParticles(level, particleOptions, gottenPosition, 0, 0, 0,0,0);
            }
            this.position++;
        } else{
            this.position = 0;
        }
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putDouble("height", this.height);
        compoundTag.putInt("position", this.position);
        compoundTag.putDouble("increaseRate", this.increaseRate);
        compoundTag.putDouble(EFFECT_DURATION, this.effectDuration);
        compoundTag.putDouble(EFFECT_STRENGTH, this.effectStrength);
        compoundTag.putDouble(EFFECT_CHANCE, this.effectChance);
        compoundTag.putDouble(LIFETIME, this.lifeTime);
        compoundTag.putDouble(DAMAGE, this.damage);
        if(ancientGolem != null) compoundTag.putUUID("spawnedGolem", ancientGolem.getUUID());
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.height = compoundTag.getDouble("height");
        this.position = compoundTag.getInt("position");
        this.increaseRate = compoundTag.getDouble("increaseRate");
        if(compoundTag.contains("spawnedGolem")){
            this.uuid = compoundTag.getUUID("spawnedGolem");
        }
        this.effectChance = compoundTag.getDouble(EFFECT_CHANCE);
        this.effectDuration = compoundTag.getDouble(EFFECT_DURATION);
        this.effectStrength = compoundTag.getDouble(EFFECT_STRENGTH);
        this.lifeTime = compoundTag.getDouble(LIFETIME);
        this.damage = compoundTag.getDouble(DAMAGE);
    }

    private void spawnEternalWizard(){
        if (this.ancientGolem == null && cloud.getOwner() != null) {
            var ancientGolemLocal = new AncientGolem(cloud.level(), (Player) cloud.getOwner(), damage, effectDuration, effectStrength, (int) lifeTime, effectChance);
            var spawnPosition = cloud.position().add(0, -1.5, 0);

            ancientGolemLocal.setInvulnerable(true);
            ancientGolemLocal.moveTo(spawnPosition);
            ancientGolemLocal.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemReg.WAND_ITEM_VITALITY.get()));
            ancientGolemLocal.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemReg.MAGE_HELMET.get()));
            ancientGolemLocal.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ItemReg.MAGE_CHESTPLATE.get()));
            ancientGolemLocal.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ItemReg.MAGE_LEGGINGS.get()));
            ancientGolemLocal.setItemSlot(EquipmentSlot.FEET, new ItemStack(ItemReg.MAGE_BOOTS.get()));
            var directionToEntity = spawnPosition.subtract(cloud.getOwner().position()).normalize();

            // Calculate the yaw so that the skeleton faces away from the player
            double yaw = Math.toDegrees(Math.atan2(directionToEntity.z, directionToEntity.x)) + 90.0;
            ancientGolemLocal.setYRot((float) yaw);
            ancientGolemLocal.setYHeadRot((float) yaw);
            ancientGolemLocal.setYBodyRot((float) yaw);
            ancientGolemLocal.setPos(spawnPosition.x, spawnPosition.y, spawnPosition.z);
            ancientGolemLocal.yRotO = (float) yaw;
            ancientGolemLocal.yHeadRotO = (float) yaw;
            ancientGolemLocal.setPersistenceRequired();
            cloud.level().addFreshEntity(ancientGolemLocal);
            ancientGolemLocal.setNoAi(true);
            this.ancientGolem = ancientGolemLocal;
        }
    }

}
