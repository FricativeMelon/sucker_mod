package mod.fricativemelon.suckermod.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockDestroyProgress {

    @Nonnull
    private BlockPos pos;
    private Block block;
    private Item item;
    private int ticksLeft;
    private int totalTicks;

    public BlockDestroyProgress( @Nonnull BlockPos pos, Block block, Item item, int ticksLeft) {
        this.pos = pos;
        this.block = block;
        this.item = item;
        this.ticksLeft = ticksLeft;
        this.totalTicks = ticksLeft;
    }

    public DestructionResult continueDestruction(World world, @Nullable BlockPos pos, Item item) {
        if (this.pos.equals(pos) && this.block == world.getBlockState(pos).getBlock() && this.item == item) {
            this.ticksLeft--;
            if (this.ticksLeft == 0) {
                world.sendBlockBreakProgress(-1, pos, -1);
                return DestructionResult.FINISHED;
            } else {
                int x = 9 - (int)(10.0 * ticksLeft / totalTicks);
                world.sendBlockBreakProgress(-1, pos, x);
                return DestructionResult.CONTINUED;
            }
        }
        world.sendBlockBreakProgress(-1, this.pos, -1);
        return DestructionResult.INTERRUPTED;
    }

    public enum DestructionResult {
        CONTINUED,
        FINISHED,
        INTERRUPTED
    }
}
