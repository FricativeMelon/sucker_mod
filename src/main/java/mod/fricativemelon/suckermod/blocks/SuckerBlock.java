package mod.fricativemelon.suckermod.blocks;

import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class SuckerBlock extends Block {
	public SuckerBlock() {
		super(Properties.create(Material.ROCK)
				.sound(SoundType.STONE)
				.hardnessAndResistance(3.5f)
		);
		this.setDefaultState(this.stateContainer.getBaseState().with(BlockStateProperties.FACING, Direction.NORTH)
		.with(BlockStateProperties.TRIGGERED, false));
	}

	private void powerChange(boolean rising, World worldIn, BlockPos pos) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof SuckerBlockTile) {
			((SuckerBlockTile)tileentity).powerChange(rising);
		}
	}


	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		boolean flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.up());
		boolean flag1 = state.get(BlockStateProperties.TRIGGERED);
		if (flag && !flag1) {
			this.powerChange(true, worldIn, pos);
			worldIn.setBlockState(pos, state.with(BlockStateProperties.TRIGGERED, true), 4);
		} else if (!flag && flag1) {
			this.powerChange(false, worldIn, pos);
			worldIn.setBlockState(pos, state.with(BlockStateProperties.TRIGGERED, false), 4);
		}

	}

	@Override
	public int tickRate(IWorldReader worldIn) {
		return 4;
	}


	@SuppressWarnings("NullableProblems")
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof SuckerBlockTile) {
				SuckerBlockTile sbt = (SuckerBlockTile) tileentity;
				sbt.dropContents(worldIn.getRandom());
				worldIn.updateComparatorOutputLevel(pos, this);
			}

			super.onReplaced(state, worldIn, pos, newState, isMoving);
		}
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);
	
		
	/*@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state,
			@Nullable LivingEntity entity, ItemStack stack) {
		if (entity != null) {
			world.setBlockState(pos, state.with(BlockStateProperties.FACING,
												getFacingFromEntity(pos, entity)), 2);
		}
	}*/
	
	//this is apparently onBlockActivated
    @Override
	public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos,
			PlayerEntity player, Hand hand, BlockRayTraceResult brtr) {
    	if (world != null && !world.isRemote) {
    		TileEntity tileEntity = world.getTileEntity(pos);
    		if (tileEntity instanceof INamedContainerProvider &&
    		    player instanceof ServerPlayerEntity) {
    			NetworkHooks.openGui((ServerPlayerEntity) player,
    					             (INamedContainerProvider) tileEntity,
    					             tileEntity.getPos());
        	} else {
        		throw new IllegalStateException("Our named container provider or server player is missing!");
        	}
    	}
		return ActionResultType.SUCCESS;
	}

	@Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
    	BlockState blockstate = super.getStateForPlacement(context);
        if (blockstate != null) {
            blockstate = blockstate.with(BlockStateProperties.FACING, context.getNearestLookingDirection().getOpposite());
        }
        return blockstate;
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
		builder.add(BlockStateProperties.TRIGGERED);
	}

}
