package ru.ntwz.survivaldebugstick;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ntwz.survivaldebugstick.callback.AttackBlockCallbackRegistry;

public class SurvivalDebugStick implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(SurvivalDebugStick.class);

    @Override
    public void onInitialize() {
        LOGGER.info("SurvivalDebugStick starting");
        AttackBlockCallbackRegistry.register(LOGGER);
        LOGGER.info("SurvivalDebugStick has been initialized!");
    }
}
