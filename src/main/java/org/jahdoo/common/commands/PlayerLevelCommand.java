package org.jahdoo.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import org.jahdoo.ascension.attachments.CasterData;
import org.jahdoo.ascension.attachments.PlayerTrialData;
import org.jahdoo.ascension.attachments.RunData;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.trading_post.ShoppingArmor;
import org.jahdoo.ascension.trading_post.ShoppingItems;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.loot_chest.LootChestBlock;
import org.jahdoo.common.items.runes.rune_data.RuneHelpers;
import org.jahdoo.common.networking.server2client.CastingDataSyncS2CP;
import org.jahdoo.common.networking.server2client.RunDataS2CP;
import org.jahdoo.common.registers.AttachmentReg;

import java.util.function.Function;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.neoforged.neoforge.network.PacketDistributor.sendToPlayer;
import static org.jahdoo.JahdooMod.MOD_ID;

public class PlayerLevelCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal(MOD_ID).requires(sender -> sender.hasPermission(2))
            .then(
                literal("caster_data")
                    .then(
                        literal("regret_abilities")
                            .then(
                                argument("targets", EntityArgument.players())
                                    .executes(
                                        context -> regretAbilities(context.getSource())
                                    )
                            )
                    )
                    .then(
                        literal("reset_player_data")
                            .then(
                                argument("targets", EntityArgument.players())
                                    .executes(
                                        context -> resetPlayerData(context.getSource())
                                    )
                            )
                    )
                    .then(
                        literal("set_player_level")
                            .then(
                                argument("targets", EntityArgument.players())
                                    .then(
                                        argument("amount", integer())
                                            .executes(
                                                context -> setPlayerLevel(context.getSource(), getInteger(context, "amount"))
                                            )
                                    )
                            )
                    )
                    .then(
                        literal("add_experience")
                            .then(
                                argument("targets", EntityArgument.players())
                                    .then(
                                        argument("amount", integer())
                                            .executes(
                                                context -> addExperience(context.getSource(), getInteger(context, "amount"))
                                            )
                                    )
                            )
                    )
                    .then(
                        literal("add_skill_points")
                            .then(
                                argument("targets", EntityArgument.players())
                                    .then(
                                        argument("amount", integer())
                                            .executes(
                                                context -> addSkillPoints(context.getSource(), getInteger(context, "amount"))
                                            )
                                    )
                            )
                    )
                    .then(
                        literal("remove_skill_points")
                            .then(
                                argument("targets", EntityArgument.players())
                                    .then(
                                        argument("amount", integer())
                                            .executes(
                                                context -> removeSkillPoints(context.getSource(), getInteger(context, "amount"))
                                            )
                                    )
                            )
                    )
            )
        );


        dispatcher.register(literal(MOD_ID).requires(sender -> sender.hasPermission(2))
            .then(
                literal("trial_data")
                    .then(
                        literal("add_dummy_trial_data")
                            .then(
                                argument("targets", EntityArgument.players())
                                    .then(
                                        argument("entries", integer())
                                            .then(
                                                argument("clear_all_data", BoolArgumentType.bool())
                                                    .executes(
                                                        context -> addDummyTrialData(context.getSource(), getInteger(context, "entries"), BoolArgumentType.getBool(context, "clear_all_data"))
                                                    )
                                            )
                                    )
                            )
                    )
            )
        );

        dispatcher.register(literal(MOD_ID).requires(sender -> sender.hasPermission(2))
            .then(
                literal("trial_data")
                    .then(
                        literal("add_dummy_trial_data")
                            .then(
                                argument("targets", EntityArgument.players())
                                    .then(
                                        argument("entries", integer())
                                            .then(
                                                argument("clear_all_data", BoolArgumentType.bool())
                                                    .executes(
                                                        context -> addDummyTrialData(context.getSource(), getInteger(context, "entries"), BoolArgumentType.getBool(context, "clear_all_data"))
                                                    )
                                            )
                                    )
                            )
                    )
            )
        );

        dispatcher.register(literal(MOD_ID).requires(sender -> sender.hasPermission(2))
            .then(
                literal("run_data")
                    .then(
                        literal("end_run")
                            .then(
                                argument("targets", EntityArgument.players())
                                    .then(
                                        argument("keep_rewards", BoolArgumentType.bool())
                                            .executes(
                                                context -> endRun(context.getSource(), BoolArgumentType.getBool(context, "keep_rewards"))
                                            )
                                    )
                            )
                    )
            )
        );

        dispatcher.register(literal(MOD_ID).requires(sender -> sender.hasPermission(2))
            .then(
                literal("give_items")
                    .then(
                        literal("random_unique_armor")
                            .then(
                                argument("count", integer())
                                    .then(
                                        argument("targets", EntityArgument.players())
                                            .executes(
                                                context -> randomUniqueArmor(context.getSource(), getInteger(context, "count"))
                                            )
                                    )
                            )
                    )
                    .then(
                        literal("shield")
                            .then(
                                argument("count", integer())
                                    .then(
                                        argument("targets", EntityArgument.players())
                                            .then(
                                                rarity(
                                                    c -> getShield(c.getSource(), getInteger(c, "count"), JahdooRarity.COMMON),
                                                    c -> getShield(c.getSource(), getInteger(c, "count"), JahdooRarity.RARE),
                                                    c -> getShield(c.getSource(), getInteger(c, "count"), JahdooRarity.EPIC),
                                                    c -> getShield(c.getSource(), getInteger(c, "count"), JahdooRarity.LEGENDARY),
                                                    c -> getShield(c.getSource(), getInteger(c, "count"), JahdooRarity.ETERNAL),
                                                    c -> getShield(c.getSource(), getInteger(c, "count"), JahdooRarity.UNIQUE),
                                                    c -> getShield(c.getSource(), getInteger(c, "count"), null)
                                                )
                                            )
                                    )
                            )
                    )
                    .then(
                        literal("wand")
                            .then(
                                argument("count", integer())
                                    .then(
                                        argument("targets", EntityArgument.players())
                                            .then(
                                                rarity(
                                                    c -> getWand(c.getSource(), getInteger(c, "count"), JahdooRarity.COMMON),
                                                    c -> getWand(c.getSource(), getInteger(c, "count"), JahdooRarity.RARE),
                                                    c -> getWand(c.getSource(), getInteger(c, "count"), JahdooRarity.EPIC),
                                                    c -> getWand(c.getSource(), getInteger(c, "count"), JahdooRarity.LEGENDARY),
                                                    c -> getWand(c.getSource(), getInteger(c, "count"), JahdooRarity.ETERNAL),
                                                    c -> getWand(c.getSource(), getInteger(c, "count"), JahdooRarity.UNIQUE),
                                                    c -> getWand(c.getSource(), getInteger(c, "count"), null)
                                                )
                                            )
                                    )
                            )
                    )
                    .then(
                        literal("battlemage_gauntlet")
                            .then(
                                argument("count", integer())
                                    .then(
                                        argument("targets", EntityArgument.players())
                                            .then(
                                                rarity(
                                                    c -> getGauntlet(c.getSource(), getInteger(c, "count"), JahdooRarity.COMMON),
                                                    c -> getGauntlet(c.getSource(), getInteger(c, "count"), JahdooRarity.RARE),
                                                    c -> getGauntlet(c.getSource(), getInteger(c, "count"), JahdooRarity.EPIC),
                                                    c -> getGauntlet(c.getSource(), getInteger(c, "count"), JahdooRarity.LEGENDARY),
                                                    c -> getGauntlet(c.getSource(), getInteger(c, "count"), JahdooRarity.ETERNAL),
                                                    c -> getGauntlet(c.getSource(), getInteger(c, "count"), JahdooRarity.UNIQUE),
                                                    c -> getGauntlet(c.getSource(), getInteger(c, "count"), null)
                                                )
                                            )
                                    )
                            )
                    )
                    .then(
                        literal("tome_of_unity")
                            .then(
                                argument("count", integer())
                                    .then(
                                        argument("targets", EntityArgument.players())
                                            .then(
                                                rarity(
                                                    c -> getTome(c.getSource(), getInteger(c, "count"), JahdooRarity.COMMON),
                                                    c -> getTome(c.getSource(), getInteger(c, "count"), JahdooRarity.RARE),
                                                    c -> getTome(c.getSource(), getInteger(c, "count"), JahdooRarity.EPIC),
                                                    c -> getTome(c.getSource(), getInteger(c, "count"), JahdooRarity.LEGENDARY),
                                                    c -> getTome(c.getSource(), getInteger(c, "count"), JahdooRarity.ETERNAL),
                                                    c -> getTome(c.getSource(), getInteger(c, "count"), JahdooRarity.UNIQUE),
                                                    c -> getTome(c.getSource(), getInteger(c, "count"), null)
                                                )
                                            )
                                    )
                            )
                    )
                    .then(
                        literal("magnet")
                            .then(
                                argument("count", integer())
                                    .then(
                                        argument("targets", EntityArgument.players())
                                            .then(
                                                rarity(
                                                    c -> getMagnet(c.getSource(), getInteger(c, "count"), JahdooRarity.COMMON),
                                                    c -> getMagnet(c.getSource(), getInteger(c, "count"), JahdooRarity.RARE),
                                                    c -> getMagnet(c.getSource(), getInteger(c, "count"), JahdooRarity.EPIC),
                                                    c -> getMagnet(c.getSource(), getInteger(c, "count"), JahdooRarity.LEGENDARY),
                                                    c -> getMagnet(c.getSource(), getInteger(c, "count"), JahdooRarity.ETERNAL),
                                                    c -> getMagnet(c.getSource(), getInteger(c, "count"), JahdooRarity.UNIQUE),
                                                    c -> getMagnet(c.getSource(), getInteger(c, "count"), null)
                                                )
                                            )
                                    )
                            )
                    )
                    .then(
                        literal("elemental_sword")
                            .then(
                                argument("count", integer())
                                    .then(
                                        argument("targets", EntityArgument.players())
                                            .then(
                                                rarity(
                                                    c -> elementalSword(c.getSource(), getInteger(c, "count"), JahdooRarity.COMMON),
                                                    c -> elementalSword(c.getSource(), getInteger(c, "count"), JahdooRarity.RARE),
                                                    c -> elementalSword(c.getSource(), getInteger(c, "count"), JahdooRarity.EPIC),
                                                    c -> elementalSword(c.getSource(), getInteger(c, "count"), JahdooRarity.LEGENDARY),
                                                    c -> elementalSword(c.getSource(), getInteger(c, "count"), JahdooRarity.ETERNAL),
                                                    c -> elementalSword(c.getSource(), getInteger(c, "count"), JahdooRarity.UNIQUE),
                                                    c -> elementalSword(c.getSource(), getInteger(c, "count"), null)
                                                )
                                            )
                                    )
                            )
                    )
                    .then(
                        literal("ingmas_sword")
                            .then(
                                argument("count", integer())
                                    .then(
                                        argument("targets", EntityArgument.players())
                                            .then(
                                                rarity(
                                                    c -> ingmasSword(c.getSource(), getInteger(c, "count"), JahdooRarity.COMMON),
                                                    c -> ingmasSword(c.getSource(), getInteger(c, "count"), JahdooRarity.RARE),
                                                    c -> ingmasSword(c.getSource(), getInteger(c, "count"), JahdooRarity.EPIC),
                                                    c -> ingmasSword(c.getSource(), getInteger(c, "count"), JahdooRarity.LEGENDARY),
                                                    c -> ingmasSword(c.getSource(), getInteger(c, "count"), JahdooRarity.ETERNAL),
                                                    c -> ingmasSword(c.getSource(), getInteger(c, "count"), JahdooRarity.UNIQUE),
                                                    c -> ingmasSword(c.getSource(), getInteger(c, "count"), null)
                                                )
                                            )
                                    )
                            )
                    )
                    .then(
                        literal("glaive")
                            .then(
                                argument("count", integer())
                                    .then(
                                        argument("targets", EntityArgument.players())
                                            .then(
                                                rarity(
                                                    c -> ancientGlaive(c.getSource(), getInteger(c, "count"), JahdooRarity.COMMON),
                                                    c -> ancientGlaive(c.getSource(), getInteger(c, "count"), JahdooRarity.RARE),
                                                    c -> ancientGlaive(c.getSource(), getInteger(c, "count"), JahdooRarity.EPIC),
                                                    c -> ancientGlaive(c.getSource(), getInteger(c, "count"), JahdooRarity.LEGENDARY),
                                                    c -> ancientGlaive(c.getSource(), getInteger(c, "count"), JahdooRarity.ETERNAL),
                                                    c -> ancientGlaive(c.getSource(), getInteger(c, "count"), JahdooRarity.UNIQUE),
                                                    c -> ancientGlaive(c.getSource(), getInteger(c, "count"), null)
                                                )
                                            )
                                    )
                            )
                    )
                    .then(
                        literal("knight_king_armor")
                            .then(
                                argument("targets", EntityArgument.players())
                                    .then(
                                        rarity(
                                            c -> getKnightKingArmor(c.getSource(), JahdooRarity.COMMON),
                                            c -> getKnightKingArmor(c.getSource(), JahdooRarity.RARE),
                                            c -> getKnightKingArmor(c.getSource(), JahdooRarity.EPIC),
                                            c -> getKnightKingArmor(c.getSource(), JahdooRarity.LEGENDARY),
                                            c -> getKnightKingArmor(c.getSource(), JahdooRarity.ETERNAL),
                                            c -> getKnightKingArmor(c.getSource(), JahdooRarity.UNIQUE),
                                            c -> getKnightKingArmor(c.getSource(), null)
                                        )
                                    )
                            )
                    )
                    .then(
                        literal("ancient_golem_armor")
                            .then(
                                argument("targets", EntityArgument.players())
                                    .then(
                                        rarity(
                                            c -> getAncientGolem(c.getSource(), JahdooRarity.COMMON),
                                            c -> getAncientGolem(c.getSource(), JahdooRarity.RARE),
                                            c -> getAncientGolem(c.getSource(), JahdooRarity.EPIC),
                                            c -> getAncientGolem(c.getSource(), JahdooRarity.LEGENDARY),
                                            c -> getAncientGolem(c.getSource(), JahdooRarity.ETERNAL),
                                            c -> getAncientGolem(c.getSource(), JahdooRarity.UNIQUE),
                                            c -> getAncientGolem(c.getSource(), null)
                                        )
                                    )
                            )
                    )
                    .then(
                        literal("grand_wizard_armor")
                            .then(
                                argument("targets", EntityArgument.players())
                                    .then(
                                        rarity(
                                            c -> getGrandWizardArmor(c.getSource(), JahdooRarity.COMMON),
                                            c -> getGrandWizardArmor(c.getSource(), JahdooRarity.RARE),
                                            c -> getGrandWizardArmor(c.getSource(), JahdooRarity.EPIC),
                                            c -> getGrandWizardArmor(c.getSource(), JahdooRarity.LEGENDARY),
                                            c -> getGrandWizardArmor(c.getSource(), JahdooRarity.ETERNAL),
                                            c -> getGrandWizardArmor(c.getSource(), JahdooRarity.UNIQUE),
                                            c -> getGrandWizardArmor(c.getSource(), null)
                                        )
                                    )
                            )
                    )
                    .then(
                        literal("mage_armor")
                            .then(
                                argument("targets", EntityArgument.players())
                                    .then(
                                        rarity(
                                            c -> getMageArmor(c.getSource(), JahdooRarity.COMMON),
                                            c -> getMageArmor(c.getSource(), JahdooRarity.RARE),
                                            c -> getMageArmor(c.getSource(), JahdooRarity.EPIC),
                                            c -> getMageArmor(c.getSource(), JahdooRarity.LEGENDARY),
                                            c -> getMageArmor(c.getSource(), JahdooRarity.ETERNAL),
                                            c -> getMageArmor(c.getSource(), JahdooRarity.UNIQUE),
                                            c -> getMageArmor(c.getSource(), null)
                                        )
                                    )
                            )
                    )
                    .then(
                        literal("battle_mage_armor")
                            .then(
                                argument("targets", EntityArgument.players())
                                    .then(
                                        rarity(
                                            c -> getBattleMageArmor(c.getSource(), JahdooRarity.COMMON),
                                            c -> getBattleMageArmor(c.getSource(), JahdooRarity.RARE),
                                            c -> getBattleMageArmor(c.getSource(), JahdooRarity.EPIC),
                                            c -> getBattleMageArmor(c.getSource(), JahdooRarity.LEGENDARY),
                                            c -> getBattleMageArmor(c.getSource(), JahdooRarity.ETERNAL),
                                            c -> getBattleMageArmor(c.getSource(), JahdooRarity.UNIQUE),
                                            c -> getBattleMageArmor(c.getSource(), null)
                                        )
                                    )
                            )
                    )
                    .then(
                        literal("rune")
                            .then(
                                argument("count", integer())
                                    .then(
                                        argument("targets", EntityArgument.players())
                                            .then(
                                                argument("tier", IntegerArgumentType.integer(1, 6))
                                                    .then(
                                                        rarity(
                                                            c -> getRune(c.getSource(), getInteger(c, "count"), JahdooRarity.COMMON, getInteger(c, "tier")),
                                                            c -> getRune(c.getSource(), getInteger(c, "count"), JahdooRarity.RARE, getInteger(c, "tier")),
                                                            c -> getRune(c.getSource(), getInteger(c, "count"), JahdooRarity.EPIC, getInteger(c, "tier")),
                                                            c -> getRune(c.getSource(), getInteger(c, "count"), JahdooRarity.LEGENDARY, getInteger(c, "tier")),
                                                            c -> getRune(c.getSource(), getInteger(c, "count"), JahdooRarity.ETERNAL, getInteger(c, "tier")),
                                                            c -> getRune(c.getSource(), getInteger(c, "count"), JahdooRarity.UNIQUE, getInteger(c, "tier")),
                                                            c -> getRune(c.getSource(), getInteger(c, "count"), null, getInteger(c, "tier"))
                                                        )
                                                    )
                                            )
                                    )
                            )
                    )
                    .then(
                        registerLootChestCommand()
                    )
            )
        );
    }

    public static LiteralArgumentBuilder<CommandSourceStack> registerLootChestCommand() {
        return literal("loot_chest")
            .then(literal(Helpers.EASY)
                .then(argument("key", IntegerArgumentType.integer(0, 3))
                    .executes(context -> lootChestTest(
                        context.getSource(),
                        Helpers.EASY,
                        IntegerArgumentType.getInteger(context, "key")
                    ))
                )
            )
            .then(literal(Helpers.MEDIUM)
                .then(argument("key", IntegerArgumentType.integer(0, 3))
                    .executes(context -> lootChestTest(
                        context.getSource(),
                        Helpers.MEDIUM,
                        IntegerArgumentType.getInteger(context, "key")
                    ))
                )
            )
            .then(literal(Helpers.HARD)
                .then(argument("key", IntegerArgumentType.integer(0, 3))
                    .executes(context -> lootChestTest(
                        context.getSource(),
                        Helpers.HARD,
                        IntegerArgumentType.getInteger(context, "key")
                    ))
                )
            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> rarity(
        Function<CommandContext<CommandSourceStack>, Integer> common,
        Function<CommandContext<CommandSourceStack>, Integer> rare,
        Function<CommandContext<CommandSourceStack>, Integer> epic,
        Function<CommandContext<CommandSourceStack>, Integer> legendary,
        Function<CommandContext<CommandSourceStack>, Integer> eternal,
        Function<CommandContext<CommandSourceStack>, Integer> unique,
        Function<CommandContext<CommandSourceStack>, Integer> random
    ) {
        return literal("rarity")
            .then(literal("common").executes(common::apply))
            .then(literal("rare").executes(rare::apply))
            .then(literal("epic").executes(epic::apply))
            .then(literal("legendary").executes(legendary::apply))
            .then(literal("eternal").executes(eternal::apply))
            .then(literal("unique").executes(unique::apply))
            .then(literal("random").executes(random::apply));
    }

    private static int endRun(CommandSourceStack source, boolean died){
        var player = source.getPlayer();
        if(player == null) return 0;

        RunData.endRun(player, died);
        sendToPlayer(player, new RunDataS2CP(player.getData(AttachmentReg.RUN_DATA)));
        return 1;
    }

    private static int addDummyTrialData(CommandSourceStack source, int totalEntries, boolean clearAllData){
        var player = source.getPlayer();
        if(player == null) return 0;

        PlayerTrialData.addDummyData(player.serverLevel(), player, totalEntries, clearAllData);
        return 1;
    }

    private static int addExperience(CommandSourceStack source, int experience){
        var player = source.getPlayer();
        if(player == null) return 0;

        CasterData.addExperience(player, experience);
        return 1;
    }

    private static int addSkillPoints(CommandSourceStack source, int skillPoints){
        var player = source.getPlayer();
        if(player == null) return 0;

        CasterData.incrementAbilityPoints(player, skillPoints);
        sendToPlayer(player, new CastingDataSyncS2CP(player.getData(AttachmentReg.CASTER_DATA)));
        return 1;
    }

    private static int removeSkillPoints(CommandSourceStack source, int skillPoints){
        var player = source.getPlayer();
        if(player == null) return 0;

        CasterData.decrementAbilityPoints(player, skillPoints);
        sendToPlayer(player, new CastingDataSyncS2CP(player.getData(AttachmentReg.CASTER_DATA)));
        return 1;
    }

    private static int regretAbilities(CommandSourceStack source){
        var player = source.getPlayer();
        if(player == null) return 0;

        CasterData.regretAbilities(player);
        return 1;
    }

    private static int resetPlayerData(CommandSourceStack source){
        var player = source.getPlayer();
        if(player == null) return 0;

        CasterData.clearData(player);
        return 1;
    }

    private static int setPlayerLevel(CommandSourceStack source, int level) {
        var player = source.getPlayer();
        if(player == null) return 0;

        CasterData.setToLevel(player, level);
        return 1;
    }

    private static int getShield(CommandSourceStack source, int count, JahdooRarity rarity) {
        var player = source.getPlayer();
        if(player == null) return 0;
        for (int i = 0; i < count; i++){
            Helpers.throwOrAddItem(player, ShoppingItems.getShieldWithRarity(null, rarity));
        }
        return 1;
    }

    private static int getWand(CommandSourceStack source, int count, JahdooRarity rarity) {
        var player = source.getPlayer();
        if(player == null) return 0;

        for (int i = 0; i < count; i++){
            Helpers.throwOrAddItem(player, ShoppingItems.soldWands(rarity).ShoppingItem());
        }
        return 1;
    }

    private static int getGauntlet(CommandSourceStack source, int count, JahdooRarity rarity) {
        var player = source.getPlayer();
        if(player == null) return 0;

        for (int i = 0; i < count; i++){
            Helpers.throwOrAddItem(player, ShoppingItems.getGauntletWithRarity(null, rarity));
        }
        return 1;
    }

    private static int getTome(CommandSourceStack source, int count, JahdooRarity rarity) {
        var player = source.getPlayer();
        if(player == null) return 0;

        for (int i = 0; i < count; i++){
            Helpers.throwOrAddItem(player, ShoppingItems.createTomeAttributes(null, rarity == null ? JahdooRarity.getRarity() : rarity));
        }
        return 1;
    }

    private static int getMagnet(CommandSourceStack source, int count, JahdooRarity rarity) {
        var player = source.getPlayer();
        if(player == null) return 0;

        for (int i = 0; i < count; i++){
            Helpers.throwOrAddItem(player, ShoppingItems.magnetItem(null, rarity == null ? JahdooRarity.getRarity() : rarity));
        }
        return 1;
    }

    private static int elementalSword(CommandSourceStack source, int count, JahdooRarity rarity) {
        var player = source.getPlayer();
        if(player == null) return 0;

        for (int i = 0; i < count; i++){
            Helpers.throwOrAddItem(player, ShoppingItems.elementalSword(rarity == null ? JahdooRarity.getRarity() : rarity, source.getLevel()).ShoppingItem());
        }
        return 1;
    }

    private static int ancientGlaive(CommandSourceStack source, int count, JahdooRarity rarity) {
        var player = source.getPlayer();
        if(player == null) return 0;

        for (int i = 0; i < count; i++){
            Helpers.throwOrAddItem(player, ShoppingItems.glaive(rarity == null ? JahdooRarity.getRarity() : rarity, source.getLevel()).ShoppingItem());
        }
        return 1;
    }

    private static int ingmasSword(CommandSourceStack source, int count, JahdooRarity rarity) {
        var player = source.getPlayer();
        if(player == null) return 0;

        for (int i = 0; i < count; i++){
            Helpers.throwOrAddItem(player, ShoppingItems.ingmasSword(rarity == null ? JahdooRarity.getRarity() : rarity, source.getLevel()).ShoppingItem());
        }
        return 1;
    }

    private static int getRune(CommandSourceStack source, int count, JahdooRarity rarity, int tier) {
        var player = source.getPlayer();
        if(player == null) return 0;

        for (int i = 0; i < count; i++){
            var runeRarity = rarity == null ? JahdooRarity.getRarity() : rarity;
            var index = tier - 1;
            var rarity1 = index == 5 ? JahdooRarity.getRarity() : JahdooRarity.getAllRarities(index);
            Helpers.throwOrAddItem(player, RuneHelpers.generateRandomTypAttribute(null, rarity1, runeRarity));
        }

        return 1;
    }

    private static int getKnightKingArmor(CommandSourceStack source, JahdooRarity rarity) {
        var player = source.getPlayer();
        if(player == null) return 0;

        for (var itemStack : ShoppingArmor.knightKingWithData(rarity)) {
            Helpers.throwOrAddItem(player, itemStack);
        }

        return 1;
    }

    private static int getAncientGolem(CommandSourceStack source, JahdooRarity rarity) {
        var player = source.getPlayer();
        if(player == null) return 0;

        for (var itemStack : ShoppingArmor.ancientGolemWithData(rarity)) {
            Helpers.throwOrAddItem(player, itemStack);
        }

        return 1;
    }


    private static int getMageArmor(CommandSourceStack source, JahdooRarity rarity) {
        var player = source.getPlayer();
        if(player == null) return 0;

        for (var itemStack : ShoppingArmor.mageWithData(rarity)) {
            Helpers.throwOrAddItem(player, itemStack);
        }

        return 1;
    }

    private static int getGrandWizardArmor(CommandSourceStack source, JahdooRarity rarity) {
        var player = source.getPlayer();
        if(player == null) return 0;

        for (var itemStack : ShoppingArmor.wizardWithData(rarity)) {
            Helpers.throwOrAddItem(player, itemStack);
        }

        return 1;
    }

    private static int getBattleMageArmor(CommandSourceStack source, JahdooRarity rarity) {
        var player = source.getPlayer();
        if(player == null) return 0;

        for (var itemStack : ShoppingArmor.battleMageWithData(rarity)) {
            Helpers.throwOrAddItem(player, itemStack);
        }

        return 1;
    }

    private static int randomUniqueArmor(CommandSourceStack source, int count) {
        var player = source.getPlayer();
        if(player == null) return 0;

        for (int i = 0; i < count; i++){
            Helpers.throwOrAddItem(player, ShoppingItems.shoppingArmorItem(source.getLevel()).ShoppingItem());
        }
        return 1;
    }

    private static int lootChestTest(CommandSourceStack source, String difficulty, int keyType) {
        var player = source.getPlayer();
        if(player == null) return 0;

        for(int i = 0; i < 10; i++){
            LootChestBlock.standAloneLoot(source.getLevel(), player.position(), difficulty, keyType, Helpers.getRgb());
        }

        return 1;
    }

}
