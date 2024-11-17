package net.prizowo.examplemod.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.prizowo.examplemod.Examplemod;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(BuiltInRegistries.ITEM, Examplemod.MODID);

    public static final Supplier<Item> ORBITAL_BLOCK_ITEM = ITEMS.register(
            "orbital_block",
            () -> new BlockItem(ModBlocks.ORBITAL_BLOCK.get(),
                    new Item.Properties()
                            .setId(ResourceKey.create(Registries.ITEM,
                                    ResourceLocation.fromNamespaceAndPath(Examplemod.MODID, "orbital_block")))
            )
    );

    public static final Supplier<Item> BINARY_BLACK_HOLE_BLOCK_ITEM = ITEMS.register(
            "binary_black_hole_block",
            () -> new BlockItem(ModBlocks.BINARY_BLACK_HOLE_BLOCK.get(),
                    new Item.Properties()
                            .setId(ResourceKey.create(Registries.ITEM,
                                    ResourceLocation.fromNamespaceAndPath(Examplemod.MODID, "binary_black_hole_block")))
            )
    );

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
} 