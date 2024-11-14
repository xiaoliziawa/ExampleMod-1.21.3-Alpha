package net.prizowo.examplemod.registry.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.prizowo.examplemod.Examplemod;
import net.prizowo.examplemod.entity.RideableBee;

import java.util.function.Supplier;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
        DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Examplemod.MODID);
        
    public static final Supplier<EntityType<RideableBee>> RIDEABLE_BEE = 
        ENTITY_TYPES.register("rideable_bee", 
            () -> EntityType.Builder.of(RideableBee::new, MobCategory.CREATURE)
                .sized(0.7F, 0.6F)
                .clientTrackingRange(8)
                .build(ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(),
                    ResourceLocation.fromNamespaceAndPath(Examplemod.MODID, "rideable_bee"))));
} 