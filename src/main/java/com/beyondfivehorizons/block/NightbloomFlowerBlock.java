package com.beyondfivehorizons.block;

import com.beyondfivehorizons.registry.ModBlocks;
import com.beyondfivehorizons.registry.ModItems;
import com.beyondfivehorizons.registry.ModParticles;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Elysium Evolution flora: opens and glows exclusively at night (or in dark caves),
 * allowing players to harvest Luminous Pollen while blooming.
 */
public class NightbloomFlowerBlock extends PlantBlock {
    public static final MapCodec<NightbloomFlowerBlock> CODEC = createCodec(NightbloomFlowerBlock::new);
    public static final BooleanProperty OPEN = BooleanProperty.of("open");

    private static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

    public NightbloomFlowerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(OPEN, false));
    }

    @Override
    protected MapCodec<? extends PlantBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return super.canPlantOnTop(floor, world, pos)
                || floor.isOf(ModBlocks.ELYSIUM_MOSS_STONE)
                || floor.isOf(ModBlocks.RESTORATIVE_MOSS);
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean shouldBeOpen = world.isNight() || world.getLightLevel(pos) < 8;
        if (state.get(OPEN) != shouldBeOpen) {
            world.setBlockState(pos, state.with(OPEN, shouldBeOpen), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        boolean isNightNow = world.isNight();
        if (!state.get(OPEN) && isNightNow) {
            if (!world.isClient) {
                world.setBlockState(pos, state.with(OPEN, true), Block.NOTIFY_LISTENERS);
            }
            return ActionResult.SUCCESS;
        }
        if (state.get(OPEN)) {
            if (!world.isClient) {
                dropStack(world, pos, new ItemStack(ModItems.LUMINOUS_POLLEN, 1));
                world.setBlockState(pos, state.with(OPEN, false), Block.NOTIFY_LISTENERS);
                world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
                        SoundCategory.BLOCKS, 0.7f, 1.3f);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(OPEN) && random.nextInt(3) == 0) {
            world.addParticle(ModParticles.LIVING_SPORE,
                    pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5,
                    pos.getY() + 0.6,
                    pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5,
                    0.0, 0.03, 0.0);
        }
    }
}
