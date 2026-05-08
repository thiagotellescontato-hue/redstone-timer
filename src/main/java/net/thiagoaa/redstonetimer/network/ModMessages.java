package net.thiagoaa.redstonetimer.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.thiagoaa.redstonetimer.block.RedstoneTimerBlock;
import net.thiagoaa.redstonetimer.block.entity.RedstoneTimerBlockEntity;

public class ModMessages {

    public static void registerC2SPackets() {

        PayloadTypeRegistry.playC2S().register(
                RedstoneTimerUpdatePayload.ID,
                RedstoneTimerUpdatePayload.CODEC
        );

        ServerPlayNetworking.registerGlobalReceiver(
                RedstoneTimerUpdatePayload.ID,
                (payload, context) -> context.server().execute(() -> {

                    ServerWorld world = context.player().getServerWorld();
                    BlockPos pos = payload.pos();

                    if (!(world.getBlockEntity(pos) instanceof RedstoneTimerBlockEntity blockEntity)) {
                        return;
                    }

                    BlockState state = world.getBlockState(pos);

                    blockEntity.setSeconds(payload.seconds());
                    blockEntity.setOnSeconds(payload.onSeconds());
                    blockEntity.setLoop(payload.loop());
                    blockEntity.setEnabled(payload.enabled());
                    blockEntity.setAutonomousStart(payload.autonomousStart());

                    // Cancela qualquer ciclo anterior.
                    // Isso resolve o bug do 3600 continuar "preso" mesmo depois de mudar para 2.
                    blockEntity.resetRuntime();

                    if (state.getBlock() instanceof RedstoneTimerBlock timerBlock) {

                        // Se estava ligado, desliga visualmente e remove output.
                        if (state.get(RedstoneTimerBlock.OUTPUT_POWERED)) {
                            BlockState offState = state.with(RedstoneTimerBlock.OUTPUT_POWERED, false);

                            world.setBlockState(pos, offState, Block.NOTIFY_ALL);
                            world.updateNeighbors(pos, timerBlock);
                        }

                        // Se Autonomous Start estiver ON, começa um novo ciclo usando os valores novos.
                        if (payload.enabled() && payload.autonomousStart()) {
                            timerBlock.startTimer(
                                    world,
                                    pos,
                                    world.getBlockState(pos)
                            );
                        }
                    }
                })
        );
    }
}