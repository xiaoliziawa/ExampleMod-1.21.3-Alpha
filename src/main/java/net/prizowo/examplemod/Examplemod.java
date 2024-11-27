package net.prizowo.examplemod;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.prizowo.examplemod.client.ClientSetup;
import net.prizowo.examplemod.data.DataGenerators;
import net.prizowo.examplemod.entity.RideableBee;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.api.distmarker.Dist;
import net.prizowo.examplemod.init.ModBlockEntities;
import net.prizowo.examplemod.init.ModBlocks;
import net.prizowo.examplemod.init.ModItems;
import net.prizowo.examplemod.network.NetworkHandler;
import net.prizowo.examplemod.registry.entity.ModEntityTypes;
import net.prizowo.examplemod.init.ModCreativeModeTabs;
import net.prizowo.examplemod.enchantment.ModEnchantmentEffects;
import net.prizowo.examplemod.init.ModEffects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Examplemod.MODID)
public class Examplemod {
    public static final String MODID = "examplemod";
    private static final Logger LOGGER = LoggerFactory.getLogger("ExampleMod");

    public Examplemod(IEventBus modEventBus) {

        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModEntityTypes.ENTITY_TYPES.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        
        ModEnchantmentEffects.ENTITY_EFFECTS.register(modEventBus);
        
        ModEffects.register(modEventBus);
        
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