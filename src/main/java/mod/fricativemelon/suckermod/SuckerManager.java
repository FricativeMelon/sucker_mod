package mod.fricativemelon.suckermod;

import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SuckerManager {
	
	private World worldIn;
	private BlockPos startingPos;
	private Direction dir;
	private int limit;
	private Predicate<BlockPos> isBlockToMove;
	private Predicate<BlockPos> isBlockToPass;
	private Supplier<BlockState> emptyState;
	
	public SuckerManager(World worldIn, BlockPos startingPos, Direction dir, int limit,
			Predicate<BlockPos> isBlockToMove, Predicate<BlockPos> isBlockToPass, Supplier<BlockState> emptyState) {
		super();
		this.worldIn = worldIn;
		this.startingPos = startingPos;
		this.dir = dir;
		this.limit = limit;
		this.isBlockToMove = isBlockToMove;
		this.isBlockToPass = isBlockToPass;
		this.emptyState = emptyState;
	}
	
	//returns the 
	private Tuple<Integer, Integer> mapRange() {
		return null;
	}
	
    //returns a blockstate if something is pulled to source
    private BlockState pullSatisfyingBlocks() {
    	BlockState res = null;
    	BlockPos next = startingPos.offset(dir);
    	if (isBlockToMove.test(next)) {
    		res = worldIn.getBlockState(next);
    		worldIn.setBlockState(next, emptyState.get(), 75);
    	}
    	int i = 0;
    	while(i < limit) {
    		i++;
    		BlockPos next2 = next.offset(dir);
    		if (isBlockToPass.test(next)) {
    			if (isBlockToMove.test(next2)) {
    				worldIn.setBlockState(next, worldIn.getBlockState(next2), 75);
    				worldIn.setBlockState(next2, emptyState.get(), 75);
    			}	
    		} else {
    			break;
    		}
    	}
    	return res;
    }
    
    private void pushSatisfyingBlocks() {
    	BlockState res = null;
    	BlockPos next = startingPos.offset(dir.getOpposite());
    	if (isBlockToMove.test(next)) {
    		res = worldIn.getBlockState(next);
    		worldIn.setBlockState(next, emptyState.get(), 75);
    	}
    }
    
    public void suck() {
    	boolean[] arrRight = new boolean[limit];
    	boolean[] arrLeft = new boolean[limit];
    	BlockState res = this.pullSatisfyingBlocks();
    	
    }
	
}
