package net.prizowo.examplemod.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.prizowo.examplemod.Examplemod;
import net.prizowo.examplemod.block.entity.OrbitalBlockEntity;
import org.jetbrains.annotations.NotNull;

public record OrbitalRenderPacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<OrbitalRenderPacket> TYPE = new Type<>(Examplemod.prefix("orbital_render"));
    
    public static final StreamCodec<FriendlyByteBuf, OrbitalRenderPacket> STREAM_CODEC = StreamCodec.composite(
        StreamCodec.of(
            (buf, packet) -> buf.writeBlockPos(packet.pos),
            buf -> new OrbitalRenderPacket(buf.readBlockPos())
        ),
        packet -> packet,
        packet -> packet
    );

    public static void handle(final OrbitalRenderPacket data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() != null) {
                BlockEntity blockEntity = ctx.player().level().getBlockEntity(data.pos);
                if (blockEntity instanceof OrbitalBlockEntity orbital) {
                    orbital.setForceRender(true);
                }
            }
        });
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
} 