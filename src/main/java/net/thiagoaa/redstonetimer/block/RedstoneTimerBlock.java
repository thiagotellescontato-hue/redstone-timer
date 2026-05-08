package net.thiagoaa.redstonetimer.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.thiagoaa.redstonetimer.block.entity.RedstoneTimerBlockEntity;
import net.thiagoaa.redstonetimer.config.RedstoneTimerConfig;
import org.jetbrains.annotations.Nullable;

public class RedstoneTimerBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty OUTPUT_POWERED = BooleanProperty.of("output_powered");

    public static final MapCodec<RedstoneTimerBlock> CODEC =
            createCodec(settings -> new RedstoneTimerBlock());

    private static final int CLICK_INTERVAL_TICKS = 5;
    private static final float CLICK_VOLUME = 0.12f;
    private static final float CLICK_PITCH = 1.15f;

    public RedstoneTimerBlock() {
        super(AbstractBlock.Settings.create()
                .strength(3f)
                .sounds(BlockSoundGroup.STONE)
                .nonOpaque()
                .solidBlock((state, world, pos) -> false)
                .suffocates((state, world, pos) -> false)
                .blockVision((state, world, pos) -> false)
        );

        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(OUTPUT_POWERED, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, OUTPUT_POWERED);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RedstoneTimerBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(OUTPUT_POWERED, false);
    }

    private Direction getInputSide(BlockState state) {
        return state.get(FACING);
    }

    private Direction getOutputSide(BlockState state) {
        return state.get(FACING).getOpposite();
    }

    private BlockPos getInputPos(BlockState state, BlockPos pos) {
        return pos.offset(getInputSide(state));
    }

    private BlockPos getOutputPos(BlockState state, BlockPos pos) {
        return pos.offset(getOutputSide(state));
    }

    private boolean isReceivingFrontRedstone(World world, BlockPos pos, BlockState state) {
        BlockPos inputPos = getInputPos(state, pos);
        BlockState inputState = world.getBlockState(inputPos);

        if (inputState.getBlock() instanceof RedstoneTimerBlock) {
            return false;
        }

        if (inputState.contains(Properties.POWER)
                && inputState.get(Properties.POWER) > 0) {
            return true;
        }

        if (inputState.contains(Properties.POWERED)
                && inputState.get(Properties.POWERED)) {
            return true;
        }

        return world.getEmittedRedstonePower(inputPos, getInputSide(state)) > 0
                || world.getEmittedRedstonePower(inputPos, getInputSide(state).getOpposite()) > 0;
    }

    private void updateOutputNeighbors(World world, BlockPos pos, BlockState state) {
        world.updateNeighbors(pos, this);
        world.updateNeighbors(getOutputPos(state, pos), this);
    }

    private void playTimerClick(World world, BlockPos pos) {
        if (!RedstoneTimerConfig.blockSound) {
            return;
        }

        world.playSound(
                null,
                pos,
                SoundEvents.BLOCK_LEVER_CLICK,
                SoundCategory.BLOCKS,
                CLICK_VOLUME,
                CLICK_PITCH
        );
    }

    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected int getWeakRedstonePower(
            BlockState state,
            BlockView world,
            BlockPos pos,
            Direction direction
    ) {
        if (!state.get(OUTPUT_POWERED)) {
            return 0;
        }

        // Só emite sinal pelo back/output.
        return direction == getOutputSide(state).getOpposite() ? 15 : 0;
    }

    @Override
    protected int getStrongRedstonePower(
            BlockState state,
            BlockView world,
            BlockPos pos,
            Direction direction
    ) {
        // Não emite strong power.
        // Isso evita o bloco energizar lâmpadas/blocos adjacentes de forma lateral.
        return 0;
    }

    @Override
    protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && world.getBlockEntity(pos) instanceof RedstoneTimerBlockEntity blockEntity) {
            player.openHandledScreen(blockEntity);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) {
            return;
        }

        // Só aceita update vindo exatamente da frente/input.
        // Isso impede lanterna/tocha/lâmpada no output de iniciar o timer.
        if (!sourcePos.equals(getInputPos(state, pos))) {
            return;
        }

        if (isReceivingFrontRedstone(world, pos, state)) {
            startTimer(world, pos, state);
        }
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (world.getBlockEntity(pos) instanceof RedstoneTimerBlockEntity blockEntity) {
                blockEntity.resetRuntime();
            }

            if (state.get(OUTPUT_POWERED)) {
                BlockState offState = state.with(OUTPUT_POWERED, false);
                updateOutputNeighbors(world, pos, offState);
            }
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    public void startTimer(World world, BlockPos pos, BlockState state) {
        if (!(world.getBlockEntity(pos) instanceof RedstoneTimerBlockEntity blockEntity)) {
            return;
        }

        if (!blockEntity.isEnabled()) {
            return;
        }

        if (blockEntity.isRunning()) {
            return;
        }

        if (state.get(OUTPUT_POWERED)) {
            return;
        }

        blockEntity.setRunning(true);
        blockEntity.setDelayTicksRemaining(blockEntity.getSeconds() * 20);
        blockEntity.setOnTicksRemaining(0);

        world.scheduleBlockTick(pos, this, 1);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!(world.getBlockEntity(pos) instanceof RedstoneTimerBlockEntity blockEntity)) {
            return;
        }

        if (!blockEntity.isEnabled()) {
            blockEntity.resetRuntime();

            if (state.get(OUTPUT_POWERED)) {
                BlockState unpoweredState = state.with(OUTPUT_POWERED, false);
                world.setBlockState(pos, unpoweredState, Block.NOTIFY_ALL);
                updateOutputNeighbors(world, pos, unpoweredState);
            }

            return;
        }

        boolean autonomous = blockEntity.isAutonomousStart();

        // Proteção contra tick antigo de timer quebrado/recolocado.
        if (!blockEntity.isRunning() && !state.get(OUTPUT_POWERED)) {
            return;
        }

        // Fase 1: contando Delay.
        if (!state.get(OUTPUT_POWERED)) {
            if (blockEntity.getDelayTicksRemaining() > 0) {
                blockEntity.decreaseDelayTicksRemaining(CLICK_INTERVAL_TICKS);
                world.scheduleBlockTick(pos, this, CLICK_INTERVAL_TICKS);
                return;
            }

            BlockState poweredState = state.with(OUTPUT_POWERED, true);

            world.setBlockState(pos, poweredState, Block.NOTIFY_ALL);
            updateOutputNeighbors(world, pos, poweredState);

            blockEntity.setOnTicksRemaining(blockEntity.getOnSeconds() * 20);

            playTimerClick(world, pos);
            world.scheduleBlockTick(pos, this, CLICK_INTERVAL_TICKS);
            return;
        }

        // Fase 2: contando On Time.
        blockEntity.decreaseOnTicksRemaining(CLICK_INTERVAL_TICKS);

        if (blockEntity.getOnTicksRemaining() <= 0) {
            BlockState unpoweredState = state.with(OUTPUT_POWERED, false);

            world.setBlockState(pos, unpoweredState, Block.NOTIFY_ALL);
            updateOutputNeighbors(world, pos, unpoweredState);

            blockEntity.resetRuntime();

            if (blockEntity.isLoop() && blockEntity.isEnabled()) {
                if (autonomous || isReceivingFrontRedstone(world, pos, world.getBlockState(pos))) {
                    startTimer(world, pos, world.getBlockState(pos));
                }
            } else if (autonomous && blockEntity.isEnabled()) {
                blockEntity.setAutonomousStart(false);
            }

            return;
        }

        playTimerClick(world, pos);
        world.scheduleBlockTick(pos, this, CLICK_INTERVAL_TICKS);
    }
}