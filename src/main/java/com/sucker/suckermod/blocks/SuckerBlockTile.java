package com.sucker.suckermod.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sucker.suckermod.items.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
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

import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel;

@SuppressWarnings("ALL")
public class SuckerBlockTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {
	private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
	private int ticks;
	private BlockState prevState;
	private Item prevItem;


	public SuckerBlockTile() {
		super(ModBlocks.SUCKERBLOCK_TILE);
		this.ticks = 0;
		this.prevState = null;
		this.prevItem = null;
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

	public void resolveHarvest(Item item, BlockState state, BlockPos pos) {
		TileEntity tileentity = state.hasTileEntity() ? world.getTileEntity(pos) : null;
		if (state.getMaterial().isToolNotRequired() || item.canHarvestBlock(state)) {
			//noinspection ConstantConditions
			Block.spawnDrops(state, world, pos, tileentity, null, ItemStack.EMPTY);
		}
		world.removeBlock(pos, true);
		prevState = null;
	}

	//turns the block into an item
	public void setUpBlockTicks(BlockPos pos) {
		if (handler.isPresent()) {
			@SuppressWarnings("ConstantConditions")
			BlockState state = world.getBlockState(pos);
			IItemHandler h = handler.orElseThrow(() -> new RuntimeException("invalid itemhandler"));
			ItemStack stack = h.getStackInSlot(0);
			Item item = stack.getItem();
			if (state.getMaterial() != Material.AIR) {
				prevState = state;
				prevItem = item;
				ticks = (int) (20*getDigTime(stack, pos));
				if (ticks == 0) {
					resolveHarvest(item, state, pos);
				}
			}
		}
	}

	public boolean checkBlockTicks(BlockPos pos) {
		@SuppressWarnings("ConstantConditions")
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() != prevState.getBlock()) {
			prevState = null;
			ticks = 5;
			return false;
		} else if (handler.isPresent()) {
			IItemHandler h = handler.orElseThrow(() -> new RuntimeException("invalid itemhandler"));
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

	public double getDigTime(ItemStack stack, BlockPos pos) {
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


	@Override
	public void tick() {
		BlockPos newPos = pos.offset(world.getBlockState(pos).get(BlockStateProperties.FACING));
		if (prevState == null) {
			if (ticks > 0) {
				ticks--;
			} else {
				setUpBlockTicks(newPos);
			}
		} else if (checkBlockTicks(newPos)) {
			if (ticks-- == 0) {
				resolveHarvest(prevItem, prevState, newPos);
				ticks = 5;
			}
		}
	}

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

	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new SuckerBlockContainer(i, world, pos, playerInventory, playerEntity);
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new StringTextComponent(getType().getRegistryName().getPath());
	}
}
