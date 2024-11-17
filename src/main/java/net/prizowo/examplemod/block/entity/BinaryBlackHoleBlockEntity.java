package net.prizowo.examplemod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.prizowo.examplemod.init.ModBlockEntities;
import net.minecraft.client.Minecraft;

public class BinaryBlackHoleBlockEntity extends BlockEntity {
    private Vec3 blackHole1Pos = new Vec3(2.0, 0.5, 2.0);
    private Vec3 blackHole2Pos = new Vec3(-2.0, -0.5, -2.0);
    private Vec3 blackHole3Pos = new Vec3(0.0, 2.0, 0.0);
    
    private Vec3 blackHole1Velocity = new Vec3(0.05, 0.02, 0.05);
    private Vec3 blackHole2Velocity = new Vec3(-0.05, -0.02, -0.05);
    private Vec3 blackHole3Velocity = new Vec3(0.03, -0.05, -0.03);
    
    // 物理参数
    private static final double G = 0.2;
    private static final double MIN_DISTANCE = 1.0;
    private static final double SLINGSHOT_BOOST = 3.0;
    private static final double MAX_VELOCITY = 0.8;
    private static final double TIME_STEP = 0.1;

    // 添加更新频率控制
    private static final int UPDATE_INTERVAL = 4; // 每4tick更新一次
    private static final int MAX_ACTIVE_BLOCKS = 8; // 限制同时活动的方块数量
    private static int activeBlocks = 0;

    public BinaryBlackHoleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BINARY_BLACK_HOLE_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BinaryBlackHoleBlockEntity blockEntity) {
        if (level.isClientSide) {
            blockEntity.updateBlackHoles();
        }
    }

    private void updateBlackHoles() {
        // 一次性计算所有力
        Vec3[] forces = calculateForces();
        
        // 更新速度和位置
        updateVelocitiesAndPositions(forces);
        
        // 边界检查
        boundaryCheck();
    }

    private Vec3[] calculateForces() {
        Vec3[] forces = new Vec3[3];
        forces[0] = Vec3.ZERO;
        forces[1] = Vec3.ZERO;
        forces[2] = Vec3.ZERO;

        // 只计算主要的引力作用
        calculateForceBetween(0, 1, forces);
        calculateForceBetween(1, 2, forces);
        // 移除 0-2 的计算以减少计算量

        return forces;
    }

    private void calculateForceBetween(int i, int j, Vec3[] forces) {
        Vec3 pos1 = getPosition(i);
        Vec3 pos2 = getPosition(j);
        Vec3 delta = pos2.subtract(pos1);
        double distance = delta.length();
        
        if (distance < MIN_DISTANCE) {
            handleCollision(i, j, delta);
            return;
        }

        double forceMagnitude = G / (distance * distance);
        Vec3 force = delta.normalize().scale(forceMagnitude);
        
        forces[i] = forces[i].add(force);
        forces[j] = forces[j].subtract(force);
    }

    private void handleCollision(int i, int j, Vec3 delta) {
        Vec3 normal = delta.normalize();
        Vec3 tangent = new Vec3(-normal.z, 0, normal.x);
        
        // 计算弹射速度
        double boostVelocity = SLINGSHOT_BOOST;
        
        // 应用弹射速度
        setVelocity(i, tangent.scale(boostVelocity));
        setVelocity(j, tangent.scale(-boostVelocity));
    }

    private Vec3 getPosition(int index) {
        return switch (index) {
            case 0 -> blackHole1Pos;
            case 1 -> blackHole2Pos;
            case 2 -> blackHole3Pos;
            default -> Vec3.ZERO;
        };
    }

    private void setVelocity(int index, Vec3 velocity) {
        velocity = limitVelocity(velocity);
        switch (index) {
            case 0 -> blackHole1Velocity = velocity;
            case 1 -> blackHole2Velocity = velocity;
            case 2 -> blackHole3Velocity = velocity;
        }
    }

    private void updateVelocitiesAndPositions(Vec3[] forces) {
        // 更新速度
        blackHole1Velocity = updateVelocity(blackHole1Velocity, forces[0]);
        blackHole2Velocity = updateVelocity(blackHole2Velocity, forces[1]);
        blackHole3Velocity = updateVelocity(blackHole3Velocity, forces[2]);
        
        // 更新位置
        blackHole1Pos = blackHole1Pos.add(blackHole1Velocity.scale(TIME_STEP));
        blackHole2Pos = blackHole2Pos.add(blackHole2Velocity.scale(TIME_STEP));
        blackHole3Pos = blackHole3Pos.add(blackHole3Velocity.scale(TIME_STEP));
    }

    private Vec3 updateVelocity(Vec3 velocity, Vec3 force) {
        return limitVelocity(velocity.add(force.scale(TIME_STEP)));
    }

    private Vec3 limitVelocity(Vec3 velocity) {
        double speed = velocity.length();
        if (speed > MAX_VELOCITY) {
            return velocity.scale(MAX_VELOCITY / speed);
        }
        return velocity;
    }

    private void boundaryCheck() {
        double bound = 8.0;
        double yBound = 6.0;
        
        blackHole1Pos = checkBounds(blackHole1Pos, 0, bound, yBound);
        blackHole2Pos = checkBounds(blackHole2Pos, 1, bound, yBound);
        blackHole3Pos = checkBounds(blackHole3Pos, 2, bound, yBound);
    }
    
    private Vec3 checkBounds(Vec3 pos, int index, double bound, double yBound) {
        Vec3 newPos = pos;
        boolean bounced = false;
        
        if (Math.abs(pos.x) > bound) {
            Vec3 velocity = getVelocity(index);
            setVelocity(index, new Vec3(-velocity.x * 0.8, velocity.y, velocity.z));
            newPos = new Vec3(Math.signum(pos.x) * bound, pos.y, pos.z);
            bounced = true;
        }
        if (Math.abs(pos.y) > yBound) {
            Vec3 velocity = getVelocity(index);
            setVelocity(index, new Vec3(velocity.x, -velocity.y * 0.8, velocity.z));
            newPos = new Vec3(newPos.x, Math.signum(pos.y) * yBound, pos.z);
            bounced = true;
        }
        if (Math.abs(pos.z) > bound) {
            Vec3 velocity = getVelocity(index);
            setVelocity(index, new Vec3(velocity.x, velocity.y, -velocity.z * 0.8));
            newPos = new Vec3(newPos.x, newPos.y, Math.signum(pos.z) * bound);
            bounced = true;
        }
        
        return newPos;
    }

    private Vec3 getVelocity(int index) {
        return switch (index) {
            case 0 -> blackHole1Velocity;
            case 1 -> blackHole2Velocity;
            case 2 -> blackHole3Velocity;
            default -> Vec3.ZERO;
        };
    }

    public Vec3 getBlackHole1Pos() { return blackHole1Pos; }
    public Vec3 getBlackHole2Pos() { return blackHole2Pos; }
    public Vec3 getBlackHole3Pos() { return blackHole3Pos; }
    
    public BlockPos getBlockPosition() {
        return this.worldPosition;
    }

    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition).inflate(16.0D);
    }
} 