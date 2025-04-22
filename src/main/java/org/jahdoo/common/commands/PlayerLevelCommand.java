package org.jahdoo.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import org.jahdoo.ascension.attachments.CasterData;
import org.jahdoo.ascension.attachments.PlayerTrialData;
import org.jahdoo.ascension.attachments.RunData;
import org.jahdoo.common.networking.server2client.RunDataS2CP;
import org.jahdoo.common.registers.AttachmentReg;

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
                                        Commands.argument("amount", IntegerArgumentType.integer())
                                            .executes(
                                                context -> setPlayerLevel(context.getSource(), IntegerArgumentType.getInteger(context, "amount"))
                                            )
                                    )
                            )
                    )
                    .then(
                        Commands.literal("add_experience")
                            .then(
                                Commands.argument("targets", EntityArgument.players())
                                    .then(
                                        Commands.argument("amount", IntegerArgumentType.integer())
                                            .executes(
                                                context -> addExperience(context.getSource(), IntegerArgumentType.getInteger(context, "amount"))
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
                                        Commands.argument("entries", IntegerArgumentType.integer())
                                            .then(
                                                Commands.argument("clear_all_data", BoolArgumentType.bool())
                                                    .executes(
                                                        context -> addDummyTrialData(context.getSource(), IntegerArgumentType.getInteger(context, "entries"), BoolArgumentType.getBool(context, "clear_all_data"))
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
                                        Commands.argument("entries", IntegerArgumentType.integer())
                                            .then(
                                                Commands.argument("clear_all_data", BoolArgumentType.bool())
                                                    .executes(
                                                        context -> addDummyTrialData(context.getSource(), IntegerArgumentType.getInteger(context, "entries"), BoolArgumentType.getBool(context, "clear_all_data"))
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

}
