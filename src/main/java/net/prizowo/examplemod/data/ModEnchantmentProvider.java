package net.prizowo.examplemod.data;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AllOf;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.prizowo.examplemod.Examplemod;
import net.prizowo.examplemod.enchantment.effect.ApplyEvilEffect;

public class ModEnchantmentProvider {
    public static final ResourceKey<Enchantment> EVIL = registerKey("evil");

    private static ResourceKey<Enchantment> registerKey(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, Examplemod.prefix(name));
    }

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<Item> items = context.lookup(Registries.ITEM);

        register(context, EVIL, new Enchantment.Builder(Enchantment.definition(
                items.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
                1, 3,
                Enchantment.dynamicCost(1, 10),
                Enchantment.dynamicCost(15, 10),
                8,
                EquipmentSlotGroup.MAINHAND))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK,
                        EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM,
                        AllOf.entityEffects(
                                new ApplyEvilEffect(
                                    LevelBasedValue.constant(400.0F),
                                    LevelBasedValue.perLevel(1.0F))),
                        LootItemRandomChanceCondition.randomChance(
                            ConstantValue.exactly(1.0F))));
    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }
} 