package net.prizowo.examplemod.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.prizowo.examplemod.Examplemod;
import net.prizowo.examplemod.block.entity.OrbitalBlockEntity;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Examplemod.MODID);

    public static final Supplier<BlockEntityType<OrbitalBlockEntity>> ORBITAL_BLOCK_ENTITY = 
        BLOCK_ENTITIES.register("orbital_block",
            () -> new BlockEntityType<>(
                    OrbitalBlockEntity::new,
                ModBlocks.ORBITAL_BLOCK.get()
            )
        );

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}