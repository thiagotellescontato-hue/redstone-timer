package net.thiagoaa.redstonetimer;

import net.fabricmc.api.ModInitializer;
import net.thiagoaa.redstonetimer.block.ModBlocks;
import net.thiagoaa.redstonetimer.block.entity.ModBlockEntities;
import net.thiagoaa.redstonetimer.network.ModMessages;
import net.thiagoaa.redstonetimer.screen.ModScreenHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedstoneTimer implements ModInitializer {

    public static final String MOD_ID = "redstone_timer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModBlocks.registerModBlocks();
        ModBlockEntities.registerBlockEntities();
        ModScreenHandlers.registerScreenHandlers();
        ModMessages.registerC2SPackets();
    }
}