package mod.fricativemelon.suckermod.blocks;

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

    private BlockState prevState;
    private Item prevItem;
    private int maxTicks;

    public HarvesterBlockTile() {
        super(ModBlocks.HARVESTERBLOCK_TILE);
        this.prevState = null;
        this.prevItem = null;
        this.maxTicks = 1;
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

        resetState(0);
    }

    protected void resolveBlockTicks(BlockPos pos) {
        IItemHandler h = getHandler();
        if (h != null) {
            @SuppressWarnings("ConstantConditions")
            BlockState state = world.getBlockState(pos);
            ItemStack stack = h.getStackInSlot(0);
            stack = h.extractItem(0, stack.getMaxStackSize(), false);
            resolveHarvest(stack, state, pos);
            ticks = 5;
            h.insertItem(0, stack, false);
        }
    }

    protected void onHarvest(ItemStack itemStack, BlockPos blockpos) {
        BlockState state = world.getBlockState(blockpos);
        prevState = state;
        prevItem = itemStack.getItem();
        maxTicks = (int)(20 * getDigTime(world, itemStack, blockpos));
        ticks = maxTicks;
        if (ticks == 0) {
            resolveHarvest(itemStack, state, blockpos);
        }
        setFacingPos(blockpos);
    }

    protected boolean checkBlockTicks(BlockPos pos) {
        IItemHandler h = getHandler();
        //noinspection ConstantConditions
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() != prevState.getBlock()) {
            resetState(5);
            return false;
        } else if (h != null) {
            ItemStack stack = h.getStackInSlot(0);
            Item item = stack.getItem();
            if (item != prevItem) {
                resetState(0);
                return false;
            }
        }
        return state.getBlock() != ModBlocks.HARVESTER_ARM_BLOCK
                || state.has(FACING)
                && state.get(FACING) != world.getBlockState(this.pos).get(FACING);
    }

    public void tick() {
        super.tick();
        BlockPos fp = getFacingPos();
        if (prevState == null) {
            if (ticks > 0) {
                ticks--;
            } else {
                if (!world.getBlockState(this.pos).get(TRIGGERED)) {
                    tryRetract();
                    return;
                }
                setUpBlockTicks(fp);
            }
        } else if (checkBlockTicks(fp)) {
            ticks--;
            int x = 9 - (int)(10.0 * ticks / maxTicks);
            world.sendBlockBreakProgress(-1, fp, x);
            if (ticks == 0) {
                resolveBlockTicks(fp);
                world.sendBlockBreakProgress(-1, fp, 0);
                ticks = 5;
            }
        }
    }

    protected void resetState(int ticks) {
        super.resetState(ticks);
        this.prevState = null;
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
