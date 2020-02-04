package mod.fricativemelon.suckermod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public abstract class PoweredFaceBlock extends DirectionalBlock {
    public static final BooleanProperty POWERED_FROM_FACE = BooleanProperty.create("powered_from_face");

    protected PoweredFaceBlock(Properties builder) {
        super(builder);
    }

    protected static BlockState addDefaults(BlockState state) {
        return state.with(FACING, Direction.NORTH).with(POWERED_FROM_FACE, false);
    }

    //called when a scheduled tick happens on this block
    public void func_225534_a_(BlockState state, ServerWorld sWorld, BlockPos pos, Random rand) {
        sWorld.setBlockState(pos, state.with(POWERED_FROM_FACE, false));
    }

    /*public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return 15;
        //return !blockState.get(POWERED_FROM_FACE) ? 0 : blockState.getWeakPower(blockAccess, pos, side);
    }*/

    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        if (!blockState.get(POWERED_FROM_FACE) || side != blockState.get(FACING).getOpposite()) {
            return 0;
        } else {
            return 15;
        }
    }

    public boolean canProvidePower(BlockState state) {
        return true;
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(FACING, POWERED_FROM_FACE);
    }
}
