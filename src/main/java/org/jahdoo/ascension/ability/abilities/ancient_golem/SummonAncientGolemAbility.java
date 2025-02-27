package org.jahdoo.ascension.ability.abilities.ancient_golem;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.registers.ElementRegistry;
import org.jahdoo.common.registers.EntityPropertyRegister;
import org.jahdoo.common.registers.SoundRegister;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;

public class SummonAncientGolemAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = Helpers.res("ancient_golem");

    @Override
    public void invokeAbility(Player player) {
        var location = player.pick(40, 0,false).getLocation();
        var selectedAbility = EntityPropertyRegister.SUMMON_ANCIENT_GOLEM.get().setAbilityId();
        var intern = abilityId.getPath().intern();
        var aoeCloud = new AoeCloud(player.level(), player, 0f, selectedAbility, intern);
        aoeCloud.setPos(location.x, location.y, location.z);
        player.level().addFreshEntity(aoeCloud);
        player.level().playSound(null, BlockPos.containing(location), SoundEvents.ELDER_GUARDIAN_DEATH, SoundSource.BLOCKS, 2f, 1.4f);
        player.level().playSound(null, BlockPos.containing(location), SoundRegister.EXPLOSION.get(), SoundSource.BLOCKS, 2f, 1.2f);
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.ETERNAL;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setStaticMana(200)
            .setStaticCooldown(6000)
            .setDamage(40, 10, 5)
            .setEffectStrength(10, 0, 1)
            .setEffectDuration(600, 200, 50)
            .setEffectChance(60, 20, 10)
            .setCastingDistance(30, 10, 5)
            .setLifetime(12000, 2400, 1200)
            .build();
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_MINER_DESCRIPTION;
    }

    @Override
    public int getCastType() {
        return DISTANCE_CAST;
    }

    @Override
    public int getCastDuration(Player player) {
        return 0;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementRegistry.VITALITY.get();
    }

}
