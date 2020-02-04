package mod.fricativemelon.suckermod.blocks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IOccupiable {

    boolean isOccupied(World world, BlockPos pos);
}
