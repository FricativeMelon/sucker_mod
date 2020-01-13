package mod.fricativemelon.suckermod.blocks;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {

	@ObjectHolder("suckermod:placerblock")
	public static PlacerBlock PLACERBLOCK;

	@ObjectHolder("suckermod:placerblock")
	public static TileEntityType<PlacerBlockTile> PLACERBLOCK_TILE;

	@ObjectHolder("suckermod:placerblock")
	public static ContainerType<PlacerBlockContainer> PLACERBLOCK_CONTAINER;

	@ObjectHolder("suckermod:harvesterblock")
	public static HarvesterBlock HARVESTERBLOCK;

	@ObjectHolder("suckermod:harvesterblock")
	public static TileEntityType<HarvesterBlockTile> HARVESTERBLOCK_TILE;

	@ObjectHolder("suckermod:harvesterblock")
	public static ContainerType<HarvesterBlockContainer> HARVESTERBLOCK_CONTAINER;

	@ObjectHolder("suckermod:rotaterblock")
	public static RotaterBlock ROTATERBLOCK;

	//@ObjectHolder("suckermod:lavafreezeblock")
	//public static SuckerBlock LAVAFREEZEBLOCK;
	
	//@ObjectHolder("suckermod:flowfreezeblock")
	//public static SuckerBlock FLOWFREEZEBLOCK;
}
