package net.prizowo.examplemod;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.prizowo.examplemod.client.ClientSetup;
import net.prizowo.examplemod.entity.RideableBee;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.api.distmarker.Dist;
import net.prizowo.examplemod.init.ModBlockEntities;
import net.prizowo.examplemod.init.ModBlocks;
import net.prizowo.examplemod.init.ModItems;
import net.prizowo.examplemod.network.NetworkHandler;
import net.prizowo.examplemod.registry.entity.ModEntityTypes;

@Mod(Examplemod.MODID)
public class Examplemod {
    public static final String MODID = "examplemod";

    public Examplemod(IEventBus modEventBus) {
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModEntityTypes.ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(this::registerEntityAttributes);
        modEventBus.register(NetworkHandler.class);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.register(ClientSetup.class);
        }
    }
    
    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.RIDEABLE_BEE.get(), RideableBee.createAttributes().build());
    }

    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}