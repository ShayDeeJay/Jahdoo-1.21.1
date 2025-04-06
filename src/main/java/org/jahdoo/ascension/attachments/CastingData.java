package org.jahdoo.ascension.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.networking.server2client.CooldownsSyncS2CP;
import org.jahdoo.common.networking.server2client.ManaSyncS2CP;
import org.jahdoo.common.registers.AttributeReg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.neoforged.neoforge.network.PacketDistributor.sendToPlayer;
import static org.jahdoo.common.registers.AttachmentReg.CASTER_DATA;

public class CastingData implements IAttachment {

    private static final String MANA = "jahdoo_magic_data_mana";
    private static final String COOLDOWNS = "jahdoo_magic_data_cooldowns";
    private static final String COOLDOWNS_STATIC = "jahdoo_magic_data_cooldowns";
    private static final Logger LOGGER = LoggerFactory.getLogger(CastingData.class);

    private int xp;
    private int abilityPoints;
    private double manaPool;
    private String selectedAbility = "";
    private Map<String, Integer> abilityCooldowns = new Object2IntOpenHashMap<>();
    private Map<String, Integer> abilityCooldownsStatic = new Object2IntOpenHashMap<>();
    private List<AbilityHolder> unlockedAbilities = new ArrayList<>();

    public CastingData(
        int xp,
        int abilityPoints,
        double manaPool,
        String selectedAbility,

        Map<String, Integer> abilityCooldowns,
        Map<String, Integer> abilityCooldownsStatic,
        List<AbilityHolder> unlockedAbilities
    ) {
        this.xp = xp;
        this.abilityPoints = abilityPoints;
        this.manaPool = manaPool;
        this.selectedAbility = selectedAbility;
        this.abilityCooldowns = abilityCooldowns;
        this.abilityCooldownsStatic = abilityCooldownsStatic;
        this.unlockedAbilities = unlockedAbilities;
    }

    public CastingData(){}

    public double getManaPool() {
        return manaPool;
    }

    public void manaRegen(Player player){
        this.regenMana(player);
    }

    public void refillMana(Player player){
         this.manaPool = getMaxMana(player);
    }

    public void clearLevels() {
        this.xp = 0;
    }

    public void setXp(int level) {
        this.xp += level;
    }

    public int getExp() {
        return xp;
    }

    public int getAbilityPoints(){
        return this.abilityPoints;
    }

    public void incrementAbilityPoints(int points){
        this.abilityPoints += points;
    }

    public void decrementAbilityPoints(int points){
        this.abilityPoints = Math.max(0, this.abilityPoints - points);
    }

    public void calculateAbilityPoints(int points){
        var currentLevel = getLevelFromExp(xp);
        var level = getLevelFromExp(xp + points);

        incrementAbilityPoints(Math.max(0, level - currentLevel));
    }

    public int getLevel(){
        return getLevelFromExp(this.xp);
    }

    public void setLocalMana(double manaPool){
        this.manaPool = manaPool;
    }

    public void updateAbility(AbilityHolder holder){
        if(holder.abilityName().isEmpty()) return;

        for (var unlockedAbility : this.unlockedAbilities) {
            if(unlockedAbility.abilityName().equals(holder.abilityName())){
                var index = this.unlockedAbilities.indexOf(unlockedAbility);
                this.unlockedAbilities.remove(unlockedAbility);
                this.unlockedAbilities.add(index, holder);
                return;
            }
        }

        this.unlockedAbilities.add(holder);
    }

    public boolean hasAbility(AbilityHolder holder){
        for (var unlockedAbility : this.unlockedAbilities) {
            if(unlockedAbility.abilityName().equals(holder.abilityName())){
                return true;
            }
        }
        return false;
    }

    public void setSelectedAbility(String selectedAbility) {
        this.selectedAbility = selectedAbility;
    }

    public String getSelectedAbility() {
        return selectedAbility;
    }

    public void syncHolders(List<AbilityHolder> holders){
        this.unlockedAbilities = holders;
    }

    public void clearAllAbilities(){
        this.unlockedAbilities = new ArrayList<>();
        this.selectedAbility = "";
    }

    public List<AbilityHolder> getUnlockedAbilities(){
        return this.unlockedAbilities;
    }

    public AbilityHolder getHolder(String id){
        return this.unlockedAbilities.stream().filter(s -> s.abilityName().equals(id)).findFirst().get();
    }

    public Optional<AbilityHolder> getHolderOptional(String id){
        return this.unlockedAbilities.stream().filter(s -> s.abilityName().equals(id)).findFirst();
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

    public static int getLevel(Player player){
        return player.getData(CASTER_DATA).getLevel();
    }

    public static void addExperience(Player player, int exp){
        var data = player.getData(CASTER_DATA);
        data.setXp(exp);
        data.calculateAbilityPoints(exp);
    }

    public static void cooldownTickEvent(ServerPlayer serverPlayer){
        var casterData = serverPlayer.getData(CASTER_DATA);
        if(!casterData.abilityCooldowns.isEmpty()){
            try{
                casterData.applyAllCooldowns();
            } catch (Exception e){
                JahdooMod.LOGGER.error("e: ", e);
            }
            sendToPlayer(serverPlayer, new CooldownsSyncS2CP(casterData.getAllCooldowns(), casterData.getAllCooldownsStatic()));
        }
    }

    private double getModifiedMana(Player player){
        var getRegen = player.getAttribute(AttributeReg.MANA_REGEN);
        var baseManaRegen = 0.15;

        if(getRegen != null) {
            var regenPercentage = getRegen.getValue();
            var calculatedRegen = (baseManaRegen * regenPercentage) / 100;
            return calculatedRegen + baseManaRegen;
        }

        return baseManaRegen;
    }

    public static String selectedAbility(LivingEntity livingEntity){
        return livingEntity.getData(CASTER_DATA).getSelectedAbility();
    }

    public static double getSpecificValue(AbilityHolder abilityHolder, String modifier){
        var specificValue = abilityHolder.data().abilityProperties().get(modifier);

        if(specificValue == null) return 0;
        return specificValue.setValue();
    }

    public static double getSpecificValue(Player player, String modifier){
        var getValue = CastingData.entityHolderWithSelected(player).data().abilityProperties().get(modifier);
        return getValue != null ? getValue.setValue() : 0;
    }

    public static AbilityHolder entityHolderWithSelected(LivingEntity livingEntity){
        var data = livingEntity.getData(CASTER_DATA);
        var holder = data.getHolderOptional(data.getSelectedAbility());
        return holder.orElse(AbilityHolder.DEFAULT);
    }

    public static AbilityHolder entityHolder(LivingEntity livingEntity, String id){
        return livingEntity.getData(CASTER_DATA).getHolder(id);
    }

    public static void incrementAbilityPoints(LivingEntity livingEntity, int abilityPoints){
        livingEntity.getData(CASTER_DATA).incrementAbilityPoints(abilityPoints);
    }

    public static void decrementAbilityPoints(LivingEntity livingEntity, int abilityPoints){
        livingEntity.getData(CASTER_DATA).decrementAbilityPoints(abilityPoints);
    }

    public static void clearLevels(Player player){
        player.getData(CASTER_DATA).clearLevels();
    }

    public static int getXpNeededForNextLevel(int level) {
        if (level >= 0 && level <= 15) {
            return 2 * level + 7;
        } else if (level >= 16 && level <= 30) {
            return 5 * level - 38;
        } else {
            return 9 * level - 158;
        }
    }

    public static boolean checkAndConsume(Player player, int cost){
        var available = CastingData.getAbilityPoints(player);
        var canPurchase = available >= cost;
        if(!canPurchase) return false;
        CastingData.decrementAbilityPoints(player, cost);
        return true;
    }

    public static float getExperienceProgress(Player player) {
        var data = player.getData(CASTER_DATA);
        var totalXp = data.getExp();
        var level = data.getLevel();

        int xpForCurrentLevel = getExpFromLevel(level);
        int xpForNextLevel = getXpNeededForNextLevel(level);
        if (xpForNextLevel == 0) return 0f; // Avoid division by zero
        return (float)(totalXp - xpForCurrentLevel) / (float)xpForNextLevel;
    }

    public static int getLevelFromExp(int xp) {
        if (xp < 0) return 0;
        if (xp < 352)  return (int)Math.floor((-6 + Math.sqrt(36 + 4 * xp)) / 2);
        if (xp < 1507)  return (int)Math.floor((40.5 + Math.sqrt(1640.25 - 10 * (360 - xp))) / 5);
        return (int)Math.floor((162.5 + Math.sqrt(26406.25 - 18 * (2220 - xp))) / 9);
    }

    public static int getExpFromLevel(int level){
        if(level >= 1 && level <= 16) return (int)(Math.pow(level, 2) + 6 * level);
        if(level >= 17 && level <= 31) return (int)( 2.5 * Math.pow(level, 2) - 40.5 * level + 360);
        if(level >= 32) return (int)(4.5 * Math.pow(level, 2) - 162.5 * level + 2220);
        return 0;
    }

    public static int getAbilityPoints(Player player){
        return player.getData(CASTER_DATA).getAbilityPoints();
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

    public static final Codec<CastingData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.INT.fieldOf("xp").forGetter(CastingData::getExp),
            Codec.INT.fieldOf("ability_points").forGetter(CastingData::getAbilityPoints),
            Codec.DOUBLE.fieldOf("mana_pool").forGetter(CastingData::getManaPool),
            Codec.STRING.fieldOf("selected_ability").forGetter(CastingData::getSelectedAbility),
            Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("ability_cooldowns").forGetter(CastingData::getAllCooldowns),
            Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("ability_cooldowns_static").forGetter(CastingData::getAllCooldownsStatic),
            Codec.list(AbilityHolder.CODEC).fieldOf("unlocked_abilities").forGetter(CastingData::getUnlockedAbilities)
        ).apply(instance, CastingData::new)
    );

    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        var cooldowns = new CompoundTag();
        var cooldownsStatic = new CompoundTag();

        this.abilityCooldowns.forEach(cooldowns::putInt);
        this.abilityCooldownsStatic.forEach(cooldownsStatic::putInt);

        nbt.put(CastingData.COOLDOWNS, cooldowns);
        nbt.put(CastingData.COOLDOWNS_STATIC, cooldownsStatic);
        nbt.putDouble(MANA, manaPool);
        nbt.putInt("level", this.xp);
        nbt.putInt("ability_points", this.abilityPoints);
        nbt.putString("selected_ability", this.selectedAbility);
        AbilityHolder.saveListHolders(this.unlockedAbilities, nbt);
    }

    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        manaPool = nbt.getDouble(MANA);
        this.selectedAbility = nbt.getString("selected_ability");

        nbt.getCompound(COOLDOWNS).
            getAllKeys()
            .forEach(keys -> abilityCooldowns.put(keys, nbt.getCompound(COOLDOWNS).getInt(keys)));

        nbt.getCompound(COOLDOWNS_STATIC)
            .getAllKeys()
            .forEach(keys -> abilityCooldownsStatic.put(keys, nbt.getCompound(COOLDOWNS_STATIC).getInt(keys)));

        this.xp = nbt.getInt("level");
        this.abilityPoints = nbt.getInt("ability_points");
        this.unlockedAbilities = AbilityHolder.readListHolders(nbt);
    }
}
