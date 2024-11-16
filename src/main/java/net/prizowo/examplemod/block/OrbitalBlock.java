package net.prizowo.examplemod.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import net.prizowo.examplemod.block.entity.OrbitalBlockEntity;
import net.prizowo.examplemod.init.ModBlockEntities;
import net.prizowo.examplemod.network.OrbitalRenderPacket;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class OrbitalBlock extends BaseEntityBlock {
    public static final MapCodec<OrbitalBlock> CODEC = simpleCodec(OrbitalBlock::new);

    public OrbitalBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        OrbitalBlockEntity blockEntity = new OrbitalBlockEntity(pos, state);
        if (state.getBlock() == this) {
            PacketDistributor.sendToAllPlayers(new OrbitalRenderPacket(pos));
        }
        return blockEntity;
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide && state.getBlock() != oldState.getBlock()) {
            PacketDistributor.sendToAllPlayers(new OrbitalRenderPacket(pos));
        }
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.ORBITAL_BLOCK_ENTITY.get(), OrbitalBlockEntity::tick);
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof OrbitalBlockEntity orbital) {
                orbital.setForceRender(false);
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }
} 