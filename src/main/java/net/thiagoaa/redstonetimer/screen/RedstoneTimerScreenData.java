package net.thiagoaa.redstonetimer.screen;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;

public record RedstoneTimerScreenData(
        BlockPos pos,
        int seconds,
        int onSeconds,
        boolean loop,
        boolean enabled,
        boolean autonomousStart
) {

    public static final PacketCodec<RegistryByteBuf, RedstoneTimerScreenData> PACKET_CODEC =
            PacketCodec.of(
                    (value, buf) -> {
                        buf.writeBlockPos(value.pos());
                        buf.writeInt(value.seconds());
                        buf.writeInt(value.onSeconds());
                        buf.writeBoolean(value.loop());
                        buf.writeBoolean(value.enabled());
                        buf.writeBoolean(value.autonomousStart());
                    },
                    buf -> new RedstoneTimerScreenData(
                            buf.readBlockPos(),
                            buf.readInt(),
                            buf.readInt(),
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean()
                    )
            );
}