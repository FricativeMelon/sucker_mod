package mod.fricativemelon.suckermod.items;

import mod.fricativemelon.suckermod.SuckerMod;

import net.minecraft.item.Item;

public class PipeItem extends Item {
	public PipeItem() {
		super(new Item.Properties()
			.group(SuckerMod.setup.itemGroup));
		setRegistryName("pipeitem");
	}
}
