package org.jahdoo.ascension.ability.abilities_combat.eternal_wizard;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.PositionFinders;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.entities.eternal_wizard.EternalWizard;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.ItemReg;

import java.util.UUID;

import static net.minecraft.world.entity.EquipmentSlot.*;
import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.ascension.utils.Helpers.attributeModifierCalculator;
import static org.jahdoo.ascension.utils.Helpers.res;
import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticle;
import static org.jahdoo.common.registers.AttributeReg.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.common.registers.AttributeReg.VITALITY_MAGIC_DAMAGE_MULTIPLIER;

public class SummonEternalWizard extends DefaultEntityBehaviour {

    private static final ResourceLocation abilityId = res("summon_eternal_wizard_property");
    private EternalWizard eternalWizard;
    private double increaseRate = 0.1;
    private double effectDuration;
    private double effectStrength;
    private double effectChance;
    private double lifeTime;
    private double damage;
    private double height;
    private int position;
    private UUID uuid;

    @Override
    public void getAoeCloud(AoeCloud aoeCloud) {
        super.getAoeCloud(aoeCloud);
        var player = this.cloud.getOwner();
        var damage = this.getTag(DAMAGE);
        if(player != null){
            this.damage = attributeModifierCalculator(
                player, (float) damage, true,
                MAGIC_DAMAGE_MULTIPLIER,
                VITALITY_MAGIC_DAMAGE_MULTIPLIER
            );
        }
        this.effectDuration = getTag(EFFECT_DURATION);
        this.effectStrength = getTag(EFFECT_STRENGTH);
        this.effectChance = getTag(EFFECT_CHANCE);
        this.lifeTime = getTag(LIFETIME);
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return this.cloud.getwandabilityholder();
    }

    @Override
    public String abilityId() {
        return SummonEternalWizardAbility.abilityId.getPath().intern();
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
        return new SummonEternalWizard();
    }

    private void setSpawnParticles(Level level){
        var bakedParticle = bakedParticle(ElementReg.vitality().id(), 20, 3f, false);
        PositionFinders.innerRadiusRandom(cloud.position(), 0.8, 5).forEach(
            positions -> ParticleHandlers.sendParticles(level, bakedParticle, positions, 1, 0, 1,0,0.05)
        );
    }

    @Override
    public void onTickMethod() {
        if(cloud.level() instanceof ServerLevel serverLevel){
            if(eternalWizard == null && uuid != null) this.eternalWizard = (EternalWizard) serverLevel.getEntity(uuid);
        }
        if(this.eternalWizard != null) this.clientDiggingParticles(this.eternalWizard, level());
        this.spawnAnimation();
        this.spawnEternalWizard();
        this.setSpawnParticles(level());
        this.setOuterRingPulses(level());
    }

    public void clientDiggingParticles(LivingEntity livingEntity, Level level) {
        var randomsource = livingEntity.getRandom();
        var blockstate = livingEntity.getBlockStateOn();
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            for (int i = 0; i < 15; ++i) {
                var d0 = livingEntity.getX() + (double) Mth.randomBetween(randomsource, -0.5F, 0.5F);
                var d1 = livingEntity.getY();
                var d2 = livingEntity.getZ() + (double) Mth.randomBetween(randomsource, -0.5F, 0.5F);
                ParticleHandlers.sendParticles(level, new BlockParticleOption(ParticleTypes.BLOCK, blockstate), new Vec3(d0, d1, d2), 2, 0, 0.5,0,0.5);
            }
        }
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.height = compoundTag.getDouble("height");
        this.position = compoundTag.getInt("position");
        this.increaseRate = compoundTag.getDouble("increaseRate");
        this.uuid = compoundTag.getUUID("spawnedWizard");
        this.damage = compoundTag.getDouble(DAMAGE);
        this.effectChance = compoundTag.getDouble(EFFECT_CHANCE);
        this.effectDuration = compoundTag.getDouble(EFFECT_DURATION);
        this.effectStrength = compoundTag.getDouble(EFFECT_STRENGTH);
        this.lifeTime = compoundTag.getDouble(LIFETIME);
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putDouble("height", this.height);
        compoundTag.putInt("position", this.position);
        compoundTag.putDouble("increaseRate", this.increaseRate);
        compoundTag.putDouble(DAMAGE, this.damage);
        compoundTag.putDouble(EFFECT_DURATION, this.effectDuration);
        compoundTag.putDouble(EFFECT_STRENGTH, this.effectStrength);
        compoundTag.putDouble(EFFECT_CHANCE, this.effectChance);
        compoundTag.putDouble(LIFETIME, this.lifeTime);
        if(eternalWizard != null) compoundTag.putUUID("spawnedWizard", eternalWizard.getUUID());
    }

    private void spawnAnimation(){
        if(eternalWizard == null) return;

        if (eternalWizard.position().y < cloud.position().y + 0.5) {
            if (eternalWizard.isNoAi()) {
                eternalWizard.moveTo(eternalWizard.position().add(0, increaseRate, 0));
                if (increaseRate > 0.2) increaseRate -= 0.1;
            }
        }

        if (cloud.tickCount > 18) {
            if (eternalWizard.isInvulnerable()) eternalWizard.setInvulnerable(false);
            if (eternalWizard.isNoAi()) eternalWizard.setNoAi(false);
            cloud.discard();
        }
    }

    private void setOuterRingPulses(Level level){
        var positions = PositionFinders.getOuterRingOfRadiusList(cloud.position(), 0.8, 20);
        var particleOptions = ParticleHandlers.genericParticle(ParticleStore.MAGIC_PARTICLE, this.getElementType(), 10, 0.1f, true);
        if(this.height < 1) this.height += 0.05; else this.height = 0;

        if(position < positions.size()){
            for(double i = 0;  i < 3; i += 1){
                Vec3 gottenPosition = positions.get(position).add(0, this.height + i,0);
                ParticleHandlers.sendParticles(
                    level, particleOptions, gottenPosition,
                    0, 0, 0,0,0
                );
            }
            this.position++;
        } else{
            this.position = 0;
        }
    }

    private void spawnEternalWizard(){
        if (this.eternalWizard == null && cloud.getOwner() != null) {

            var eternalWizard = new EternalWizard(cloud.level(), (Player) cloud.getOwner(), damage, effectDuration, effectStrength, (int) lifeTime, effectChance);
            var spawnPosition = cloud.position().add(0, -1, 0);
            eternalWizard.setInvulnerable(true);
            eternalWizard.moveTo(spawnPosition);
            eternalWizard.setItemSlot(MAINHAND, new ItemStack(ItemReg.WAND_ITEM_VITALITY.get()));
            eternalWizard.setItemSlot(HEAD, new ItemStack(ItemReg.MAGE_HELMET.get()));
            eternalWizard.setItemSlot(CHEST, new ItemStack(ItemReg.MAGE_CHESTPLATE.get()));
            eternalWizard.setItemSlot(LEGS, new ItemStack(ItemReg.MAGE_LEGGINGS.get()));
            eternalWizard.setItemSlot(FEET, new ItemStack(ItemReg.MAGE_BOOTS.get()));
            var directionToEntity = spawnPosition.subtract(cloud.getOwner().position()).normalize();

            // Calculate the yaw so that the skeleton faces away from the player
            var yaw = Math.toDegrees(Math.atan2(directionToEntity.z, directionToEntity.x)) + 90.0;
            eternalWizard.setYRot((float) yaw);
            eternalWizard.setYHeadRot((float) yaw);
            eternalWizard.setYBodyRot((float) yaw);
            eternalWizard.setPos(spawnPosition.x, spawnPosition.y, spawnPosition.z);
            eternalWizard.yRotO = (float) yaw;
            eternalWizard.yHeadRotO = (float) yaw;
            eternalWizard.setPersistenceRequired();
            cloud.level().addFreshEntity(eternalWizard);
            eternalWizard.setNoAi(true);
            this.eternalWizard = eternalWizard;

        }
    }

}
