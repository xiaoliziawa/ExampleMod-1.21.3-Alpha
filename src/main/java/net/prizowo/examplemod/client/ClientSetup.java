package net.prizowo.examplemod.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.prizowo.examplemod.Examplemod;
import net.prizowo.examplemod.client.renderer.OrbitalBlockEntityRenderer;
import net.prizowo.examplemod.client.renderer.BinaryBlackHoleBlockEntityRenderer;
import net.prizowo.examplemod.init.ModBlockEntities;
import net.prizowo.examplemod.registry.entity.ModEntityTypes;

@EventBusSubscriber(modid = Examplemod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            BlockEntityRenderers.register(ModBlockEntities.ORBITAL_BLOCK_ENTITY.get(), OrbitalBlockEntityRenderer::new);
            BlockEntityRenderers.register(ModBlockEntities.BINARY_BLACK_HOLE_BLOCK_ENTITY.get(), BinaryBlackHoleBlockEntityRenderer::new);
            
            EntityRenderers.register(ModEntityTypes.RIDEABLE_BEE.get(), BeeRenderer::new);
        });
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.DESCEND_KEY);
        event.register(KeyBindings.SPEED_UP);
        event.register(KeyBindings.SPEED_DOWN);
        event.register(KeyBindings.TOGGLE_HEALTH_BAR);
    }
} 