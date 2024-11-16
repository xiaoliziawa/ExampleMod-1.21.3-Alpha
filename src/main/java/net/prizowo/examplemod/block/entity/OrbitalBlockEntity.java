package net.prizowo.examplemod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.prizowo.examplemod.init.ModBlockEntities;
import net.neoforged.neoforge.network.PacketDistributor;
import net.prizowo.examplemod.network.OrbitalRenderPacket;

import java.util.ArrayList;
import java.util.List;

public class OrbitalBlockEntity extends BlockEntity {
    private final List<Planet> planets = new ArrayList<>();
    private final List<Asteroid> asteroidBelt = new ArrayList<>();
    private final Comet halleyComet;
    private static final float BASE_SPEED = 1.0f;
    private float sunRotation = 0;
    private boolean forceRender = false;
    private static final int ASTEROID_COUNT = 150;  // 小行星数量
    private static float SPEED_MULTIPLIER = 1.0f;  // 添加速度倍率控制

    public OrbitalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ORBITAL_BLOCK_ENTITY.get(), pos, state);
        
        // 初始化小行星带 - 调整位置到火星和木星之间的更合适位置
        float asteroidBeltRadius = 12.0f;  // 基础半径
        float beltWidth = 1.0f;  // 进一步减小带宽
        
        for (int i = 0; i < ASTEROID_COUNT; i++) {
            // 使用更均匀的角度分布
            float angle = (float) (i * (Math.PI * 2.0f / ASTEROID_COUNT));
            // 使用更平滑的半径变化
            float radiusVariation = (float) (Math.cos(i * 6.0 * Math.PI / ASTEROID_COUNT) * beltWidth);
            asteroidBelt.add(new Asteroid(
                asteroidBeltRadius + radiusVariation,
                angle,
                BASE_SPEED * 0.1f  // 降低基础速度
            ));
        }

        // 初始化哈雷彗星
        halleyComet = new Comet(
            30.0f,          // 最远距离
            3.0f,           // 最近距离
            BASE_SPEED * 0.001f,  // 非常慢的速度
            0.0f            // 初始角度
        );

        planets.add(new Planet(
            1.0f,           
            3.0f,           // 最内层保持3格距离
            BASE_SPEED * 4.15f,
            0.383f,         
            0.034f,         
            58.6f          
        ));

        planets.add(new Planet(
            1.0f,
            5.0f,          // 增加到5格
            BASE_SPEED * 1.62f,
            0.949f,
            177.4f,
            -243f
        ));

        planets.add(new Planet(
            1.0f,
            7.5f,          // 增加到7.5格
            BASE_SPEED * 1.0f,
            1.0f,
            23.4f,
            1.0f
        ));

        planets.add(new Planet(
            1.0f,
            10.0f,         // 增加到10格
            BASE_SPEED * 0.53f,
            0.532f,
            25.2f,
            1.03f
        ));

        planets.add(new Planet(
            1.0f,
            14.0f,         // 增加到14格
            BASE_SPEED * 0.084f,
            2.0f,
            3.1f,
            0.41f
        ));

        planets.add(new Planet(
            1.0f,
            18.0f,         // 增加到18格
            BASE_SPEED * 0.034f,
            1.8f,
            26.7f,
            0.45f
        ));

        planets.add(new Planet(
            1.0f,
            22.0f,         // 增加到22格
            BASE_SPEED * 0.012f,
            1.5f,
            97.8f,
            0.72f
        ));

        planets.add(new Planet(
            1.0f,
            26.0f,         // 增加到26格
            BASE_SPEED * 0.006f,
            1.5f,
            28.3f,
            0.67f
        ));
    }

    public static void adjustSpeedMultiplier(float factor) {
        SPEED_MULTIPLIER *= factor;
        // 限制最小和最大速度
        SPEED_MULTIPLIER = Math.max(0.0625f, Math.min(SPEED_MULTIPLIER, 16.0f));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, OrbitalBlockEntity blockEntity) {
        if (level.isClientSide) {
            for (Planet planet : blockEntity.planets) {
                planet.tick(SPEED_MULTIPLIER);  // 传入速度倍率
            }
            for (Asteroid asteroid : blockEntity.asteroidBelt) {
                asteroid.tick(SPEED_MULTIPLIER);
            }
            blockEntity.halleyComet.tick(SPEED_MULTIPLIER);
            blockEntity.sunRotation = (blockEntity.sunRotation + 0.1f * SPEED_MULTIPLIER) % 360;
        }
    }

    public List<Planet> getPlanets() {
        return planets;
    }

    public float getSunRotation() {
        return sunRotation;
    }

    public void setForceRender(boolean force) {
        this.forceRender = force;
    }

    public boolean shouldForceRender() {
        return this.forceRender;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.forceRender = false;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            // 修改为向所有玩家发送
            PacketDistributor.sendToAllPlayers(new OrbitalRenderPacket(worldPosition));
        }
    }

    public List<Asteroid> getAsteroidBelt() {
        return asteroidBelt;
    }

    public Comet getHalleyComet() {
        return halleyComet;
    }

    public static class Planet {
        private float angle;          // 使用float就够了
        private final float height;
        private final float orbitRadius;
        private final float speed;
        private final float size;
        private final float tilt;
        private float rotation;
        private final float rotationSpeed;

        public Planet(float height, float orbitRadius, float speed, 
                     float size, float tilt, float rotationPeriod) {
            this.angle = (float) (Math.random() * Math.PI * 2);
            this.height = height;
            this.orbitRadius = orbitRadius;
            this.speed = speed * 0.1f;  // 增加公转速度
            this.size = size;
            this.tilt = tilt;
            this.rotation = 0;
            this.rotationSpeed = speed * 0.5f;  // 增加自转速度
        }

        public void tick(float speedMultiplier) {
            angle += speed * speedMultiplier;
            if (angle > (float)(Math.PI * 2)) {
                angle -= (float)(Math.PI * 2);
            }
            rotation += rotationSpeed * speedMultiplier;
            if (rotation > 360) {
                rotation -= 360;
            }
        }

        public float getAngle() { return angle; }
        public float getRotation() { return rotation; }
        public float getOrbitRadius() { return orbitRadius; }
        public float getSize() { return size; }
        public float getTilt() { return tilt; }
        public float getSpeed() { return speed; }
        public float getRotationSpeed() { return rotationSpeed; }
    }

    public static class Asteroid {
        private float angle;
        private final float orbitRadius;
        private final float speed;
        private final float size;
        private float rotation;
        private final float rotationSpeed;
        private final float verticalOffset;

        public Asteroid(float orbitRadius, float angle, float speed) {
            this.orbitRadius = orbitRadius;
            this.angle = angle;
            this.speed = speed * (0.95f + (float)Math.random() * 0.1f);
            this.size = 0.08f + (float)Math.random() * 0.04f;
            this.rotation = (float)(Math.random() * 360);
            this.rotationSpeed = speed * 15f * (0.8f + (float)Math.random() * 0.4f);
            this.verticalOffset = (float)(Math.random() * 0.4 - 0.2);
        }

        public void tick(float speedMultiplier) {
            angle = (angle + speed * speedMultiplier) % (float)(Math.PI * 2);
            rotation = (rotation + rotationSpeed * speedMultiplier) % 360;
        }

        public float getSpeed() { return speed; }
        public float getRotationSpeed() { return rotationSpeed; }
        
        public float getAngle() { return angle; }
        public float getOrbitRadius() { return orbitRadius; }
        public float getSize() { return size; }
        public float getRotation() { return rotation; }
        public float getVerticalOffset() { return verticalOffset; }
    }

    public static class Comet {
        private float angle;
        private final float maxRadius;
        private final float minRadius;
        private final float speed;
        private float currentRadius;

        public Comet(float maxRadius, float minRadius, float speed, float startAngle) {
            this.maxRadius = maxRadius;
            this.minRadius = minRadius;
            this.speed = speed;
            this.angle = startAngle;
            this.currentRadius = maxRadius;
        }

        public void tick(float speedMultiplier) {
            angle += speed * speedMultiplier;
            if (angle > Math.PI * 2) {
                angle -= Math.PI * 2;
            }
            currentRadius = (float) (minRadius + (maxRadius - minRadius) * 
                (1 + Math.cos(angle)) / 2);
        }

        public float getAngle() { return angle; }
        public float getCurrentRadius() { return currentRadius; }
    }
} 