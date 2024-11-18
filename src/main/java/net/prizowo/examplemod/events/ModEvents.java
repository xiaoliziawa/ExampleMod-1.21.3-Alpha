package net.prizowo.examplemod.events;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.prizowo.examplemod.Examplemod;
import net.prizowo.examplemod.entity.RideableBee;
import net.prizowo.examplemod.network.SwarmTriggerPacket;

@EventBusSubscriber(modid = Examplemod.MODID)
public class ModEvents {
    
    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();
        if (player.level().isClientSide && player.getVehicle() instanceof RideableBee) {
            PacketDistributor.sendToServer(new SwarmTriggerPacket());
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide && player.getVehicle() instanceof RideableBee bee) {
            bee.summonSwarm();
        }
    }
} 