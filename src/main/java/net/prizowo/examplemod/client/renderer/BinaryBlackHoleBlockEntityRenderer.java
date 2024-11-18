package net.prizowo.examplemod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.prizowo.examplemod.block.entity.BinaryBlackHoleBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class BinaryBlackHoleBlockEntityRenderer implements BlockEntityRenderer<BinaryBlackHoleBlockEntity> {

    // 事件视界的材质层级
    private static final BlockState[] EVENT_HORIZON_LAYERS = {
        Blocks.BLACK_CONCRETE.defaultBlockState(),      // 核心
        Blocks.BLACK_CONCRETE.defaultBlockState(),      // 内层
        Blocks.BLACK_STAINED_GLASS.defaultBlockState(), // 中内层
        Blocks.TINTED_GLASS.defaultBlockState(),        // 中外层
        Blocks.BLACK_STAINED_GLASS.defaultBlockState()  // 外层
    };

    // 光柱材质
    private static final BlockState[] BEAM_MATERIALS = {
        Blocks.LIGHT_GRAY_STAINED_GLASS.defaultBlockState(),
        Blocks.GRAY_STAINED_GLASS.defaultBlockState(),
        Blocks.BLACK_STAINED_GLASS.defaultBlockState()
    };

    // 吸积盘内层材质
    private static final BlockState[] ACCRETION_DISK_CORE = {
        Blocks.GRAY_CONCRETE.defaultBlockState(),           // 内层
        Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState(),     // 中层
        Blocks.GRAY_CONCRETE.defaultBlockState(),           // 外层
    };

    // 吸积盘外层材质
    private static final BlockState[] ACCRETION_DISK_OUTER = {
        Blocks.GRAY_STAINED_GLASS.defaultBlockState(),      // 内层
        Blocks.LIGHT_GRAY_STAINED_GLASS.defaultBlockState(), // 中层
        Blocks.GRAY_STAINED_GLASS.defaultBlockState(),      // 外层
        Blocks.BLACK_STAINED_GLASS.defaultBlockState()      // 最外层
    };

    public BinaryBlackHoleBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }
    @Override
    public void render(BinaryBlackHoleBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack,
                      @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float time = (float) ((System.nanoTime() / 1_000_000_000.0) % 1000.0);
        float globalRotation = time * (float)Math.PI * 0.3f;
        
        poseStack.pushPose();
        poseStack.translate(0.5, 15.0, 0.5);  // 提升15格高
        poseStack.scale(6.0f, 6.0f, 6.0f);    // 缩放比例
        
        // 整体倾斜角度
        float tiltAngle = 30.0f;
        poseStack.mulPose(new Quaternionf().rotateZ((float)Math.toRadians(tiltAngle)));
        
        // 渲染事件视界
        renderEventHorizon(poseStack, bufferSource, packedLight, packedOverlay, time);
        
        // 渲染吸积盘
        renderAccretionDisk(poseStack, bufferSource, packedLight, packedOverlay, time, globalRotation);
        
        // 渲染光柱
        renderEnergyBeams(poseStack, bufferSource, packedLight, packedOverlay, time);
        
        poseStack.popPose();
    }

    private void renderEventHorizon(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float time) {
        poseStack.pushPose();
        
        // 事件视界的脉动效果
        float pulseScale = 1.0f + (float)Math.sin(time * 2.0f) * 0.05f;
        
        // 渲染更大的事件视界
        for(int i = 0; i < EVENT_HORIZON_LAYERS.length; i++) {
            float layerScale = (2.0f - i * 0.15f) * pulseScale;  // 增加基础大小
            poseStack.pushPose();
            poseStack.mulPose(new Quaternionf().rotateY(time * (0.2f + i * 0.1f)));
            poseStack.mulPose(new Quaternionf().rotateX(time * 0.1f * (i + 1)));
            poseStack.scale(layerScale, layerScale, layerScale);
            poseStack.translate(-0.5, -0.5, -0.5);
            
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                EVENT_HORIZON_LAYERS[i],
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay
            );
            poseStack.popPose();
        }
        
        poseStack.popPose();
    }

    private void renderEnergyBeams(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float time) {
        float beamHeight = 24.0f;
        int segments = 20;         // 光束分段数
        float beamWidth = 0.15f;
        
        for(int i = 0; i < 4; i++) {
            poseStack.pushPose();
            
            float xzAngle = i * 90.0f + 45.0f;
            poseStack.mulPose(new Quaternionf().rotateY((float)Math.toRadians(xzAngle)));
            
            float yDirection = (i < 2) ? 1.0f : -1.0f;
            
            // 光束的层数
            for(int layer = 0; layer < 3; layer++) {
                float layerScale = 1.0f - (layer * 0.25f);
                
                for(int j = 0; j < segments; j++) {
                    float heightProgress = j / (float)(segments - 1);
                    float height = yDirection * heightProgress * beamHeight;
                    
                    // 波动效果
                    float waveOffset = (float)Math.sin(time * 3 + heightProgress * 10) * 0.05f * (1 - heightProgress);
                    float rotationAngle = time * 2 + heightProgress * 6;
                    
                    poseStack.pushPose();
                    poseStack.translate(0, height, 0);
                    poseStack.mulPose(new Quaternionf().rotateY(rotationAngle));
                    
                    float spiralX = (float)Math.cos(heightProgress * 10 + time * 3) * waveOffset * layerScale;
                    float spiralZ = (float)Math.sin(heightProgress * 10 + time * 3) * waveOffset * layerScale;
                    
                    poseStack.translate(spiralX, 0, spiralZ);
                    
                    float scale = beamWidth * layerScale * (1.0f - heightProgress * 0.8f);
                    poseStack.scale(scale, 0.08f, scale);
                    
                    poseStack.translate(-0.5, -0.5, -0.5);
                    
                    int materialIndex = Math.min((int)(layer + heightProgress * 2), BEAM_MATERIALS.length - 1);
                    BlockState material = BEAM_MATERIALS[materialIndex];
                    
                    Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                        material,
                        poseStack,
                        bufferSource,
                        packedLight,
                        packedOverlay
                    );
                    poseStack.popPose();
                }
            }
            poseStack.popPose();
        }
    }

    private void renderAccretionDisk(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float time, float globalRotation) {
        // 分段数和环数
        renderDiskLayer(poseStack, bufferSource, packedLight, packedOverlay, time, globalRotation,
                       1.4f, 3.0f, 64, 8, 0.2f, ACCRETION_DISK_CORE);  // 减少分段数和环数
        
        renderDiskLayer(poseStack, bufferSource, packedLight, packedOverlay, time, globalRotation,
                       3.0f, 6.0f, 72, 10, 0.1f, ACCRETION_DISK_OUTER); // 减少分段数和环数
    }

    private void renderDiskLayer(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay,
                                float time, float globalRotation, float minRadius, float maxRadius, 
                                int segmentsBase, int rings, float maxTilt, BlockState[] materials) {
        for(int ring = 0; ring < rings; ring++) {
            float ringProgress = ring / (float)(rings - 1);
            float ringRadius = minRadius + (maxRadius - minRadius) * (ringProgress * 0.97f);
            float ringSpeed = 1.0f - ringProgress * 0.7f;
            float ringTilt = maxTilt * (1.0f - ringProgress);
            float ringHeight = (float)Math.sin(ringProgress * Math.PI) * 0.2f;
            int segments = segmentsBase + ring * 4; // 每环增加的分段数
            float scale = 0.25f * (1.0f - ringProgress * 0.4f); // 基础大小
            
            for(int i = 0; i < segments; i++) {
                float segmentProgress = i / (float)segments;
                float angleOffset = (ring % 2 == 0) ? 0 : ((float)Math.PI / segments);
                float angle = segmentProgress * 2 * (float)Math.PI + angleOffset + time * ringSpeed + globalRotation;
                
                // 扰动计算
                float spiralOffset = (float)Math.sin(angle * 2 + time * 0.3f) * 0.1f * ringProgress;
                float turbulence = (float)Math.sin(angle * 5 + time * 0.5f) * 0.05f;
                
                float x = (float)Math.cos(angle) * (ringRadius + spiralOffset);
                float y = ringHeight + turbulence;
                float z = (float)Math.sin(angle) * (ringRadius + spiralOffset);
                
                poseStack.pushPose();
                poseStack.translate(x, y, z);
                poseStack.mulPose(new Quaternionf().rotateY(angle));
                poseStack.mulPose(new Quaternionf().rotateX(ringTilt + turbulence));
                
                // 使用更大的单个粒子而不是多层渲染
                poseStack.scale(scale * 1.2f, scale * 0.2f, scale * 1.2f);
                poseStack.translate(-0.5, -0.5, -0.5);
                
                BlockState material = materials[Math.min(ring / 2, materials.length - 1)];
                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                    material,
                    poseStack,
                    bufferSource,
                    packedLight,
                    packedOverlay
                );
                
                poseStack.popPose();
            }
        }
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull BinaryBlackHoleBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull BinaryBlackHoleBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos).inflate(512.0);
    }
} 