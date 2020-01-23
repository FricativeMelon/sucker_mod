package mod.fricativemelon.suckermod.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.plaf.basic.BasicComboBoxUI;

import mod.fricativemelon.suckermod.items.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import static net.minecraft.block.Block.getStateId;
import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel;

public abstract class SuckerBlockTile extends TileEntity
		implements ITickableTileEntity, INamedContainerProvider, ITransferable {
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

	protected BlockPos getFacingPos() {
		return pos.offset(myDir());
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
		Direction myDir = world.getBlockState(this.pos).get(BlockStateProperties.FACING);
		BlockPos end = getArmEnd();
		if (end != null && !end.equals(this.pos.offset(myDir))) {
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
				resetState(5);
				this.markDirty();
			}

		}
	}

	protected boolean canExtendRod(BlockPos blockpos) {
		IItemHandler h = this.getHandler();
		if (h != null) {
			ItemStack pillars = h.extractItem(1, 1, true);
			if (pillars.getCount() > 0 && pillars.getItem() == ModItems.PIPEITEM) {
				AxisAlignedBB aabb = new AxisAlignedBB(blockpos);
				List<LivingEntity> L = world.getEntitiesWithinAABB(LivingEntity.class, aabb, null);
				return L.size() == 0;
			}
		}
		return false;
	}

	protected boolean extendRod(BlockPos blockpos) {
		IItemHandler h = this.getHandler();
		if (h != null) {
			if (canExtendRod(blockpos)) {
				h.extractItem(1, 1, false);
				BlockState newState = ModBlocks.HARVESTER_ARM.block.getDefaultState()
						.with(DirectionalBlock.FACING, myDir());
				world.setBlockState(blockpos, newState);
				return true;
			}
		}
		return false;
	}


	protected Direction myDir() {
		return world.getBlockState(this.pos).get(BlockStateProperties.FACING);
	}

	//turns the block into an item
	//places the block
	protected void setUpBlockTicks(BlockPos pos) {
		IItemHandler h = this.getHandler();
		if (h != null && world != null) {
			ItemStack stack = h.getStackInSlot(0);
			stack = h.extractItem(0, stack.getMaxStackSize(), false);
			BlockPos end = getArmEnd();
			if (end == null) {
			} else if (isHarvestable(world, end) && onHarvest(stack, end)) {
			} else {
				if (!extendRod(end)) {
					onNoExtend(end);
				}
				resetState(5);
			}
			h.insertItem(0, stack, false);
			this.markDirty();
		}
	}

	protected void resetState(int ticks) {
		this.ticks = ticks;
	}

	protected void operantTick() {
		if (ticks > 0) {
			ticks--;
		} else {
			if (!world.getBlockState(this.pos).get(BlockStateProperties.TRIGGERED)) {
				tryRetract();
				return;
			}
			setUpBlockTicks(getFacingPos());
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
					&& s.get(BlockStateProperties.FACING) == myDir) {
				blockpos = blockpos.offset(myDir);
			} else {
				return blockpos;
			}
		}
		return null;
	}

	protected void powerChange(boolean rising) {}

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

	@Override
	public TileEntity transfer() {
		IItemHandler h = getHandler();
		HarvesterBlockTile tileEntity = new HarvesterBlockTile();
		IItemHandler h2 = tileEntity.getHandler();
		if (h != null && h2 != null) {
			System.out.println("transferring items...");
			ItemStack stack1 = h.extractItem(0, h.getStackInSlot(0).getMaxStackSize(), false);
			ItemStack stack2 = h.extractItem(1, h.getStackInSlot(1).getMaxStackSize(), false);
			h.insertItem(0, stack1, false);
			h.insertItem(1, stack2, false);
		}
		return tileEntity;
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
