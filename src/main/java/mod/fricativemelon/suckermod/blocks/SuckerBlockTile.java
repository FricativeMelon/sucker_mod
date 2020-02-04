package mod.fricativemelon.suckermod.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mod.fricativemelon.suckermod.items.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static mod.fricativemelon.suckermod.blocks.PoweredFaceBlock.POWERED_FROM_FACE;
import static net.minecraft.block.DirectionalBlock.FACING;
import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel;

public abstract class SuckerBlockTile extends TileEntity
		implements ITickableTileEntity, INamedContainerProvider {

	protected static int TICK_PAUSE = 10;

	private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
	private int ticks;

	public SuckerBlockTile(TileEntityType<?> block) {
		super(block);
		this.ticks = 0;
	}

	protected enum PlacementStatus {
		PLACEABLE,
		UNPLACEABLE,
		SOLID
	}

	protected static boolean isHarvestable(World worldIn, BlockPos pos) {
		BlockState state = worldIn.getBlockState(pos);
		return !state.isAir(worldIn, pos) && state.getMaterial() != Material.WATER;
	}

	protected static PlacementStatus isPassable(World worldIn, BlockPos pos, Block block) {
		BlockState state = worldIn.getBlockState(pos);
		if (state.isSolid()) {
			return PlacementStatus.SOLID;
		} else if (state.isAir(worldIn, pos) && block.getDefaultState().isValidPosition(worldIn, pos)) {
			return PlacementStatus.PLACEABLE;
		} else {
			return PlacementStatus.UNPLACEABLE;
		}
	}

	public static List<ItemStack> dropItemHandlerContents(IItemHandler itemHandler, Random random) {
		final List<ItemStack> drops = new ArrayList<>();

		for (int slot = 0; slot < itemHandler.getSlots(); ++slot) {
			while (!itemHandler.getStackInSlot(slot).isEmpty()) {
				final int amount = random.nextInt(21) + 10;

				if (!itemHandler.extractItem(slot, amount, true).isEmpty()) {
					final ItemStack itemStack = itemHandler.extractItem(slot, amount, false);
					drops.add(itemStack);
				}
			}
		}

		return drops;
	}

	public static boolean isStackAcceptable(int slot, @Nonnull ItemStack stack) {
		return (slot == 1) == (stack.getItem() == ModItems.PIPEITEM);
		//return stack.getItem() == Items.DIAMOND;
	}

	public static double getDigTime(World world, ItemStack stack, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		double s = stack.getDestroySpeed(state);
		if (state.getMaterial().isToolNotRequired() || stack.getItem().canHarvestBlock(state)) {
			if (s > 1.0F) {
				int i = getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
				if (i > 0 && !stack.isEmpty()) {
					s += (float) (i * i + 1);
				}
			}
			return 1.5*state.getBlockHardness(world, pos) / s;
		} else {
			return 5*state.getBlockHardness(world, pos) / s;
		}
	}

	protected IItemHandler getHandler() {
		return handler.orElse(null);
	}

	protected void resolveHarvest(ItemStack stack, BlockState state, BlockPos pos) { }

	protected void resolveBlockTicks(BlockPos pos) { }

	protected boolean onHarvest(ItemStack itemStack, BlockPos blockpos) {
		return true;
	}

	protected boolean postRetract(BlockPos blockpos) {
		return false;
	}

	protected void onNoExtend(BlockPos pos) { }

	protected void tryRetract() {
		Direction myDir = world.getBlockState(this.pos).get(FACING);
		BlockPos end = getArmEnd();
		if (end != null) {
			if (end.equals(this.pos.offset(myDir))) {
				BlockState newState = world.getBlockState(this.pos)
						.with(SuckerBlock.PROCESS_STATE, SuckerBlock.ProcessState.AT_REST);
				world.setBlockState(this.pos, newState);
			} else {
				IItemHandler h = getHandler();
				if (h != null) {
					ItemStack stack = h.getStackInSlot(1);
					if (stack.getItem() != ModItems.PIPEITEM && stack.getCount() > 0
							|| stack.getMaxStackSize() <= stack.getCount()) {
						return;
					}
					end = end.offset(myDir.getOpposite());
					stack = h.extractItem(1, stack.getMaxStackSize(), false);
					if (stack.getCount() == 0) {
						stack = new ItemStack(ModItems.PIPEITEM, 1);
					} else {
						stack.setCount(stack.getCount() + 1);
					}
					h.insertItem(1, stack, false);
					if (!postRetract(end)) {
						world.removeBlock(end, true);
					}
					resetState(TICK_PAUSE);
					this.markDirty();
				}
			}

		}
	}

	private boolean entitiesInWay(BlockPos blockpos) {
		AxisAlignedBB aabb = new AxisAlignedBB(blockpos);
		List<LivingEntity> L = world.getEntitiesWithinAABB(LivingEntity.class, aabb, null);
		return L.size() != 0;
	}

	private boolean outOfRods() {
		IItemHandler h = this.getHandler();
		if (h != null) {
			ItemStack pillars = h.extractItem(1, 1, true);
			return pillars.getCount() == 0 || pillars.getItem() != ModItems.PIPEITEM;
		}
		return true;
	}

	protected boolean canExtendRod(BlockPos blockpos) {
		return !outOfRods() && !entitiesInWay(blockpos);
	}

	protected void extendRod(BlockPos blockpos, int makePowered) {
		IItemHandler h = this.getHandler();
		if (h != null) {
			h.extractItem(1, 1, false);
			BlockState newState = ModBlocks.HARVESTER_ARM.block.getDefaultState()
					.with(FACING, myDir());
			if (makePowered > 0) {
				newState = newState.with(POWERED_FROM_FACE, true);
				world.getPendingBlockTicks().scheduleTick(blockpos, ModBlocks.HARVESTER_ARM.block, makePowered);
			}
			world.setBlockState(blockpos, newState, 3);
        }
	}


	protected Direction myDir() {
		return world.getBlockState(this.pos).get(FACING);
	}

	//turns the block into an item
	//places the block
	protected void setUpBlockTicks() {
		IItemHandler h = this.getHandler();
		if (h != null && world != null) {
			ItemStack stack = h.getStackInSlot(0);
			BlockPos end = getArmEnd();
			if (end == null) {
			} else if (isHarvestable(world, end)) {
				onHarvest(stack, end);
			} else {
				if (outOfRods()) {
					onNoExtend(end);
				}
				else if (!entitiesInWay(end)) {
					extendRod(end, 0);
				}
				resetState(TICK_PAUSE);
			}
			if (outOfRods()) {
				BlockState newState = world.getBlockState(this.pos)
						.with(SuckerBlock.PROCESS_STATE, SuckerBlock.ProcessState.RETRACTING);
				world.setBlockState(this.pos, newState);
			}
			this.markDirty();
		}
	}

	protected void resetState(int ticks) {
		this.ticks = ticks;
	}

	protected void operantTick() {
		SuckerBlock.ProcessState processState = (world.getBlockState(this.pos).get(SuckerBlock.PROCESS_STATE));
		if (processState == SuckerBlock.ProcessState.AT_REST) {
			ticks = SuckerBlockTile.TICK_PAUSE;
			return;
		}
		if (ticks > 0) {
			ticks--;
		} else {
			if (processState == SuckerBlock.ProcessState.EXTENDING) {
				setUpBlockTicks();
			} else {
				tryRetract();
			}
		}
	}

	public void tick() {
		if (world == null || world.isRemote) {
			return;
		}
		operantTick();
	}

	public void dropContents(Random random) {
		if (handler.isPresent()) {
			IItemHandler h = handler.orElseThrow(() -> new RuntimeException("invalid itemhandler"));
			List<ItemStack> L = dropItemHandlerContents(h, random);
			double x = (double)pos.getX();
			double y = (double)pos.getY();
			double z = (double)pos.getZ();
			for (ItemStack stack: L) {
				InventoryHelper.spawnItemStack(world, x, y, z, stack);
			}
		}
	}

	protected @Nullable BlockPos getArmEnd() {
		Direction myDir = myDir();
		BlockPos blockpos = pos.offset(myDir);
		while (world.isAreaLoaded(pos, 1)) {
			BlockState s = world.getBlockState(blockpos);
			if (s.getBlock() == ModBlocks.HARVESTER_ARM.block
					&& s.get(FACING) == myDir) {
				blockpos = blockpos.offset(myDir);
			} else {
				return blockpos;
			}
		}
		return null;
	}

	/*public void tickOld() {
		if (world == null || world.isRemote) {
			return;
		}
		getFacingPos();
		if (prevState == null) {
			if (ticks > 0) {
				ticks--;
			} else {
				setUpBlockTicks(facingPos);
			}
		} else if (checkBlockTicks(facingPos)) {
			int x = 9 - (int)(10.0 * --ticks / maxTicks);
			world.sendBlockBreakProgress(-1, facingPos, x);
			if (ticks == 0) {
				resolveBlockTicks(facingPos);
				ticks = 5;
			}
		}
	}*/


	@SuppressWarnings("unchecked")
	@Override
	public void read(CompoundNBT tag) {
		CompoundNBT invTag = tag.getCompound("inv");
		handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(invTag));
		super.read(tag);
	}

	@Nonnull
	@Override
	public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h -> {
            @SuppressWarnings("unchecked")
			CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("inv", compound);
        });
		return super.write(tag);
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return handler.cast();
		}
		return super.getCapability(cap, side);
	}

	private IItemHandler createHandler() {
		return new ItemStackHandler(2) {
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				return SuckerBlockTile.isStackAcceptable(slot, stack);
			}
			
			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				if (!SuckerBlockTile.isStackAcceptable(slot, stack)) {
					return stack;
				}
				return super.insertItem(slot, stack, simulate);
			}
		};
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new StringTextComponent(getType().getRegistryName().getPath());
	}
}
