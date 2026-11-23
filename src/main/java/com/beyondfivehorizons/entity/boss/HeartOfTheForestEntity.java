package com.beyondfivehorizons.entity.boss;

import com.beyondfivehorizons.dimension.HorizonDimension;
import com.beyondfivehorizons.entity.mobs.RootwalkerEntity;
import com.beyondfivehorizons.progression.HorizonProgressionState;
import com.beyondfivehorizons.registry.ModBlocks;
import com.beyondfivehorizons.registry.ModEntities;
import com.beyondfivehorizons.registry.ModItems;
import com.beyondfivehorizons.registry.ModParticles;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Elysium Boss: Heart of the Forest (Сердце Леса).
 * Colossal ancient tree entity with 3 distinct combat phases:
 *  - Phase 1 (100%-66% HP): Root spikes, ranged vitality projectiles, Rootwalker summons.
 *  - Phase 2 (66%-33% HP): Spawns Vitality Buds around the arena that shield and heal the boss
 *    until players destroy them to expose the vulnerable heart.
 *  - Phase 3 (<33% HP): Enraged canopy storm with rapid root obstacles, shockwave pulse, and double summons.
 * Drops: Heart of Elysium (Сердце Элизиума) and records Elysium boss victory in HorizonProgressionState.
 */
public class HeartOfTheForestEntity extends HostileEntity {
    private static final TrackedData<Integer> PHASE = DataTracker.registerData(
            HeartOfTheForestEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> SHIELDED = DataTracker.registerData(
            HeartOfTheForestEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private final ServerBossBar bossBar = new ServerBossBar(
            this.getDisplayName(), BossBar.Color.GREEN, BossBar.Style.NOTCHED_10);

    private BlockPos arenaCenter;
    private final List<BlockPos> activeBuds = new ArrayList<>();
    private final List<BlockPos> temporaryRoots = new ArrayList<>();

    private int attackCooldown = 60;
    private int summonCooldown = 140;
    private boolean phase2Triggered = false;
    private boolean phase3Triggered = false;

    public HeartOfTheForestEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 150;
    }

    public static DefaultAttributeContainer.Builder createBossAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 360.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.16)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12.0)
                .add(EntityAttributes.GENERIC_ARMOR, 10.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(PHASE, 1);
        builder.add(SHIELDED, false);
    }

    public int getPhase() {
        return this.dataTracker.get(PHASE);
    }

    public boolean isShielded() {
        return this.dataTracker.get(SHIELDED);
    }

    public void setArenaCenter(BlockPos pos) {
        this.arenaCenter = pos.toImmutable();
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 0.9, true));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 32.0f));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void checkDespawn() {
        // Boss never despawns passively
    }

    @Override
    public void setCustomName(@Nullable Text name) {
        super.setCustomName(name);
        this.bossBar.setName(this.getDisplayName());
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        if (!(this.getWorld() instanceof ServerWorld sw)) {
            return;
        }

        if (this.arenaCenter == null) {
            this.arenaCenter = this.getBlockPos();
        }

        float hpRatio = this.getHealth() / this.getMaxHealth();
        this.bossBar.setPercent(hpRatio);

        // Phase transitions
        if (!phase2Triggered && hpRatio <= 0.66f) {
            phase2Triggered = true;
            this.dataTracker.set(PHASE, 2);
            spawnVitalityBuds(sw);
            announceNearby(sw, "message.beyondfivehorizons.heart_phase2", Formatting.YELLOW);
        }
        if (!phase3Triggered && hpRatio <= 0.33f) {
            phase3Triggered = true;
            this.dataTracker.set(PHASE, 3);
            this.bossBar.setColor(BossBar.Color.PURPLE);
            performShockwave(sw);
            announceNearby(sw, "message.beyondfivehorizons.heart_phase3", Formatting.LIGHT_PURPLE);
        }

        // Update Vitality Buds shield & healing
        boolean hasAliveBuds = updateVitalityBuds(sw);
        this.dataTracker.set(SHIELDED, hasAliveBuds);
        if (hasAliveBuds) {
            this.bossBar.setColor(BossBar.Color.YELLOW);
            if (this.age % 25 == 0) {
                this.heal(2.5f);
                sw.spawnParticles(ModParticles.LIVING_SPORE,
                        this.getX(), this.getBodyY(0.5), this.getZ(),
                        12, 1.2, 1.5, 1.2, 0.04);
            }
        } else if (getPhase() == 2) {
            this.bossBar.setColor(BossBar.Color.GREEN);
        }

        // Combat attacks
        if (attackCooldown > 0) attackCooldown--;
        if (summonCooldown > 0) summonCooldown--;

        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            if (attackCooldown <= 0) {
                int phase = getPhase();
                if (this.random.nextInt(3) == 0) {
                    launchRangedBarrage(sw, target);
                } else {
                    performRootEruption(sw, target);
                }
                attackCooldown = phase == 3 ? 45 : (phase == 2 ? 65 : 80);
            }

            if (summonCooldown <= 0) {
                int count = getPhase() == 3 ? 2 : 1;
                summonRootwalkers(sw, target, count);
                summonCooldown = getPhase() == 3 ? 180 : 240;
            }
        }

        // Periodically clean old temporary root barriers so arena stays navigable
        if (this.age % 120 == 0 && !temporaryRoots.isEmpty()) {
            BlockPos oldest = temporaryRoots.remove(0);
            if (sw.getBlockState(oldest).isOf(ModBlocks.ELYSIUM_ROOT_BLOCK)) {
                sw.setBlockState(oldest, Blocks.AIR.getDefaultState());
            }
        }
    }

    private void spawnVitalityBuds(ServerWorld sw) {
        int[][] offsets = {{7, 0}, {-7, 0}, {0, 7}, {0, -7}};
        for (int[] off : offsets) {
            BlockPos budBase = arenaCenter.add(off[0], 0, off[1]);
            for (int dy = 3; dy >= -3; dy--) {
                BlockPos p = budBase.up(dy);
                if (sw.getBlockState(p).isAir() && !sw.getBlockState(p.down()).isAir()) {
                    sw.setBlockState(p, ModBlocks.VITALITY_BUD.getDefaultState());
                    activeBuds.add(p.toImmutable());
                    sw.spawnParticles(ModParticles.LIVING_SPORE,
                            p.getX() + 0.5, p.getY() + 0.7, p.getZ() + 0.5,
                            20, 0.5, 0.5, 0.5, 0.05);
                    break;
                }
            }
        }
        sw.playSound(null, this.getBlockPos(), SoundEvents.BLOCK_BEACON_POWER_SELECT,
                SoundCategory.HOSTILE, 1.2f, 0.7f);
    }

    private boolean updateVitalityBuds(ServerWorld sw) {
        Iterator<BlockPos> it = activeBuds.iterator();
        while (it.hasNext()) {
            BlockPos p = it.next();
            if (!sw.getBlockState(p).isOf(ModBlocks.VITALITY_BUD)) {
                it.remove();
            }
        }
        return !activeBuds.isEmpty();
    }

    private void performRootEruption(ServerWorld sw, LivingEntity target) {
        BlockPos targetPos = target.getBlockPos();
        // Place temporary root obstacle & damage entities at target position
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (Math.abs(dx) + Math.abs(dz) == 1 && this.random.nextBoolean()) {
                    BlockPos rootPos = targetPos.add(dx, 0, dz);
                    if (sw.getBlockState(rootPos).isAir() && !sw.getBlockState(rootPos.down()).isAir()) {
                        sw.setBlockState(rootPos, ModBlocks.ELYSIUM_ROOT_BLOCK.getDefaultState());
                        temporaryRoots.add(rootPos.toImmutable());
                    }
                }
            }
        }
        Box impact = new Box(targetPos).expand(2.2);
        for (PlayerEntity p : sw.getEntitiesByClass(PlayerEntity.class, impact, LivingEntity::isAlive)) {
            p.damage(sw.getDamageSources().mobAttack(this), 7.5f);
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 2));
        }
        sw.spawnParticles(ModParticles.LIVING_SPORE,
                target.getX(), target.getY() + 0.6, target.getZ(),
                22, 1.2, 0.5, 1.2, 0.06);
        sw.playSound(null, targetPos, SoundEvents.BLOCK_ROOTS_PLACE, SoundCategory.HOSTILE, 1.1f, 0.6f);
    }

    private void launchRangedBarrage(ServerWorld sw, LivingEntity target) {
        Vec3d dir = target.getEyePos().subtract(this.getEyePos()).normalize();
        SmallFireballEntity bolt = new SmallFireballEntity(sw, this, dir);
        bolt.setPosition(this.getX(), this.getEyeY() - 0.3, this.getZ());
        sw.spawnEntity(bolt);
        sw.playSound(null, this.getBlockPos(), SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 0.9f, 1.3f);
    }

    private void performShockwave(ServerWorld sw) {
        Box blast = this.getBoundingBox().expand(9.0);
        for (PlayerEntity p : sw.getEntitiesByClass(PlayerEntity.class, blast, LivingEntity::isAlive)) {
            Vec3d push = p.getPos().subtract(this.getPos()).normalize();
            p.addVelocity(push.x * 1.4, 0.55, push.z * 1.4);
            p.velocityModified = true;
            p.damage(sw.getDamageSources().magic(), 8.0f);
        }
        sw.spawnParticles(ModParticles.LIVING_SPORE,
                this.getX(), this.getBodyY(0.5), this.getZ(),
                50, 4.0, 1.5, 4.0, 0.1);
        sw.playSound(null, this.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE.value(),
                SoundCategory.HOSTILE, 1.2f, 0.7f);
    }

    private void summonRootwalkers(ServerWorld sw, LivingEntity target, int count) {
        List<RootwalkerEntity> existing = sw.getEntitiesByClass(
                RootwalkerEntity.class, this.getBoundingBox().expand(24.0), LivingEntity::isAlive);
        if (existing.size() >= 4) {
            return;
        }
        for (int i = 0; i < count; i++) {
            RootwalkerEntity rw = ModEntities.ROOTWALKER.create(sw);
            if (rw != null) {
                double angle = this.random.nextDouble() * Math.PI * 2.0;
                rw.refreshPositionAndAngles(
                        this.getX() + Math.cos(angle) * 4.5,
                        this.getY(),
                        this.getZ() + Math.sin(angle) * 4.5,
                        this.getYaw(), 0.0f);
                rw.setTarget(target);
                sw.spawnEntity(rw);
            }
        }
    }

    private void announceNearby(ServerWorld sw, String key, Formatting color) {
        for (ServerPlayerEntity p : sw.getPlayers(pl -> pl.squaredDistanceTo(this) < 64 * 64)) {
            p.sendMessage(Text.translatable(key).formatted(color, Formatting.BOLD), false);
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        // While Vitality Buds are alive, boss takes 75% reduced damage
        if (isShielded() && !source.isIn(net.minecraft.registry.tag.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            amount *= 0.25f;
        }
        return super.damage(source, amount);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (this.getWorld() instanceof ServerWorld sw) {
            // Clean up arena temporary blocks
            for (BlockPos bud : activeBuds) {
                if (sw.getBlockState(bud).isOf(ModBlocks.VITALITY_BUD)) {
                    sw.setBlockState(bud, Blocks.AIR.getDefaultState());
                }
            }
            for (BlockPos root : temporaryRoots) {
                if (sw.getBlockState(root).isOf(ModBlocks.ELYSIUM_ROOT_BLOCK)) {
                    sw.setBlockState(root, Blocks.AIR.getDefaultState());
                }
            }
            activeBuds.clear();
            temporaryRoots.clear();

            // Drop guaranteed Artifact: Heart of Elysium
            this.dropStack(new ItemStack(ModItems.HEART_OF_ELYSIUM, 1));
            this.dropStack(new ItemStack(ModItems.LIVING_ESSENCE, 6));

            // Record victory in HorizonProgressionState for nearby players
            if (sw.getServer() != null) {
                HorizonProgressionState state = HorizonProgressionState.get(sw.getServer());
                for (ServerPlayerEntity player : sw.getPlayers(p -> p.squaredDistanceTo(this) < 64 * 64)) {
                    state.markBossDefeated(player.getUuid(), HorizonDimension.ELYSIUM);
                    player.sendMessage(Text.translatable("message.beyondfivehorizons.heart_defeated")
                            .formatted(Formatting.GOLD, Formatting.BOLD), false);
                }
            }
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Phase", getPhase());
        nbt.putBoolean("Phase2Triggered", phase2Triggered);
        nbt.putBoolean("Phase3Triggered", phase3Triggered);
        if (arenaCenter != null) {
            nbt.putInt("ArenaX", arenaCenter.getX());
            nbt.putInt("ArenaY", arenaCenter.getY());
            nbt.putInt("ArenaZ", arenaCenter.getZ());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("Phase")) {
            this.dataTracker.set(PHASE, nbt.getInt("Phase"));
        }
        phase2Triggered = nbt.getBoolean("Phase2Triggered");
        phase3Triggered = nbt.getBoolean("Phase3Triggered");
        if (nbt.contains("ArenaX")) {
            arenaCenter = new BlockPos(nbt.getInt("ArenaX"), nbt.getInt("ArenaY"), nbt.getInt("ArenaZ"));
        }
        if (this.hasCustomName()) {
            this.bossBar.setName(this.getDisplayName());
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WARDEN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLOCK_WOOD_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ENDER_DRAGON_GROWL;
    }
}
