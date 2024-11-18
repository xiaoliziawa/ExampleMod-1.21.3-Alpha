package net.prizowo.examplemod.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.prizowo.examplemod.Examplemod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

public class EvilEffect extends MobEffect {
    public static final ResourceLocation MOVEMENT_SPEED_MODIFIER = Examplemod.prefix("evil_slowdown");
    public static final double EVIL_MULTIPLIER = -0.5D;

    public EvilEffect() {
        super(MobEffectCategory.HARMFUL, 0x56CBFD);
        this.addAttributeModifier(
            Attributes.MOVEMENT_SPEED,
            MOVEMENT_SPEED_MODIFIER,
            EVIL_MULTIPLIER,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(@NotNull ServerLevel level, LivingEntity entity, int amplifier) {
        entity.setIsInPowderSnow(true);
        if (amplifier > 0) {
            entity.setTicksFrozen(Math.min(entity.getTicksRequiredToFreeze(), entity.getTicksFrozen() + 2));
        }
        return true;
    }
} 