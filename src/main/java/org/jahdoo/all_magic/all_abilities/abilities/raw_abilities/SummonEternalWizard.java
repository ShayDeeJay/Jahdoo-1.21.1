package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.all_abilities.abilities.SummonEternalWizardAbility;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.entities.EternalWizard;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.registers.AttributesRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.UUID;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.all_magic.AbilityBuilder.*;

public class SummonEternalWizard extends DefaultEntityBehaviour {
    double height;
    int position;
    double increaseRate = 0.6;
    EternalWizard eternalWizard;
    UUID uuid;
    private double damage;
    private double effectDuration;
    private double effectStrength;
    private double effectChance;
    private double lifeTime;

    @Override
    public void getAoeCloud(AoeCloud aoeCloud) {
        super.getAoeCloud(aoeCloud);
        var player = this.aoeCloud.getOwner();
        var damage = this.getTag(DAMAGE);
        this.damage = ModHelpers.attributeModifierCalculator(
            player,
            (float) damage,
            this.getElementType(),
            AttributesRegister.MAGIC_DAMAGE_MULTIPLIER,
            true
        );
        this.effectDuration = getTag(EFFECT_DURATION);
        this.effectStrength = getTag(EFFECT_STRENGTH);
        this.effectChance = getTag(EFFECT_CHANCE);
        this.lifeTime = getTag(LIFETIME);
    }

    @Override
    public double getTag(String name) {
        var wandAbilityHolder = this.aoeCloud.getwandabilityholder();
        var ability = SummonEternalWizardAbility.abilityId.getPath().intern();
        return ModHelpers.getModifierValue(wandAbilityHolder, ability).get(name).actualValue();
    }

    @Override
    public void onTickMethod() {
        if(aoeCloud.level() instanceof ServerLevel serverLevel){
            if(eternalWizard == null && uuid != null) this.eternalWizard = (EternalWizard) serverLevel.getEntity(uuid);
        }
        if(this.eternalWizard != null) this.clientDiggingParticles(this.eternalWizard, level());

        this.spawnAnimation();
        this.spawnEternalWizard();
        this.setSpawnParticles(level());
        this.setOuterRingPulses(level());
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
        if(eternalWizard != null){
            compoundTag.putUUID("spawnedWizard", eternalWizard.getUUID());
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
    public AbstractElement getElementType() {
        return ElementRegistry.VITALITY.get();
    }

    private Level level(){
        return this.aoeCloud.level();
    }

    private void spawnAnimation(){
        if(eternalWizard == null) return;
        if (eternalWizard.position().y < aoeCloud.position().y + 0.5) {
            if (eternalWizard.isNoAi()) {
                eternalWizard.moveTo(eternalWizard.position().add(0, increaseRate, 0));
                if (increaseRate > 0.2) increaseRate -= 0.1;
            }
        }

        if (aoeCloud.tickCount > 18) {
            if (eternalWizard.isInvulnerable()) eternalWizard.setInvulnerable(false);
            if (eternalWizard.isNoAi()) eternalWizard.setNoAi(false);
            aoeCloud.discard();
        }
    }

    private void spawnEternalWizard(){
        if (this.eternalWizard == null && aoeCloud.getOwner() != null) {
            EternalWizard eternalWizard = new EternalWizard(aoeCloud.level(), (Player) aoeCloud.getOwner(), (int) damage, (int) effectDuration, (int) effectStrength, (int) lifeTime, (int) effectChance);
            eternalWizard.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemsRegister.WAND_ITEM_VITALITY.get()));
            Vec3 spawnPosition = aoeCloud.position().add(0, -1, 0);
            eternalWizard.setInvulnerable(true);
            eternalWizard.moveTo(spawnPosition);
            Vec3 directionToEntity = spawnPosition.subtract(aoeCloud.getOwner().position()).normalize();

            // Calculate the yaw so that the skeleton faces away from the player
            double yaw = Math.toDegrees(Math.atan2(directionToEntity.z, directionToEntity.x)) + 90.0;
            eternalWizard.setYRot((float) yaw);
            eternalWizard.setYHeadRot((float) yaw);
            eternalWizard.setYBodyRot((float) yaw);
            eternalWizard.setPos(spawnPosition.x, spawnPosition.y, spawnPosition.z);
            eternalWizard.yRotO = (float) yaw;
            eternalWizard.yHeadRotO = (float) yaw;

            aoeCloud.level().addFreshEntity(eternalWizard);
            eternalWizard.setNoAi(true);
            this.eternalWizard = eternalWizard;
        }
    }

    public void clientDiggingParticles(LivingEntity livingEntity, Level level) {
        RandomSource randomsource = livingEntity.getRandom();
        BlockState blockstate = livingEntity.getBlockStateOn();
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            for (int i = 0; i < 15; ++i) {
                double d0 = livingEntity.getX() + (double) Mth.randomBetween(randomsource, -0.5F, 0.5F);
                double d1 = livingEntity.getY();
                double d2 = livingEntity.getZ() + (double) Mth.randomBetween(randomsource, -0.5F, 0.5F);
                ParticleHandlers.sendParticles(level, new BlockParticleOption(ParticleTypes.BLOCK, blockstate), new Vec3(d0, d1, d2), 2, 0, 0.5,0,0.5);
            }
        }
    }

    private void setSpawnParticles(Level level){
        BakedParticleOptions bakedParticle = new BakedParticleOptions(ElementRegistry.VITALITY.get().getTypeId(), 20, 3f, false);
        PositionGetters.getInnerRingOfRadiusRandom(aoeCloud.position(), 0.8, 5).forEach(
            positions -> ParticleHandlers.sendParticles(level, bakedParticle, positions, 1, 0, 1,0,0.05)
        );
    }

    private void setOuterRingPulses(Level level){
        var positions = PositionGetters.getOuterRingOfRadiusList(aoeCloud.position(), 0.8, 20);
        GenericParticleOptions particleOptions = genericParticleOptions(ParticleStore.MAGIC_PARTICLE_SELECTION, this.getElementType(), 10, 0.1f, true);
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

    ResourceLocation abilityId = ModHelpers.modResourceLocation("summon_eternal_wizard_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new SummonEternalWizard();
    }
}
