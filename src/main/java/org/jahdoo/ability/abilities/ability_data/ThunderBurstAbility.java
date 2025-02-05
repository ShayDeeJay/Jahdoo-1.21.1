package org.jahdoo.ability.abilities.ability_data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.AbilityBuilder;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.components.ability_holder.AbilityHolder;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.utils.ModHelpers;

import java.util.Map;

import static org.jahdoo.ability.AbilityBuilder.DAMAGE;
import static org.jahdoo.ability.ability_components.LightningTrail.getLightningTrailModifiers;
import static org.jahdoo.registers.AttributesRegister.LIGHTNING_MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.registers.AttributesRegister.MAGIC_DAMAGE_MULTIPLIER;

public class ThunderBurstAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = ModHelpers.res("thunder_burst");
    public static final String NUMBER_OF_THUNDERBOLTS = "Bolt Count";

    private Map<String, AbilityHolder.AbilityModifiers> tagModifierHelper(Player player){
        var wandAbilityHolder = player.getItemInHand(player.getUsedItemHand()).get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
        return ModHelpers.getModifierValue(wandAbilityHolder, abilityId.getPath().intern());
    }

    @Override
    public void invokeAbility(Player player) {
        var modifiers = this.tagModifierHelper(player);
        var damage = ModHelpers.attributeModifierCalculator(
            player, (float) modifiers.get(DAMAGE).actualValue(),true,
            LIGHTNING_MAGIC_DAMAGE_MULTIPLIER, MAGIC_DAMAGE_MULTIPLIER
        );
        var numberOfBolts = modifiers.get(NUMBER_OF_THUNDERBOLTS).setValue();
        var lightningTrailModifiers = getLightningTrailModifiers(damage, 0.16, 10, 0);

        ModHelpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundRegister.BOLT.get(), 2f,1f);

        fireMultiShotProjectile((int) numberOfBolts , 1.2f, player, 0.02,
            () -> new GenericProjectile(
                player, 0,
                EntityPropertyRegister.LIGHTNING_TRAIL.get().setAbilityId(),
                lightningTrailModifiers, -1,
                abilityId.getPath().intern(),
                this.getElemenType()
            )
        );
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.RARE;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setStaticMana(60)
            .setStaticCooldown(300)
            .setDamage(40, 15, 5)
            .setAbilityTagModifiersRandom(NUMBER_OF_THUNDERBOLTS, 20,10, true, 2)
            .build();
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_MINER_DESCRIPTION;
    }

    @Override
    public int getCastType() {
        return PROJECTILE_CAST;
    }

    @Override
    public int getCastDuration(Player player) {
        return 0;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementRegistry.LIGHTNING.get();
    }
}
