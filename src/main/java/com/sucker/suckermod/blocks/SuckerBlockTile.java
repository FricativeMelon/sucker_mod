package com.sucker.suckermod.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sucker.suckermod.items.ModItems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
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

public class SuckerBlockTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {
	private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
	
	public SuckerBlockTile() {
		super(ModBlocks.SUCKERBLOCK_TILE);
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
				InventoryHelper.spawnItemStack(world, x, y, z, stack);
			}
		}
	}

	@Override
	public void tick() {
	}
	
	@Override
	public void read(CompoundNBT tag) {
		CompoundNBT invTag = tag.getCompound("inv");
		handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(invTag));
		super.read(tag);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h -> {
            @SuppressWarnings("unchecked")
			CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("inv", compound);
        });
		return super.write(tag);
	}
	
	@SuppressWarnings("unchecked")
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return handler.cast();
		}
		return super.getCapability(cap, side);
	}
	
	private static boolean isStackAcceptable(int slot, @Nonnull ItemStack stack) {
		return stack.getItem() == Items.DIAMOND;
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

	@Override
	public ITextComponent getDisplayName() {
		return new StringTextComponent(getType().getRegistryName().getPath());
	}
}
