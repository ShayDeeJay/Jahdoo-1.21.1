package org.jahdoo.block.challange_altar;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.attachments.player_abilities.ModularChaosCubeProperties;
import org.jahdoo.entities.CustomZombie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class RoundGenerator {
    private final List<LivingEntity> registerMobs = new ArrayList<>();
    private final List<LivingEntity> getActiveMobs = new ArrayList<>();
    private final ChallengeAltarBlockEntity bEntity;
    private final int round;

    public RoundGenerator(ChallengeAltarBlockEntity bEntity, int round){
        this.bEntity = bEntity;
        this.round = round;
        this.addEntities();
    }

    private void addEntities(){
        var createNewCount = round * 3;
        for(int i = 0; i < createNewCount; i++){
            if (this.bEntity != null && bEntity.getLevel() instanceof ServerLevel serverLevel) {
                var zombie = new CustomZombie(serverLevel, null);
                this.registerMobs.add(zombie);
            }
        }
    }

    public List<LivingEntity> getRegisterMobs() {
        return registerMobs;
    }

    public List<LivingEntity> getActiveMobs() {
        return this.getActiveMobs;
    }

    public int getRound() {
        return round;
    }

    public Optional<LivingEntity> getMob(){
        if(!registerMobs.isEmpty()){
            var entity = this.getRegisterMobs().getFirst();
            this.getActiveMobs.add(entity);
            this.registerMobs.remove(entity);
            return Optional.ofNullable(entity);
        }
        return Optional.empty();
    }

}
