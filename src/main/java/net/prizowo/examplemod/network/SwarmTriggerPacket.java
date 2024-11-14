package net.prizowo.examplemod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.prizowo.examplemod.Examplemod;
import net.prizowo.examplemod.entity.RideableBee;
import org.jetbrains.annotations.NotNull;

public record SwarmTriggerPacket() implements CustomPacketPayload {
    public static final Type<SwarmTriggerPacket> TYPE = new Type<>(Examplemod.prefix("swarm_trigger"));
    
    public static final StreamCodec<FriendlyByteBuf, SwarmTriggerPacket> STREAM_CODEC = StreamCodec.composite(
        StreamCodec.of(
            (buf, packet) -> {},
            buf -> new SwarmTriggerPacket()
        ),
        packet -> packet,
        packet -> packet
    );

    public static void handle(final SwarmTriggerPacket data, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player && 
                player.getVehicle() instanceof RideableBee bee) {
                bee.summonSwarm();
            }
        });
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
} 