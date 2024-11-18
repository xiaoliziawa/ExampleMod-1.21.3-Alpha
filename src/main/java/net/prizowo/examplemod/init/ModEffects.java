package net.prizowo.examplemod.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.prizowo.examplemod.Examplemod;
import net.prizowo.examplemod.effect.EvilEffect;

import java.util.function.Supplier;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = 
        DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, Examplemod.MODID);

    public static final Supplier<MobEffect> EVIL = MOB_EFFECTS.register(
            "evil",
            EvilEffect::new
    );

    public static void register(IEventBus modEventBus) {
        MOB_EFFECTS.register(modEventBus);
    }
} 