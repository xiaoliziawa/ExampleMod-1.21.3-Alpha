package net.prizowo.examplemod.network;

import net.prizowo.examplemod.Examplemod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Examplemod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkHandler {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Examplemod.MODID)
            .versioned("1.0.0");

        registrar.playToServer(
            SwarmTriggerPacket.TYPE,
            SwarmTriggerPacket.STREAM_CODEC,
            SwarmTriggerPacket::handle
        );

        registrar.playToClient(
            OrbitalRenderPacket.TYPE,
            OrbitalRenderPacket.STREAM_CODEC,
            OrbitalRenderPacket::handle
        );
    }
} 