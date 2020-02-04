package mod.fricativemelon.suckermod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class MoverBlockStructureHelper {

    enum PushType {
        //AIR,
        //LIQUID,
        REPLACEABLE,
        //FRAGILE,
        NON_SOLID,
        SOLID,
        IMMOBILE
    }

    //Creates a new TileEntity with the same data as the one at blockpos in worldIn
    public static TileEntity tryTotalDup(World worldIn, BlockPos blockpos) {
        BlockState state = worldIn.getBlockState(blockpos);
        if (state.hasTileEntity()) {
            TileEntity tileEntity = worldIn.getTileEntity(blockpos);
            TileEntity newTE = state.createTileEntity(worldIn);
            if (tileEntity != null && newTE != null) {
                CompoundNBT nbt = new CompoundNBT();
                tileEntity.write(nbt);
                newTE.read(nbt);
                return newTE;
            }
        }
        return null;
    }

    public static PushType getBlockPushType(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Material m = state.getMaterial();
        float hardness = state.getBlockHardness(world, pos);
        //if (m == Material.AIR) {
        //    return PushType.AIR;
        //} else if (m.isLiquid()) {
        //    return PushType.LIQUID;}
        if (m.isReplaceable()) {
            return PushType.REPLACEABLE;
        } else if (hardness < 0) {
            return PushType.IMMOBILE;
        //} else if (hardness == 0) {
        //    return PushType.FRAGILE;
        } else if (state.isSolid()) {
            return PushType.SOLID;
        } else {
            return PushType.NON_SOLID;
        }
    }

    public static Stack<BlockPos> getBlocksToMove(World world, BlockPos pushPoint, Direction pushDirection) {
        Stack<BlockPos> positions = new Stack<>();
        while (true) {
            switch(getBlockPushType(world, pushPoint)) {
                case IMMOBILE:
                    return new Stack<>();
                case SOLID:
                    if (positions.size() < 12) {
                        positions.push(pushPoint);
                        break;
                    } else {
                        return new Stack<>();
                    }
                default:
                    return positions;
            }
            pushPoint = pushPoint.offset(pushDirection);
        }
    }

    public static void copyBlock(World world, BlockPos pos, Direction dir) {
        BlockPos toPos = pos.offset(dir);
        BlockState state = world.getBlockState(pos);
        state = Block.getValidBlockForPosition(state, world, toPos);
        world.setBlockState(toPos, state, 67);
    }

    public static boolean moveBlocks(World world, BlockPos pushPoint, Direction dir, BlockState pushedState) {
        Stack<BlockPos> positions = getBlocksToMove(world, pushPoint, dir);
        List<TileEntity> tiles = new ArrayList<>(13);
        List<BlockPos> tilePos = new ArrayList<>(13);
        if (positions.empty()) {
            return false;
        }
        while (!positions.empty()) {
            BlockPos pos = positions.pop();
            copyBlock(world, pos, dir);
            TileEntity tile = tryTotalDup(world, pos);
            if (tile != null) {
                tilePos.add(pos.offset(dir));
                tiles.add(tile);
                world.removeTileEntity(pos);
            }
        }
        world.setBlockState(pushPoint, pushedState, 67);
        for (int i = 0; i < tiles.size(); i++) {
            world.setTileEntity(tilePos.get(i), tiles.get(i));
        }
        return true;
    }
}
