package ru.ntwz.jasds.callback;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import ru.ntwz.jasds.config.BlacklistConfig;
import ru.ntwz.jasds.config.ConfigManager;
import ru.ntwz.jasds.config.JASDSConfig;
import ru.ntwz.jasds.config.WhitelistConfig;

import java.util.List;

public class CommandRegistrationCallbackRegistry {
    public static void register(Logger logger) {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registry, env) -> dispatcher.register(CommandManager.literal("jasds")
                .requires(CommandRegistrationCallbackRegistry::hasPermission)
                .then(CommandManager.literal("reload")
                        .executes(CommandRegistrationCallbackRegistry::executeConfigReload)
                )
                .then(CommandManager.literal("whitelist")
                        .then(CommandManager.literal("list")
                                .executes(CommandRegistrationCallbackRegistry::executeWhitelistList)
                        )
                        .then(CommandManager.literal("enable")
                                .executes(ctx -> toggleWhitelist(ctx, true))
                        )
                        .then(CommandManager.literal("disable")
                                .executes(ctx -> toggleWhitelist(ctx, false))
                        )
                )
                .then(CommandManager.literal("blacklist")
                        .then(CommandManager.literal("list")
                                .executes(CommandRegistrationCallbackRegistry::executeBlacklistList)
                        )
                        .then(CommandManager.literal("enable")
                                .executes(ctx -> toggleBlacklist(ctx, true))
                        )
                        .then(CommandManager.literal("disable")
                                .executes(ctx -> toggleBlacklist(ctx, false))
                        )
                )
                .then(CommandManager.literal("cooldowns")
                        .then(CommandManager.literal("use")
                                .then(CommandManager.literal("get")
                                        .executes(CommandRegistrationCallbackRegistry::executeCooldownsUseGet)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("ticks", IntegerArgumentType.integer())
                                                .executes(CommandRegistrationCallbackRegistry::executeCooldownsUseSet)
                                        )
                                )
                        )
                        .then(CommandManager.literal("swap")
                                .then(CommandManager.literal("get")
                                        .executes(CommandRegistrationCallbackRegistry::executeCooldownsSwapGet)
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("ticks", IntegerArgumentType.integer())
                                                .executes(CommandRegistrationCallbackRegistry::executeCooldownsSwapSet)
                                        )
                                )
                        )
                )
        )));
        logger.info("Registered CommandRegistrationCallback");
    }

    private static boolean hasPermission(ServerCommandSource src) {
        return src.hasPermissionLevel(2);
    }

    private static int executeConfigReload(CommandContext<ServerCommandSource> ctx) {
        ConfigManager.loadConfig();
        ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Config reloaded!"), true);
        return 1;
    }

    private static int executeWhitelistList(CommandContext<ServerCommandSource> ctx) {
        WhitelistConfig config = ConfigManager.getWhitelist();
        List<String> whitelistBlocks = config.blocks;
        List<String> whitelistProperties = config.properties;

        String propertiesString = String.join("§r, §e", whitelistProperties);
        String blocksString = String.join("§r, §e", whitelistBlocks);

        if (whitelistBlocks.isEmpty())
            ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Blocks whitelist is empty"), true);
        else
            ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Whitelisted blocks: §e" + blocksString + "§r"), true);
        if (whitelistProperties.isEmpty())
            ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Properties whitelist is empty"), true);
        else
            ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Whitelisted properties: §e" + propertiesString + "§r"), true);

        return 1;
    }

    private static int executeBlacklistList(CommandContext<ServerCommandSource> ctx) {
        BlacklistConfig config = ConfigManager.getBlacklist();
        List<String> blacklistBlocks = config.blocks;
        List<String> blacklistProperties = config.properties;

        String propertiesString = String.join("§r, §e", blacklistBlocks);
        String blocksString = String.join("§r, §e", blacklistProperties);

        if (blacklistBlocks.isEmpty())
            ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Blocks blacklist is empty"), true);
        else
            ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Blacklisted blocks: §e" + blocksString + "§r"), true);
        if (blacklistProperties.isEmpty())
            ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Properties blacklist is empty"), true);
        else
            ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Blacklisted properties: §e" + propertiesString + "§r"), true);

        return 1;
    }

    private static int toggleWhitelist(CommandContext<ServerCommandSource> ctx, boolean enable) {
        JASDSConfig config = ConfigManager.getConfig();
        config.whitelistEnabled = enable;
        ConfigManager.saveConfig();
        ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Whitelist " + (enable ? "enabled" : "disabled") + "!"), true);
        return 1;
    }

    private static int toggleBlacklist(CommandContext<ServerCommandSource> ctx, boolean enable) {
        JASDSConfig config = ConfigManager.getConfig();
        config.blacklistEnabled = enable;
        ConfigManager.saveConfig();
        ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Blacklist " + (enable ? "enabled" : "disabled") + "!"), true);
        return 1;
    }

    private static int executeCooldownsUseGet(CommandContext<ServerCommandSource> ctx) {
        JASDSConfig config = ConfigManager.getConfig();
        ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Use cooldown: §e" + config.useCooldown + "§r ticks"), true);
        return 1;
    }

    private static int executeCooldownsSwapGet(CommandContext<ServerCommandSource> ctx) {
        JASDSConfig config = ConfigManager.getConfig();
        ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Swap cooldown: §e" + config.propertySwapCooldown + "§r ticks"), true);
        return 1;
    }

    private static int executeCooldownsUseSet(CommandContext<ServerCommandSource> ctx) {
        int ticks = IntegerArgumentType.getInteger(ctx, "ticks");
        JASDSConfig config = ConfigManager.getConfig();
        config.useCooldown = ticks;
        ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Use cooldown is set to §e" + config.useCooldown + "§r ticks"), true);
        ConfigManager.saveConfig();
        return 1;
    }

    private static int executeCooldownsSwapSet(CommandContext<ServerCommandSource> ctx) {
        int ticks = IntegerArgumentType.getInteger(ctx, "ticks");
        JASDSConfig config = ConfigManager.getConfig();
        config.propertySwapCooldown = ticks;
        ctx.getSource().sendFeedback(() -> Text.literal("§a[JASDS]§r Swap cooldown is set to §e" + config.propertySwapCooldown + "§r ticks"), true);
        ConfigManager.saveConfig();
        return 1;
    }
}
