package org.jahdoo.trial_nexus.mobs.mob_setup;

import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.Ignis_Entity;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.Maledictus.Maledictus_Entity;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.Scylla.Scylla_Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.common.block.altar.AltarBlockEntity;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.shaydee.shaydeeapi.Helpers;

import java.util.List;

import static com.github.L_Ender.cataclysm.init.ModEntities.*;
import static org.jahdoo.trial_nexus.mobs.MobSpawnManager.addAndPositionEntity;
import static org.jahdoo.trial_nexus.mobs.MobSpawnManager.generateMob;

public class BossMobs {

    public static LivingEntity getRandomBoss(ServerLevel serverLevel, InstanceData instanceData) {
        var getBosses = List.of(
            maledictus(serverLevel, instanceData),
            scylla(serverLevel, instanceData),
            getIgnis(serverLevel, instanceData)
        );
        return Helpers.listRandom(getBosses);
    }

    public static LivingEntity maledictus(ServerLevel serverLevel, InstanceData instanceData){
        return generateMob(new Maledictus_Entity(MALEDICTUS.get(), serverLevel), instanceData);
    }

    public static LivingEntity scylla(ServerLevel serverLevel, InstanceData instanceData){
        return generateMob(new Scylla_Entity(SCYLLA.get(), serverLevel), instanceData);
    }


    public static LivingEntity getIgnis(ServerLevel serverLevel, InstanceData instanceData){
        return generateMob(new Ignis_Entity(IGNIS.get(), serverLevel), instanceData);
    }

    public static void getBoss(AltarBlockEntity entity, ServerLevel serverLevel, InstanceData data){
        var boss = getRandomBoss(serverLevel, data);
        boss.getPersistentData().putBoolean("boss", true);
        boss.setYBodyRot(entity.direction.toYRot());

        addAndPositionEntity(serverLevel, entity.getBlockPos().relative(entity.direction, -3), boss);
        entity.onField.add(boss.getUUID());
    }

}
