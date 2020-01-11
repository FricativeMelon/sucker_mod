package mod.fricativemelon.suckermod.setup;

import mod.fricativemelon.suckermod.blocks.ModBlocks;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModSetup {
	
	public ItemGroup itemGroup = new ItemGroup("suckermod") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModBlocks.PLACERBLOCK);
		}
	};
	
	public void init() {
		
	}
}
