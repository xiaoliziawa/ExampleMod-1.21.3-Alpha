package net.prizowo.examplemod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.prizowo.examplemod.block.entity.OrbitalBlockEntity;
import org.jetbrains.annotations.NotNull;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OrbitalBlockEntityRenderer implements BlockEntityRenderer<OrbitalBlockEntity> {
    private static final BlockState[] CELESTIAL_BODIES = {
        Blocks.GLOWSTONE.defaultBlockState(),        // 太阳
        Blocks.STONE.defaultBlockState(),            // 水星
        Blocks.SANDSTONE.defaultBlockState(),        // 金星
        Blocks.GRASS_BLOCK.defaultBlockState(),      // 地球
        Blocks.RED_SANDSTONE.defaultBlockState(),    // 火星
        Blocks.ORANGE_CONCRETE.defaultBlockState(),  // 木星
        Blocks.YELLOW_CONCRETE.defaultBlockState(),  // 土星
        Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState(), // 天王星
        Blocks.BLUE_CONCRETE.defaultBlockState()     // 海王星
    };

    public OrbitalBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public boolean shouldRenderOffScreen(OrbitalBlockEntity blockEntity) {
        return true;  // 总是渲染，即使在屏幕外
    }

    @Override
    public int getViewDistance() {
        return 256;  // 与信标相同的渲染距离
    }

    @Override
    public boolean shouldRender(OrbitalBlockEntity blockEntity, Vec3 cameraPos) {
        return Vec3.atCenterOf(blockEntity.getBlockPos())
                  .multiply(1.0, 0.0, 1.0)
                  .closerThan(cameraPos.multiply(1.0, 0.0, 1.0), 
                             this.getViewDistance());
    }

    @Override
    public AABB getRenderBoundingBox(OrbitalBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX() - 100.0, pos.getY(), pos.getZ() - 100.0,
                       pos.getX() + 101.0, pos.getY() + 16.0, pos.getZ() + 101.0);
    }

    @Override
    public void render(OrbitalBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                      @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        
        // 渲染太阳 - 固定在中心自转
        poseStack.pushPose();
        // 1. 移动到方块中心
        poseStack.translate(0.5, 0.5, 0.5);
        // 2. 缩放
        poseStack.scale(2.5f, 2.5f, 2.5f);
        // 3. 移动使方块中心对齐
        poseStack.translate(-0.5, -0.5, -0.5);
        // 4. 在原地自转
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.getSunRotation()));
        poseStack.translate(-0.5, -0.5, -0.5);
        
        // 渲染太阳
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
            CELESTIAL_BODIES[0], poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();

        // 渲染行星
        for (int i = 0; i < blockEntity.getPlanets().size(); i++) {
            OrbitalBlockEntity.Planet planet = blockEntity.getPlanets().get(i);
            
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            
            float angle = planet.getAngle() + (partialTick * planet.getSpeed());
            float x = (float) (Math.cos(angle) * planet.getOrbitRadius());
            float z = (float) (Math.sin(angle) * planet.getOrbitRadius());
            
            poseStack.translate(x, 0, z);
            poseStack.mulPose(Axis.ZP.rotationDegrees(planet.getTilt()));
            
            float rotation = planet.getRotation() + (partialTick * planet.getRotationSpeed());
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            
            float planetScale = planet.getSize() * 0.5f;
            poseStack.scale(planetScale, planetScale, planetScale);
            poseStack.translate(-0.5, -0.5, -0.5);
            
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                CELESTIAL_BODIES[i + 1], poseStack, bufferSource, packedLight, packedOverlay);
            
            poseStack.popPose();
        }

        // 渲染小行星带
        for (OrbitalBlockEntity.Asteroid asteroid : blockEntity.getAsteroidBelt()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            
            // 计算小行星位置
            float angle = asteroid.getAngle() + (partialTick * asteroid.getSpeed());
            float x = (float) (Math.cos(angle) * asteroid.getOrbitRadius());
            float z = (float) (Math.sin(angle) * asteroid.getOrbitRadius());
            
            // 应用位置和垂直偏移
            poseStack.translate(x, asteroid.getVerticalOffset(), z);
            
            // 应用旋转
            float rotation = asteroid.getRotation() + (partialTick * asteroid.getRotationSpeed());
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            
            // 缩放和渲染
            float scale = asteroid.getSize();
            poseStack.scale(scale, scale, scale);
            poseStack.translate(-0.5, -0.5, -0.5);
            
            // 使用圆石渲染小行星
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                Blocks.COBBLESTONE.defaultBlockState(), 
                poseStack, bufferSource, packedLight, packedOverlay);
            
            poseStack.popPose();
        }

        // 渲染哈雷彗星
        OrbitalBlockEntity.Comet comet = blockEntity.getHalleyComet();
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        
        float cometAngle = comet.getAngle();
        float cometRadius = comet.getCurrentRadius();
        float x = (float) (Math.cos(cometAngle) * cometRadius);
        float z = (float) (Math.sin(cometAngle) * cometRadius);
        
        poseStack.translate(x, 0, z);
        
        // 渲染彗星（使用冰方块）
        float cometScale = 0.3f;
        poseStack.scale(cometScale, cometScale, cometScale);
        poseStack.translate(-0.5, -0.5, -0.5);
        
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
            Blocks.PACKED_ICE.defaultBlockState(), 
            poseStack, bufferSource, packedLight, packedOverlay);
        
        poseStack.popPose();
        
        poseStack.popPose();
    }

    // 简化renderBlock方法，只用于行星渲染
    private void renderBlock(PoseStack poseStack, MultiBufferSource bufferSource, 
                             BlockState state, float scale, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
            state, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }
} 