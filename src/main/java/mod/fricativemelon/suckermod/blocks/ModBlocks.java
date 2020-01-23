package mod.fricativemelon.suckermod.blocks;

import mod.fricativemelon.suckermod.utils.RegisteredBlock;
import mod.fricativemelon.suckermod.utils.RegisteredTileContainerBlock;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {

	public static RegisteredTileContainerBlock<PlacerBlock, PlacerBlockTile, PlacerBlockContainer> PLACER =
			new RegisteredTileContainerBlock<>("placerblock");

	public static RegisteredTileContainerBlock<HarvesterBlock, HarvesterBlockTile, HarvesterBlockContainer> HARVESTER =
			new RegisteredTileContainerBlock<>("harvesterblock");

	public static RegisteredTileContainerBlock<MoverBlock, MoverBlockTile, MoverBlockContainer> MOVER =
			new RegisteredTileContainerBlock<>("moverblock");

	public static RegisteredBlock<HarvesterArmBlock> HARVESTER_ARM =
			new RegisteredBlock<>("harvesterarmblock");

	public static RegisteredBlock<RotaterBlock> ROTATER =
			new RegisteredBlock<>("rotaterblock");

	/*@ObjectHolder("suckermod:placerblock")
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
	public static ContainerType<HarvesterBlockContainer> HARVESTERBLOCK_CONTAINER;*/

}
