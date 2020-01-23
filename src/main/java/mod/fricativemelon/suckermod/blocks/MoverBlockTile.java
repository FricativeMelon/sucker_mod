package mod.fricativemelon.suckermod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MoverBlockTile extends SuckerBlockTile {
    public MoverBlockTile() {
        super(ModBlocks.MOVER.tile);
    }

    //Creates a new TileEntity with the same inventory as the one at blockpos in worldIn
    /*private static TileEntity tryInventoryDup(World worldIn, BlockPos blockpos) {
        BlockState state = worldIn.getBlockState(blockpos);
        if (state.hasTileEntity()) {
            TileEntity tileEntity = worldIn.getTileEntity(blockpos);
            if (tileEntity instanceof IInventory) {
                TileEntity newTE = state.createTileEntity(worldIn);
                if (newTE instanceof IInventory) {
                    IInventory invFrom = (IInventory) tileEntity;
                    IInventory invTo = (IInventory) tileEntity;
                    for (int i = 0; i < Math.min(invFrom.getSizeInventory(), invTo.getSizeInventory()); i++) {
                        invTo.setInventorySlotContents(i, invFrom.getStackInSlot(i));
                    }
                    return newTE;
                }
            }
        }
        return null;
    }*/

    //Creates a new TileEntity with the same data as the one at blockpos in worldIn
    private static TileEntity tryTotalDup(World worldIn, BlockPos blockpos) {
        BlockState state = worldIn.getBlockState(blockpos);
        if (state.hasTileEntity()) {
            TileEntity tileEntity = worldIn.getTileEntity(blockpos);
            TileEntity newTE = state.createTileEntity(worldIn);
            if (tileEntity != null && newTE != null) {
                CompoundNBT nbt = new CompoundNBT();
                tileEntity.write(nbt);
                newTE.read(nbt);
                tileEntity.remove();
                return newTE;
            }
        }
        return null;
    }

    private static void analyzeBlockState(World worldIn, BlockPos blockpos) {
        BlockState state = worldIn.getBlockState(blockpos);
        System.out.println(state.getBlock().getTranslationKey());
        if (!state.isSolid()) { System.out.println("Non-solid");}
        if (!state.isValidPosition(worldIn, blockpos)) { System.out.println("isn't valid");}
        if (!state.isNormalCube(worldIn, blockpos)) { System.out.println("Not Normal Cube");}
        if (!state.isOpaqueCube(worldIn, blockpos)) { System.out.println("Not Opaque Cube");}
        if (state.isFoliage(worldIn, blockpos)) { System.out.println("Foliage");}
        if (state.allowsMovement(worldIn, blockpos, PathType.AIR)) { System.out.println("Air movement!");}
        if (state.allowsMovement(worldIn, blockpos, PathType.LAND)) { System.out.println("Land movement!");}
        if (state.allowsMovement(worldIn, blockpos, PathType.WATER)) { System.out.println("Water movement!");}
        if (state.canBeReplacedByLeaves(worldIn, blockpos)) { System.out.println("Replaceable by leaves");}
        if (state.canBeReplacedByLogs(worldIn, blockpos)) { System.out.println("Replaceable by logs");}
        for (Direction dir: Direction.values()) {
            if (state.canBeConnectedTo(worldIn, blockpos, dir)) {System.out.println("Can be connected in direction " + dir); }
        }
        System.out.println(state.getMaterial().getPushReaction());
        if (state.getMaterial().isReplaceable()) {
            System.out.println("Is Replaceable");
        }
        VoxelShape vs = state.getCollisionShape(worldIn, blockpos);
        if (vs.isEmpty()) {
            System.out.println("box[EMPTY]");
        } else {
            System.out.println(vs.getBoundingBox());
        }
    }

    private static boolean canBeMoved(World worldIn, BlockPos blockpos, BlockPos otherPos) {
        BlockState state = worldIn.getBlockState(blockpos);
        if (!state.isValidPosition(worldIn, otherPos)) {
            return false;
        }
        if (state.has(BlockStateProperties.CHEST_TYPE)
                && state.get(BlockStateProperties.CHEST_TYPE) != ChestType.SINGLE) {
            return false;
        }
        if (state.has(BlockStateProperties.EXTENDED)
                && state.get(BlockStateProperties.EXTENDED)) {
            return false;
        }
        if (state.has(BlockStateProperties.PISTON_TYPE)) {
            return false;
        }
        if (state.getBlock() instanceof ISupportsRods) {
            Direction myDir = state.get(BlockStateProperties.FACING);
            BlockState otherState = worldIn.getBlockState(blockpos.offset(myDir));
            if (otherState.getBlock() == ModBlocks.HARVESTER_ARM.block
                    && otherState.get(BlockStateProperties.FACING) == myDir) {
                return false;
            }
        }
        return true;
    }

    //idea: push block forward and replace with arm
    @Override
    protected boolean onHarvest(ItemStack itemStack, BlockPos blockpos) {
        Direction myDir = world.getBlockState(this.pos).get(BlockStateProperties.FACING);
        BlockPos otherPos = blockpos.offset(myDir);
        if (!canBeMoved(world, blockpos, otherPos)) {
            return true;
        }
        BlockState state = world.getBlockState(blockpos);
        BlockState otherState = world.getBlockState(otherPos);
        if (otherState.getMaterial().isReplaceable() && canExtendRod(blockpos)) {
            //analyzeBlockState(world, blockpos);
            TileEntity te = tryTotalDup(world, blockpos);
            extendRod(blockpos);
            world.setBlockState(otherPos, state);
            if (te != null) {
                world.setTileEntity(otherPos, te);
            }
            resetState(5);
        }
        return true;
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new MoverBlockContainer(i, world, pos, playerInventory, playerEntity);
    }
}
