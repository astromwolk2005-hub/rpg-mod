package com.beyondfivehorizons.block;

import com.beyondfivehorizons.dimension.HorizonDimension;
import com.beyondfivehorizons.portal.HorizonTeleportManager;
import com.beyondfivehorizons.registry.ModParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Dedicated two-way portal block linking Overworld and Elysium.
 */
public class ElysiumPortalBlock extends Block {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);

    public ElysiumPortalBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient) {
            return;
        }
        if (entity instanceof ServerPlayerEntity player && HorizonTeleportManager.canUsePortal(player)) {
            if (world.getRegistryKey().equals(HorizonDimension.ELYSIUM.getWorldKey())) {
                HorizonTeleportManager.teleportToOverworld(player, true);
            } else {
                HorizonTeleportManager.teleportToHorizon(player, HorizonDimension.ELYSIUM, true);
            }
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        for (int i = 0; i < 3; i++) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            world.addParticle(ModParticles.LIVING_SPORE, x, y, z,
                    (random.nextDouble() - 0.5) * 0.04, 0.04, (random.nextDouble() - 0.5) * 0.04);
        }
    }
}
