package com.sucker.suckermod.setup;

import com.sucker.suckermod.blocks.ModBlocks;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModSetup {
	
	public ItemGroup itemGroup = new ItemGroup("suckermod") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModBlocks.SUCKERBLOCK);
		}
	};
	
	public void init() {
		
	}
}
