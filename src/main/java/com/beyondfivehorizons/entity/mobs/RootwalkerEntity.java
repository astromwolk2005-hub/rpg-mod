package com.beyondfivehorizons.entity.mobs;

import com.beyondfivehorizons.registry.ModParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

/**
 * Elysium Mob #2: Rootwalker (Корнеход).
 * Hostile humanoid of roots and wood that burrows underground to ambush targets,
 * snares enemies with roots on hit, and briefly retreats underground when heavily injured.
 */
public class RootwalkerEntity extends HostileEntity {
    private static final TrackedData<Boolean> BURROWED = DataTracker.registerData(
            RootwalkerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int burrowCooldown = 60;
    private int burrowTicks = 0;

    public RootwalkerEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 8;
    }

    public static DefaultAttributeContainer.Builder createRootwalkerAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 28.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.5)
                .add(EntityAttributes.GENERIC_ARMOR, 3.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0);
    }

    public static boolean canSpawn(EntityType<RootwalkerEntity> type, WorldAccess world,
                                   SpawnReason reason, BlockPos pos, Random random) {
        return pos.getY() > 50 && world.getBlockState(pos.down()).isSolidBlock(world, pos.down());
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(BURROWED, false);
    }

    public boolean isBurrowed() {
        return this.dataTracker.get(BURROWED);
    }

    public void setBurrowed(boolean burrowed) {
        this.dataTracker.set(BURROWED, burrowed);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.15, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.9));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
        this.goalSelector.add(7, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        if (burrowCooldown > 0) {
            burrowCooldown--;
        }

        LivingEntity target = this.getTarget();
        if (isBurrowed()) {
            burrowTicks--;
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 10, 0, false, false));
            if (this.getWorld() instanceof ServerWorld sw && this.age % 3 == 0) {
                sw.spawnParticles(ModParticles.LIVING_SPORE,
                        this.getX(), this.getY() + 0.15, this.getZ(),
                        4, 0.3, 0.1, 0.3, 0.02);
            }
            if (burrowTicks <= 0) {
                emergeNear(target);
            }
        } else if (target != null && burrowCooldown <= 0) {
            double distSq = this.squaredDistanceTo(target);
            // Burrow to close distance OR retreat if below 35% HP
            if ((distSq > 36.0 && distSq < 256.0) || this.getHealth() < this.getMaxHealth() * 0.35f) {
                startBurrow();
            }
        }
    }

    private void startBurrow() {
        setBurrowed(true);
        burrowTicks = 40;
        burrowCooldown = 160;
        this.playSound(SoundEvents.BLOCK_ROOTED_DIRT_BREAK, 1.0f, 0.7f);
    }

    private void emergeNear(LivingEntity target) {
        setBurrowed(false);
        if (target != null && this.getWorld() instanceof ServerWorld sw) {
            boolean lowHealth = this.getHealth() < this.getMaxHealth() * 0.35f;
            Vec3d dir = target.getPos().subtract(this.getPos());
            if (dir.lengthSquared() > 0.01) {
                dir = dir.normalize();
                double offset = lowHealth ? -5.0 : 2.2;
                BlockPos emergePos = lowHealth
                        ? BlockPos.ofFloored(this.getPos().add(dir.multiply(offset)))
                        : target.getBlockPos().add((int) Math.round(-dir.x * 2), 0, (int) Math.round(-dir.z * 2));
                if (sw.getBlockState(emergePos).isAir() && sw.getBlockState(emergePos.up()).isAir()
                        && !sw.getBlockState(emergePos.down()).isAir()) {
                    this.refreshPositionAndAngles(emergePos.getX() + 0.5, emergePos.getY(), emergePos.getZ() + 0.5,
                            this.getYaw(), this.getPitch());
                }
            }
            sw.spawnParticles(ModParticles.LIVING_SPORE,
                    this.getX(), this.getY() + 0.5, this.getZ(),
                    14, 0.5, 0.4, 0.5, 0.05);
            this.playSound(SoundEvents.BLOCK_ROOTS_PLACE, 1.0f, 0.8f);
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean hit = super.tryAttack(target);
        if (hit && target instanceof LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1));
        }
        return hit;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLOCK_WOOD_STEP;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLOCK_WOOD_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_WOOD_BREAK;
    }
}
