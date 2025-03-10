//package org.jahdoo.common.block.challange_altar;
//
//public class ChallengeAltarBlockEntity extends SyncedBlockEntity implements GeoBlockEntity {
//
//    public ServerBossEvent bossEvent;
//    public final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
//    public int privateTicks;
//    public double animateTick;
//    private int initiateSpawning;
//    private boolean beginSpawning;
//    private int buildTick;
//    private int placeCounter;
//
//    public ChallengeAltarBlockEntity(BlockPos pos, BlockState state) {
//        super(BlockEntityReg.CHALLENGE_ALTAR_BE.get(), pos, state);
////        this.setData(CHALLENGE_ALTAR, DEFAULT);
//        this.bossEvent = new ServerBossEvent(Component.literal(""), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.NOTCHED_20);
//    }
//
//    public ChallengeLevelData altarData(){
//        return getProperties(this);
//    }
//
//    private boolean isSubRoundActive() {
//        return altarData().isSubRoundActive(this);
//    }
//
//    @Override
//    public AnimatableInstanceCache getAnimatableInstanceCache() {
//        return cache;
//    }
//
//    private boolean completeRound() {
//        return altarData().round() > 0 && altarData().round() == altarData().maxRound();
//    }
//
//    @Override
//    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
//        controllers.add(new AnimationController<>(this, this::eAnimation));
//    }
//
//    @Override
//    public void onChunkUnloaded() {
//        super.onChunkUnloaded();
//        this.bossEvent.removeAllPlayers();
//    }
//
//    private void summonMobs(int maxSpawn) {
//        if(altarData().killedMobs < altarData().maxMobs()) {
//            MobManager.summonEntities(this, maxSpawn);
//            this.beginSpawning = false;
//            this.initiateSpawning = 0;
//        }
//    }
//
//    private void updatePacket(){
//        if(getLevel() instanceof ServerLevel getLevel){
//            var pos = this.getBlockPos();
//            var payloads = new AltarBlockS2CP(pos, altarData(), privateTicks);
//            Helpers.sendPacketsToPlayerDistance(pos.getCenter(), 64, getLevel, payloads);
//        }
//    }
//
//    private void tickBossEvent() {
//        var data = this.altarData();
//        var progress = data.maxMobs > 0 ? (float) data.activeMobs().size() / data.maxSpawnableMobs() : 0.0f;
//
//        bossEvent.setVisible(isSubRoundActive() && !data.activeMobs().isEmpty());
//        bossEvent.setProgress(progress);
//        bossEvent.setName(Component.nullToEmpty(data.activeMobs().size() + " / " + data.maxSpawnableMobs()));
//    }
//
//    private void completeRound(ServerLevel level) {
//        if(completeRound()){
//            this.privateTicks = 0;
//            resetAltar(this);
//            bossEvent.removeAllPlayers();
//            level.setData(CHALLENGE_ALTAR, newRound(altarData().maxRound, DimHandler.TRADING_POST));
//            sendLevelPlayersNotification(level, "Trial Successful", SoundReg.END_TRIAL.get(), 40);
//        }
//    }
//
//    private PlayState eAnimation(AnimationState<ChallengeAltarBlockEntity> state) {
//        if(isSubRoundActive() && !isCompleted(this)) {
//            var animation = privateTicks <= 100 ? ALTAR_SPAWNING : ALTAR_IDLE;
//            return state.setAndContinue(animation);
//        }
//        return PlayState.STOP;
//    }
//
//    private void sendLevelPlayersNotification(ServerLevel level, String message, SoundEvent sound, int fadeCalc) {
//        Helpers.sendPacketsToPlayerDistance(this.getBlockPos().getCenter(), 200, level,
//            serverPlayer -> {
//                serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(fadeCalc, fadeCalc + 10, fadeCalc - 10));
//                serverPlayer.connection.send(new ClientboundSetTitleTextPacket(Helpers.withStyleComponent(message, ColourStore.PERK_GREEN)));
//                serverPlayer.playNotifySound(sound, SoundSource.NEUTRAL, 1,1);
//            }
//        );
//    }
//
//    private void manageActivePlayers(BlockPos pos) {
//        if(!(this.getLevel() instanceof ServerLevel serverLevel)) return;
//
//        for (var player : serverLevel.players()) {
//            if(!(player instanceof ServerPlayer serverPlayer)) return;
//            var inEven = bossEvent.getPlayers().contains(serverPlayer);
//
//            if (serverPlayer.distanceToSqr(pos.getCenter()) < 10000) {
//                if (!inEven) bossEvent.addPlayer(serverPlayer);
//            } else {
//                if (inEven) bossEvent.removePlayer(serverPlayer);
//            }
//        }
//    }
//
//    @Override
//    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
//        super.saveAdditional(tag, provider);
//        tag.putInt("challenge_altar.private", privateTicks);
//        tag.putInt("challenge_altar.initiateSpawn", initiateSpawning);
//        tag.putInt("challenge_altar.buildTick", buildTick);
//        tag.putInt("challenge_altar.placeCounter", placeCounter);
//        tag.putBoolean("challenge_altar.beginSpawn", beginSpawning);
//        tag.putDouble("animate", this.animateTick);
//    }
//
//    @Override
//    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
//        super.loadAdditional(tag, provider);
//        privateTicks = tag.getInt("challenge_altar.private");
//        initiateSpawning = tag.getInt("challenge_altar.initiateSpawn");
//        buildTick = tag.getInt("challenge_altar.buildTick");
//        placeCounter = tag.getInt("challenge_altar.placeCounter");
//        beginSpawning = tag.getBoolean("challenge_altar.beginSpawning");
//        animateTick = tag.getDouble("animate");
//    }
//
//    private boolean removeKilledMobs() {
//        for (var activeMob : altarData().activeMobs()) {
//            if(this.level instanceof ServerLevel serverLevel){
//                var entity = serverLevel.getEntity(activeMob);
//                if(entity != null && !entity.isAlive()){
//                    altarData().removeMob(activeMob);
//                    incrementKilledMobs(this);
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    public void tick(Level level, BlockPos pos, BlockState state) {
//        if(level instanceof ServerLevel serverLevel){
//            removeKilledMobs();
//            if(isCompleted(this)) {
//                buildTick++;
//            }
//
//            manageActivePlayers(pos);
//            tickBossEvent();
//            activeSubRoundEvent(level, pos);
//            this.completeRound(serverLevel);
//            if (privateTicks == 1) onActivationAnim(level, pos, privateTicks);
//            resetSubRound();
//            this.updatePacket();
//        }
//    }
//
//    private void resetSubRound() {
//        if(!completeRound()){
//            if (altarData().killedMobs > 0 && altarData().killedMobs == altarData().maxMobs()) {
//                this.privateTicks = 0;
//                resetSubRoundAltar(this);
//                if(getLevel() instanceof ServerLevel serverLevel){
//                    var round = getRound(this);
//                    var maxRound = getMaxRounds(this);
//                    if(round < maxRound){
//                        sendLevelPlayersNotification(serverLevel, "Round " + round, SoundReg.END_TRIAL.get(), 20);
//                    }
//                }
//                bossEvent.removeAllPlayers();
//                readyNextSubRound(this, altarData(), Math.max(1, altarData().round));
//            }
//        }
//    }
//
//    private void activeSubRoundEvent(Level level, BlockPos pos) {
//        if(isSubRoundActive()){
//            this.privateTicks++;
//            idleParticleAnim(pos, privateTicks, this.getLevel());
//            if(!removeKilledMobs() && altarData().activeMobs().isEmpty()){
//                if(privateTicks == 93) onActivationAnim(level, pos, privateTicks);
//                if(privateTicks == 96) summonMobs(altarData().maxSpawnableMobs());
//            }
//
//            var inPlay = altarData().activeMobs().size();
//            var allowed = altarData().maxMobsOnMap();
//            var maxMobs = altarData().maxMobs();
//            var killedMobs = altarData().killedMobs();
//            if(privateTicks < 100 || inPlay > allowed) return;
//
//            if((killedMobs + inPlay) < maxMobs && inPlay < allowed) {
//                if(Random.nextInt(10) == 0) summonMobs(1);
//            }
//        }
//    }
//
//}
//
