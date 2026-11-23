package com.beyondfivehorizons.item;

import com.beyondfivehorizons.portal.ElysiumPortalHelper;
import com.beyondfivehorizons.progression.HorizonProgressionState;
import com.beyondfivehorizons.registry.ModBlocks;
import com.beyondfivehorizons.registry.ModItems;
import com.beyondfivehorizons.registry.ModParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * Dual-purpose Elysium utility item:
 * 1) Purifies Blight Cores, Blighted Soil, and Blighted Logs within a 5-block radius.
 * 2) Ignites an Elysium Portal frame built from Mossy Stone Bricks or Elysium Moss Stone.
 */
public class PurifyingNectarItem extends Item {
    public PurifyingNectarItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos clicked = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        BlockState clickedState = world.getBlockState(clicked);

        // 1. Check if attempting to ignite an Elysium Portal frame
        if (ElysiumPortalHelper.isFrameBlock(clickedState)) {
            BlockPos inside = clicked.offset(context.getSide());
            if (world.getBlockState(inside).isAir()) {
                if (!world.isClient && ElysiumPortalHelper.tryLightPortal(world, inside)) {
                    world.playSound(null, inside, SoundEvents.BLOCK_BEACON_ACTIVATE,
                            SoundCategory.BLOCKS, 0.9f, 1.2f);
                    if (player != null && !player.isCreative()) {
                        context.getStack().decrement(1);
                    }
                    return ActionResult.SUCCESS;
                }
            }
        }

        // 2. Check for Blight purification
        int purifiedCount = 0;
        boolean cleansedCore = false;
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            for (BlockPos scan : BlockPos.iterate(clicked.add(-4, -2, -4), clicked.add(4, 3, 4))) {
                BlockState s = serverWorld.getBlockState(scan);
                if (s.isOf(ModBlocks.BLIGHT_CORE)) {
                    serverWorld.setBlockState(scan, ModBlocks.RESTORATIVE_MOSS.getDefaultState(), Block.NOTIFY_LISTENERS);
                    Block.dropStack(serverWorld, scan.up(), new ItemStack(ModItems.LIVING_ESSENCE, 2));
                    Block.dropStack(serverWorld, scan.up(), new ItemStack(ModItems.BLIGHTED_SPORE, 1));
                    purifiedCount++;
                    cleansedCore = true;
                } else if (s.isOf(ModBlocks.BLIGHTED_SOIL)) {
                    serverWorld.setBlockState(scan, ModBlocks.ELYSIUM_MOSS_STONE.getDefaultState(), Block.NOTIFY_LISTENERS);
                    purifiedCount++;
                } else if (s.isOf(ModBlocks.BLIGHTED_LOG)) {
                    serverWorld.setBlockState(scan, ModBlocks.ELYSIUM_LOG.getDefaultState(), Block.NOTIFY_LISTENERS);
                    purifiedCount++;
                }
            }

            if (purifiedCount > 0) {
                serverWorld.spawnParticles(ModParticles.LIVING_SPORE,
                        clicked.getX() + 0.5, clicked.getY() + 1.2, clicked.getZ() + 0.5,
                        35, 2.5, 1.2, 2.5, 0.05);
                serverWorld.playSound(null, clicked, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
                        SoundCategory.PLAYERS, 1.0f, 1.1f);
                if (player instanceof ServerPlayerEntity sp && sp.getServer() != null) {
                    if (cleansedCore) {
                        HorizonProgressionState.get(sp.getServer()).incrementPurifiedCores(sp.getUuid());
                    }
                    sp.sendMessage(Text.translatable("message.beyondfivehorizons.blight_purified", purifiedCount)
                            .formatted(Formatting.GREEN), true);
                }
                if (player != null && !player.isCreative()) {
                    context.getStack().decrement(1);
                }
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.beyondfivehorizons.purifying_nectar").formatted(Formatting.AQUA));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
