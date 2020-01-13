package mod.fricativemelon.suckermod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.lwjgl.system.CallbackI;

import javax.annotation.Nullable;

import java.util.Random;

import static net.minecraft.util.Direction.*;


public class RotaterBlock extends Block {

	private static final DirectionProperty[] rotProps = {BlockStateProperties.FACING,
												         BlockStateProperties.FACING_EXCEPT_UP,
												 	 	 BlockStateProperties.HORIZONTAL_FACING};

	private static final Direction[][] rotArrs = {{NORTH, EAST, DOWN, SOUTH, WEST, UP},
												  {NORTH, EAST, DOWN, SOUTH, WEST},
												  {NORTH, EAST, SOUTH, WEST}};

	private static final Axis[] axisArr = {Axis.X, Axis.Y, Axis.Z};
	private static final Axis[] axisHorArr = {Axis.X, Axis.Z};

	private static final SlabType[] slabArr = {SlabType.BOTTOM, SlabType.TOP};

	private static final Integer[] intArr = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

	public RotaterBlock() {
		super(Properties.create(Material.ROCK)
				.sound(SoundType.STONE)
				.hardnessAndResistance(3.5f)
		);
		this.setDefaultState(this.stateContainer.getBaseState().with(BlockStateProperties.FACING, Direction.NORTH)
		.with(BlockStateProperties.TRIGGERED, false));
		setRegistryName("rotaterblock");
	}

	public static <T> T rotateThroughArray(T d, T[] L) {
		for (int i = 0; i < L.length; i++) {
			if (d == L[i]) {
				return L[(i+1)%L.length];
			}
		}
		return d;
	}

	private static <T extends Comparable<T>> BlockState cycleBlock(BlockState state,
														          IProperty<T> iproperty, T[] arr) {
		if (state.has(iproperty)) {
			T d = state.get(iproperty);
			T newDir = rotateThroughArray(d, arr);
			return state.with(iproperty, newDir);
		}
		return null;
	}

	private static BlockState cycleAll(BlockState startState) {
		if (startState.has(BlockStateProperties.BED_PART)
		    || (startState.has(BlockStateProperties.CHEST_TYPE)
				&& startState.get(BlockStateProperties.CHEST_TYPE) != ChestType.SINGLE)) {
			return startState;
		}
		BlockState state;
		for (int i = 0; i < rotProps.length; i++) {
			state = cycleBlock(startState, rotProps[i], rotArrs[i]);
			if (state != null)  {
				return state;
			}
		}

		state = cycleBlock(startState, BlockStateProperties.AXIS, axisArr);
		if (state != null)  {
			return state;
		}
		state = cycleBlock(startState, BlockStateProperties.HORIZONTAL_AXIS, axisHorArr);
		if (state != null)  {
			return state;
		}
		state = cycleBlock(startState, BlockStateProperties.SLAB_TYPE, slabArr);
		if (state != null)  {
			return state;
		}
		state = cycleBlock(startState, BlockStateProperties.ROTATION_0_15, intArr);
		return state;
	}

	public static BlockState rotateBlock(World world, BlockPos pos, BlockState startState) {
		BlockState state = cycleAll(startState);
		if (state != null) {
			int count = 0;
			while (!state.isValidPosition(world, pos)) {
				if (count > 6) {
					state = null;
					break;
				}
				count++;
				state = cycleAll(state);
			}
		}
		return state;
	}

	public BlockPos getTargetPosition(World worldIn, BlockPos pos) {
		BlockState myState = worldIn.getBlockState(pos);
		Direction myDir = myState.get(BlockStateProperties.FACING);
		return pos.offset(myDir);
	}

	private void powerChange(boolean rising, World worldIn, BlockPos pos) {
		if (rising) {
			BlockPos newPos = this.getTargetPosition(worldIn, pos);
			BlockState startState = worldIn.getBlockState(newPos);
			BlockState state = rotateBlock(worldIn, pos, startState);
			if (state != null) {
				worldIn.setBlockState(newPos, state, 11);
			}
		}
	}

	@Override
	public void func_225534_a_(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		this.powerChange(true, world, pos);
	}

	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
								boolean isMoving) {
		boolean flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.up());
		boolean flag1 = state.get(BlockStateProperties.TRIGGERED);
		if (flag && !flag1) {
			worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1);
			worldIn.setBlockState(pos, state.with(BlockStateProperties.TRIGGERED, true), 4);
		} else if (!flag && flag1) {
			//this.powerChange(false, worldIn, pos);
			worldIn.setBlockState(pos, state.with(BlockStateProperties.TRIGGERED, false), 4);
		}
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
