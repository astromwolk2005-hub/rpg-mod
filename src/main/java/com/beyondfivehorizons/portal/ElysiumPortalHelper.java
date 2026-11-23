package com.beyondfivehorizons.portal;

import com.beyondfivehorizons.registry.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Validates, lights, and constructs Elysium Portals (frame of Elysium Moss Stone or Mossy Stone Bricks).
 */
public final class ElysiumPortalHelper {
    private ElysiumPortalHelper() {
    }

    public static boolean isFrameBlock(BlockState state) {
        return state.isOf(ModBlocks.ELYSIUM_MOSS_STONE)
                || state.isOf(ModBlocks.ELYSIUM_MOSS_BRICKS)
                || state.isOf(Blocks.MOSSY_STONE_BRICKS);
    }

    /**
     * Attempts to ignite an Elysium Portal frame adjacent to the clicked block.
     * Supports standard 4x5 frames along either X or Z axis.
     */
    public static boolean tryLightPortal(World world, BlockPos insidePos) {
        if (tryLightAlongAxis(world, insidePos, Direction.Axis.X)) {
            return true;
        }
        return tryLightAlongAxis(world, insidePos, Direction.Axis.Z);
    }

    private static boolean tryLightAlongAxis(World world, BlockPos start, Direction.Axis axis) {
        Direction right = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        Direction left = right.getOpposite();

        // Find bottom-left interior corner
        BlockPos bottom = start;
        int downLimit = 0;
        while (downLimit < 5 && world.getBlockState(bottom.down()).isAir()) {
            bottom = bottom.down();
            downLimit++;
        }
        if (!isFrameBlock(world.getBlockState(bottom.down()))) {
            return false;
        }

        BlockPos bottomLeft = bottom;
        int leftLimit = 0;
        while (leftLimit < 5 && world.getBlockState(bottomLeft.offset(left)).isAir()) {
            bottomLeft = bottomLeft.offset(left);
            leftLimit++;
        }

        // Measure interior width (2..5)
        int width = 0;
        while (width < 6 && world.getBlockState(bottomLeft.offset(right, width)).isAir()) {
            width++;
        }
        if (width < 2 || width > 5) {
            return false;
        }

        // Measure interior height (3..6)
        int height = 0;
        while (height < 7 && world.getBlockState(bottomLeft.up(height)).isAir()) {
            height++;
        }
        if (height < 3 || height > 6) {
            return false;
        }

        // Verify frame around entire rectangle
        for (int x = -1; x <= width; x++) {
            for (int y = -1; y <= height; y++) {
                boolean isCorner = (x == -1 || x == width) && (y == -1 || y == height);
                if (isCorner) continue;

                boolean isBorder = (x == -1 || x == width || y == -1 || y == height);
                BlockPos check = bottomLeft.offset(right, x).up(y);
                if (isBorder) {
                    if (!isFrameBlock(world.getBlockState(check))) {
                        return false;
                    }
                } else {
                    if (!world.getBlockState(check).isAir()) {
                        return false;
                    }
                }
            }
        }

        // Fill interior with Elysium Portal blocks
        BlockState portalState = ModBlocks.ELYSIUM_PORTAL.getDefaultState();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                world.setBlockState(bottomLeft.offset(right, x).up(y), portalState);
            }
        }
        return true;
    }

    /**
     * Ensures a safe return Elysium Portal exists near the landing point.
     */
    public static void ensureReturnPortal(ServerWorld world, BlockPos center) {
        // Check if an Elysium portal already exists within 12 blocks
        for (BlockPos scan : BlockPos.iterate(center.add(-10, -4, -10), center.add(10, 6, 10))) {
            if (world.getBlockState(scan).isOf(ModBlocks.ELYSIUM_PORTAL)) {
                return;
            }
        }

        BlockState frame = ModBlocks.ELYSIUM_MOSS_STONE.getDefaultState();
        BlockState portal = ModBlocks.ELYSIUM_PORTAL.getDefaultState();

        // Build clean pad around landing + 4x5 portal facing south
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -1; dz <= 3; dz++) {
                world.setBlockState(center.add(dx, -1, dz), frame);
                for (int dy = 0; dy <= 4; dy++) {
                    world.removeBlock(center.add(dx, dy, dz), false);
                }
            }
        }

        for (int x = -1; x <= 2; x++) {
            for (int y = 0; y <= 4; y++) {
                boolean border = (x == -1 || x == 2 || y == 0 || y == 4);
                BlockPos p = center.add(x, y, 0);
                world.setBlockState(p, border ? frame : portal);
            }
        }
    }
}
