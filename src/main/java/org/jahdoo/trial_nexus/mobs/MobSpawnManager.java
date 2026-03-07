package org.jahdoo.trial_nexus.mobs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.block.altar.AltarBlockEntity;
import org.jahdoo.common.entities.inferno_creeper.InfernoCreeper;
import org.jahdoo.common.entities.void_spider.VoidSpider;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.EffectReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.magic.effects.JahdooMobEffect;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.mobs.mob_setup.BossMobs;
import org.jahdoo.trial_nexus.mobs.mob_setup.HordeMobs;
import org.jahdoo.trial_nexus.mobs.mob_setup.SpecialMobs;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.MathHelpers;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;
import static org.jahdoo.common.entities.ancient_golem.AncientGolem.INFINITE_LIFE;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.InstanceData.difficultyFromInstance;
import static org.jahdoo.trial_nexus.level_manager.RoomData.BOSS_CRUCIBLE;
import static org.jahdoo.trial_nexus.level_manager.RoomData.CHALLENGER_DOME;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;
import static org.jahdoo.trial_nexus.utils.PositionFinders.getOuterRingOfRadiusRandom;

public class MobSpawnManager {

    public static void addBaseAttribute(Holder<Attribute> attributes, LivingEntity getEntity, double multiplier){
        if(getEntity.getAttributes().hasAttribute(attributes)){
            var attributeInstance = getEntity.getAttributes().getInstance(attributes);
            if (attributeInstance == null) return;
            attributeInstance.setBaseValue(MathHelpers.getPercentageTotal(multiplier, attributeInstance.getValue()));
        }
    }

    public static void addAndPositionEntity(ServerLevel serverLevel, BlockPos pos, LivingEntity entity){
        setOuterRingPulses(serverLevel, pos.getCenter(), entity.getBbWidth());
        SoundHelpers.getSoundWithPosition(serverLevel, pos, SoundReg.ORB_CREATE.get(), SoundSource.MASTER, 0.4F, 1.8F);
        SoundHelpers.getSoundWithPosition(serverLevel, pos, SoundEvents.ALLAY_HURT, SoundSource.MASTER, 0.3F, 0.8F);
        entity.moveTo(pos.getCenter());
        serverLevel.addFreshEntity(entity);
    }

    public static LivingEntity generateMob(LivingEntity livingEntity, InstanceData getData){
        var getEntity = livingEntity.level().getNearestPlayer(livingEntity, 200);
        setBaseAttributes(livingEntity, getData, 1);
        if (livingEntity instanceof Mob mob) mob.setTarget(getEntity);
        return livingEntity;
    }

    private static void setBaseAttributes(LivingEntity livingEntity, InstanceData getData, int multipliers) {
        addBaseAttribute(MAX_HEALTH, livingEntity, getData.getHealth() * multipliers);
        addBaseAttribute(ARMOR, livingEntity, getData.getArmor() * multipliers);
        addBaseAttribute(ATTACK_DAMAGE, livingEntity, getData.getAttackDamage() * multipliers);
        addBaseAttribute(MOVEMENT_SPEED, livingEntity, getData.getSpeed() * multipliers);
        addBaseAttribute(KNOCKBACK_RESISTANCE, livingEntity, getData.getKnockback() * multipliers);
        livingEntity.setHealth(livingEntity.getMaxHealth());
    }

    public static void setOuterRingPulses(Level level, Vec3 position, double radius){
        var lifetime = Random.nextInt(7, 10);
        var parType = ParticleStore.MAGIC_PARTICLE;
        var particleOptions = ParticleHandlers.genericParticle(parType, ColourHelpers.getRgb(), ColourHelpers.getRgb(), lifetime, 0.1f, true, 1);

        getOuterRingOfRadiusRandom(position, radius, radius * 40,
            pos -> ParticleHandlers.sendParticles(level, particleOptions, pos, 0, 0, 1,0, Random.nextDouble(0.1, 0.4))
        );
    }

    public static void assignMobs(AltarBlockEntity entity, String roomId){
        if(!(entity.getLevel() instanceof ServerLevel serverLevel)) return;
        var data = entity.getData(INSTANCE_DATA);

        if(BOSS_CRUCIBLE.isRoom(roomId)) {
            BossMobs.getBoss(entity, serverLevel, data);
            return;
        }

        if(CHALLENGER_DOME.isRoom(roomId)) {
            BossMobs.getBoss(entity, serverLevel, data);
            return;
        }

        MobSpawnManager.buildMobs(entity, serverLevel, roomId);
    }

    private static void spawnMany(
        List<LivingEntity> entities,
        int count,
        Supplier<? extends LivingEntity> factory
    ) {
        for (int i = 0; i < count; i++) {
            LivingEntity e = factory.get();
            entities.add(e);
        }
    }

    private static void buildMobs(AltarBlockEntity entity, ServerLevel serverLevel, String roomId) {
        var data = serverLevel.getData(INSTANCE_DATA);
        var entities = new ArrayList<LivingEntity>();

        spawnMany(entities, data.getHorde(),
            () -> HordeMobs.getZombies(serverLevel, data, roomId));

        spawnMany(entities, data.getSkeleton(),
            () -> HordeMobs.getSkeletons(serverLevel, data));

        spawnMany(entities, data.getEternalWizard(),
            () -> SpecialMobs.getWizard(serverLevel, data));

        spawnMany(entities, data.getVoidSpider(),
            () -> new VoidSpider(serverLevel));

        spawnMany(entities, data.getInfernoCreeper(),
            () -> new InfernoCreeper(serverLevel));

        entity.spawnableMobs.addAll(entities);
    }

    public static boolean championSpawn(ServerLevel serverLevel, LivingEntity entity, boolean spawnedChamp) {
        if(!spawnedChamp){

            var data = serverLevel.getData(AttachmentReg.INSTANCE_DATA);
            var instanceDiff = difficultyFromInstance(data);

            if (instanceDiff.isPresent() && MathHelpers.percentageChance(instanceDiff.get().getSpecialSpawnChance())) {
                var ob = instanceDiff.get();
                var id = ob.getId() * 3;

                entity.addEffect(new JahdooMobEffect(EffectReg.CHAMPION_EFFECT, INFINITE_LIFE, id));
                entity.addEffect(new JahdooMobEffect(MobEffects.GLOWING, INFINITE_LIFE, 1));

                setBaseAttributes(entity, data, id);
                return true;
            }

        }
        return false;
    }

}
