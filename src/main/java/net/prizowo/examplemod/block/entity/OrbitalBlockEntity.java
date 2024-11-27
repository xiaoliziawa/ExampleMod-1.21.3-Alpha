package net.prizowo.examplemod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.prizowo.examplemod.init.ModBlockEntities;

import java.util.ArrayList;
import java.util.List;

public class OrbitalBlockEntity extends BlockEntity {
    private final List<Planet> planets = new ArrayList<>();
    private final List<Asteroid> asteroidBelt = new ArrayList<>();
    private final Comet halleyComet;
    private static final float BASE_SPEED = 0.5f;
    private float sunRotation = 0;
    private static final int ASTEROID_COUNT = 150;
    private static float SPEED_MULTIPLIER = 1.0f;


    public OrbitalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ORBITAL_BLOCK_ENTITY.get(), pos, state);

        // 初始化小行星带
        float asteroidBeltRadius = 12.0f;
        float beltWidth = 1.0f;
        for (int i = 0; i < ASTEROID_COUNT; i++) {
            double angle = (Math.PI * 2.0 * i) / ASTEROID_COUNT;
            float radiusVariation = (float) (Math.random() * beltWidth);
            float radius = asteroidBeltRadius + radiusVariation;
            asteroidBelt.add(new Asteroid(radius, (float)angle, BASE_SPEED * 0.1f));
        }

        // 初始化行星
        planets.add(new Planet(0.0f, 3.0f, BASE_SPEED * 4.15f, 0.383f, 0.034f, 58.6f));   // 水星
        planets.add(new Planet(0.0f, 5.0f, BASE_SPEED * 1.62f, 0.949f, 177.4f, -243f));   // 金星
        planets.add(new Planet(0.0f, 7.5f, BASE_SPEED * 1.0f, 1.0f, 23.4f, 1.0f));        // 地球
        planets.add(new Planet(0.0f, 10.0f, BASE_SPEED * 0.53f, 0.532f, 25.2f, 1.03f));   // 火星
        planets.add(new Planet(0.0f, 14.0f, BASE_SPEED * 0.084f, 2.0f, 3.1f, 0.41f));     // 木星
        planets.add(new Planet(0.0f, 18.0f, BASE_SPEED * 0.034f, 1.8f, 26.7f, 0.45f));    // 土星
        planets.add(new Planet(0.0f, 22.0f, BASE_SPEED * 0.012f, 1.5f, 97.8f, 0.72f));    // 天王星
        planets.add(new Planet(0.0f, 26.0f, BASE_SPEED * 0.006f, 1.5f, 28.3f, 0.67f));    // 海王星

        // 初始化哈雷彗星
        halleyComet = new Comet(30.0f, 3.0f, BASE_SPEED * 0.001f, 0.0f);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, OrbitalBlockEntity blockEntity) {
        if (level.isClientSide) {
            // 更新行星位置
            for (Planet planet : blockEntity.planets) {
                planet.tick(SPEED_MULTIPLIER);
            }

            // 更新小行星带
            for (Asteroid asteroid : blockEntity.asteroidBelt) {
                asteroid.tick(SPEED_MULTIPLIER);
            }

            // 更新彗星
            blockEntity.halleyComet.tick(SPEED_MULTIPLIER);

            // 更新太阳自转
            blockEntity.sunRotation = (blockEntity.sunRotation + 0.1f * SPEED_MULTIPLIER) % 360;
        }
    }

    public static void adjustSpeedMultiplier(float factor) {
        SPEED_MULTIPLIER = Math.max(0.1f, Math.min(10.0f, SPEED_MULTIPLIER * factor));
    }

    // 行星类
    public static class Planet {
        private float angle;          // 轨道角度
        private final float height;
        private final float orbitRadius;
        private final float speed;
        private final float size;
        private final float tilt;
        private float rotation;       // 自转角度
        private final float rotationSpeed;

        public Planet(float height, float orbitRadius, float speed,
                      float size, float tilt, float rotationPeriod) {
            this.angle = (float) (Math.random() * Math.PI * 2);
            this.height = height;
            this.orbitRadius = orbitRadius;
            this.speed = speed * 0.1f;
            this.size = size;
            this.tilt = tilt;
            this.rotation = 0;
            this.rotationSpeed = speed * 0.5f;
        }

        public void tick(float speedMultiplier) {
            angle = (float) ((angle + speed * speedMultiplier) % (Math.PI * 2));
            rotation = (rotation + rotationSpeed * speedMultiplier) % 360;
        }

        public float getAngle() { return angle; }
        public float getRotation() { return rotation; }
        public float getOrbitRadius() { return orbitRadius; }
        public float getSize() { return size; }
        public float getTilt() { return tilt; }
        public float getSpeed() { return speed; }
        public float getRotationSpeed() { return rotationSpeed; }
    }

    // 小行星类
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
            angle = (float) ((angle + speed * speedMultiplier) % (Math.PI * 2));
            rotation = (rotation + rotationSpeed * speedMultiplier) % 360;
        }

        public float getAngle() { return angle; }
        public float getOrbitRadius() { return orbitRadius; }
        public float getSize() { return size; }
        public float getRotation() { return rotation; }
        public float getVerticalOffset() { return verticalOffset; }
        public float getSpeed() { return speed; }
        public float getRotationSpeed() { return rotationSpeed; }
    }

    // 彗星类
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
            angle = (float) ((angle + speed * speedMultiplier) % (Math.PI * 2));
            currentRadius = (float) (minRadius + (maxRadius - minRadius) *
                    (1 + Math.cos(angle)) / 2);
        }

        public float getAngle() { return angle; }
        public float getCurrentRadius() { return currentRadius; }
    }

    public List<Planet> getPlanets() {
        return planets;
    }

    public List<Asteroid> getAsteroidBelt() {
        return asteroidBelt;
    }

    public Comet getHalleyComet() {
        return halleyComet;
    }

    public float getSunRotation() {
        return sunRotation;
    }

    public BlockPos getBlockPosition() {
        return this.worldPosition;
    }

    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition).inflate(32.0D);
    }
} 