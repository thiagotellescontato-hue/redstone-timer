package net.thiagoaa.redstonetimer.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.thiagoaa.redstonetimer.RedstoneTimer;

public class ModScreenHandlers {

    public static final ScreenHandlerType<RedstoneTimerScreenHandler> REDSTONE_TIMER_SCREEN_HANDLER =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(RedstoneTimer.MOD_ID, "redstone_timer"),
                    new ExtendedScreenHandlerType<>(
                            RedstoneTimerScreenHandler::new,
                            RedstoneTimerScreenData.PACKET_CODEC
                    )
            );

    public static void registerScreenHandlers() {
        RedstoneTimer.LOGGER.info("Registering Screen Handlers for " + RedstoneTimer.MOD_ID);
    }
}