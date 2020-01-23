package mod.fricativemelon.suckermod.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MoverBlockContainer extends SuckerBlockContainer {
    public MoverBlockContainer(int id, World world, BlockPos pos, PlayerInventory inv, PlayerEntity player) {
        super(id, world, pos, inv, player, ModBlocks.MOVER.container);
    }

    public MoverBlockContainer(int id, BlockPos pos, PlayerInventory inv) {
        super(id, pos, inv, ModBlocks.MOVER.container);
    }

    @Override
    protected Block getBlock() {
        return ModBlocks.MOVER.block;
    }
}
