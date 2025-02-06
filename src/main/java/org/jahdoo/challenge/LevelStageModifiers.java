package org.jahdoo.challenge;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jahdoo.ability.effects.JahdooMobEffect;
import org.jahdoo.utils.Maths;

import static org.jahdoo.utils.ModHelpers.Random;

public class LevelStageModifiers {

    public static LootTable getByRound(int round) {
        var switchChance = Random.nextInt(2) == 0;
        int range = (round - 1) / 5; // Determine the range (0-4, 5-9, etc.)

//        return switch (range) {
//            case 0 -> getRandomLeather(); // 1-5
//            case 1 -> switchChance ? getRandomLeather() : getRandomChain(); // 6-10
//            case 2 -> getRandomChain(); // 11-15
//            case 3 -> switchChance ? getRandomChain() : getRandomIron(); // 16-20
//            case 4 -> getRandomIron(); // 21-25
//            case 5 -> switchChance ? getRandomIron() : getRandomGold(); // 26-30
//            case 6 -> getRandomGold(); // 31-35
//            case 7 -> switchChance ? getRandomGold() : getRandomDiamond(); // 36-40
//            case 8 -> getRandomDiamond(); // 41-45
//            case 9 -> switchChance ? getRandomDiamond() : getRandomNetherite(); // 46-50
//            case 10 -> getRandomNetherite(); // 51-55
//            case 11 -> switchChance ? getRandomNetherite() : getRandomEmerald(); // 56-60
//            case 12 -> getRandomEmerald(); // 61-65
//            case 13 -> switchChance ? getRandomEmerald() : getRandomObsidian(); // 66-70
//            case 14 -> getRandomObsidian(); // 71-75
//            case 15 -> switchChance ? getRandomObsidian() : getRandomRuby(); // 76-80
//            case 16 -> getRandomRuby(); // 81-85
//            case 17 -> switchChance ? getRandomRuby() : getRandomSapphire(); // 86-90
//            case 18 -> getRandomSapphire(); // 91-95
//            case 19 -> switchChance ? getRandomSapphire() : getRandomUltimate(); // 96-100
//            default -> getRandomUltimate(); // Above 100
//        };
        return null;
    }

    public static void attributeWithChance(
        Holder<Attribute> attributes,
        LivingEntity getEntity,
        int multiplier,
        int chance
    ){
        if(Maths.percentageChance(chance) && getEntity.getAttributes().hasAttribute(attributes)){
            var attributeInstance = getEntity.getAttributes().getInstance(attributes);
            if (attributeInstance == null) return;
            attributeInstance.setBaseValue(Maths.getPercentageTotal(multiplier, attributeInstance.getValue()));
        }
    }

    public static void effectWithChance(LivingEntity livingEntity, Holder<MobEffect> effect, int amplifier, int chance) {
        if(Maths.percentageChance(chance)){
            if(!livingEntity.hasEffect(effect)){
                livingEntity.addEffect(new JahdooMobEffect(effect, MobEffectInstance.INFINITE_DURATION, amplifier));
            }
        }
    }

}
