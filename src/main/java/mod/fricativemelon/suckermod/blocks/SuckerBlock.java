package mod.fricativemelon.suckermod.blocks;

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
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class SuckerBlock extends Block {
	public SuckerBlock() {
		super(Properties.create(Material.IRON)
				.sound(SoundType.METAL)
				.hardnessAndResistance(2.0f)
		);
		this.setDefaultState(this.stateContainer.getBaseState().with(BlockStateProperties.FACING, Direction.NORTH));
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
    	if (!world.isRemote) {
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
            blockstate = blockstate.with(BlockStateProperties.FACING, context.getNearestLookingDirection());
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
	}

}
