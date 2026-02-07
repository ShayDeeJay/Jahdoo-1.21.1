package org.jahdoo.trial_nexus.mobs.mob_setup;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.AcropolisMonsters.Clawdian_Entity;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.Draugar.Aptrgangr_Entity;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.Kobolediator_Entity;
import com.github.L_Ender.cataclysm.init.ModEntities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.common.block.altar.AltarBlockEntity;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import java.util.List;

import static org.jahdoo.common.block.altar.altar_states.EndAltar.oneEndAltarNotification;
import static org.jahdoo.common.block.altar.altar_states.EndAltar.removeLootAndOres;
import static org.jahdoo.trial_nexus.level_manager.BlockSetupManager.blockExitBarrier;
import static org.jahdoo.trial_nexus.level_manager.StructureManager.placeLocksWithData;
import static org.jahdoo.trial_nexus.mobs.MobSpawnManager.generateMob;

public class MiniBossMobs {

    public static LivingEntity getRandomMiniBoss(ServerLevel serverLevel, InstanceData instanceData) {
        var getBosses = List.of(
            getClawdian(serverLevel, instanceData),
            getAptrgangr(serverLevel, instanceData),
            getKobolediator(serverLevel, instanceData)
        );
        return JahdooHelpers.listRandom(getBosses);
    }

    public static LivingEntity getClawdian(ServerLevel serverLevel, InstanceData instanceData) {
        var livingEntity = new Clawdian_Entity(ModEntities.CLAWDIAN.get(), serverLevel){
            @Override
            protected InteractionResult mobInteract(Player player, InteractionHand hand) {
                onInteract(this);
                return super.mobInteract(player, hand);
            }

            @Override
            protected void triggerOnDeathMobEffects(RemovalReason removalReason) {
                super.triggerOnDeathMobEffects(removalReason);
                onDeath(this);
            }
        };
        return generateMob(livingEntity,  instanceData);
    }

    public static LivingEntity getAptrgangr(ServerLevel serverLevel, InstanceData instanceData) {
        var livingEntity = new Aptrgangr_Entity(ModEntities.APTRGANGR.get(), serverLevel){
            @Override
            protected InteractionResult mobInteract(Player player, InteractionHand hand) {
                onInteract(this);
                return super.mobInteract(player, hand);
            }

            @Override
            protected void triggerOnDeathMobEffects(RemovalReason removalReason) {
                super.triggerOnDeathMobEffects(removalReason);
                onDeath(this);

            }
        };
        return generateMob(livingEntity,  instanceData);

    }

    public static LivingEntity getKobolediator(ServerLevel serverLevel, InstanceData instanceData) {
        var livingEntity = new Kobolediator_Entity(ModEntities.KOBOLEDIATOR.get(), serverLevel){
            @Override
            protected InteractionResult mobInteract(Player player, InteractionHand hand) {
                onInteract(this);
                return super.mobInteract(player, hand);
            }

            @Override
            protected void triggerOnDeathMobEffects(RemovalReason removalReason) {
                super.triggerOnDeathMobEffects(removalReason);
                onDeath(this);
            }
        };
        return generateMob(livingEntity,  instanceData);
    }

    public static void onInteract(Mob mob){
        if(mob.isNoAi()) mob.setNoAi(false);
        var pos = mob.blockPosition();

        if(mob.level() instanceof ServerLevel serverLevel){
            blockExitBarrier(serverLevel, pos);
        }
        mob.getPersistentData().put("block_pos", NbtUtils.writeBlockPos(pos));
    }

    public static void onDeath(LivingEntity entity){
        if(entity.level() instanceof ServerLevel serverLevel){
            var pos = NbtUtils.readBlockPos(entity.getPersistentData(), "block_pos");
            if(pos.isPresent()){
                var get = pos.get();
                placeLocksWithData(serverLevel, get.below(2), false, false);
                oneEndAltarNotification(serverLevel, get);
                removeLootAndOres(serverLevel, get);
            }
        }
    }

    public static void getMiniBoss(AltarBlockEntity entity, ServerLevel serverLevel, InstanceData data){
        var miniBoss = getRandomMiniBoss(serverLevel, data);
        miniBoss.getPersistentData().putBoolean("boss", true);
        miniBoss.setYBodyRot(entity.direction.toYRot());
    }

    public static LivingEntity getMiniBoss(Direction direction, ServerLevel serverLevel, InstanceData data){
        var miniBoss = getRandomMiniBoss(serverLevel, data);

        if(miniBoss instanceof Mob mob) {
            mob.setNoAi(true);
        }

        miniBoss.getPersistentData().putBoolean("boss", true);
        miniBoss.setYBodyRot(direction.toYRot());

        return miniBoss;
    }
}
