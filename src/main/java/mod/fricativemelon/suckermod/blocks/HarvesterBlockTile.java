package mod.fricativemelon.suckermod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
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
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

import static net.minecraft.block.Block.getStateId;

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
    @SuppressWarnings("ConstantConditions")
    //turns the block into an item
    protected void setUpBlockTicks(BlockPos pos) {
        IItemHandler h = getHandler();
        if (h != null) {
            Direction myDir = world.getBlockState(this.pos).get(BlockStateProperties.FACING);
            ItemStack stack = h.getStackInSlot(0);
            stack = h.extractItem(0, stack.getMaxStackSize(), false);
            Item item = stack.getItem();
            int count = 1;
            while (count <= 12) {
                if (isHarvestable(world, pos)) {
                    BlockState state = world.getBlockState(pos);
                    prevState = state;
                    prevItem = item;
                    maxTicks = (int)(20 * getDigTime(world, stack, pos));
                    ticks = (int)maxTicks;
                    if (ticks == 0) {
                        resolveHarvest(stack, state, pos);
                    }
                    setFacingPos(pos);
                    break;
                } else {
                    count++;
                    pos = pos.offset(myDir);
                }
            }
            h.insertItem(0, stack, false);
        }
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
        return true;
    }

    private void resetState(int ticks) {
        this.prevState = null;
        this.ticks = ticks;
        setFacingPos(null);
    }

    @Override
    protected void powerChange(boolean rising) {
        if (!rising) {
            resetState(0);
        }
    }

    @Override
    public void tick() {
        BlockPos fp = getFacingPos();
        if (world == null || world.isRemote) {
            return;
        }
        if (!world.getBlockState(this.pos).get(BlockStateProperties.TRIGGERED)) {
            return;
        }
        if (prevState == null) {
            if (ticks > 0) {
                ticks--;
            } else {
                setUpBlockTicks(fp);
            }
        } else if (checkBlockTicks(fp)) {
            ticks--;
            int x = 9 - (int)(10.0 * ticks / maxTicks);
            world.sendBlockBreakProgress(-1, getFacingPos(), x);
            if (ticks == 0) {
                resolveBlockTicks(fp);
                ticks = 5;
            }
        }
    }

    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new HarvesterBlockContainer(i, world, pos, playerInventory, playerEntity);
    }
}
