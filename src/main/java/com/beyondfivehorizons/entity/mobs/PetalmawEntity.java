package com.beyondfivehorizons.entity.mobs;

import com.beyondfivehorizons.registry.ModParticles;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

/**
 * Elysium Mob #3: Petalmaw (Лепесточник).
 * Neutral disguised plant mob. Stays motionless like a giant blossom until
 * a player approaches too closely or provokes it, then blooms open and emits a pollen cloud.
 */
public class PetalmawEntity extends HostileEntity {
    private static final TrackedData<Boolean> AWAKENED = DataTracker.registerData(
            PetalmawEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int pollenCooldown = 0;

    public PetalmawEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 10;
    }

    public static DefaultAttributeContainer.Builder createPetalmawAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 34.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.21)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.5)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 14.0);
    }

    public static boolean canSpawn(EntityType<PetalmawEntity> type, WorldAccess world,
                                   SpawnReason reason, BlockPos pos, Random random) {
        return pos.getY() > 55 && world.getBlockState(pos.down()).isSolidBlock(world, pos.down());
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(AWAKENED, false);
    }

    public boolean isAwakened() {
        return this.dataTracker.get(AWAKENED);
    }

    public void setAwakened(boolean awakened) {
        this.dataTracker.set(AWAKENED, awakened);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0, false) {
            @Override
            public boolean canStart() {
                return isAwakened() && super.canStart();
            }
        });
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.targetSelector.add(1, new RevengeGoal(this));
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        if (pollenCooldown > 0) {
            pollenCooldown--;
        }

        PlayerEntity closePlayer = this.getWorld().getClosestPlayer(this, 4.5);
        if (!isAwakened()) {
            this.getNavigation().stop();
            if (closePlayer != null && !closePlayer.isCreative() && !closePlayer.isSpectator()) {
                setAwakened(true);
                this.setTarget(closePlayer);
                this.playSound(SoundEvents.ENTITY_EVOKER_FANGS_ATTACK, 0.9f, 1.2f);
            }
        } else {
            LivingEntity target = this.getTarget();
            if (target == null || !target.isAlive() || this.squaredDistanceTo(target) > 196.0) {
                setAwakened(false);
                this.setTarget(null);
            } else if (pollenCooldown <= 0 && this.squaredDistanceTo(target) < 25.0) {
                releasePollenCloud();
                pollenCooldown = 120;
            }
        }
    }

    private void releasePollenCloud() {
        if (this.getWorld() instanceof ServerWorld sw) {
            AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(sw, this.getX(), this.getY() + 0.5, this.getZ());
            cloud.setOwner(this);
            cloud.setRadius(3.0f);
            cloud.setDuration(90);
            cloud.setWaitTime(5);
            cloud.addEffect(new StatusEffectInstance(StatusEffects.POISON, 70, 0));
            cloud.addEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 70, 0));
            sw.spawnEntity(cloud);
            sw.spawnParticles(ModParticles.LIVING_SPORE,
                    this.getX(), this.getY() + 0.8, this.getZ(),
                    20, 1.5, 0.5, 1.5, 0.05);
            this.playSound(SoundEvents.BLOCK_SPORE_BLOSSOM_FALL, 1.0f, 0.8f);
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!isAwakened()) {
            setAwakened(true);
        }
        return super.damage(source, amount);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLOCK_GRASS_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_GRASS_BREAK;
    }
}
