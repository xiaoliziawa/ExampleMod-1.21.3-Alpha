package net.prizowo.examplemod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.prizowo.examplemod.init.ModBlockEntities;

public class BinaryBlackHoleBlockEntity extends BlockEntity {
    private float rotation = 0.0f;
    private static final float ROTATION_SPEED = 1.0f;

    public BinaryBlackHoleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BINARY_BLACK_HOLE_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BinaryBlackHoleBlockEntity blockEntity) {
        if (level.isClientSide) {
            blockEntity.rotation = (blockEntity.rotation + ROTATION_SPEED) % 360.0f;
        }
    }

    public float getRotation() {
        return rotation;
    }
} 