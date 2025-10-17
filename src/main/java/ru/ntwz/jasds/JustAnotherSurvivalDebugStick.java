package ru.ntwz.jasds;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ntwz.jasds.callback.AttackBlockCallbackRegistry;

public class JustAnotherSurvivalDebugStick implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Just Another Survival Debug Stick");

    @Override
    public void onInitialize() {
        LOGGER.info("Mod starting");
        AttackBlockCallbackRegistry.register(LOGGER);
        LOGGER.info("Mod has been initialized!");
    }
}
