package net.thiagoaa.redstonetimer.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

public class RedstoneTimerScreenHandler extends ScreenHandler {

    private final BlockPos pos;
    private final int seconds;
    private final int onSeconds;
    private final boolean loop;
    private final boolean enabled;
    private final boolean autonomousStart;

    public RedstoneTimerScreenHandler(int syncId, PlayerInventory playerInventory, RedstoneTimerScreenData data) {
        super(ModScreenHandlers.REDSTONE_TIMER_SCREEN_HANDLER, syncId);

        this.pos = data.pos();
        this.seconds = data.seconds();
        this.onSeconds = data.onSeconds();
        this.loop = data.loop();
        this.enabled = data.enabled();
        this.autonomousStart = data.autonomousStart();
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getOnSeconds() {
        return onSeconds;
    }

    public boolean isLoop() {
        return loop;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAutonomousStart() {
        return autonomousStart;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }
}