package org.jahdoo.ascension.attachments;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.networking.server2client.CooldownsSyncS2CP;
import org.jahdoo.common.networking.server2client.ManaSyncS2CP;
import org.jahdoo.common.registers.AttributeReg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static net.neoforged.neoforge.network.PacketDistributor.sendToPlayer;
import static org.jahdoo.common.registers.AttachmentReg.CASTER_DATA;

public class CastingData implements AbstractAttachment{

    private static final String MANA = "jahdoo_magic_data_mana";
    private static final String COOLDOWNS = "jahdoo_magic_data_cooldowns";
    private static final String COOLDOWNS_STATIC = "jahdoo_magic_data_cooldowns";
    private static final Logger LOGGER = LoggerFactory.getLogger(CastingData.class);

    private double manaPool;
    private Map<String, Integer> abilityCooldowns = new Object2IntOpenHashMap<>();
    private Map<String, Integer> abilityCooldownsStatic = new Object2IntOpenHashMap<>();

    public double getManaPool() {
        return manaPool;
    }

    public void manaRegen(Player player){
        this.regenMana(player);
    }

    public void refillMana(Player player){
         this.manaPool = getMaxMana(player);
    }

    public void setLocalMana(double manaPool){
        this.manaPool = manaPool;
    }

    public Map<String, Integer> getAllCooldowns() {
        return abilityCooldowns;
    }

    public Map<String, Integer> getAllCooldownsStatic() {
        return abilityCooldownsStatic;
    }

    public int getStaticCooldown(String abilityId){
        return this.abilityCooldownsStatic.get(abilityId);
    }

    public int getCooldown(String abilityId){
        return this.abilityCooldowns.get(abilityId);
    }

    public boolean isAbilityOnCooldown(String abilityId){
        return this.abilityCooldowns.containsKey(abilityId);
    }

    public void setLocalCooldowns(Map<String, Integer> abilityCooldowns){
        this.abilityCooldowns = abilityCooldowns;
    }

    public void setLocalCooldownsStatic(Map<String, Integer> abilityCooldowns){
        this.abilityCooldownsStatic = abilityCooldowns;
    }

    public void addMana(double manaPool, Player player) {
        this.manaPool = Math.min(manaPool + manaPool, getMaxMana(player));
    }

    public void regenMana(Player player) {
        this.manaPool = Math.min(manaPool + getModifiedMana(player), getMaxMana(player));
    }

    public void addCooldown(String ability, int cooldown){
        abilityCooldownsStatic.put(ability, cooldown);
        abilityCooldowns.put(ability, cooldown);
    }

    public void removeAbilityFromCooldown(String ability){
        this.abilityCooldowns.remove(ability);
        abilityCooldownsStatic.remove(ability);
    }

    public int getMaxMana(Player player){
        var maxMana = player.getAttribute(AttributeReg.MANA_POOL);
        return maxMana != null ? (int) maxMana.getValue() : 100;
    }

    public void subtractMana(double regenMana, Player player) {
        this.manaPool = Math.max(manaPool - regenMana, 0);
        if(player instanceof ServerPlayer serverPlayer) sendToPlayer(serverPlayer, new ManaSyncS2CP(manaPool));
    }

    public static void manaTickEvent(ServerPlayer serverPlayer) {
        var magicData = serverPlayer.getData(CASTER_DATA);
        magicData.manaRegen(serverPlayer);
        sendToPlayer(serverPlayer, new ManaSyncS2CP(magicData.getManaPool()));
    }

    public static void cooldownTickEvent(ServerPlayer serverPlayer){
        var cooldowns = serverPlayer.getData(CASTER_DATA);
        try{
            cooldowns.applyAllCooldowns();
        } catch (Exception e){
            JahdooMod.LOGGER.error("e: ", e);
        }
        sendToPlayer(serverPlayer, new CooldownsSyncS2CP(cooldowns.getAllCooldowns(), cooldowns.getAllCooldownsStatic()));
    }

    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        manaPool = nbt.getDouble(MANA);
        nbt.getCompound(COOLDOWNS).getAllKeys().forEach(
            keys -> abilityCooldowns.put(keys, nbt.getCompound(COOLDOWNS).getInt(keys))
        );
        nbt.getCompound(COOLDOWNS_STATIC).getAllKeys().forEach(
            keys -> abilityCooldownsStatic.put(keys, nbt.getCompound(COOLDOWNS_STATIC).getInt(keys))
        );
    }

    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        CompoundTag cooldowns = new CompoundTag();
        CompoundTag cooldownsStatic = new CompoundTag();
        this.abilityCooldowns.forEach(cooldowns::putInt);
        this.abilityCooldownsStatic.forEach(cooldownsStatic::putInt);
        nbt.put(CastingData.COOLDOWNS, cooldowns);
        nbt.putDouble(MANA, manaPool);
    }

    private double getModifiedMana(Player player){
        var getRegen = player.getAttribute(AttributeReg.MANA_REGEN);
        double baseManaRegen = 0.15;
        if(getRegen != null) {
            var regenPercentage = getRegen.getValue();
            var calculatedRegen = (baseManaRegen * regenPercentage) / 100;
            return calculatedRegen + baseManaRegen;
        }

        return baseManaRegen;
    }

    public void applyAllCooldowns(){
        if(abilityCooldowns.isEmpty()) return;
        abilityCooldowns.forEach(
            (ability, cooldown) -> {
                if(cooldown > 0) {
                    abilityCooldowns.put(ability, cooldown - 1);
                } else {
                    abilityCooldowns.remove(ability);
                    abilityCooldownsStatic.remove(ability);
                }
            }
        );
    }
}
