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
import net.prizowo.examplemod.block.entity.OrbitalBlockEntity;
import net.prizowo.examplemod.init.ModBlockEntities;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.Shapes;

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
        return new OrbitalBlockEntity(pos, state);
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return level.isClientSide ?
                createTickerHelper(type, ModBlockEntities.ORBITAL_BLOCK_ENTITY.get(), OrbitalBlockEntity::tick) :
                null;  // 服务端返回null，不执行tick
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (level.getBlockEntity(pos) instanceof OrbitalBlockEntity blockEntity) {
            VoxelShape finalShape = box(4, 4, 4, 12, 12, 12); // 太阳的碰撞箱
            
            // 只给较大的行星添加碰撞箱
            for (OrbitalBlockEntity.Planet planet : blockEntity.getPlanets()) {
                float angle = planet.getAngle();
                float radius = planet.getOrbitRadius();
                float size = planet.getSize();
                
                // 只给大于一定尺寸的行星添加碰撞箱
                if (size >= 0.5f) {
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    
                    double boxSize = size * 4; // 减小碰撞箱的大小
                    double centerX = 8 + x * 16;
                    double centerZ = 8 + z * 16;
                    
                    // 确保最小值始终小于最大值
                    double minX = Math.min(centerX - boxSize, centerX + boxSize);
                    double maxX = Math.max(centerX - boxSize, centerX + boxSize);
                    double minZ = Math.min(centerZ - boxSize, centerZ + boxSize);
                    double maxZ = Math.max(centerZ - boxSize, centerZ + boxSize);
                    
                    // 限制在方块范围内
                    minX = Math.max(0, Math.min(16, minX));
                    maxX = Math.max(0, Math.min(16, maxX));
                    minZ = Math.max(0, Math.min(16, minZ));
                    maxZ = Math.max(0, Math.min(16, maxZ));
                    
                    if (minX < maxX && minZ < maxZ) { // 确保有效的碰撞箱
                        VoxelShape planetShape = box(minX, 4, minZ, maxX, 12, maxZ);
                        finalShape = Shapes.or(finalShape, planetShape);
                    }
                }
            }
            
            return finalShape;
        }
        return box(0, 0, 0, 16, 16, 16);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return this.getShape(state, level, pos, context);
    }
} 