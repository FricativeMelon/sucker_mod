package mod.fricativemelon.suckermod.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
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

@SuppressWarnings("ALL")
public abstract class SuckerBlockTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {
	private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
	protected int ticks;
	private BlockPos facingPos;

	public SuckerBlockTile(TileEntityType<?> block) {
		super(block);
		this.ticks = 0;
		this.facingPos = null;
	}

	protected enum PlacementStatus {
		PLACEABLE,
		UNPLACEABLE,
		SOLID
	}

	protected void setFacingPos(BlockPos pos) {
		facingPos = pos;
	}

	protected static boolean isHarvestable(World worldIn, BlockPos pos) {
		BlockState state = worldIn.getBlockState(pos);
		if (state.isAir(worldIn, pos) || state.getMaterial() == Material.WATER) {
			return false;
		} else {
			return true;
		}
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

	protected IItemHandler getHandler() {
		return handler.orElse(null);
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

	protected BlockPos getFacingPos() {
		if (facingPos == null) {
			return pos.offset(world.getBlockState(pos).get(BlockStateProperties.FACING));
		}
		return facingPos;
	}

	protected void resolveHarvest(ItemStack stack, BlockState state, BlockPos pos) { }

	protected void resolveBlockTicks(BlockPos pos) { }

	//turns the block into an item
	protected void setUpBlockTicks(BlockPos pos) { }

	protected boolean checkBlockTicks(BlockPos pos) {
		return false;
	}

	protected void triggerTickChange() { }

	public void dropContents(Random random) {
		if (handler.isPresent()) {
			IItemHandler h = handler.orElseThrow(() -> new RuntimeException("invalid itemhandler"));
			List<ItemStack> L = dropItemHandlerContents(h, random);
			double x = (double)pos.getX();
			double y = (double)pos.getY();
			double z = (double)pos.getZ();
			for (ItemStack stack: L) {
				assert world != null;
				InventoryHelper.spawnItemStack(world, x, y, z, stack);
			}
		}
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
	
	public static boolean isStackAcceptable(int slot, @Nonnull ItemStack stack) {
		return true;
		//return stack.getItem() == Items.DIAMOND;
	}
	
	private IItemHandler createHandler() {
		return new ItemStackHandler(1) {
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
