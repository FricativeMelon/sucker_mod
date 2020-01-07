package com.sucker.suckermod.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sucker.suckermod.items.ModItems;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class SuckerBlockTile extends TileEntity implements ITickableTileEntity {
	private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
	
	public SuckerBlockTile() {
		super(ModBlocks.SUCKERBLOCK_TILE);
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
		return stack.getItem() == ModBlocks.SUCKERBLOCK.asItem();
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
}
