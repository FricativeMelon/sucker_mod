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
import net.minecraft.tileentity.TileEntity;
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

        prevState = null;
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
        }
    }

    //turns the block into an item
    protected void setUpBlockTicks(BlockPos pos) {
        IItemHandler h = getHandler();
        if (h != null) {
            @SuppressWarnings("ConstantConditions")
            BlockState state = world.getBlockState(pos);
            ItemStack stack = h.getStackInSlot(0);
            stack = h.extractItem(0, stack.getMaxStackSize(), false);
            Item item = stack.getItem();
			/*} else if (item instanceof BucketItem) {
				Fluid flu = ((BucketItem) item).getFluid();
				if (flu == Fluids.EMPTY) {
					if (state.getBlock() instanceof FlowingFluidBlock) {
						FlowingFluidBlock b = (FlowingFluidBlock) state.getBlock();
						if (b.getFluidState(state).isSource()) {
							Fluid flu2 = b.getFluid();
							world.setBlockState(pos, Blocks.AIR.getDefaultState());
							if (flu2 == Fluids.LAVA) {
								//h.extractItem(0)
								h.insertItem(0, new ItemStack(Items.LAVA_BUCKET), true);
							} else if (flu2 == Fluids.WATER) {
								h.insertItem(0, new ItemStack(Items.LAVA_BUCKET), true);
							}
						}
					}
				} else {
					Block blo = Blocks.AIR;
					if (flu == Fluids.LAVA) {
						blo = Blocks.LAVA;
					} else if (flu == Fluids.WATER) {
						blo = Blocks.WATER;
					}
					world.setBlockState(pos, blo.getDefaultState());
					h.insertItem(0, new ItemStack(Items.BUCKET), true);
				}*/
            if (state.getMaterial() != Material.AIR) {
                prevState = state;
                prevItem = item;
                maxTicks = (int)(20 * getDigTime(world, stack, pos));
                ticks = (int)maxTicks;
                if (ticks == 0) {
                    resolveHarvest(stack, state, pos);
                }
            }
            h.insertItem(0, stack, false);
        }
    }

    protected boolean isDelaying() {
        return prevState == null;
    }

    protected boolean checkBlockTicks(BlockPos pos) {
        IItemHandler h = getHandler();
        @SuppressWarnings("ConstantConditions")
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() != prevState.getBlock()) {
            prevState = null;
            ticks = 5;
            return false;
        } else if (h != null) {
            ItemStack stack = h.getStackInSlot(0);
            Item item = stack.getItem();
            if (item != prevItem) {
                prevState = null;
                ticks = 0;
                return false;
            }
        }
        return true;
    }

    protected void triggerTickChange() {
        int x = 9 - (int)(10.0 * ticks / maxTicks);
        world.sendBlockBreakProgress(-1, getFacingPos(), x);
    }

    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new HarvesterBlockContainer(i, world, pos, playerInventory, playerEntity);
    }
}
