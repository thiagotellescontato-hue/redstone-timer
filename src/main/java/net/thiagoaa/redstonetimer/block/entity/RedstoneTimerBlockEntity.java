package net.thiagoaa.redstonetimer.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.thiagoaa.redstonetimer.screen.RedstoneTimerScreenData;
import net.thiagoaa.redstonetimer.screen.RedstoneTimerScreenHandler;
import org.jetbrains.annotations.Nullable;

public class RedstoneTimerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<RedstoneTimerScreenData> {

    private int seconds = 0;
    private int onSeconds = 1;

    public static final int MAX_SECONDS = 3600;

    private boolean loop = false;
    private boolean enabled = true;
    private boolean running = false;
    private boolean autonomousStart = false;

    private int delayTicksRemaining = 0;
    private int onTicksRemaining = 0;

    public RedstoneTimerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REDSTONE_TIMER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public RedstoneTimerScreenData getScreenOpeningData(ServerPlayerEntity player) {
        return new RedstoneTimerScreenData(this.pos, this.seconds, this.onSeconds, this.loop, this.enabled, this.autonomousStart);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.redstone_timer.redstone_timer");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new RedstoneTimerScreenHandler(
                syncId,
                playerInventory,
                new RedstoneTimerScreenData(this.pos, this.seconds, this.onSeconds, this.loop, this.enabled, this.autonomousStart)
        );
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = Math.min(MAX_SECONDS, Math.max(0, seconds));
        markDirty();
    }

    public int getOnSeconds() {
        return onSeconds;
    }

    public void setOnSeconds(int onSeconds) {
        this.onSeconds = Math.min(MAX_SECONDS, Math.max(1, onSeconds));
        markDirty();
    }

    public int getDelayTicksRemaining() {
        return delayTicksRemaining;
    }

    public void setDelayTicksRemaining(int delayTicksRemaining) {
        this.delayTicksRemaining = Math.max(0, delayTicksRemaining);
        markDirty();
    }

    public void decreaseDelayTicksRemaining(int ticks) {
        this.delayTicksRemaining = Math.max(0, this.delayTicksRemaining - ticks);
        markDirty();
    }

    public int getOnTicksRemaining() {
        return onTicksRemaining;
    }

    public void setOnTicksRemaining(int onTicksRemaining) {
        this.onTicksRemaining = Math.max(0, onTicksRemaining);
        markDirty();
    }

    public void decreaseOnTicksRemaining(int ticks) {
        this.onTicksRemaining = Math.max(0, this.onTicksRemaining - ticks);
        markDirty();
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
        markDirty();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        markDirty();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
        markDirty();
    }

    public boolean isAutonomousStart() {
        return autonomousStart;
    }

    public void setAutonomousStart(boolean autonomousStart) {
        this.autonomousStart = autonomousStart;
        markDirty();
    }

    public void resetRuntime() {
        this.running = false;
        this.delayTicksRemaining = 0;
        this.onTicksRemaining = 0;
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        nbt.putInt("Seconds", seconds);
        nbt.putInt("OnSeconds", onSeconds);
        nbt.putInt("DelayTicksRemaining", delayTicksRemaining);
        nbt.putInt("OnTicksRemaining", onTicksRemaining);

        nbt.putBoolean("Loop", loop);
        nbt.putBoolean("Enabled", enabled);
        nbt.putBoolean("Running", running);
        nbt.putBoolean("AutonomousStart", autonomousStart);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        seconds = nbt.getInt("Seconds");
        onSeconds = nbt.contains("OnSeconds") ? nbt.getInt("OnSeconds") : 1;
        delayTicksRemaining = nbt.getInt("DelayTicksRemaining");
        onTicksRemaining = nbt.getInt("OnTicksRemaining");

        loop = nbt.getBoolean("Loop");
        enabled = !nbt.contains("Enabled") || nbt.getBoolean("Enabled");
        running = nbt.getBoolean("Running");
        autonomousStart = nbt.getBoolean("AutonomousStart");

        if (seconds < 0) {
            seconds = 0;
        }

        if (seconds > MAX_SECONDS) {
            seconds = MAX_SECONDS;
        }

        if (onSeconds <= 0) {
            onSeconds = 1;
        }

        if (onSeconds > MAX_SECONDS) {
            onSeconds = MAX_SECONDS;
        }
    }
}