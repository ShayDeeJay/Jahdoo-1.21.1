package org.jahdoo.trial_nexus.attachments;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.networking.server2client.CastingDataSyncS2CP;
import org.jahdoo.common.networking.server2client.CastingUnlocksSyncS2CP;
import org.jahdoo.common.networking.server2client.CooldownsSyncS2CP;
import org.jahdoo.common.networking.server2client.ManaSyncS2CP;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.common.registers.EffectReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.AbilityReg;
import org.jahdoo.common.registers.mod.SkillReg;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jetbrains.annotations.NotNull;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.*;

import static java.lang.String.valueOf;
import static net.minecraft.util.FastColor.ARGB32.color;
import static net.minecraft.world.entity.ai.attributes.Attributes.STEP_HEIGHT;
import static net.neoforged.neoforge.network.PacketDistributor.sendToPlayer;
import static org.jahdoo.common.registers.AttachmentReg.CASTER_DATA;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;

public class CasterData implements IAttachment {

    private static final String MANA = "jahdoo_magic_data_mana";
    private static final String COOLDOWNS = "jahdoo_magic_data_cooldowns";
    private static final String COOLDOWNS_STATIC = "jahdoo_magic_data_cooldowns";
    private static final Logger LOGGER = LoggerFactory.getLogger(CasterData.class);
    public static final List<@NotNull String> EMPTY = List.of("", "", "", "", "", "", "", "", "", "", "", "");
    public static final int UNLOCKED_AT = 10;

    private int xp;
    private int allowedSlots;
    private int abilityPoints;
    private int refundableSkillPoints;
    private int multiKill;
    private int loadoutIndex = -1;
    private double manaPool;

    private Map<String, Integer> abilityCooldowns = new Object2IntOpenHashMap<>();
    private Map<String, Integer> abilityCooldownsStatic = new Object2IntOpenHashMap<>();
    private Map<Integer, LoadoutObj> loadouts = new Int2ObjectLinkedOpenHashMap<>();

    private String selectedAbility = "";
    private List<AbilityHolder> unlockedAbilities = new ArrayList<>();
    public List<String> abilitySlots = new ArrayList<>();
    private List<String> unlockedSkills = new ArrayList<>();
    private List<String> activeSkills = new ArrayList<>();

    public CasterData(
        int xp,
        int allowedSlots,
        int abilityPoints,
        int refundableSkillPoints,
        int multiKill,
        int loadoutIndex,
        double manaPool,
        String selectedAbility,
        Map<String, Integer> abilityCooldowns,
        Map<String, Integer> abilityCooldownsStatic,
        List<AbilityHolder> unlockedAbilities,
        List<String> abilitySlots,
        List<String> unlockedSkills,
        List<String> activeSkills,
        Map<Integer, LoadoutObj> savedLoadouts
    ) {
        this.xp = xp;
        this.allowedSlots = allowedSlots;
        this.abilityPoints = abilityPoints;
        this.refundableSkillPoints = refundableSkillPoints;
        this.multiKill = multiKill;
        this.loadoutIndex = loadoutIndex;
        this.manaPool = manaPool;
        this.selectedAbility = selectedAbility;
        this.abilityCooldowns = abilityCooldowns;
        this.abilityCooldownsStatic = abilityCooldownsStatic;
        this.unlockedAbilities = unlockedAbilities;
        this.abilitySlots = abilitySlots;
        this.unlockedSkills = unlockedSkills;
        this.activeSkills = activeSkills;
        this.loadouts = savedLoadouts;
    }

    public record LoadoutObj(
        List<AbilityHolder> holders,
        List<String> skills,
        List<String> activeSkills,
        List<String> abilitySlots,
        int skillPoints,
        String selected
    ) {

        public static final Codec<LoadoutObj> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                Codec.list(AbilityHolder.CODEC).fieldOf("unlocked_abilities").forGetter(LoadoutObj::holders),
                Codec.list(Codec.STRING).fieldOf("unlocked_skills").forGetter(LoadoutObj::skills),
                Codec.list(Codec.STRING).fieldOf("active_skills").forGetter(LoadoutObj::activeSkills),
                Codec.list(Codec.STRING).fieldOf("ability_slots").forGetter(LoadoutObj::abilitySlots),
                Codec.INT.fieldOf("skill_points").forGetter(LoadoutObj::skillPoints),
                Codec.STRING.fieldOf("selected").forGetter(LoadoutObj::selected)
            ).apply(instance, LoadoutObj::new)
        );

        public static CompoundTag saveNBT(LoadoutObj loadout) {
            var tag = new CompoundTag();

            AbilityHolder.saveListHolders(loadout.holders, tag);

            var skillsTag = new ListTag();
            for (var skill : loadout.skills) {
                skillsTag.add(StringTag.valueOf(skill));
            }
            tag.put("skills", skillsTag);

            var activeSkillsTag = new ListTag();
            for (var skill : loadout.activeSkills) {
                activeSkillsTag.add(StringTag.valueOf(skill));
            }
            tag.put("active_skills", activeSkillsTag);

            var slotsTag = new ListTag();
            for (var slot : loadout.abilitySlots) {
                slotsTag.add(StringTag.valueOf(slot));
            }
            tag.put("ability_slots", slotsTag);

            tag.putInt("skill_points", loadout.skillPoints);
            tag.putString("selected", loadout.selected);

            return tag;
        }

        public static LoadoutObj loadNBT(CompoundTag tag) {
            var holders = AbilityHolder.readListHolders(tag);

            var skills = new ArrayList<String>();
            var skillsTag = tag.getList("skills", Tag.TAG_STRING);
            for (var i = 0; i < skillsTag.size(); i++) {
                skills.add(skillsTag.getString(i));
            }

            var activeSkills = new ArrayList<String>();
            var activeSkillsTag = tag.getList("active_skills", Tag.TAG_STRING);
            for (var i = 0; i < activeSkillsTag.size(); i++) {
                activeSkills.add(activeSkillsTag.getString(i));
            }

            var abilitySlots = new ArrayList<String>();
            var slotsTag = tag.getList("ability_slots", Tag.TAG_STRING);
            for (var i = 0; i < slotsTag.size(); i++) {
                abilitySlots.add(slotsTag.getString(i));
            }

            var skillPoints = tag.getInt("skill_points");
            var selected = tag.getString("selected");

            return new LoadoutObj(holders, skills, activeSkills, abilitySlots, skillPoints, selected);
        }
    }

    public void addLoadout(int index){
        var temp = new Int2ObjectLinkedOpenHashMap<>(loadouts);
        temp.put(index, getLoadoutObj());
        this.loadoutIndex = index;
        loadouts = temp;
    }

    public void removeLoadout(int index){
        var temp = new Int2ObjectLinkedOpenHashMap<>(loadouts);
        temp.remove(index);
        if(this.loadoutIndex == index) this.loadoutIndex = -1;
        loadouts = temp;
    }

    private @NotNull LoadoutObj getLoadoutObj() {
        var aPoints = abilityPoints;
        var sPoints = refundableSkillPoints;
        var charged = sPoints - aPoints;
        return new LoadoutObj(this.unlockedAbilities, this.unlockedSkills, this.activeSkills, this.abilitySlots, charged, this.selectedAbility);
    }

    public void initLoadout(int index){
        var unlocked = loadouts.get(index);
        this.loadoutIndex = index;
        if(unlocked != null){
            this.unlockedAbilities = unlocked.holders();
            this.unlockedSkills = unlocked.skills();
            this.activeSkills = unlocked.activeSkills();
            this.abilitySlots = unlocked.abilitySlots();
            var points = unlocked.skillPoints();
            this.selectedAbility = unlocked.selected;
            this.decrementAbilityPoints(points);
        }
    }

    public Map<Integer, LoadoutObj> getLoadouts() {
        return loadouts;
    }

    public void setLoadouts(Map<Integer, LoadoutObj> loadouts) {
        this.loadouts = loadouts;
    }

    public void setUnlockedAbilities(List<AbilityHolder> unlockedAbilities) {
        this.unlockedAbilities = unlockedAbilities;
    }

    public void setUnlockedSkills(List<String> unlockedSkills) {
        this.unlockedSkills = unlockedSkills;
    }

    public CasterData(){
        abilitySlots.addAll(EMPTY);
    }

    public List<String> getActiveSkills() {
        return activeSkills;
    }

    public void setActiveSkills(List<String> activeSkills) {
        this.activeSkills = activeSkills;
    }

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

    public int getAllowedSlots() {
        return allowedSlots;
    }

    public void setAllowedSlots(int allowedSlots) {
        this.allowedSlots = Math.min(this.allowedSlots + allowedSlots, 12);
    }

    public int getAbilityPoints(){
        return this.abilityPoints;
    }

    public int getRefundableSkillPoints(){
        return this.refundableSkillPoints;
    }

    public int getLoadoutIndex(){
        return this.loadoutIndex;
    }

    public void incrementAbilityPoints(int points){
        this.abilityPoints += points;
        this.refundableSkillPoints += points;
    }

    public void decrementAbilityPoints(int points){
        this.abilityPoints = Math.max(0, this.abilityPoints - points);
    }

    public void calculateAbilityPoints(Player player, int points){
        var currentLevel = getLevelFromExp(xp);
        var level = getLevelFromExp(xp + points);
        var abilityPoints = level - currentLevel;

        if(abilityPoints > 0){
            incrementAbilityPoints(abilityPoints);
            player.makeSound(SoundReg.LEVEL_UP.get());
            var allowedSlots1 = Math.divideExact(level, UNLOCKED_AT) - Math.divideExact(currentLevel, UNLOCKED_AT);
            this.setAllowedSlots(allowedSlots1);
            for(int i = 0; i < 100; i++){
                var particle = ParticleHandlers.getNonBakedParticles(color(167, 84, 168), color(167, 84, 168), 27, Random.nextInt(1, 3));
                var x = player.getRandomX(0.5);
                var y = player.getRandomY();
                var z = player.getRandomZ(0.5);
                double xSpeed = Random.nextDouble(0.1, 0.3) - 0.2;
                double ySpeed = Random.nextDouble(0.1, 0.3);
                double zSpeed = Random.nextDouble(0.1, 0.3) - 0.2;
                player.level().addParticle(particle, x, y, z, xSpeed, ySpeed, zSpeed);
            }
        }
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
                this.unlockedAbilities.set(index, holder);
                updateLoadout();
                return;
            }
        }

        this.unlockedAbilities.add(holder);
        updateLoadout();
    }

    public void updateLoadout() {
        if(this.loadoutIndex >= 0) {
            this.loadouts.put(this.loadoutIndex, getLoadoutObj());
        }
    }


    public void checkMultiKill(Player player, int multiKillTarget){
        if(this.multiKill > 0){
            if(multiKill > multiKillTarget){
                player.sendSystemMessage(TextHelpers.withStyleComponent("MULTIKILL!!", ColourHelpers.getMagnetRangeGreen()).copy());
                JahdooHelpers.getSoundWithPositionV(player.level(), player.position(), SoundReg.QUEST_COMPLETE.get(), 1, 1.6F);
            }
            this.multiKill = 0;
        }
    }

    public void incrementMultiKill(){
        this.multiKill += 1;
    }


    public static void checkMultiKillStatic(Player player, int multiKillCount){
        if(!(LevelGenerator.isNexus(player.level()))) return;
        player.getData(CASTER_DATA).checkMultiKill(player, multiKillCount);
    }

    public static void incrementMultiKillStatic(Entity entity){
        if(entity instanceof Player player){
            player.getData(CASTER_DATA).incrementMultiKill();
        }
    }

    public boolean hasAbility(String ability){
        for (var unlockedAbility : this.unlockedAbilities) {
            if(unlockedAbility.abilityName().equals(ability)){
                return true;
            }
        }
        return false;
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

    public int getMultiKill() {
        return multiKill;
    }

    public void clearData(){
        sharedReset();

        this.loadouts = new Int2ObjectLinkedOpenHashMap<>();
        this.abilityPoints = 0;
        this.allowedSlots = 2;
        this.xp = 0;
        this.refundableSkillPoints = 0;
    }

    private void sharedReset() {
        this.unlockedAbilities = new ArrayList<>();
        this.abilitySlots = new ArrayList<>(EMPTY);
        this.unlockedSkills = new ArrayList<>();
        this.activeSkills = new ArrayList<>();
        this.selectedAbility = "";
        this.loadoutIndex = -1;
        addFreeAbilities(this);
    }

    public static void regretAbilities(LivingEntity livingEntity, boolean playAudio){
        if(livingEntity instanceof ServerPlayer serverPlayer){
            var data = livingEntity.getData(CASTER_DATA);

            data.sharedReset();
            data.abilityPoints = data.refundableSkillPoints;

            sharedPackets(serverPlayer, data);

            if(playAudio) {
                JahdooHelpers.sendClientSound(serverPlayer, SoundReg.REJECT.get(), 1, 1, false);
                JahdooHelpers.sendClientSound(serverPlayer, SoundReg.ORB_CREATE.get(), 0.4F, 2, false);
            }
        }
    }

    public static void sharedPackets(ServerPlayer serverPlayer, CasterData data){
        sendToPlayer(serverPlayer, new CastingDataSyncS2CP(data));
        sendToPlayer(serverPlayer, new CastingUnlocksSyncS2CP(data));
    }

    public List<String> getAbilitySlots(){
        return this.abilitySlots;
    }

    public List<String> getUnlockedSkills(){
        return this.unlockedSkills;
    }

    public void toggleSkill(String skillId){
        if(!this.activeSkills.contains(skillId)){
            this.activeSkills.add(skillId);
        } else {
            this.activeSkills.remove(skillId);
        }
        updateLoadout();
    }

    public void addNewSkill(String skillId){
        if(hasUnlockedSkill(skillId)){
            this.unlockedSkills.add(skillId);
            this.updateLoadout();
        }
    }

    public boolean hasUnlockedSkill(String skillId){
        return !this.unlockedSkills.contains(skillId);
    }

    public void addAbilitySlot(String selectedAbility){
        if (hasUnlockedSkill(selectedAbility)) {
            for (int i = 0; i < this.abilitySlots.size(); i++) {
                if (this.abilitySlots.get(i).isEmpty()) {
                    this.abilitySlots.set(i, selectedAbility);
                    this.updateLoadout();
                    return;
                }
            }
        }
    }

    public void playerInit(){
        this.setAllowedSlots(2);
        this.incrementAbilityPoints(2);
    }

    public void removeAbilitySlot(String selectedAbility){
        int index = this.abilitySlots.indexOf(selectedAbility);
        if (index == -1) return;

        for (int i = index; i < this.abilitySlots.size() - 1; i++) {
            this.abilitySlots.set(i, this.abilitySlots.get(i + 1));
        }

        this.abilitySlots.set(this.abilitySlots.size() - 1, "");
        if(Objects.equals(selectedAbility, this.selectedAbility)) this.selectedAbility = "";
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
        var manaRegen = player.hasEffect(EffectReg.INFINITE_MANA) ? 100000 : manaPool + getModifiedMana(player);
        var maxMana = getMaxMana(player);

        this.manaPool = Math.min(manaRegen, maxMana);
        if(this.manaPool < maxMana){
            var curioSlotsItems = CuriosApi.getCuriosInventory(player);
            if (curioSlotsItems.isPresent()) {
                var shieldSlots = curioSlotsItems.get().findCurios("relic");
                if(!shieldSlots.isEmpty()){
                    var getTome = shieldSlots.getFirst().stack();
                    if (JahdooHelpers.durabilityDamageCount(getTome) > 0 && getTome.is(ItemReg.TOME_OF_UNITY)) {
                        JahdooHelpers.hurtAndKeepItemChanced(getTome, 1, player.level(), player, 200);
                    }
                }
            }
        }
    }

    public void addCooldown(String ability, int cooldown){
        abilityCooldownsStatic.put(ability, cooldown);
        abilityCooldowns.put(ability, cooldown);
    }

    public void removeAbilityFromCooldown(String ability){
        this.abilityCooldowns.remove(ability);
        abilityCooldownsStatic.remove(ability);
    }

    public double getMaxMana(Player player){
        var maxMana = player.getAttribute(AttributeReg.MANA_POOL);
        var normalMana = maxMana != null ? maxMana.getValue() : 100;
        return player.hasEffect(EffectReg.INFINITE_MANA) ? 100000 : normalMana;
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

    public static void addStep(Player player){
        var attributes = player.getAttributes();
        var stepSkill = JahdooHelpers.res("step_skill");
        if (hasSkill(player, SkillReg.CLIMBER.get().id())) {
            if(!attributes.hasModifier(STEP_HEIGHT, stepSkill)){
                var modifier = new AttributeModifier(stepSkill, 1, AttributeModifier.Operation.ADD_VALUE);
                Multimap<Holder<Attribute>, AttributeModifier> multiMap = HashMultimap.create();
                multiMap.put(STEP_HEIGHT, modifier);
                attributes.addTransientAttributeModifiers(multiMap);
                JahdooHelpers.addTransientAttribute(player, 1, "step_skill", STEP_HEIGHT);
            }
        } else if (attributes.hasModifier(STEP_HEIGHT, stepSkill)) {
            Objects.requireNonNull(player.getAttribute(STEP_HEIGHT)).removeModifiers();
        }
    }

    public static void removeStep(Player player){
    }

    public static void addExperience(Player player, int exp){
        var data = player.getData(CASTER_DATA);
        data.calculateAbilityPoints(player, exp);
        data.setXp(exp);
    }

    public static void cooldownTickEvent(ServerPlayer serverPlayer){
        CasterData.addStep(serverPlayer);

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
            return (baseManaRegen * regenPercentage) / 100 + baseManaRegen;
        }

        return baseManaRegen;
    }

    public static Boolean hasSkill(LivingEntity livingEntity, String skill){
        return livingEntity.getData(CASTER_DATA).getActiveSkills().contains(skill);
    }

    public static boolean isNewLoadout(LivingEntity livingEntity){
        var data = livingEntity.getData(CASTER_DATA);
        return data.loadoutIndex == -1 && data.refundableSkillPoints == data.abilityPoints;
    }

    public static boolean hasLoadout(LivingEntity livingEntity, int index){
        return livingEntity.getData(CASTER_DATA).getLoadouts().containsKey(index);
    }

    public static Boolean hasAbility(LivingEntity livingEntity,  String selectedAbility){
        return livingEntity.getData(CASTER_DATA).hasAbility(selectedAbility);
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
        var getValue = CasterData.entityHolderWithSelected(player).data().abilityProperties().get(modifier);
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

    public static void addFreeAbilities(CasterData castingData){
        castingData.updateAbility(AbilityReg.FETCH.get().setModifiers());
        castingData.updateAbility(AbilityReg.HAMMER.get().setModifiers());
        castingData.updateAbility(AbilityReg.FARMERS_TOUCH.get().setModifiers());
        castingData.updateAbility(AbilityReg.WALL_PLACER.get().setModifiers());
        castingData.updateAbility(AbilityReg.LIGHT_PLACER.get().setModifiers());
    }

    public static void clearData(Player player){
        var data = player.getData(CASTER_DATA);
        data.clearData();
        if(player instanceof ServerPlayer serverPlayer){
            CasterData.sharedPackets(serverPlayer, data);
        }
    }

    public static boolean checkAndConsume(Player player, int cost){
        if (canPurchaseAbility(player, cost)) return false;
        CasterData.decrementAbilityPoints(player, cost);
        return true;
    }

    public static boolean canPurchaseAbility(Player player, int cost) {
        var available = CasterData.getAbilityPoints(player);
        var canPurchase = available >= cost;
        return !canPurchase;
    }

    public static int getXpRemainingToNextLevel(Player player) {
        var data = player.getData(CASTER_DATA);
        var totalXp = data.getExp();
        var level = data.getLevel();

        int xpForCurrentLevel = getExpFromLevel(level);
        int xpForNextLevel = getXpNeededForNextLevel(level);

        int xpIntoLevel = totalXp - xpForCurrentLevel;
        int xpRemaining = xpForNextLevel - xpIntoLevel;

        return Math.max(xpRemaining, 0); // Avoid negative values
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

    public static void setToLevel(Player player, int level) {
        var data = player.getData(CASTER_DATA);
        data.clearLevels();
        CasterData.addExperience(player, CasterData.getExpFromLevel(level));
        if(player instanceof ServerPlayer serverPlayer){
            sharedPackets(serverPlayer, data);
        }
    }

    static int xpScale = 6;

    public static int getXpNeededForNextLevel(int level) {
        if (level >= 0 && level <= 15) return xpScale * (2 * level + 7);
        if (level >= 16 && level <= 30) return xpScale * (5 * level - 38);

        return xpScale * (9 * level - 158);
    }

    public static int getExpFromLevel(int level) {
        if(level >= 1 && level <= 16) return xpScale * (int)(Math.pow(level, 2) + 6 * level);
        if(level >= 17 && level <= 31) return xpScale * (int)(2.5 * Math.pow(level, 2) - 40.5 * level + 360);
        if(level >= 32) return xpScale * (int)(4.5 * Math.pow(level, 2) - 162.5 * level + 2220);
        return 0;
    }

    public static int getLevelFromExp(int xp) {
        if (xp < 0) return 0;
        if (xp < xpScale * 352) return (int)Math.floor((-6 + Math.sqrt(36 + 4 * (xp / (double) xpScale))) / 2);
        if (xp < xpScale * 1507) return (int)Math.floor((40.5 + Math.sqrt(1640.25 - UNLOCKED_AT * (360 - (xp / (double) xpScale)))) / 5);

        return (int)Math.floor((162.5 + Math.sqrt(26406.25 - 18 * (2220 - (xp / (double) xpScale)))) / 9);
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

    public static final Codec<CasterData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.INT.fieldOf("xp").forGetter(CasterData::getExp),
            Codec.INT.fieldOf("available_slots").forGetter(CasterData::getAllowedSlots),
            Codec.INT.fieldOf("ability_points").forGetter(CasterData::getAbilityPoints),
            Codec.INT.fieldOf("refundable_skill_points").forGetter(CasterData::getRefundableSkillPoints),
            Codec.INT.fieldOf("multi_kill").forGetter(CasterData::getMultiKill),
            Codec.INT.fieldOf("loadout_index").forGetter(CasterData::getLoadoutIndex),
            Codec.DOUBLE.fieldOf("mana_pool").forGetter(CasterData::getManaPool),
            Codec.STRING.fieldOf("selected_ability").forGetter(CasterData::getSelectedAbility),
            Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("ability_cooldowns").forGetter(CasterData::getAllCooldowns),
            Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("ability_cooldowns_static").forGetter(CasterData::getAllCooldownsStatic),
            Codec.list(AbilityHolder.CODEC).fieldOf("unlocked_abilities").forGetter(CasterData::getUnlockedAbilities),
            Codec.list(Codec.STRING).fieldOf("ability_slots").forGetter(CasterData::getAbilitySlots),
            Codec.list(Codec.STRING).fieldOf("unlocked_skills").forGetter(CasterData::getUnlockedSkills),
            Codec.list(Codec.STRING).fieldOf("active_skills").forGetter(CasterData::getActiveSkills),
            Codec.unboundedMap(Codec.STRING.xmap(Integer::parseInt, String::valueOf), LoadoutObj.CODEC).fieldOf("loadouts").forGetter(CasterData::getLoadouts)
        ).apply(instance, CasterData::new)
    );

    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        var cooldowns = new CompoundTag();
        var cooldownsStatic = new CompoundTag();
        var abilitySlots = new CompoundTag();
        var unlockedSkills = new CompoundTag();
        var activeSkills = new CompoundTag();

        for (int i = 0; i < this.abilitySlots.size(); i++) {
            var abilitySlot = this.abilitySlots.get(i);
            if (!abilitySlot.isEmpty()) {
                abilitySlots.putString(valueOf(i), abilitySlot); // Save using index
            }
        }

        for (var unlockedSkill : this.unlockedSkills) {
            unlockedSkills.putString(unlockedSkill, unlockedSkill);
        }

        for (var activeSkill : this.activeSkills) {
            activeSkills.putString(activeSkill, activeSkill);
        }

        this.abilityCooldowns.forEach(cooldowns::putInt);
        this.abilityCooldownsStatic.forEach(cooldownsStatic::putInt);

        nbt.putInt("allowed_slots", Math.max(this.allowedSlots, 2));
        nbt.put(CasterData.COOLDOWNS, cooldowns);
        nbt.put(CasterData.COOLDOWNS_STATIC, cooldownsStatic);
        nbt.put("ability_slots", abilitySlots);
        nbt.put("unlocked_skills", unlockedSkills);
        nbt.put("active_skills", activeSkills);
        nbt.putInt("multi_kill", multiKill);
        nbt.putInt("loadout_index", loadoutIndex);
        nbt.putDouble(MANA, manaPool);
        nbt.putInt("level", this.xp);
        nbt.putInt("ability_points", this.abilityPoints);
        nbt.putInt("refundable_skill_points", this.refundableSkillPoints);
        nbt.putString("selected_ability", this.selectedAbility);
        if(unlockedAbilities == null){
            this.unlockedAbilities = new ArrayList<>();
        } else {
            AbilityHolder.saveListHolders(this.unlockedAbilities, nbt);
        }

        var loadoutsTag = new CompoundTag();

        for (var entry : this.loadouts.entrySet()) {
            var loadoutId = entry.getKey();
            var abilities = entry.getValue();
            var x = LoadoutObj.saveNBT(abilities);

            loadoutsTag.put(String.valueOf(loadoutId), x);
        }

        nbt.put("loadouts", loadoutsTag);
    }

    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        manaPool = nbt.getDouble(MANA);
        selectedAbility = nbt.getString("selected_ability");

        nbt.getCompound(COOLDOWNS)
            .getAllKeys()
            .forEach(key -> abilityCooldowns.put(key, nbt.getCompound(COOLDOWNS).getInt(key)));

        nbt.getCompound(COOLDOWNS_STATIC)
            .getAllKeys()
            .forEach(key -> abilityCooldownsStatic.put(key, nbt.getCompound(COOLDOWNS_STATIC).getInt(key)));

        var slots = nbt.getCompound("ability_slots");

        for (String key : slots.getAllKeys()) {
            var index = Integer.parseInt(key);
            var ability = slots.getString(key);
            this.abilitySlots.set(index, ability);
        }

        for (int i = this.abilitySlots.size(); i < 12; i++) {
            this.abilitySlots.add("");
        }

        this.unlockedSkills.addAll(nbt.getCompound("unlocked_skills").getAllKeys());
        this.activeSkills.addAll(nbt.getCompound("active_skills").getAllKeys());

        this.allowedSlots = nbt.getInt("allowed_slots");
        this.xp = nbt.getInt("level");
        this.multiKill = nbt.getInt("multi_kill");
        this.loadoutIndex = nbt.getInt("loadout_index");
        this.abilityPoints = nbt.getInt("ability_points");
        this.refundableSkillPoints = nbt.getInt("refundable_skill_points");
        this.unlockedAbilities = AbilityHolder.readListHolders(nbt);

        Map<Integer, LoadoutObj> loadout = new Int2ObjectLinkedOpenHashMap<>();

        CompoundTag loadoutsTag = nbt.getCompound("loadouts");

        for (String key : loadoutsTag.getAllKeys()) {
            int loadoutId = Integer.parseInt(key);
            CompoundTag loadoutTag = loadoutsTag.getCompound(key);

            LoadoutObj abilities = LoadoutObj.loadNBT(loadoutTag);
            loadout.put(loadoutId, abilities);
        }

        this.loadouts = loadout;

    }
}
