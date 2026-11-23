package com.beyondfivehorizons.block;

import com.beyondfivehorizons.dimension.HorizonDimension;
import com.beyondfivehorizons.portal.HorizonTeleportManager;
import com.beyondfivehorizons.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ObeliskCoreBlock extends Block {
    public ObeliskCoreBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world,
                                             BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        HorizonDimension target = resolveDimensionFromKeyItem(stack);
        if (target == null) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
            if (world.getRegistryKey().equals(target.getWorldKey())) {
                HorizonTeleportManager.teleportToOverworld(serverPlayer);
            } else {
                HorizonTeleportManager.teleportToHorizon(serverPlayer, target);
            }
            if (!serverPlayer.isCreative()) {
                stack.decrement(1);
            }
        }
        return ItemActionResult.success(world.isClient);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos,
                                 PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            if (HorizonDimension.fromWorldKey(world.getRegistryKey()).isPresent()
                    && player.isSneaking()
                    && player instanceof ServerPlayerEntity serverPlayer) {
                HorizonTeleportManager.teleportToOverworld(serverPlayer);
            } else {
                player.sendMessage(Text.translatable("message.beyondfivehorizons.obelisk_hint"), true);
            }
        }
        return ActionResult.SUCCESS;
    }

    private HorizonDimension resolveDimensionFromKeyItem(ItemStack stack) {
        if (stack.isOf(ModItems.LIVING_ESSENCE)) return HorizonDimension.ELYSIUM;
        if (stack.isOf(ModItems.CHRONODUST)) return HorizonDimension.CHRONOS;
        if (stack.isOf(ModItems.SPATIAL_CRYSTAL)) return HorizonDimension.ASTRALIA;
        if (stack.isOf(ModItems.PRIMAL_RESIN)) return HorizonDimension.UNDERROOT;
        if (stack.isOf(ModItems.ASHEN_EMBER)) return HorizonDimension.ASHEN_CROWN;
        return null;
    }
}
