package org.jahdoo.capabilities;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.networking.packet.server2client.CooldownsDataSyncS2CPacket;
import org.jahdoo.networking.packet.server2client.ManaDataSyncS2CPacket;
import org.jahdoo.registers.AttributesRegister;

import java.util.Map;

import static org.jahdoo.registers.AttachmentRegister.CASTER_DATA;

public class CastingData implements AbstractAttachment{

    private static final String mana = "jahdoo_magic_data_mana";
    private static final String regenSpeed = "jahdoo_magic_data_mana_regen";
    private static final String cooldowns = "jahdoo_magic_data_cooldowns";
    private static final String cooldownsStatic = "jahdoo_magic_data_cooldowns";

    //Mana system
    private double manaPool;
    private final double baseRegenSpeed = 0.1;
    private final int MIN_MANA = 0;

    public double getManaPool() {
        return manaPool;
    }

    public int getMaxMana(Player player){
        var maxMana = player.getAttribute(AttributesRegister.MANA_POOL);
        return maxMana != null ? (int) maxMana.getValue() : 100;
    }

    public void subtractMana(double regenMana) {
        this.manaPool = Math.max(manaPool - regenMana, MIN_MANA);
    }

    public void manaRegen(Player player){
        this.regenMana(player);
    }

    private double getModifiedMana(Player player){
        var getRegen = player.getAttribute(AttributesRegister.MANA_REGEN);

        if(getRegen != null) {
            var regenPercentage = getRegen.getValue();
            var calculatedRegen = baseRegenSpeed / 100 * regenPercentage;
            return calculatedRegen + baseRegenSpeed;
        }

        return baseRegenSpeed;
    }

    public void regenMana(Player player) {
        this.manaPool = Math.min(manaPool + getModifiedMana(player), getMaxMana(player));
    }

    public void setLocalMana(double manaPool){
        this.manaPool = manaPool;
    }

    public static void manaTickEvent(ServerPlayer serverPlayer) {
        var magicData = serverPlayer.getData(CASTER_DATA);
        magicData.manaRegen(serverPlayer);
        PacketDistributor.sendToPlayer(serverPlayer, new ManaDataSyncS2CPacket(magicData.getManaPool()));
    }

    private Map<String, Integer> abilityCooldowns = new Object2IntOpenHashMap<>();
    private Map<String, Integer> abilityCooldownsStatic = new Object2IntOpenHashMap<>();


    public static void cooldownTickEvent(ServerPlayer serverPlayer){
        var cooldowns = serverPlayer.getData(CASTER_DATA);
        cooldowns.applyAllCooldowns();
        PacketDistributor.sendToPlayer(serverPlayer, new CooldownsDataSyncS2CPacket(cooldowns.getAllCooldowns(), cooldowns.getAllCooldownsStatic()));
    }

    public Map<String, Integer> getAllCooldowns() {
        return abilityCooldowns;
    }

    public Map<String, Integer> getAllCooldownsStatic() {
        return abilityCooldownsStatic;
    }

    public void addCooldown(String ability, int cooldown){
        abilityCooldownsStatic.put(ability, cooldown);
        abilityCooldowns.put(ability, cooldown);
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

    public void removeAbilityFromCooldown(String ability){
        this.abilityCooldowns.remove(ability);
    }

    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        CompoundTag cooldowns = new CompoundTag();
        CompoundTag cooldownsStatic = new CompoundTag();
        this.abilityCooldowns.forEach(cooldowns::putInt);
        this.abilityCooldownsStatic.forEach(cooldownsStatic::putInt);
        nbt.put(CastingData.cooldowns, cooldowns);
        nbt.putDouble(mana, manaPool);
    }

    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        manaPool = nbt.getDouble(mana);
        nbt.getCompound(cooldowns).getAllKeys().forEach(
            keys -> abilityCooldowns.put(keys, nbt.getCompound(cooldowns).getInt(keys))
        );
        nbt.getCompound(cooldownsStatic).getAllKeys().forEach(
            keys -> abilityCooldownsStatic.put(keys, nbt.getCompound(cooldownsStatic).getInt(keys))
        );
    }
}
