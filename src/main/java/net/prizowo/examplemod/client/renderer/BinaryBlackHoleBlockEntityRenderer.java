package net.prizowo.examplemod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.prizowo.examplemod.block.entity.BinaryBlackHoleBlockEntity;
import org.jetbrains.annotations.NotNull;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BinaryBlackHoleBlockEntityRenderer implements BlockEntityRenderer<BinaryBlackHoleBlockEntity> {
    private static final BlockState BLACK_HOLE_BLOCK = Blocks.OBSIDIAN.defaultBlockState();
    private static final float RENDER_HEIGHT = 2.0f;
    private static final float BLACK_HOLE_SCALE = 0.4f;

    public BinaryBlackHoleBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BinaryBlackHoleBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                      @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, RENDER_HEIGHT, 0.5);
        
        renderBlackHole(blockEntity.getBlackHole1Pos(), poseStack, bufferSource, packedLight, packedOverlay);
        renderBlackHole(blockEntity.getBlackHole2Pos(), poseStack, bufferSource, packedLight, packedOverlay);
        renderBlackHole(blockEntity.getBlackHole3Pos(), poseStack, bufferSource, packedLight, packedOverlay);
        
        poseStack.popPose();
    }

    private void renderBlackHole(Vec3 pos, PoseStack poseStack,
                               MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(pos.x, pos.y, pos.z);
        
        float time = (float) (System.currentTimeMillis() % 4000) / 4000.0f;
        poseStack.mulPose(Axis.YP.rotationDegrees(time * 360.0f));
        
        poseStack.scale(BLACK_HOLE_SCALE, BLACK_HOLE_SCALE, BLACK_HOLE_SCALE);
        poseStack.translate(-0.5, -0.5, -0.5);
        
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
            BLACK_HOLE_BLOCK, poseStack, bufferSource, packedLight, packedOverlay);
        
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(BinaryBlackHoleBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }

    @Override
    public boolean shouldRender(BinaryBlackHoleBlockEntity blockEntity, Vec3 cameraPos) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(BinaryBlackHoleBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(
            pos.getX() - 32.0, pos.getY() - 32.0, pos.getZ() - 32.0,
            pos.getX() + 32.0, pos.getY() + 32.0, pos.getZ() + 32.0
        );
    }
} 