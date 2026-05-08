package net.thiagoaa.redstonetimer.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.thiagoaa.redstonetimer.RedstoneTimer;
import net.thiagoaa.redstonetimer.item.RedstoneTimerBlockItem;

public class ModBlocks {

    public static final RedstoneTimerBlock REDSTONE_TIMER = registerBlock(
            new RedstoneTimerBlock()
    );

    private static RedstoneTimerBlock registerBlock(RedstoneTimerBlock block) {
        registerBlockItem(block);
        return Registry.register(
                Registries.BLOCK,
                Identifier.of(RedstoneTimer.MOD_ID, "redstone_timer"),
                block
        );
    }

    private static void registerBlockItem(RedstoneTimerBlock block) {
        Registry.register(
                Registries.ITEM,
                Identifier.of(RedstoneTimer.MOD_ID, "redstone_timer"),
                new RedstoneTimerBlockItem(block, new Item.Settings())
        );
    }

    public static void registerModBlocks() {
        RedstoneTimer.LOGGER.info("Registering Mod Blocks for " + RedstoneTimer.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> entries.add(REDSTONE_TIMER));
    }
}