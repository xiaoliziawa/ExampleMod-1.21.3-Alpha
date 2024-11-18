package net.prizowo.examplemod.enchantment;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.prizowo.examplemod.Examplemod;
import net.prizowo.examplemod.enchantment.effect.ApplyEvilEffect;

public class ModEnchantmentEffects {
    public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> ENTITY_EFFECTS = 
        DeferredRegister.create(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Examplemod.MODID);

    public static final MapCodec<ApplyEvilEffect> APPLY_EVIL = net.prizowo.examplemod.enchantment.effect.ApplyEvilEffect.CODEC;

    static {
        ENTITY_EFFECTS.register("apply_evil", () -> APPLY_EVIL);
    }
} 