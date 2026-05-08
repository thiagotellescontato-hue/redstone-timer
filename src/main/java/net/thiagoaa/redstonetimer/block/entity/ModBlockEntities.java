package net.thiagoaa.redstonetimer.block.entity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.thiagoaa.redstonetimer.RedstoneTimer;
import net.thiagoaa.redstonetimer.block.ModBlocks;

public class ModBlockEntities {

    public static final BlockEntityType<RedstoneTimerBlockEntity> REDSTONE_TIMER_BLOCK_ENTITY =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(RedstoneTimer.MOD_ID, "redstone_timer"),
                    BlockEntityType.Builder.create(
                            RedstoneTimerBlockEntity::new,
                            ModBlocks.REDSTONE_TIMER
                    ).build()
            );

    public static void registerBlockEntities() {
        RedstoneTimer.LOGGER.info("Registering Block Entities for " + RedstoneTimer.MOD_ID);
    }
}