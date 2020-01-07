package com.sucker.suckermod.blocks;

import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class SuckerBlock extends Block {
	public SuckerBlock() {
		super(Properties.create(Material.IRON)
				.sound(SoundType.METAL)
				.hardnessAndResistance(2.0f)
		);
		this.setDefaultState(this.stateContainer.getBaseState().with(BlockStateProperties.FACING, Direction.NORTH));
		setRegistryName("suckerblock");
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new SuckerBlockTile();
	}
	
	/*@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state,
			@Nullable LivingEntity entity, ItemStack stack) {
		if (entity != null) {
			world.setBlockState(pos, state.with(BlockStateProperties.FACING,
												getFacingFromEntity(pos, entity)), 2);
		}
	}*/
	
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate = super.getStateForPlacement(context);
        if (blockstate != null) {
            blockstate = blockstate.with(BlockStateProperties.FACING, context.getNearestLookingDirection());
        }
        return blockstate;
    }
	
   public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
       if (oldState.getBlock() != state.getBlock()) {
    	   System.out.println("Running try absorb");
           Direction dir = state.get(BlockStateProperties.FACING);
           this.tryAbsorb(worldIn, pos, dir);
       }
   }
   
   public static Direction getFacingFromEntity(BlockPos clickedBlock,
			LivingEntity entity) {
		clickedBlock = entity.getPosition().subtract(clickedBlock);
		return Direction.getFacingFromVector(
				(float) (clickedBlock.getX()),
				(float) (clickedBlock.getY()),
				(float) (clickedBlock.getZ()));
	}
	
	@Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.FACING);
	}
	
    protected void tryAbsorb(World worldIn, BlockPos pos, Direction dir) {
        if (this.absorb(worldIn, pos, dir)) {
            //worldIn.setBlockState(pos, Blocks.WET_SPONGE.getDefaultState(), 2);
            //worldIn.playEvent(2001, pos, Block.getStateId(Blocks.WATER.getDefaultState()));
        }
    }
    
    //private determineMotion()
    
    //new strat: create a helper block that spreads through lava and can be crafter back to lava
    
    
    //returns a blockstate if something is pulled to source
    private BlockState pullSatisfyingBlocks(World worldIn, BlockPos startingPos, Direction dir, int limit,
    		Predicate<BlockPos> isBlockToMove, Predicate<BlockPos> isBlockToPass, Supplier<BlockState> empty) {
    	BlockState res = null;
    	BlockPos next = startingPos.offset(dir);
    	if (isBlockToMove.test(next)) {
    		res = worldIn.getBlockState(next);
    		worldIn.setBlockState(next, empty.get(), 75);
    	}
    	int i = 0;
    	while(i < limit) {
    		i++;
    		BlockPos next2 = next.offset(dir);
    		if (isBlockToPass.test(next)) {
    			if (isBlockToMove.test(next2)) {
    				worldIn.setBlockState(next, worldIn.getBlockState(next2), 75);
    				worldIn.setBlockState(next2, empty.get(), 75);
    			}	
    		} else {
    			break;
    		}
    	}
    	return res;
    }
    
    private BlockState pushSatisfyingBlocks(World worldIn, BlockPos startingPos, Direction dir, int limit,
    		Predicate<BlockPos> isBlockToMove, Predicate<BlockPos> isBlockToPass, Supplier<BlockState> empty) {
    	return null;
    }
    
    private boolean absorb(World worldIn, BlockPos pos, Direction dir) {
 	    System.out.println("Running actual absorb");
	   	Predicate<BlockPos> rrr = (BlockPos bs) -> worldIn.getFluidState(bs).isTagged(FluidTags.LAVA);
	   	Predicate<BlockPos> aaa = (BlockPos bs) -> worldIn.isAirBlock(bs);
	   	Supplier<BlockState> eee = () -> Blocks.AIR.getDefaultState();
	   	BlockState res = pullSatisfyingBlocks(worldIn, pos, dir, 12, rrr, aaa, eee);
	   	
	   	return true;
/*
        Direction[] dirs = {dir.getOpposite(), dir};
        
        for(Direction direction : dirs) {
        	
            //this is the number of blocks we've absorbed
            int i = 0;
            Tuple<BlockPos, Integer> tuple = new Tuple<>(pos.offset(dir), 1);
        
        while(tuple != null) {
    	    //do all these actions until queue is empty
            BlockPos blockpos = tuple.getA();
            //this will be the distance away from source we are processing
            int j = tuple.getB();
            tuple = null;
        	    //do these actions for every direction
            	BlockPos blockpos1 = blockpos.offset(direction);
            	BlockState blockstate = worldIn.getBlockState(blockpos1);
            	IFluidState ifluidstate = worldIn.getFluidState(blockpos1);
            	Material material = blockstate.getMaterial();
            	//only if the block is lava do we keep going down this branch
            	if (ifluidstate.isTagged(FluidTags.LAVA)) {
            		System.out.println("Lava block!");
            		//if the block being processed is full liquid
            		if (blockstate.getBlock() instanceof IBucketPickupHandler && ((IBucketPickupHandler)blockstate.getBlock()).pickupFluid(worldIn, blockpos1, blockstate) != Fluids.EMPTY) {
            			//new strat: move block to previous location
                		System.out.println("picked up block!");
            			worldIn.setBlockState(blockpos, Blocks.LAVA.getDefaultState(), 75);
            			++i;
            			if (j < 13) {
            				tuple = new Tuple<>(blockpos1, j + 1);
            			}
            		//if the block is instead flowing
            		} else if (blockstate.getBlock() instanceof FlowingFluidBlock) {
                		System.out.println("Flowing block!");
            			worldIn.setBlockState(blockpos, blockstate, 75);
            			worldIn.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 75);
            			++i;
            			if (j < 13) {
            				tuple = new Tuple<>(blockpos1, j + 1);
            			}
	                //no need to do this for lava
	                } else if (material == Material.OCEAN_PLANT || material == Material.SEA_GRASS) {
	                   TileEntity tileentity = blockstate.getBlock().hasTileEntity() ? worldIn.getTileEntity(blockpos1) : null;
	                   spawnDrops(blockstate, worldIn, blockpos1, tileentity);
	                   worldIn.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 3);
	                   ++i;
	                   if (j < 6) {
	                      queue.add(new Tuple<>(blockpos1, j + 1));
	                   }
	                
            		}
            	} else if (worldIn.isAirBlock(blockpos1)) {
            		System.out.println("Air block!");
            		if (j < 13) {
        				tuple = new Tuple<>(blockpos1, j + 1);
        			}
            	}

            //stop once you process the maximum number of things
            if (i > 64) {
                break;
            }
        }

        return i > 0;
        }
    return false;*/
    }
}
