package net.prizowo.examplemod.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.prizowo.examplemod.Examplemod;
import net.prizowo.examplemod.block.OrbitalBlock;
import net.prizowo.examplemod.block.BinaryBlackHoleBlock;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Examplemod.MODID);

    public static final Supplier<Block> ORBITAL_BLOCK = BLOCKS.register(
            "orbital_block",
            () -> new OrbitalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .noOcclusion()
                    .lightLevel(state -> 15)
                    .strength(50.0F, 1200.0F)
                    .setId(ResourceKey.create(Registries.BLOCK,
                            ResourceLocation.fromNamespaceAndPath(Examplemod.MODID, "orbital_block")))
                    .noLootTable()
            )
    );

    public static final Supplier<Block> BINARY_BLACK_HOLE_BLOCK = BLOCKS.register(
            "binary_black_hole_block",
            () -> new BinaryBlackHoleBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .noOcclusion()
                    .lightLevel(state -> 15)
                    .strength(50.0F, 1200.0F)
                    .setId(ResourceKey.create(Registries.BLOCK,
                            ResourceLocation.fromNamespaceAndPath(Examplemod.MODID, "binary_black_hole_block")))
                    .noLootTable()
            )
    );

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}