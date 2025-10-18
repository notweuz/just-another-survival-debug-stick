package ru.ntwz.jasds.callback;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import ru.ntwz.jasds.config.BlacklistConfig;
import ru.ntwz.jasds.config.ConfigManager;
import ru.ntwz.jasds.config.WhitelistConfig;

import java.util.List;

public class CommandRegistrationCallbackRegistry {
    public static void register(Logger logger) {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registry, env) -> {
            dispatcher.register(CommandManager.literal("jasds")
                    .requires(CommandRegistrationCallbackRegistry::hasPermission)
                    .then(CommandManager.literal("reload")
                            .executes(CommandRegistrationCallbackRegistry::executeConfigReload)
                    )
                    .then(CommandManager.literal("whitelist")
                            .then(CommandManager.literal("list")
                                    .executes(CommandRegistrationCallbackRegistry::executeWhitelistList)
                            )
                    )
                    .then(CommandManager.literal("blacklist")
                            .then(CommandManager.literal("list")
                                    .executes(CommandRegistrationCallbackRegistry::executeBlacklistList)
                            )
                    )
            );
        }));
        logger.info("Registered CommandRegistrationCallback");
    }

    private static boolean hasPermission(ServerCommandSource src) {
        return src.hasPermissionLevel(2);
    }

    private static int executeConfigReload(CommandContext<ServerCommandSource> ctx) {
        ConfigManager.loadConfig();
        ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Config reloaded!"), true);
        return 0;
    }

    private static int executeWhitelistList(CommandContext<ServerCommandSource> ctx) {
        WhitelistConfig config = ConfigManager.getWhitelist();
        List<String> whitelistBlocks = config.blocks;
        List<String> whitelistProperties = config.properties;

        String propertiesString = String.join("§r, §e", whitelistProperties);
        String blocksString = String.join("§r, §e", whitelistBlocks);

        if (whitelistBlocks.isEmpty()) ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Blocks whitelist is empty"), true);
        else ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Whitelisted blocks: §e" + blocksString + "§r"), true);
        if (whitelistProperties.isEmpty()) ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Properties whitelist is empty"), true);
        else ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Whitelisted properties: §e" + propertiesString + "§r"), true);

        return 0;
    }

    private static int executeBlacklistList(CommandContext<ServerCommandSource> ctx) {
        BlacklistConfig config = ConfigManager.getBlacklist();
        List<String> blacklistblocks = config.blocks;
        List<String> blacklistProperties = config.properties;

        String propertiesString = String.join("§r, §e", blacklistblocks);
        String blocksString = String.join("§r, §e", blacklistProperties);

        if (blacklistblocks.isEmpty()) ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Blocks blacklist is empty"), true);
        else ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Blacklisted blocks: §e" + blocksString + "§r"), true);
        if (blacklistProperties.isEmpty()) ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Properties blacklist is empty"), true);
        else ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Blacklisted properties: §e" + propertiesString + "§r"), true);

        return 0;
    }
}
