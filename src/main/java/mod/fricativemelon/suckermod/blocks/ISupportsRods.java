package mod.fricativemelon.suckermod.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISupportsRods extends IOccupiable {
    default boolean isOccupied(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.has(SuckerBlock.PROCESS_STATE) && state.get(SuckerBlock.PROCESS_STATE) != SuckerBlock.ProcessState.AT_REST) {
            return true;
        }
        Direction myDir = state.get(PoweredFaceBlock.FACING);
        BlockState otherState = world.getBlockState(pos.offset(myDir));
        return otherState.getBlock() == ModBlocks.HARVESTER_ARM.block
                && otherState.get(PoweredFaceBlock.FACING) == myDir;
    }
}
