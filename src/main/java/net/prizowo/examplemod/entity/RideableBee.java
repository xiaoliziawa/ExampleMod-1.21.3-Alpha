package net.prizowo.examplemod.entity;

import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.player.LocalPlayer;
import net.prizowo.examplemod.client.KeyBindings;

import java.util.List;

public class RideableBee extends Bee {
    
    public RideableBee(EntityType<? extends Bee> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Bee.createAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.FLYING_SPEED, 0.6D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            player.startRiding(this);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.isAlive()) {
            if (this.isVehicle() && getControllingPassenger() instanceof Player) {
                LivingEntity livingentity = this.getControllingPassenger();
                
                // 设置实体朝向
                this.setYRot(livingentity.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(livingentity.getXRot() * 0.5F);
                this.setRot(this.getYRot(), this.getXRot());
                
                // 获取玩家输入
                float forward = livingentity.zza;
                float strafe = livingentity.xxa;
                
                // 处理飞行
                if (livingentity instanceof LocalPlayer player) {
                    if (player.input.keyPresses.jump()) {
                        vec3 = vec3.add(0, 0.3, 0);
                    }
                    // 使用注册的按键
                    if (KeyBindings.DESCEND_KEY.isDown()) {
                        vec3 = vec3.add(0, -0.3, 0);
                    }
                }
                
                // 移动实体
                this.setSpeed(0.3F);
                super.travel(new Vec3(strafe, vec3.y, forward));
                return;
            }
        }
        super.travel(vec3);
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return (LivingEntity) this.getFirstPassenger();
    }

    public void summonSwarm() {
        if (!this.level().isClientSide) {
            // 获取周围的实体
            AABB box = this.getBoundingBox().inflate(16.0D);
            List<Entity> entities = this.level().getEntities(this, box);
            
            // 生成5只攻击蜜蜂
            for (int i = 0; i < 5; i++) {
                Bee bee = EntityType.BEE.create(this.level(), EntitySpawnReason.TRIGGERED);
                if (bee != null) {
                    bee.setPos(this.getX(), this.getY() + 1, this.getZ());
                    
                    // 设置蜜蜂为愤怒状态
                    bee.setRemainingPersistentAngerTime(400);
                    
                    // 随机选择目标（排除玩家和RideableBee）
                    for (Entity target : entities) {
                        if (target instanceof LivingEntity && 
                            !(target instanceof Player) && 
                            !(target instanceof RideableBee)) {
                            bee.setTarget((LivingEntity)target);
                            break;
                        }
                    }
                    
                    this.level().addFreshEntity(bee);
                }
            }
        }
    }
} 