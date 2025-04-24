package org.jahdoo.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import org.jahdoo.ascension.attachments.CasterData;
import org.jahdoo.ascension.attachments.PlayerTrialData;
import org.jahdoo.ascension.attachments.RunData;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.trading_post.ShoppingItems;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.networking.server2client.CastingDataSyncS2CP;
import org.jahdoo.common.networking.server2client.RunDataS2CP;
import org.jahdoo.common.registers.AttachmentReg;

import java.util.function.Function;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.neoforged.neoforge.network.PacketDistributor.sendToPlayer;
import static org.jahdoo.JahdooMod.MOD_ID;

public class PlayerLevelCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(MOD_ID).requires(sender -> sender.hasPermission(2))
            .then(
                Commands.literal("caster_data")
                    .then(
                        Commands.literal("regret_abilities")
                            .then(
                                Commands.argument("targets", EntityArgument.players())
                                    .executes(
                                        context -> regretAbilities(context.getSource())
                                    )
                            )
                    )
                    .then(
                        Commands.literal("reset_player_data")
                            .then(
                                Commands.argument("targets", EntityArgument.players())
                                    .executes(
                                        context -> resetPlayerData(context.getSource())
                                    )
                            )
                    )
                    .then(
                        Commands.literal("set_player_level")
                            .then(
                                Commands.argument("targets", EntityArgument.players())
                                    .then(
                                        Commands.argument("amount", integer())
                                            .executes(
                                                context -> setPlayerLevel(context.getSource(), getInteger(context, "amount"))
                                            )
                                    )
                            )
                    )
                    .then(
                        Commands.literal("add_experience")
                            .then(
                                Commands.argument("targets", EntityArgument.players())
                                    .then(
                                        Commands.argument("amount", integer())
                                            .executes(
                                                context -> addExperience(context.getSource(), getInteger(context, "amount"))
                                            )
                                    )
                            )
                    )
                    .then(
                        Commands.literal("add_skill_points")
                            .then(
                                Commands.argument("targets", EntityArgument.players())
                                    .then(
                                        Commands.argument("amount", integer())
                                            .executes(
                                                context -> addSkillPoints(context.getSource(), getInteger(context, "amount"))
                                            )
                                    )
                            )
                    )
                    .then(
                        Commands.literal("remove_skill_points")
                            .then(
                                Commands.argument("targets", EntityArgument.players())
                                    .then(
                                        Commands.argument("amount", integer())
                                            .executes(
                                                context -> removeSkillPoints(context.getSource(), getInteger(context, "amount"))
                                            )
                                    )
                            )
                    )
            )
        );


        dispatcher.register(Commands.literal(MOD_ID).requires(sender -> sender.hasPermission(2))
            .then(
                Commands.literal("trial_data")
                    .then(
                        Commands.literal("add_dummy_trial_data")
                            .then(
                                Commands.argument("targets", EntityArgument.players())
                                    .then(
                                        Commands.argument("entries", integer())
                                            .then(
                                                Commands.argument("clear_all_data", BoolArgumentType.bool())
                                                    .executes(
                                                        context -> addDummyTrialData(context.getSource(), getInteger(context, "entries"), BoolArgumentType.getBool(context, "clear_all_data"))
                                                    )
                                            )
                                    )
                            )
                    )
            )
        );

        dispatcher.register(Commands.literal(MOD_ID).requires(sender -> sender.hasPermission(2))
            .then(
                Commands.literal("trial_data")
                    .then(
                        Commands.literal("add_dummy_trial_data")
                            .then(
                                Commands.argument("targets", EntityArgument.players())
                                    .then(
                                        Commands.argument("entries", integer())
                                            .then(
                                                Commands.argument("clear_all_data", BoolArgumentType.bool())
                                                    .executes(
                                                        context -> addDummyTrialData(context.getSource(), getInteger(context, "entries"), BoolArgumentType.getBool(context, "clear_all_data"))
                                                    )
                                            )
                                    )
                            )
                    )
            )
        );

        dispatcher.register(Commands.literal(MOD_ID).requires(sender -> sender.hasPermission(2))
            .then(
                Commands.literal("run_data")
                    .then(
                        Commands.literal("end_run")
                            .then(
                                Commands.argument("targets", EntityArgument.players())
                                    .then(
                                        Commands.argument("keep_rewards", BoolArgumentType.bool())
                                            .executes(
                                                context -> endRun(context.getSource(), BoolArgumentType.getBool(context, "keep_rewards"))
                                            )
                                    )
                            )
                    )
            )
        );

        dispatcher.register(Commands.literal(MOD_ID).requires(sender -> sender.hasPermission(2))
            .then(
                Commands.literal("give_items")
                    .then(
                        Commands.literal("shield")
                            .then(
                                Commands.argument("count", integer())
                                    .then(
                                        Commands.argument("targets", EntityArgument.players())
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
                        Commands.literal("wand")
                            .then(
                                Commands.argument("count", integer())
                                    .then(
                                        Commands.argument("targets", EntityArgument.players())
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
                        Commands.literal("rune")
                            .then(
                                Commands.argument("count", integer())
                                    .then(
                                        Commands.argument("targets", EntityArgument.players())
                                            .executes(
                                                context -> getRune(context.getSource(), getInteger(context, "count"))
                                            )
                                    )
                            )
                    )
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
        return Commands.literal("rarity")
            .then(Commands.literal("common").executes(common::apply))
            .then(Commands.literal("rare").executes(rare::apply))
            .then(Commands.literal("epic").executes(epic::apply))
            .then(Commands.literal("legendary").executes(legendary::apply))
            .then(Commands.literal("eternal").executes(eternal::apply))
            .then(Commands.literal("unique").executes(unique::apply))
            .then(Commands.literal("random").executes(random::apply));
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
            Helpers.throwOrAddItem(player, ShoppingItems.getShieldWithRarity(rarity));
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

    private static int getRune(CommandSourceStack source, int count) {
        var player = source.getPlayer();
        if(player == null) return 0;

        for (int i = 0; i < count; i++){
            Helpers.throwOrAddItem(player, ShoppingItems.shoppingRuneItem().ShoppingItem());
        }
        return 1;
    }

}
