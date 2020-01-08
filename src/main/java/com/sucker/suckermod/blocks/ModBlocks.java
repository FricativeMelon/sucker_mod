package com.sucker.suckermod.blocks;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {
	
	@ObjectHolder("suckermod:suckerblock")
	public static SuckerBlock SUCKERBLOCK;
	
	@ObjectHolder("suckermod:suckerblock")
	public static TileEntityType<SuckerBlockTile> SUCKERBLOCK_TILE;
	
	@ObjectHolder("suckermod:suckerblock")
	public static ContainerType<SuckerBlockContainer> SUCKERBLOCK_CONTAINER;
	//@ObjectHolder("suckermod:lavafreezeblock")
	//public static SuckerBlock LAVAFREEZEBLOCK;
	
	//@ObjectHolder("suckermod:flowfreezeblock")
	//public static SuckerBlock FLOWFREEZEBLOCK;
}
