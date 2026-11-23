package com.beyondfivehorizons.item;

import com.beyondfivehorizons.registry.ModBlocks;
import com.beyondfivehorizons.registry.ModItems;
import com.beyondfivehorizons.registry.ModParticles;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

/**
 * Elysium signature weapon #2: Staff of Roots.
 * Consumes Living Essence to raise a barrier of living roots and ensnare enemies ahead.
 */
public class RootStaffItem extends Item {
    public RootStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!user.isCreative() && !consumeLivingEssence(user)) {
            if (!world.isClient) {
                user.sendMessage(Text.translatable("message.beyondfivehorizons.needs_living_essence")
                        .formatted(Formatting.RED), true);
            }
            return TypedActionResult.fail(stack);
        }

        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            Vec3d look = user.getRotationVec(1.0f).normalize();
            Vec3d horizontal = new Vec3d(look.x, 0.0, look.z);
            if (horizontal.lengthSquared() < 0.001) {
                horizontal = new Vec3d(1.0, 0.0, 0.0);
            } else {
                horizontal = horizontal.normalize();
            }

            // Raise 3 root pillars in an arc 4 blocks ahead
            Vec3d right = new Vec3d(-horizontal.z, 0.0, horizontal.x);
            BlockPos centerTarget = BlockPos.ofFloored(user.getPos().add(horizontal.multiply(4.0)));

            for (int side = -1; side <= 1; side++) {
                BlockPos base = centerTarget.add((int) Math.round(right.x * side), 0, (int) Math.round(right.z * side));
                // Align to ground within +-2 blocks
                for (int dy = 2; dy >= -2; dy--) {
                    BlockPos check = base.up(dy);
                    if (serverWorld.getBlockState(check).isAir() && !serverWorld.getBlockState(check.down()).isAir()) {
                        serverWorld.setBlockState(check, ModBlocks.ELYSIUM_ROOT_BLOCK.getDefaultState());
                        if (serverWorld.getBlockState(check.up()).isAir()) {
                            serverWorld.setBlockState(check.up(), ModBlocks.ELYSIUM_ROOT_BLOCK.getDefaultState());
                        }
                        serverWorld.spawnParticles(ModParticles.LIVING_SPORE,
                                check.getX() + 0.5, check.getY() + 1.0, check.getZ() + 0.5,
                                8, 0.3, 0.5, 0.3, 0.03);
                        break;
                    }
                }
            }

            // Ensnare and damage hostile entities in a cone ahead
            Box snareBox = new Box(centerTarget).expand(3.5, 2.5, 3.5);
            for (LivingEntity target : serverWorld.getEntitiesByClass(LivingEntity.class, snareBox,
                    e -> e != user && e.isAlive())) {
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 3));
                target.damage(serverWorld.getDamageSources().magic(), 6.0f);
                serverWorld.spawnParticles(ModParticles.LIVING_SPORE,
                        target.getX(), target.getBodyY(0.5), target.getZ(),
                        12, 0.4, 0.4, 0.4, 0.05);
            }

            serverWorld.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_ROOTS_PLACE,
                    SoundCategory.PLAYERS, 1.0f, 0.8f);
            stack.damage(1, user, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            user.getItemCooldownManager().set(this, 60);
        }

        return TypedActionResult.success(stack, world.isClient);
    }

    private boolean consumeLivingEssence(PlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack invStack = player.getInventory().getStack(i);
            if (invStack.isOf(ModItems.LIVING_ESSENCE)) {
                invStack.decrement(1);
                return true;
            }
        }
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.beyondfivehorizons.root_staff").formatted(Formatting.DARK_GREEN));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
