package net.thiagoaa.redstonetimer;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.thiagoaa.redstonetimer.screen.ModScreenHandlers;
import net.thiagoaa.redstonetimer.screen.client.RedstoneTimerScreen;

public class RedstoneTimerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        RedstoneTimer.LOGGER.info("[Redstone Timer] Client initializer carregou");

        HandledScreens.register(
                ModScreenHandlers.REDSTONE_TIMER_SCREEN_HANDLER,
                RedstoneTimerScreen::new
        );
    }
}