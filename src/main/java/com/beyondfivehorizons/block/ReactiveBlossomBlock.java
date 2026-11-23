package com.beyondfivehorizons.block;

import com.beyondfivehorizons.registry.ModBlocks;
import com.beyondfivehorizons.registry.ModParticles;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Elysium Evolution flora: reacts when a player or mob approaches/touches it,
 * releasing a brief burst of luminous vitality and closing for a short cooldown.
 */
public class ReactiveBlossomBlock extends PlantBlock {
    public static final MapCodec<ReactiveBlossomBlock> CODEC = createCodec(ReactiveBlossomBlock::new);
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");

    private static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 11.0, 14.0);

    public ReactiveBlossomBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ACTIVE, true));
    }

    @Override
    protected MapCodec<? extends PlantBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
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
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && state.get(ACTIVE) && entity instanceof LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 100, 0));
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 60, 0));
            world.setBlockState(pos, state.with(ACTIVE, false), Block.NOTIFY_LISTENERS);
            world.scheduleBlockTick(pos, this, 160);
            world.playSound(null, pos, SoundEvents.BLOCK_SPORE_BLOSSOM_STEP,
                    SoundCategory.BLOCKS, 0.8f, 1.4f);
            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ModParticles.LIVING_SPORE,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        12, 0.4, 0.3, 0.4, 0.04);
            }
        }
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.get(ACTIVE)) {
            world.setBlockState(pos, state.with(ACTIVE, true), Block.NOTIFY_LISTENERS);
        }
    }
}
