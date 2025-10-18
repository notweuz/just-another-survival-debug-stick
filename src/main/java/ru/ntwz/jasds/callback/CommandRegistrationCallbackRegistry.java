package ru.ntwz.jasds.callback;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import ru.ntwz.jasds.config.ConfigManager;

public class CommandRegistrationCallbackRegistry {
    public static void register(Logger logger) {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registry, env) -> {
            dispatcher.register(CommandManager.literal("jasds")
                    .requires(CommandRegistrationCallbackRegistry::hasPermission)
                    .then(CommandManager.literal("reload")
                            .executes(CommandRegistrationCallbackRegistry::executeConfigReload)
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
}
