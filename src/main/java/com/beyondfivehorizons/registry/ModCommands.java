package com.beyondfivehorizons.registry;

import com.beyondfivehorizons.BeyondFiveHorizons;
import com.beyondfivehorizons.dimension.HorizonDimension;
import com.beyondfivehorizons.portal.HorizonTeleportManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Registers operator commands for verifying all five dimensions during development:
 * /horizons tp <elysium|chronos|astralia|underroot|ashen_crown|overworld>
 */
public final class ModCommands {
    private ModCommands() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("horizons")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("tp")
                                .then(CommandManager.argument("dimension", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            List<String> options = Stream.concat(
                                                    Arrays.stream(HorizonDimension.values()).map(HorizonDimension::getId),
                                                    Stream.of("overworld")
                                            ).toList();
                                            return CommandSource.suggestMatching(options, builder);
                                        })
                                        .executes(ModCommands::executeTeleport)))));

        BeyondFiveHorizons.LOGGER.info("Registered Beyond the Five Horizons commands.");
    }

    private static int executeTeleport(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.translatable("message.beyondfivehorizons.command_player_only"));
            return 0;
        }

        String dimArg = StringArgumentType.getString(context, "dimension");
        if ("overworld".equalsIgnoreCase(dimArg)) {
            HorizonTeleportManager.teleportToOverworld(player);
            return 1;
        }

        Optional<HorizonDimension> target = HorizonDimension.fromId(dimArg);
        if (target.isEmpty()) {
            source.sendError(Text.translatable("message.beyondfivehorizons.unknown_dimension", dimArg));
            return 0;
        }

        return HorizonTeleportManager.teleportToHorizon(player, target.get()) ? 1 : 0;
    }
}
