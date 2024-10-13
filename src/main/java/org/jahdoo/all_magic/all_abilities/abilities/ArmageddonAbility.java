package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.JahdooRarity;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.ProjectilePropertyRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.all_magic.AbilityBuilder;

public class ArmageddonAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = GeneralHelpers.modResourceLocation("armageddon");
    public static final String aoe = "Area of Effect";
    public static final String speed = "Spawning Speed";

    @Override
    public void invokeAbility(Player player) {
        Vec3 location = player.pick(40, 0,false).getLocation();
        AoeCloud aoeCloud = new AoeCloud(player.level(), player, 3f, ProjectilePropertyRegister.ARMAGEDDON.get().setAbilityId(), abilityId.getPath().intern());
        aoeCloud.setPos(location.x, location.y, location.z);
        player.level().addFreshEntity(aoeCloud);
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
            .setMana(80, 40, 1)
            .setCooldown(2400, 800, 100)
            .setDamage(25, 10, 1)
            .setCastingDistance(30, 10, 5)
            .setLifetime(500, 300, 10)
            .setAbilityTagModifiersRandom(aoe, 6,1, true, 1)
            .setAbilityTagModifiersRandom(speed, 30,5, false, 5)
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
        return ElementRegistry.INFERNO.get();
    }
}
