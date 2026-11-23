package com.beyondfivehorizons.block;

import com.beyondfivehorizons.entity.boss.HeartOfTheForestEntity;
import com.beyondfivehorizons.registry.ModEntities;
import com.beyondfivehorizons.registry.ModItems;
import com.beyondfivehorizons.registry.ModParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

/**
 * Boss summoning altar located in the center of the Temple of the First Seed.
 * Presenting Living Essence awakens the Heart of the Forest.
 */
public class FirstSeedAltarBlock extends Block {
    public FirstSeedAltarBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world,
                                             BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!stack.isOf(ModItems.LIVING_ESSENCE)) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            List<HeartOfTheForestEntity> existing = serverWorld.getEntitiesByClass(
                    HeartOfTheForestEntity.class, new Box(pos).expand(64.0), e -> e.isAlive());
            if (!existing.isEmpty()) {
                player.sendMessage(Text.translatable("message.beyondfivehorizons.boss_already_active")
                        .formatted(Formatting.YELLOW), true);
                return ItemActionResult.SUCCESS;
            }

            HeartOfTheForestEntity boss = ModEntities.HEART_OF_THE_FOREST.create(serverWorld);
            if (boss != null) {
                boss.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 0.0f, 0.0f);
                boss.setArenaCenter(pos);
                serverWorld.spawnEntity(boss);

                serverWorld.spawnParticles(ModParticles.LIVING_SPORE,
                        pos.getX() + 0.5, pos.getY() + 2.0, pos.getZ() + 0.5,
                        60, 2.5, 2.5, 2.5, 0.08);
                serverWorld.playSound(null, pos, SoundEvents.ENTITY_WITHER_SPAWN,
                        SoundCategory.HOSTILE, 1.0f, 0.8f);

                for (PlayerEntity nearby : serverWorld.getPlayers(p -> p.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) < 64 * 64)) {
                    nearby.sendMessage(Text.translatable("message.beyondfivehorizons.heart_awakens")
                            .formatted(Formatting.DARK_GREEN, Formatting.BOLD), false);
                }

                if (!player.isCreative()) {
                    stack.decrement(1);
                }
            }
        }
        return ItemActionResult.success(world.isClient);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            player.sendMessage(Text.translatable("message.beyondfivehorizons.first_seed_altar_hint")
                    .formatted(Formatting.AQUA), true);
        }
        return ActionResult.SUCCESS;
    }
}
