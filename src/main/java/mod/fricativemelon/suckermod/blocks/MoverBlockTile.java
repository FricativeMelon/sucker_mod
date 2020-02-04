package mod.fricativemelon.suckermod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;


import javax.annotation.Nullable;
import java.util.*;

import static mod.fricativemelon.suckermod.blocks.PoweredFaceBlock.POWERED_FROM_FACE;
import static net.minecraft.block.DirectionalBlock.FACING;

public class MoverBlockTile extends SuckerBlockTile {
    public MoverBlockTile() {
        super(ModBlocks.MOVER.tile);
    }

    /*private static void analyzeBlockState(World worldIn, BlockPos blockpos) {
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
    }*/

    private boolean noPushExemption(BlockPos blockpos) {
        BlockState state = world.getBlockState(blockpos);
        if (state.has(BlockStateProperties.EXTENDED)
                && state.get(BlockStateProperties.EXTENDED)) {
            return false;
        }
        if (state.has(BlockStateProperties.PISTON_TYPE)) {
            return false;
        }
        Block block = state.getBlock();
        if (block instanceof IOccupiable && ((IOccupiable) block).isOccupied(world, blockpos)) {
            return false;
        }
        return true;
    }

    private boolean canPush(BlockPos blockpos, BlockPos otherPos) {
        BlockState state = world.getBlockState(blockpos);
        BlockState otherState = world.getBlockState(otherPos);
        return noPushExemption(blockpos) && state.isSolid()
                && !(state.getBlockHardness(world, blockpos) < 0)
                && otherState.getMaterial().isReplaceable() && canExtendRod(blockpos)
                && state.isValidPosition(world, otherPos);
    }

    private int getPowerPushed() {
        IItemHandler h = getHandler();
        if (h != null) {
            ItemStack stack =  h.getStackInSlot(0);
            Item item = stack.getItem();
            if (item == Items.REDSTONE) {
                return stack.getCount();
            } else if (item == Items.REDSTONE_BLOCK) {
                return 9*stack.getCount();
            }
        }
        return 0;
    }

    //idea: push block forward and replace with arm
    @Override
    protected boolean onHarvest(ItemStack itemStack, BlockPos blockpos) {
        Direction myDir = myDir();
        //BlockPos otherPos = blockpos.offset(myDir);
        //BlockState state = world.getBlockState(blockpos);
        /*if (canPush(blockpos, otherPos)) {
            //analyzeBlockState(world, blockpos);
            TileEntity te = MoverBlockStructureHelper.tryTotalDup(world, blockpos);
            //IFluidState ifluidstate = world.getFluidState(pos);
            //return world.setBlockState(blockpos, ifluidstate.getBlockState(), 67);
            state = Block.getValidBlockForPosition(state, world, otherPos);
            world.setBlockState(otherPos, state, 67);
            int pp = this.getPowerPushed();
            world.removeTileEntity(blockpos);
            extendRod(blockpos, pp);
            if (te != null) {
                world.setTileEntity(otherPos, te);
            }
            resetState(TICK_PAUSE);
        }*/
        if (noPushExemption(blockpos)) {
            BlockState newState = ModBlocks.HARVESTER_ARM.block.getDefaultState()
                    .with(FACING, myDir);
            int makePowered = this.getPowerPushed();
            if (makePowered > 0) {
                newState = newState.with(POWERED_FROM_FACE, true);
            }
            boolean res = MoverBlockStructureHelper.moveBlocks(world, blockpos, myDir, newState);
            if (res) {
                if (makePowered > 0) {
                    world.getPendingBlockTicks().scheduleTick(blockpos, ModBlocks.HARVESTER_ARM.block, makePowered);
                }
                IItemHandler h = this.getHandler();
                if (h != null) {
                    h.extractItem(1, 1, false);
                }
            }
            resetState(TICK_PAUSE);
        }
        return true;
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new MoverBlockContainer(i, world, pos, playerInventory, playerEntity);
    }
}
