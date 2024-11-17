package net.prizowo.examplemod.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.prizowo.examplemod.block.entity.BinaryBlackHoleBlockEntity;
import net.prizowo.examplemod.init.ModBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BinaryBlackHoleBlock extends BaseEntityBlock {
    public static final MapCodec<BinaryBlackHoleBlock> CODEC = simpleCodec(BinaryBlackHoleBlock::new);

    public BinaryBlackHoleBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new BinaryBlackHoleBlockEntity(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide) {
            return null;
        }
        return type == ModBlockEntities.BINARY_BLACK_HOLE_BLOCK_ENTITY.get() ? 
            BaseEntityBlock.createTickerHelper(
                type, 
                ModBlockEntities.BINARY_BLACK_HOLE_BLOCK_ENTITY.get(), 
                (level1, pos, state1, blockEntity) -> BinaryBlackHoleBlockEntity.tick(level1, pos, state1, blockEntity)
            ) : null;
    }
}
