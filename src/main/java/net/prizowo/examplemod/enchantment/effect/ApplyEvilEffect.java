package net.prizowo.examplemod.enchantment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record ApplyEvilEffect(LevelBasedValue duration, LevelBasedValue amplifier) implements EnchantmentEntityEffect {
    public static final MapCodec<ApplyEvilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("duration").forGetter(net.prizowo.examplemod.enchantment.effect.ApplyEvilEffect::duration),
            LevelBasedValue.CODEC.fieldOf("amplifier").forGetter(net.prizowo.examplemod.enchantment.effect.ApplyEvilEffect::amplifier))
            .apply(instance, net.prizowo.examplemod.enchantment.effect.ApplyEvilEffect::new));

    @Override
    public void apply(@NotNull ServerLevel level, int enchantLevel, @NotNull EnchantedItemInUse item, @NotNull Entity victim, @NotNull Vec3 position) {
        if (victim instanceof LivingEntity entity) {
            int duration = Math.round(this.duration.calculate(enchantLevel) * 20.0F);
            int amplifier = Math.max(0, Math.round(this.amplifier.calculate(enchantLevel))); 

            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, amplifier));
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration, amplifier));

            if (enchantLevel >= 2) {
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration / 2, 0));
            }

            if (enchantLevel >= 3) {
                entity.addEffect(new MobEffectInstance(MobEffects.WITHER, duration / 4, 0));
            }

            entity.setIsInPowderSnow(true);
        }
    }

    @Override
    public @NotNull MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
} 