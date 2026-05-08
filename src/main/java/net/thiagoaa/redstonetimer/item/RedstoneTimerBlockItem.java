package net.thiagoaa.redstonetimer.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

public class RedstoneTimerBlockItem extends BlockItem {

    public RedstoneTimerBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.redstone_timer.redstone_timer.1"));
        tooltip.add(Text.translatable("tooltip.redstone_timer.redstone_timer.2"));

        super.appendTooltip(stack, context, tooltip, type);
    }
}