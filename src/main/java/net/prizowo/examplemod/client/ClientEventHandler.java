package net.prizowo.examplemod.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.prizowo.examplemod.Examplemod;
import net.prizowo.examplemod.block.entity.OrbitalBlockEntity;

@EventBusSubscriber(modid = Examplemod.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (KeyBindings.SPEED_DOWN.consumeClick()) {
            OrbitalBlockEntity.adjustSpeedMultiplier(0.5f);  // 减速一半
        }
        while (KeyBindings.SPEED_UP.consumeClick()) {
            OrbitalBlockEntity.adjustSpeedMultiplier(2.0f);  // 加速一倍
        }
    }

} 