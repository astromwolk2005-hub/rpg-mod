package com.beyondfivehorizons.entity.mobs;

import com.beyondfivehorizons.registry.ModBlocks;
import com.beyondfivehorizons.registry.ModEntities;
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
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

/**
 * Elysium Mob #4: Arboreal Guardian (Древесный страж).
 * Rare elite wooden humanoid guarding ancient trees.
 * Gains Resistance near large trees, raises root obstacles, and summons Rootwalkers.
 */
public class ArborealGuardianEntity extends HostileEntity {
    private int abilityCooldown = 100;

    public ArborealGuardianEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 25;
    }

    public static DefaultAttributeContainer.Builder createArborealGuardianAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 85.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0)
                .add(EntityAttributes.GENERIC_ARMOR, 8.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.75)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 28.0);
    }

    public static boolean canSpawn(EntityType<ArborealGuardianEntity> type, WorldAccess world,
                                   SpawnReason reason, BlockPos pos, Random random) {
        return pos.getY() > 55 && random.nextInt(3) == 0
                && world.getBlockState(pos.down()).isSolidBlock(world, pos.down());
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 12.0f));
        this.goalSelector.add(7, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    protected void mobTick() {
        super.mobTick();

        // Passive protection near ancient trees (checked once every 2 seconds)
        if (this.age % 40 == 0 && isNearAncientTree()) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 60, 0, true, true));
        }

        if (abilityCooldown > 0) {
            abilityCooldown--;
        }

        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive() && abilityCooldown <= 0 && this.getWorld() instanceof ServerWorld sw) {
            double distSq = this.squaredDistanceTo(target);
            if (distSq < 196.0) {
                if (this.random.nextBoolean()) {
                    // Raise a root obstacle near the target
                    BlockPos rootPos = target.getBlockPos().add(this.random.nextInt(3) - 1, 0, this.random.nextInt(3) - 1);
                    if (sw.getBlockState(rootPos).isAir() && !sw.getBlockState(rootPos.down()).isAir()) {
                        sw.setBlockState(rootPos, ModBlocks.ELYSIUM_ROOT_BLOCK.getDefaultState());
                        sw.spawnParticles(ModParticles.LIVING_SPORE,
                                rootPos.getX() + 0.5, rootPos.getY() + 0.8, rootPos.getZ() + 0.5,
                                12, 0.4, 0.4, 0.4, 0.04);
                    }
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 70, 1));
                } else {
                    // Summon a Rootwalker ally
                    RootwalkerEntity minion = ModEntities.ROOTWALKER.create(sw);
                    if (minion != null) {
                        minion.refreshPositionAndAngles(this.getX() + 1.5, this.getY(), this.getZ() + 1.5, this.getYaw(), 0.0f);
                        minion.setTarget(target);
                        sw.spawnEntity(minion);
                    }
                }
                this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 0.65f);
                abilityCooldown = 160;
            }
        }
    }

    private boolean isNearAncientTree() {
        BlockPos pos = this.getBlockPos();
        for (BlockPos scan : BlockPos.iterate(pos.add(-4, 0, -4), pos.add(4, 3, 4))) {
            var state = this.getWorld().getBlockState(scan);
            if (state.isOf(ModBlocks.ELYSIUM_LOG) || state.isOf(ModBlocks.ELYSIUM_HOLLOW_LOG) || state.isIn(BlockTags.LOGS)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean hit = super.tryAttack(target);
        if (hit && target instanceof LivingEntity living) {
            living.takeKnockback(0.9, this.getX() - living.getX(), this.getZ() - living.getZ());
        }
        return hit;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_IRON_GOLEM_STEP;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLOCK_WOOD_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_IRON_GOLEM_DEATH;
    }
}
