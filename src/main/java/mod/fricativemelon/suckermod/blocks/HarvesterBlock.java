package mod.fricativemelon.suckermod.blocks;

import jdk.nashorn.internal.objects.annotations.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.Untainted;

public class HarvesterBlock extends SuckerBlock {

    public HarvesterBlock() {
        super();
    }

    public boolean isOccupied(World world, BlockPos pos) {
        boolean res = super.isOccupied(world, pos);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof HarvesterBlockTile) {
            res = res || ((HarvesterBlockTile) te).isHarvesting();
        }
        return res;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new HarvesterBlockTile();
    }
}
