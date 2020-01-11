package mod.fricativemelon.suckermod.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class PlacerBlock extends SuckerBlock {

    public PlacerBlock() {
        super();
        setRegistryName("placerblock");
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PlacerBlockTile();
    }
}
