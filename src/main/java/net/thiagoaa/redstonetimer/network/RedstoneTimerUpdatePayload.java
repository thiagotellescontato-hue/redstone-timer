package net.thiagoaa.redstonetimer.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.thiagoaa.redstonetimer.RedstoneTimer;

public record RedstoneTimerUpdatePayload(
        BlockPos pos,
        int seconds,
        int onSeconds,
        boolean loop,
        boolean enabled,
        boolean autonomousStart
) implements CustomPayload {

    public static final Id<RedstoneTimerUpdatePayload> ID =
            new Id<>(Identifier.of(RedstoneTimer.MOD_ID, "update_timer"));

    public static final PacketCodec<PacketByteBuf, RedstoneTimerUpdatePayload> CODEC =
            PacketCodec.of(
                    (value, buf) -> {
                        buf.writeBlockPos(value.pos());
                        buf.writeInt(value.seconds());
                        buf.writeInt(value.onSeconds());
                        buf.writeBoolean(value.loop());
                        buf.writeBoolean(value.enabled());
                        buf.writeBoolean(value.autonomousStart());
                    },
                    buf -> new RedstoneTimerUpdatePayload(
                            buf.readBlockPos(),
                            buf.readInt(),
                            buf.readInt(),
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean()
                    )
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}