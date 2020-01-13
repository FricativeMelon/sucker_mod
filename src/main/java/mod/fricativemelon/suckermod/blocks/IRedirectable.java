package mod.fricativemelon.suckermod.blocks;

import net.minecraft.util.math.BlockPos;

import java.util.function.Supplier;

public interface IRedirectable {

    BlockPos getTarget();

    void setTargetGetter(Supplier<BlockPos> sup);

}
