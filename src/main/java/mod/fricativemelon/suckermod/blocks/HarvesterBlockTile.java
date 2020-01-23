package mod.fricativemelon.suckermod.blocks;

import mod.fricativemelon.suckermod.blocks.BlockDestroyProgress.DestructionResult;
import mod.fricativemelon.suckermod.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

import static net.minecraft.block.Block.getStateId;
import static net.minecraft.block.Block.nudgeEntitiesWithNewState;
import static net.minecraft.state.properties.BlockStateProperties.*;

public class HarvesterBlockTile extends SuckerBlockTile {

    private BlockDestroyProgress bdp;

    public HarvesterBlockTile() {
        super(ModBlocks.HARVESTER.tile);
        this.bdp = null;
    }

    protected void resolveHarvest(ItemStack stack, BlockState state, BlockPos pos) {
        world.playEvent(null, 2001, pos, getStateId(state));
        IFluidState ifluidstate = world.getFluidState(pos);
        world.setBlockState(pos, ifluidstate.getBlockState(), 11);
        if (stack.attemptDamageItem(1, world.getRandom(), null)) {
            stack.shrink(1);
            stack.setDamage(0);
        }

        TileEntity tileentity = state.hasTileEntity() ? world.getTileEntity(pos) : null;
        if (state.getMaterial().isToolNotRequired() || stack.canHarvestBlock(state)) {
            //noinspection ConstantConditions
            Block.spawnDrops(state, world, pos, tileentity, null, stack);
        }

        resetState(5);
    }

    protected void resolveBlockTicks(BlockPos pos) {
        IItemHandler h = getHandler();
        if (h != null) {
            @SuppressWarnings("ConstantConditions")
            BlockState state = world.getBlockState(pos);
            ItemStack stack = h.getStackInSlot(0);
            stack = h.extractItem(0, stack.getMaxStackSize(), false);
            resolveHarvest(stack, state, pos);
            h.insertItem(0, stack, false);
            this.markDirty();
        }
    }

    protected boolean onHarvest(ItemStack itemStack, BlockPos blockpos) {
        BlockState state = world.getBlockState(blockpos);
        int t = (int)(20 * getDigTime(world, itemStack, blockpos));
        if (t == 0) {
            resolveHarvest(itemStack, state, blockpos);
            resetState(0);
        } else {
            this.bdp = new BlockDestroyProgress(blockpos, state.getBlock(), itemStack.getItem(), t);
        }
        return true;
    }

    @Override
    protected void operantTick() {
        if (bdp == null) {
            super.operantTick();
        } else {
            IItemHandler h = getHandler();
            if (h != null) {
                BlockPos end = getArmEnd();
                DestructionResult res = bdp.continueDestruction(world, end, h.getStackInSlot(0).getItem());
                switch (res) {
                    case CONTINUED: break;
                    case FINISHED: resolveBlockTicks(end);
                    case INTERRUPTED: resetState(0);
                }
            }
        }
    }

    protected void resetState(int ticks) {
        super.resetState(ticks);
        if (bdp != null) {
            this.bdp.continueDestruction(world, null, null);
            this.bdp = null;
        }
    }

    @Override
    protected void powerChange(boolean rising) {
        resetState(0);
    }

    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new HarvesterBlockContainer(i, world, pos, playerInventory, playerEntity);
    }
}
