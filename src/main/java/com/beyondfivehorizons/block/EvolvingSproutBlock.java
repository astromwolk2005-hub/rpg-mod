package com.beyondfivehorizons.block;

import com.beyondfivehorizons.registry.ModBlocks;
import com.beyondfivehorizons.registry.ModItems;
import com.beyondfivehorizons.registry.ModParticles;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

/**
 * Elysium Evolution mechanic:
 * Gradually evolves through 4 growth stages (0..3).
 * Responds to neighboring blocks (water, crystal blocks, restorative moss)
 * by evolving faster and adapting into crystal blooms or releasing Living Essence.
 */
public class EvolvingSproutBlock extends PlantBlock implements Fertilizable {
    public static final MapCodec<EvolvingSproutBlock> CODEC = createCodec(EvolvingSproutBlock::new);
    public static final IntProperty STAGE = IntProperty.of("stage", 0, 3);

    private static final VoxelShape[] SHAPES = new VoxelShape[]{
            Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 6.0, 12.0),
            Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 10.0, 13.0),
            Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 14.0, 14.0),
            Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0)
    };

    public EvolvingSproutBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(STAGE, 0));
    }

    @Override
    protected MapCodec<? extends PlantBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(STAGE)];
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return super.canPlantOnTop(floor, world, pos)
                || floor.isOf(ModBlocks.ELYSIUM_MOSS_STONE)
                || floor.isOf(ModBlocks.RESTORATIVE_MOSS)
                || floor.isOf(ModBlocks.ELYSIUM_ROOT_BLOCK);
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int stage = state.get(STAGE);
        boolean nearCrystal = hasNeighbor(world, pos, ModBlocks.ELYSIUM_CRYSTAL_BLOCK);
        boolean nearWater = hasNeighbor(world, pos, Blocks.WATER);
        int chance = nearCrystal ? 2 : (nearWater ? 3 : 5);

        if (random.nextInt(chance) == 0) {
            evolve(world, pos, state, random);
        }
    }

    private void evolve(ServerWorld world, BlockPos pos, BlockState state, Random random) {
        int stage = state.get(STAGE);
        if (stage < 3) {
            world.setBlockState(pos, state.with(STAGE, stage + 1), Block.NOTIFY_LISTENERS);
            world.spawnParticles(ModParticles.LIVING_SPORE,
                    pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5,
                    6, 0.25, 0.25, 0.25, 0.02);
        } else if (hasNeighbor(world, pos, ModBlocks.ELYSIUM_CRYSTAL_BLOCK) && random.nextInt(3) == 0) {
            // Adapts to nearby crystal by sprouting a Nightbloom or dropping essence
            world.setBlockState(pos, ModBlocks.NIGHTBLOOM_FLOWER.getDefaultState(), Block.NOTIFY_LISTENERS);
        } else if (world.getBlockState(pos.up()).isAir() && random.nextInt(4) == 0) {
            // Spreads a small root shoot nearby if space permits (strictly capped)
            Direction dir = Direction.Type.HORIZONTAL.random(random);
            BlockPos adj = pos.offset(dir);
            if (world.getBlockState(adj).isAir() && canPlantOnTop(world.getBlockState(adj.down()), world, adj.down())) {
                world.setBlockState(adj, this.getDefaultState().with(STAGE, 0), Block.NOTIFY_LISTENERS);
            }
        }
    }

    private boolean hasNeighbor(World world, BlockPos pos, Block block) {
        for (Direction dir : Direction.values()) {
            if (world.getBlockState(pos.offset(dir)).isOf(block)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (state.get(STAGE) == 3) {
            if (!world.isClient) {
                dropStack(world, pos, new ItemStack(ModItems.LIVING_ESSENCE, 1));
                world.setBlockState(pos, state.with(STAGE, 1), Block.NOTIFY_LISTENERS);
                world.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES,
                        SoundCategory.BLOCKS, 0.8f, 1.2f);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return state.get(STAGE) < 3;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        evolve(world, pos, state, random);
    }
}
