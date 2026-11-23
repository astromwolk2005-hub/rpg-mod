package com.beyondfivehorizons.entity.mobs;

import com.beyondfivehorizons.registry.ModBlocks;
import com.beyondfivehorizons.registry.ModParticles;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.EnumSet;

/**
 * Elysium Mob #1: Firefly (Светляк).
 * Peaceful glowing flying creature that avoids hostiles, reacts to light,
 * and drifts toward rare Elysium crystals/plants to guide explorers.
 */
public class FireflyEntity extends PathAwareEntity {
    public FireflyEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 20, true);
        this.setNoGravity(true);
    }

    public static DefaultAttributeContainer.Builder createFireflyAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.55)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
    }

    public static boolean canSpawn(EntityType<FireflyEntity> type, WorldAccess world,
                                   SpawnReason reason, BlockPos pos, Random random) {
        return pos.getY() > 55 && world.getBlockState(pos).isAir();
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation nav = new BirdNavigation(this, world);
        nav.setCanPathThroughDoors(false);
        nav.setCanSwim(false);
        nav.setCanEnterOpenDoors(true);
        return nav;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new FleeEntityGoal<>(this, HostileEntity.class, 8.0f, 1.3, 1.6));
        this.goalSelector.add(1, new GuideToRareFloraGoal(this));
        this.goalSelector.add(2, new FlyGoal(this, 1.0));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(4, new LookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient && this.age % 4 == 0) {
            this.getWorld().addParticle(ModParticles.LIVING_SPORE,
                    this.getX(), this.getY() + 0.2, this.getZ(),
                    0.0, 0.015, 0.0);
        }
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, net.minecraft.entity.damage.DamageSource damageSource) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME;
    }

    @Override
    protected float getSoundVolume() {
        return 0.35f;
    }

    private static class GuideToRareFloraGoal extends Goal {
        private final FireflyEntity firefly;
        private BlockPos targetPos;
        private int cooldown;

        GuideToRareFloraGoal(FireflyEntity firefly) {
            this.firefly = firefly;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (cooldown > 0) {
                cooldown--;
                return false;
            }
            PlayerEntity nearbyPlayer = firefly.getWorld().getClosestPlayer(firefly, 10.0);
            if (nearbyPlayer == null) {
                return false;
            }
            BlockPos origin = firefly.getBlockPos();
            for (BlockPos scan : BlockPos.iterateOutwards(origin, 10, 5, 10)) {
                var state = firefly.getWorld().getBlockState(scan);
                if (state.isOf(ModBlocks.ELYSIUM_CRYSTAL_BLOCK)
                        || state.isOf(ModBlocks.ELYSIUM_CRYSTAL_ORE)
                        || state.isOf(ModBlocks.NIGHTBLOOM_FLOWER)
                        || state.isOf(ModBlocks.FIRST_SEED_ALTAR)) {
                    targetPos = scan.up(2).toImmutable();
                    return true;
                }
            }
            cooldown = 100;
            return false;
        }

        @Override
        public boolean shouldContinue() {
            return targetPos != null && firefly.squaredDistanceTo(targetPos.getX(), targetPos.getY(), targetPos.getZ()) > 4.0;
        }

        @Override
        public void start() {
            if (targetPos != null) {
                firefly.getNavigation().startMovingTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, 1.15);
            }
        }

        @Override
        public void stop() {
            targetPos = null;
            cooldown = 140;
        }
    }
}
