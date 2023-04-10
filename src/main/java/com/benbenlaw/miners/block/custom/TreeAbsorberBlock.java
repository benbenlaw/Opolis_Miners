package com.benbenlaw.miners.block.custom;

import com.benbenlaw.miners.block.entities.ModBlockEntities;
import com.benbenlaw.miners.block.entities.custom.TreeAbsorberBlockEntity;
import com.benbenlaw.miners.recipe.CapBlocksRecipe;
import com.benbenlaw.miners.recipe.NoInventoryRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class TreeAbsorberBlock extends BaseEntityBlock {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public TreeAbsorberBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)));
    }

    private int tickRate;

    @Override
    public InteractionResult use(BlockState pState, Level level, BlockPos blockPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {

        tickRate = 220;

        //Check for Cap and apply correct tickrate
        if(!level.getBlockState(blockPos.above(11).south(3).west(3)).is(Blocks.AIR) && !level.getBlockState(blockPos.above(11).south(3).east(3)).is(Blocks.AIR) &&
                !level.getBlockState(blockPos.above(11).north(3).east(3)).is(Blocks.AIR) && !level.getBlockState(blockPos.above(11).north(3).west(3)).is(Blocks.AIR)) {

            for (CapBlocksRecipe match : level.getRecipeManager().getRecipesFor(CapBlocksRecipe.Type.INSTANCE, NoInventoryRecipe.INSTANCE, level)) {

                String blockName = match.getBlock();
                Block speedBlock = Registry.BLOCK.get(new ResourceLocation(blockName));
                TagKey<Block> blockTag = BlockTags.create(new ResourceLocation(blockName));

                if (level.getBlockState(blockPos.above(11).south(3).west(3)).is(speedBlock) ||
                        level.getBlockState(blockPos.above(11).south(3).west(3)).getBlockHolder().is(blockTag)) {

                    if (level.getBlockState(blockPos.above(11).south(3).east(3)).is(speedBlock) ||
                            level.getBlockState(blockPos.above(11).south(3).east(3)).getBlockHolder().is(blockTag)) {

                        if (level.getBlockState(blockPos.above(11).north(3).east(3)).is(speedBlock) ||
                                level.getBlockState(blockPos.above(11).north(3).east(3)).getBlockHolder().is(blockTag)) {

                            if (level.getBlockState(blockPos.above(11).north(3).west(3)).is(speedBlock) ||
                                    level.getBlockState(blockPos.above(11).north(3).west(3)).is(blockTag)) {
                                tickRate = match.getTickRate();
                            }
                        }
                    }
                }
            }
        }
        if (level.getBlockState(blockPos.above(11).south(3).west(3)).is(Blocks.AIR) && level.getBlockState(blockPos.above(11).south(3).east(3)).is(Blocks.AIR) &&
                level.getBlockState(blockPos.above(11).north(3).east(3)).is(Blocks.AIR) && level.getBlockState(blockPos.above(11).north(3).west(3)).is(Blocks.AIR)) {
            tickRate = 220;
        }

        if(!level.isClientSide()) {
            if (pHand.equals(InteractionHand.MAIN_HAND)){
                if (pState.getValue(TreeAbsorberBlock.LIT)) {

                    BlockState block = level.getBlockState(blockPos.above(2));
                    Block translatedBlock = block.getBlock();
                    String translatedName = translatedBlock.getName().getString();

                    if (tickRate == 220){
                        pPlayer.sendSystemMessage(Component.literal("No speed caps detected or missing some caps!").withStyle(ChatFormatting.RED));
                    }
                    pPlayer.sendSystemMessage(Component.literal("Current tick rate is " + tickRate).withStyle(ChatFormatting.GREEN));
                    pPlayer.sendSystemMessage(Component.literal("Collecting " + translatedName + " and more!").withStyle(ChatFormatting.GREEN));
                }
                if(!pState.getValue(MinerBaseBlock.LIT)) {
                    pPlayer.sendSystemMessage(Component.literal("Not Collecting! Check the tree structure and make sure your using wooden support frames.").withStyle(ChatFormatting.RED));
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55484_) {
        p_55484_.add(LIT);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(LIT, false);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TreeAbsorberBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ModBlockEntities.TREE_ABSORBER_BLOCK_ENTITY.get(),
                (world, blockPos, blockState, blockEntity) -> blockEntity.tick());
    }
}
